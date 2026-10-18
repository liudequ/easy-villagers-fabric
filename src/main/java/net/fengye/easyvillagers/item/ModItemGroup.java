package net.fengye.easyvillagers.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fengye.easyvillagers.block.ModBlocks;
import net.minecraft.class_1761;
import net.minecraft.class_1799;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ModItemGroup {
    public static final class_1761 EASY_VILLAGERS_GROUP = FabricItemGroup.builder()
            .method_47320(() -> new class_1799(ModItems.VILLAGER_ITEM)) // icon
            .method_47321(class_2561.method_43471("itemGroup.easy_villagers.main")) // displayName
            .method_47317((displayContext, entries) -> {
                entries.method_45421(ModItems.VILLAGER_ITEM);
                entries.method_45421(ModBlocks.TRADER_ITEM);
                entries.method_45421(ModBlocks.AUTO_TRADER_ITEM);
                entries.method_45421(ModBlocks.BREEDER_ITEM);
                entries.method_45421(ModBlocks.INCUBATOR_ITEM);
                entries.method_45421(ModBlocks.CONVERTER_ITEM);
            })
            .method_47324(); // build

    public static void register() {
        class_2378.method_10230(class_7923.field_44687, new class_2960("easy_villagers", "main"), EASY_VILLAGERS_GROUP);
    }
}
