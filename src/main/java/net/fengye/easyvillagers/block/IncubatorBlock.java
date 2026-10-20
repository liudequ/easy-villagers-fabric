package net.fengye.easyvillagers.block;

import net.fengye.easyvillagers.block.entity.IncubatorBlockEntity;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.item.VillagerItem;
import net.minecraft.class_1264;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1750;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2237;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2464;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2741;
import net.minecraft.class_2753;
import net.minecraft.class_3965;
import net.minecraft.class_5558;

public class IncubatorBlock extends class_2237 {
    public static final class_2753 FACING = class_2741.field_12481;

    public IncubatorBlock(class_2248.class_2251 settings) {
        super(settings);
        this.method_9590(this.method_9564().method_11657(FACING, class_2350.field_11043));
    }

    @Override
    protected void method_9515(class_2689.class_2690<class_2248, class_2680> builder) {
        builder.method_11667(FACING);
    }

    @Override
    public class_2680 method_9605(class_1750 ctx) {
        return this.method_9564().method_11657(FACING, ctx.method_8042().method_10153());
    }

    @Override
    public class_2464 method_9604(class_2680 state) {
        return class_2464.field_11458; // MODEL
    }

    @Override
    public class_2586 method_10123(class_2338 pos, class_2680 state) {
        return new IncubatorBlockEntity(pos, state);
    }

    @Override
    public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) {
        return world.field_9236 ? null : method_31618(type, ModBlocks.INCUBATOR_BLOCK_ENTITY, IncubatorBlockEntity::tick);
    }

    @Override
    public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) {
        if (world.field_9236) {
            return class_1269.field_5812;
        }

        class_2586 be = world.method_8321(pos);
        if (!(be instanceof IncubatorBlockEntity incubator)) {
            return class_1269.field_5811;
        }

        class_1799 mainStack = player.method_6047();

        // 1. 放入幼年村民
        if (mainStack.method_7909() == ModItems.VILLAGER_ITEM) {
            if (!VillagerItem.isBaby(mainStack)) {
                player.method_7353(class_2561.method_43470("§c⚠️ 孵化机仅支持放入幼年村民！"), true);
                return class_1269.field_5812;
            }
            if (!incubator.hasBaby()) {
                class_1799 toInsert = mainStack.method_7972();
                toInsert.method_7939(1);
                incubator.method_5447(IncubatorBlockEntity.INPUT_SLOT, toInsert);
                if (!player.method_7337()) {
                    mainStack.method_7934(1);
                }
                player.method_7353(class_2561.method_43470("§a已将幼年村民放入孵化机！"), true);
                return class_1269.field_5812;
            } else {
                player.method_7353(class_2561.method_43470("§c孵化机内已有幼年村民正在催熟！"), true);
                return class_1269.field_5812;
            }
        }

        // 2. 潜行空手取出
        if (player.method_5715() && mainStack.method_7960()) {
            class_1799 out = incubator.method_5438(IncubatorBlockEntity.OUTPUT_SLOT);
            if (!out.method_7960()) {
                class_1799 extracted = incubator.method_5441(IncubatorBlockEntity.OUTPUT_SLOT);
                if (!player.method_31548().method_7394(extracted)) {
                    class_2248.method_9577(world, pos, extracted);
                }
                player.method_7353(class_2561.method_43470("§a已取出催熟长大的成年村民！"), true);
                return class_1269.field_5812;
            }
            class_1799 in = incubator.method_5438(IncubatorBlockEntity.INPUT_SLOT);
            if (!in.method_7960()) {
                class_1799 extracted = incubator.method_5441(IncubatorBlockEntity.INPUT_SLOT);
                if (!player.method_31548().method_7394(extracted)) {
                    class_2248.method_9577(world, pos, extracted);
                }
                player.method_7353(class_2561.method_43470("§e已取出幼年村民！"), true);
                return class_1269.field_5812;
            }
        }

        // 3. 普通右键查看状态
        boolean hasBaby = incubator.hasBaby();
        int progress = incubator.getGrowthProgressPercent();
        boolean hasAdult = !incubator.method_5438(IncubatorBlockEntity.OUTPUT_SLOT).method_7960();
        StringBuilder sb = new StringBuilder("§e🐣 幼体孵化机: §f");
        if (hasBaby) {
            sb.append("[状态: 极速催熟中] [进度: ").append(progress).append("%]");
        } else if (hasAdult) {
            sb.append("[状态: §a催熟完成，已可取出成年村民§f]");
        } else {
            sb.append("[状态: 空闲 (请放入幼年村民)]");
        }
        player.method_7353(class_2561.method_43470(sb.toString()), true);

        return class_1269.field_5812;
    }

    @Override
    public void method_9536(class_2680 state, class_1937 world, class_2338 pos, class_2680 newState, boolean moved) {
        if (!state.method_27852(newState.method_26204())) {
            class_2586 be = world.method_8321(pos);
            if (be instanceof IncubatorBlockEntity incubator) {
                class_1264.method_5451(world, pos, incubator);
            }
            super.method_9536(state, world, pos, newState, moved);
        }
    }
}
