#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 1. 查找 Java/Javac
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
    JAR="$JAVA_HOME/bin/jar"
    JAVA="$JAVA_HOME/bin/java"
elif command -v javac >/dev/null 2>&1; then
    JAVAC="javac"
    JAR="jar"
    JAVA="java"
elif [ -x "/home/fengye/mc-adventure/jdk17/bin/javac" ]; then
    JAVAC="/home/fengye/mc-adventure/jdk17/bin/javac"
    JAR="/home/fengye/mc-adventure/jdk17/bin/jar"
    JAVA="/home/fengye/mc-adventure/jdk17/bin/java"
else
    echo "Error: javac not found. Please install JDK 17+ or set JAVA_HOME."
    exit 1
fi

echo "Using Java compiler: $($JAVAC -version 2>&1)"

CACHE_DIR="${EV_CACHE_DIR:-$DIR/.cache}"
mkdir -p "$CACHE_DIR"

# 2. 检查并准备依赖
CLIENT_INTERMEDIARY=""
LOADER_JAR=""
FAPI_JARS_DIR=""
JOML_JAR=""
BRIGADIER_JAR=""
DFU_JAR=""
GSON_JAR=""
MIXIN_JAR=""

# 优先检查本地fengye专属开发环境缓存
if [ "${EV_STANDALONE:-0}" != "1" ] && [ -f "/home/fengye/mc-mod-dev/cache/client-intermediary.jar" ] && [ -d "/home/fengye/mc-adventure/libraries" ]; then
    echo "Detected local development environment. Using local cached libraries..."
    CLIENT_INTERMEDIARY="/home/fengye/mc-mod-dev/cache/client-intermediary.jar"
    LOADER_JAR="/home/fengye/mc-adventure/libraries/net/fabricmc/fabric-loader/0.16.14/fabric-loader-0.16.14.jar"
    FAPI_JARS_DIR="/home/fengye/mc-mod-dev/cache/extracted_jars"
    JOML_JAR="/home/fengye/mc-adventure/libraries/org/joml/joml/1.10.5/joml-1.10.5.jar"
    BRIGADIER_JAR="/home/fengye/mc-adventure/libraries/com/mojang/brigadier/1.1.8/brigadier-1.1.8.jar"
    DFU_JAR="/home/fengye/mc-adventure/libraries/com/mojang/datafixerupper/6.0.8/datafixerupper-6.0.8.jar"
    GSON_JAR="/home/fengye/mc-adventure/libraries/com/google/code/gson/gson/2.10/gson-2.10.jar"
    MIXIN_JAR="/home/fengye/mc-mod-dev/cache/sponge-mixin-0.15.5+mixin.0.8.7.jar"
else
    echo "Preparing standalone dependencies in $CACHE_DIR..."
    
    # 依赖下载函数
    download_if_missing() {
        local file="$1"
        local url="$2"
        if [ ! -f "$file" ]; then
            echo "Downloading $(basename "$file")..."
            curl --fail --silent --show-error --location --retry 3 --retry-all-errors --connect-timeout 20 --max-time 180 "$url" -o "$file.part"
            mv "$file.part" "$file"
        fi
    }

    download_if_missing "$CACHE_DIR/fabric-loader-0.16.14.jar" "https://maven.fabricmc.net/net/fabricmc/fabric-loader/0.16.14/fabric-loader-0.16.14.jar"
    download_if_missing "$CACHE_DIR/fabric-api-0.92.12+1.20.1.jar" "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/0.92.12+1.20.1/fabric-api-0.92.12+1.20.1.jar"
    download_if_missing "$CACHE_DIR/joml-1.10.5.jar" "https://repo1.maven.org/maven2/org/joml/joml/1.10.5/joml-1.10.5.jar"
    download_if_missing "$CACHE_DIR/brigadier-1.1.8.jar" "https://libraries.minecraft.net/com/mojang/brigadier/1.1.8/brigadier-1.1.8.jar"
    download_if_missing "$CACHE_DIR/datafixerupper-6.0.8.jar" "https://libraries.minecraft.net/com/mojang/datafixerupper/6.0.8/datafixerupper-6.0.8.jar"
    
    download_if_missing "$CACHE_DIR/gson-2.10.jar" "https://libraries.minecraft.net/com/google/code/gson/gson/2.10/gson-2.10.jar"
    download_if_missing "$CACHE_DIR/sponge-mixin-0.15.5+mixin.0.8.7.jar" "https://maven.fabricmc.net/net/fabricmc/sponge-mixin/0.15.5+mixin.0.8.7/sponge-mixin-0.15.5+mixin.0.8.7.jar"

    # 解压 Fabric API 内置模块
    FAPI_JARS_DIR="$CACHE_DIR/fapi_modules"
    if [ ! -d "$FAPI_JARS_DIR" ] || [ -z "$(ls -A "$FAPI_JARS_DIR" 2>/dev/null)" ]; then
        echo "Extracting Fabric API modules..."
        mkdir -p "$FAPI_JARS_DIR"
        unzip -q -j "$CACHE_DIR/fabric-api-0.92.12+1.20.1.jar" "META-INF/jars/*.jar" -d "$FAPI_JARS_DIR"
    fi

    # 准备 client-intermediary.jar
    CLIENT_INTERMEDIARY="$CACHE_DIR/client-intermediary.jar"
    if [ ! -f "$CLIENT_INTERMEDIARY" ]; then
        echo "Generating client-intermediary.jar from vanilla client and Fabric mappings..."
        download_if_missing "$CACHE_DIR/client.jar" "https://piston-data.mojang.com/v1/objects/84fe48e5c898d25d1976a9163274219468965825/client.jar"
        download_if_missing "$CACHE_DIR/mappings.tiny" "https://raw.githubusercontent.com/FabricMC/intermediary/master/mappings/1.20.1.tiny"
        
        # Loader 内置 TinyRemapper，但未内置其 ASM 运行库；全新 CI 缓存必须显式补齐。
        REMAP_CP="$CACHE_DIR/fabric-loader-0.16.14.jar"
        for module in asm asm-analysis asm-commons asm-tree asm-util; do
            download_if_missing "$CACHE_DIR/$module-9.8.jar" "https://maven.fabricmc.net/org/ow2/asm/$module/9.8/$module-9.8.jar"
            REMAP_CP="$REMAP_CP:$CACHE_DIR/$module-9.8.jar"
        done
        mkdir -p "$CACHE_DIR/remap_classes"
        "$JAVAC" -cp "$REMAP_CP" -d "$CACHE_DIR/remap_classes" "$DIR/scripts/RemapClient.java"
        "$JAVA" -Xmx1G -cp "$CACHE_DIR/remap_classes:$REMAP_CP" net.fengye.easyvillagers.build.RemapClient \
            "$CACHE_DIR/client.jar" "$CLIENT_INTERMEDIARY" "$CACHE_DIR/mappings.tiny"
    fi

    LOADER_JAR="$CACHE_DIR/fabric-loader-0.16.14.jar"
    JOML_JAR="$CACHE_DIR/joml-1.10.5.jar"
    BRIGADIER_JAR="$CACHE_DIR/brigadier-1.1.8.jar"
    DFU_JAR="$CACHE_DIR/datafixerupper-6.0.8.jar"
    GSON_JAR="$CACHE_DIR/gson-2.10.jar"
    MIXIN_JAR="$CACHE_DIR/sponge-mixin-0.15.5+mixin.0.8.7.jar"
fi

# 3. 构建编译 Classpath
CP="$CLIENT_INTERMEDIARY:$LOADER_JAR:$JOML_JAR:$BRIGADIER_JAR:$DFU_JAR:$GSON_JAR:$MIXIN_JAR"
for f in "$FAPI_JARS_DIR"/*.jar; do
    if [ -f "$f" ]; then
        CP="$CP:$f"
    fi
done

# 4. 编译 Java 源码
BUILD_DIR="$DIR/build"
CLASSES_DIR="$BUILD_DIR/classes"
rm -rf "$CLASSES_DIR"
mkdir -p "$CLASSES_DIR"

echo "Compiling Java sources..."
SOURCES=$(find "$DIR/src/main/java" -name "*.java")
"$JAVAC" --release 17 -proc:none -encoding UTF-8 -cp "$CP" -d "$CLASSES_DIR" $SOURCES

# 5. 复制资源文件
echo "Copying resources..."
cp -r "$DIR/src/main/resources/"* "$CLASSES_DIR/"

# 6. 打包生成 jar
LIBS_DIR="$BUILD_DIR/libs"
mkdir -p "$LIBS_DIR"
MOD_VERSION=$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["version"])' "$DIR/src/main/resources/fabric.mod.json")
TARGET_JAR="$LIBS_DIR/easy-villagers-fabric-$MOD_VERSION.jar"
# 每个版本只生成一个文件，避免旧文件名指向新版内容或 Release 附件重复。
find "$LIBS_DIR" -maxdepth 1 -type f -name 'easy-villagers-fabric-*.jar' -delete

echo "Packaging jar to $TARGET_JAR..."
"$JAR" -cf "$TARGET_JAR" -C "$CLASSES_DIR" .

echo "Build successful! Target: $TARGET_JAR"
ls -lh "$TARGET_JAR"
