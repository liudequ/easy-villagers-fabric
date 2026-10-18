package net.fengye.easyvillagers.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fengye.easyvillagers.block.entity.AutoTraderBlockEntity;
import net.fengye.easyvillagers.block.entity.TraderBlockEntity;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2591;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ModBlocks {
    public static final String MOD_ID = "easy_villagers";

    // 1. 普通交易展柜方块与方块实体
    public static final class_2248 TRADER_BLOCK = new TraderBlock(
            FabricBlockSettings.copyOf(class_2246.field_10562).nonOpaque()
    );

    public static final class_1792 TRADER_ITEM = new class_1747(
            TRADER_BLOCK,
            new class_1792.class_1793()
    );

    public static final class_2591<TraderBlockEntity> TRADER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(
            TraderBlockEntity::new,
            TRADER_BLOCK
    ).build();

    // 2. 全自动交易展柜方块与方块实体（阶段三）
    public static final class_2248 AUTO_TRADER_BLOCK = new AutoTraderBlock(
            FabricBlockSettings.copyOf(class_2246.field_10562).nonOpaque()
    );

    public static final class_1792 AUTO_TRADER_ITEM = new class_1747(
            AUTO_TRADER_BLOCK,
            new class_1792.class_1793()
    );

    public static final class_2591<AutoTraderBlockEntity> AUTO_TRADER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(
            AutoTraderBlockEntity::new,
            AUTO_TRADER_BLOCK
    ).build();

    public static void register() {
        // 注册普通交易展柜
        class_2378.method_10230(class_7923.field_41175, new class_2960(MOD_ID, "trader"), TRADER_BLOCK);
        class_2378.method_10230(class_7923.field_41178, new class_2960(MOD_ID, "trader"), TRADER_ITEM);
        class_2378.method_10230(class_7923.field_41181, new class_2960(MOD_ID, "trader"), TRADER_BLOCK_ENTITY);

        // 注册全自动交易展柜
        class_2378.method_10230(class_7923.field_41175, new class_2960(MOD_ID, "auto_trader"), AUTO_TRADER_BLOCK);
        class_2378.method_10230(class_7923.field_41178, new class_2960(MOD_ID, "auto_trader"), AUTO_TRADER_ITEM);
        class_2378.method_10230(class_7923.field_41181, new class_2960(MOD_ID, "auto_trader"), AUTO_TRADER_BLOCK_ENTITY);
    }
}
