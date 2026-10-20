package net.fengye.easyvillagers.block;

import net.fengye.easyvillagers.block.entity.BreederBlockEntity;
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

public class BreederBlock extends class_2237 {
    public static final class_2753 FACING = class_2741.field_12481;

    public BreederBlock(class_2248.class_2251 settings) {
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
        return new BreederBlockEntity(pos, state);
    }

    @Override
    public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) {
        return world.field_9236 ? null : method_31618(type, ModBlocks.BREEDER_BLOCK_ENTITY, BreederBlockEntity::tick);
    }

    @Override
    public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) {
        if (world.field_9236) {
            return class_1269.field_5812;
        }

        class_2586 be = world.method_8321(pos);
        if (!(be instanceof BreederBlockEntity breeder)) {
            return class_1269.field_5811;
        }

        class_1799 heldStack = player.method_5998(hand);

        // 1. 放入村民
        if (heldStack.method_7909() == ModItems.VILLAGER_ITEM) {
            if (VillagerItem.isBaby(heldStack)) {
                player.method_7353(class_2561.method_43470("§c⚠️ 幼年村民无法参与繁殖，请放入成年村民！"), true);
                return class_1269.field_5812;
            }
            if (breeder.getVillagerCount() < 2) {
                breeder.addVillager(heldStack);
                if (!player.method_7337()) {
                    heldStack.method_7934(1);
                }
                player.method_7353(class_2561.method_43470("§a已将村民放入繁殖机 (" + breeder.getVillagerCount() + "/2)"), true);
                return class_1269.field_5812;
            } else {
                player.method_7353(class_2561.method_43470("§c繁殖机内村民已满 (2/2)！"), true);
                return class_1269.field_5812;
            }
        }

        // 2. 放入食物（面包、胡萝卜、马铃薯、甜菜根）
        if (BreederBlockEntity.isVillagerFood(heldStack)) {
            if (breeder.insertFood(heldStack)) {
                player.method_7353(class_2561.method_43470("§a已投入食物！当前食物储备: " + breeder.getTotalFoodPoints() + " 点"), true);
                return class_1269.field_5812;
            } else {
                player.method_7353(class_2561.method_43470("§c食物槽已满！"), true);
                return class_1269.field_5812;
            }
        }

        // 3. 潜行空手取出村民
        if (player.method_5715() && heldStack.method_7960()) {
            if (breeder.getVillagerCount() > 0) {
                class_1799 vil = breeder.extractVillager();
                if (!player.method_31548().method_7394(vil)) {
                    class_2248.method_9577(world, pos, vil);
                }
                player.method_7353(class_2561.method_43470("§e已取出繁殖机中的村民 (剩余: " + breeder.getVillagerCount() + "/2)"), true);
                return class_1269.field_5812;
            }
        }

        // 4. 普通右键查看状态
        int vCount = breeder.getVillagerCount();
        int food = breeder.getTotalFoodPoints();
        int progress = breeder.getBreedProgressPercent();
        player.method_7353(class_2561.method_43470("§d❤️ 村民繁殖机: §f[村民: " + vCount + "/2] [食物值: " + food + "点] [繁殖进度: " + progress + "%]"), true);

        return class_1269.field_5812;
    }

    @Override
    public void method_9536(class_2680 state, class_1937 world, class_2338 pos, class_2680 newState, boolean moved) {
        if (!state.method_27852(newState.method_26204())) {
            class_2586 be = world.method_8321(pos);
            if (be instanceof BreederBlockEntity breeder) {
                class_1264.method_5451(world, pos, breeder);
                while (breeder.getVillagerCount() > 0) {
                    class_1799 vil = breeder.extractVillager();
                    class_2248.method_9577(world, pos, vil);
                }
            }
            super.method_9536(state, world, pos, newState, moved);
        }
    }
}
