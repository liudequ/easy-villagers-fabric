package net.fengye.easyvillagers;

import net.fabricmc.api.ModInitializer;

public class EasyVillagersMod implements ModInitializer {
    public static final String MOD_ID = "easy_villagers";

    @Override
    public void onInitialize() {
        System.out.println("[EasyVillagers-Fabric] Initializing Easy Villagers Fabric Edition...");
        // 服务端与通用逻辑注册（方块、物品、网络通信、方块实体等）
        System.out.println("[EasyVillagers-Fabric] Mod initialized successfully!");
    }
}
