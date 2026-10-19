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

CACHE_DIR="$DIR/.cache"
mkdir -p "$CACHE_DIR"

# 2. 检查并准备依赖
CLIENT_INTERMEDIARY=""
LOADER_JAR=""
FAPI_JARS_DIR=""
JOML_JAR=""
BRIGADIER_JAR=""
DFU_JAR=""

# 优先检查本地fengye专属开发环境缓存
if [ -f "/home/fengye/mc-mod-dev/cache/client-intermediary.jar" ] && [ -d "/home/fengye/mc-adventure/libraries" ]; then
    echo "Detected local development environment. Using local cached libraries..."
    CLIENT_INTERMEDIARY="/home/fengye/mc-mod-dev/cache/client-intermediary.jar"
    LOADER_JAR="/home/fengye/mc-adventure/libraries/net/fabricmc/fabric-loader/0.16.14/fabric-loader-0.16.14.jar"
    FAPI_JARS_DIR="/home/fengye/mc-mod-dev/cache/extracted_jars"
    JOML_JAR="/home/fengye/mc-adventure/libraries/org/joml/joml/1.10.5/joml-1.10.5.jar"
    BRIGADIER_JAR="/home/fengye/mc-adventure/libraries/com/mojang/brigadier/1.1.8/brigadier-1.1.8.jar"
    DFU_JAR="/home/fengye/mc-adventure/libraries/com/mojang/datafixerupper/6.0.8/datafixerupper-6.0.8.jar"
else
    echo "Preparing standalone dependencies in $CACHE_DIR..."
    
    # 依赖下载函数
    download_if_missing() {
        local file="$1"
        local url="$2"
        if [ ! -f "$file" ]; then
            echo "Downloading $(basename "$file")..."
            curl -sSL --retry 3 "$url" -o "$file"
        fi
    }

    download_if_missing "$CACHE_DIR/fabric-loader-0.16.14.jar" "https://maven.fabricmc.net/net/fabricmc/fabric-loader/0.16.14/fabric-loader-0.16.14.jar"
    download_if_missing "$CACHE_DIR/fabric-api-0.92.12+1.20.1.jar" "https://cdn.modrinth.com/data/P7dR8mSH/versions/8ZpYmE1B/fabric-api-0.92.12%2B1.20.1.jar"
    download_if_missing "$CACHE_DIR/joml-1.10.5.jar" "https://repo1.maven.org/maven2/org/joml/joml/1.10.5/joml-1.10.5.jar"
    download_if_missing "$CACHE_DIR/brigadier-1.1.8.jar" "https://libraries.minecraft.net/com/mojang/brigadier/1.1.8/brigadier-1.1.8.jar"
    download_if_missing "$CACHE_DIR/datafixerupper-6.0.8.jar" "https://libraries.minecraft.net/com/mojang/datafixerupper/6.0.8/datafixerupper-6.0.8.jar"
    
    # 解压 Fabric API 内置模块
    FAPI_JARS_DIR="$CACHE_DIR/fapi_modules"
    if [ ! -d "$FAPI_JARS_DIR" ] || [ -z "$(ls -A "$FAPI_JARS_DIR" 2>/dev/null)" ]; then
        echo "Extracting Fabric API modules..."
        mkdir -p "$FAPI_JARS_DIR"
        unzip -q -j "$CACHE_DIR/fabric-api-0.92.12+1.20.1.jar" "META-INF/jars/*.jar" -d "$FAPI_JARS_DIR" || true
    fi

    # 准备 client-intermediary.jar
    CLIENT_INTERMEDIARY="$CACHE_DIR/client-intermediary.jar"
    if [ ! -f "$CLIENT_INTERMEDIARY" ]; then
        echo "Generating client-intermediary.jar from vanilla client and Fabric mappings..."
        download_if_missing "$CACHE_DIR/client.jar" "https://piston-data.mojang.com/v1/objects/84fe48e5c898d25d1976a9163274219468965825/client.jar"
        download_if_missing "$CACHE_DIR/mappings.tiny" "https://raw.githubusercontent.com/FabricMC/intermediary/master/mappings/1.20.1.tiny"
        
        # 编译并运行 RemapClient
        mkdir -p "$CACHE_DIR/remap_classes"
        "$JAVAC" -cp "$CACHE_DIR/fabric-loader-0.16.14.jar" -d "$CACHE_DIR/remap_classes" "$DIR/scripts/RemapClient.java"
        "$JAVA" -cp "$CACHE_DIR/remap_classes:$CACHE_DIR/fabric-loader-0.16.14.jar" net.fengye.easyvillagers.build.RemapClient \
            "$CACHE_DIR/client.jar" "$CLIENT_INTERMEDIARY" "$CACHE_DIR/mappings.tiny"
    fi

    LOADER_JAR="$CACHE_DIR/fabric-loader-0.16.14.jar"
    JOML_JAR="$CACHE_DIR/joml-1.10.5.jar"
    BRIGADIER_JAR="$CACHE_DIR/brigadier-1.1.8.jar"
    DFU_JAR="$CACHE_DIR/datafixerupper-6.0.8.jar"
fi

# 3. 构建编译 Classpath
CP="$CLIENT_INTERMEDIARY:$LOADER_JAR:$JOML_JAR:$BRIGADIER_JAR:$DFU_JAR"
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
"$JAVAC" -proc:none -encoding UTF-8 -cp "$CP" -d "$CLASSES_DIR" $SOURCES

# 5. 复制资源文件
echo "Copying resources..."
cp -r "$DIR/src/main/resources/"* "$CLASSES_DIR/"

# 6. 打包生成 jar
LIBS_DIR="$BUILD_DIR/libs"
mkdir -p "$LIBS_DIR"
TARGET_JAR="$LIBS_DIR/easy-villagers-fabric-1.0.2.jar"

echo "Packaging jar to $TARGET_JAR..."
"$JAR" -cf "$TARGET_JAR" -C "$CLASSES_DIR" .
cp -f "$TARGET_JAR" "$LIBS_DIR/easy-villagers-fabric-1.0.0.jar"

echo "Build successful! Target: $TARGET_JAR"
ls -lh "$TARGET_JAR"
