package net.fengye.easyvillagers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EasyVillagersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[EasyVillagers-Fabric] Initializing Client Renderers & Handlers...");
        // 客户端渲染器注册（方块实体 3D 微缩渲染器 BER、GUI 屏幕等）
    }
}
