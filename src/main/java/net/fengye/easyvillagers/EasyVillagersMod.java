package net.fengye.easyvillagers;

import net.fabricmc.api.ModInitializer;

public class EasyVillagersMod implements ModInitializer {
    public static final String MOD_ID = "easy_villagers";

    @Override
    public void onInitialize() {
        System.out.println("[EasyVillagers-Fabric] Initializing Easy Villagers Fabric Edition...");
        // 注册物品
        net.fengye.easyvillagers.item.ModItems.register();
        // 注册方块与方块实体
        net.fengye.easyvillagers.block.ModBlocks.register();
        // 注册创造模式物品组
        net.fengye.easyvillagers.item.ModItemGroup.register();
        // 注册村民抓取监听
        net.fengye.easyvillagers.event.VillagerPickupHandler.register();
        System.out.println("[EasyVillagers-Fabric] Mod initialized successfully!");
    }
}
