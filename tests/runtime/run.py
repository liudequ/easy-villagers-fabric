#!/usr/bin/env python3
"""在隔离世界验证服务端交易行为及客户端 Mixin，不加载正式存档。"""
import argparse
import json
import os
from pathlib import Path
import shutil
import socket
import subprocess
import tempfile
import time
import zipfile


def package_test(java_bin, classpath, source, output, entrypoint, environment):
    classes = output / "test-classes"
    classes.mkdir()
    subprocess.run([str(java_bin / "javac"), "--release", "17", "-proc:none", "-encoding", "UTF-8",
                    "-cp", classpath, "-d", str(classes), str(source)], check=True)
    metadata = {"schemaVersion": 1, "id": "ev_preview_smoke", "version": "1",
                "environment": environment,
                "entrypoints": {entrypoint: ["net.fengye.easyvillagers." + (
                    "util.PreviewSmoke" if environment == "server" else "smoke.ClientMixinSmoke")]}}
    (classes / "fabric.mod.json").write_text(json.dumps(metadata), encoding="utf-8")
    with zipfile.ZipFile(output / "mods" / "ev-preview-smoke.jar", "w") as jar:
        for file in classes.rglob("*"):
            if file.is_file():
                jar.write(file, file.relative_to(classes))


def server_check(command, directory):
    log_file = directory / "runtime.log"
    with log_file.open("w") as log:
        process = subprocess.Popen(command, cwd=directory, stdin=subprocess.PIPE,
                                   stdout=log, stderr=subprocess.STDOUT, text=True)
        try:
            deadline = time.monotonic() + 180
            while process.poll() is None and time.monotonic() < deadline:
                if "[EV-SMOKE FINISHED]" in log_file.read_text(errors="replace"):
                    break
                time.sleep(0.5)
            if process.poll() is None:
                process.stdin.write("stop\n")
                process.stdin.flush()
            process.wait(timeout=40)
        finally:
            if process.poll() is None:
                process.terminate()
                try:
                    process.wait(timeout=15)
                except subprocess.TimeoutExpired:
                    # 仅结束本脚本创建的隔离测试 JVM，绝不触碰正式服务进程。
                    process.kill()
                    process.wait()
    text = log_file.read_text(errors="replace")
    for line in text.splitlines():
        if "EV-SMOKE" in line:
            print(line)
    if "[EV-SMOKE FINISHED]" not in text or "[EV-SMOKE FAILED]" in text:
        raise RuntimeError(text[-12000:])


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--server-dir", required=True, type=Path,
                        help="已初始化的 Fabric 1.20.1 服务端目录（仅复制依赖）")
    parser.add_argument("--cache-dir", required=True, type=Path,
                        help="包含 client.jar、client-intermediary.jar、extracted_jars 的编译缓存")
    parser.add_argument("--java-home", type=Path, default=os.environ.get("JAVA_HOME"))
    args = parser.parse_args()
    server = args.server_dir.resolve()
    cache = args.cache_dir.resolve()
    java_bin = args.java_home.resolve() / "bin" if args.java_home else Path(shutil.which("javac")).resolve().parent
    root = Path(__file__).resolve().parents[2]
    version = json.loads((root / "src/main/resources/fabric.mod.json").read_text())["version"]
    mod = root / "build/libs" / f"easy-villagers-fabric-{version}.jar"
    fabric_api = next((server / "mods").glob("fabric-api-*.jar"))
    libraries = list((server / "libraries").rglob("*.jar"))
    compile_cp = os.pathsep.join(map(str, [cache / "client-intermediary.jar", mod]
                                   + libraries + list((cache / "extracted_jars").glob("*.jar"))))
    # 保留日志和失败现场供排查；目录每次随机创建，不能指向正式世界。
    directory = Path(tempfile.mkdtemp(prefix="ev-preview-tests-"))
    print(f"隔离测试目录：{directory}", flush=True)
    runtime = directory / "server"
    client = directory / "client"
    for target in (runtime, client):
        (target / "mods").mkdir(parents=True)
        shutil.copy2(mod, target / "mods")
        shutil.copy2(fabric_api, target / "mods")
    shutil.copy2(server / "fabric-server-launch.jar", runtime)
    for relative in ("libraries", "versions", ".fabric/server"):
        shutil.copytree(server / relative, runtime / relative)
    (runtime / "eula.txt").write_text("eula=true\n")
    with socket.socket() as listener:
        listener.bind(("127.0.0.1", 0))
        port = listener.getsockname()[1]
    (runtime / "server.properties").write_text(
        f"server-ip=127.0.0.1\nserver-port={port}\nonline-mode=false\nenable-rcon=false\n"
        "view-distance=2\nsimulation-distance=2\nlevel-type=minecraft:flat\n"
        'generator-settings={"layers":[{"block":"minecraft:bedrock","height":1}],"biome":"minecraft:plains"}\n'
        "generate-structures=false\nmax-tick-time=90000\n")
    package_test(java_bin, compile_cp, root / "tests/runtime/PreviewSmoke.java", runtime, "main", "server")
    server_check([str(java_bin / "java"), "-Xms128M", "-Xmx512M", "-jar",
                  "fabric-server-launch.jar", "nogui"], runtime)
    package_test(java_bin, compile_cp, root / "tests/runtime/ClientMixinSmoke.java", client, "preLaunch", "client")
    client_cp = os.pathsep.join(map(str, libraries + [cache / "client.jar"]))
    with (client / "runtime.log").open("w") as log:
        result = subprocess.run([str(java_bin / "java"), "-Xmx512M", "-Djava.awt.headless=true",
                                 f"-Dfabric.gameJarPath={cache / 'client.jar'}", "-cp", client_cp,
                                 "net.fabricmc.loader.impl.launch.knot.KnotClient", "--version", "1.20.1",
                                 "--gameDir", str(client), "--assetsDir", str(client / "assets")],
                                cwd=client, stdout=log, stderr=subprocess.STDOUT, timeout=120)
    text = (client / "runtime.log").read_text(errors="replace")
    if result.returncode != 0 or "[EV-CLIENT-MIXIN PASS]" not in text:
        raise RuntimeError(text[-12000:])
    print("[EV-CLIENT-MIXIN PASS] MerchantScreen 变换及类校验通过（不包含实际画面测试）")


if __name__ == "__main__":
    main()
