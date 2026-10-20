package net.fengye.easyvillagers.block;

import net.fengye.easyvillagers.block.entity.ConverterBlockEntity;
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

public class ConverterBlock extends class_2237 {
    public static final class_2753 FACING = class_2741.field_12481;

    public ConverterBlock(class_2248.class_2251 settings) {
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
        return new ConverterBlockEntity(pos, state);
    }

    @Override
    public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) {
        return world.field_9236 ? null : method_31618(type, ModBlocks.CONVERTER_BLOCK_ENTITY, ConverterBlockEntity::tick);
    }

    @Override
    public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) {
        if (world.field_9236) {
            return class_1269.field_5812;
        }

        class_2586 be = world.method_8321(pos);
        if (!(be instanceof ConverterBlockEntity converter)) {
            return class_1269.field_5811;
        }

        class_1799 mainStack = player.method_6047();

        // 1. 放入村民
        if (mainStack.method_7909() == ModItems.VILLAGER_ITEM) {
            if (VillagerItem.isBaby(mainStack)) {
                player.method_7353(class_2561.method_43470("§c⚠️ 幼年村民无法参与僵尸转化治愈！"), true);
                return class_1269.field_5812;
            }
            if (!converter.hasVillager() && converter.method_5438(ConverterBlockEntity.SLOT_OUTPUT).method_7960()) {
                converter.addVillager(mainStack);
                if (!player.method_7337()) {
                    mainStack.method_7934(1);
                }
                player.method_7353(class_2561.method_43470("§a已将村民放入转化机，等待金苹果与虚弱药水！"), true);
                return class_1269.field_5812;
            } else {
                player.method_7353(class_2561.method_43470("§c转化机内已有村民正在处理！"), true);
                return class_1269.field_5812;
            }
        }

        // 2. 放入金苹果
        if (ConverterBlockEntity.isGoldenApple(mainStack)) {
            if (converter.insertGoldenApple(mainStack)) {
                player.method_7353(class_2561.method_43470("§6已投入金苹果！"), true);
                return class_1269.field_5812;
            }
        }

        // 3. 放入虚弱药水
        if (ConverterBlockEntity.isWeaknessPotion(mainStack)) {
            if (converter.insertWeaknessPotion(mainStack)) {
                player.method_7353(class_2561.method_43470("§d已投入虚弱药水！"), true);
                return class_1269.field_5812;
            }
        }

        // 4. 潜行空手取出
        if (player.method_5715() && mainStack.method_7960()) {
            class_1799 out = converter.method_5438(ConverterBlockEntity.SLOT_OUTPUT);
            if (!out.method_7960()) {
                class_1799 extracted = converter.method_5441(ConverterBlockEntity.SLOT_OUTPUT);
                if (!player.method_31548().method_7394(extracted)) {
                    class_2248.method_9577(world, pos, extracted);
                }
                player.method_7353(class_2561.method_43470("§6✨ 成功取出获得永久骨折特惠的治愈村民！"), true);
                return class_1269.field_5812;
            }
            if (converter.hasVillager() && converter.getCurrentState() == ConverterBlockEntity.State.IDLE) {
                class_1799 extracted = converter.extractVillager();
                if (!player.method_31548().method_7394(extracted)) {
                    class_2248.method_9577(world, pos, extracted);
                }
                player.method_7353(class_2561.method_43470("§e已取出未转化的村民！"), true);
                return class_1269.field_5812;
            }
        }

        // 5. 普通右键查看状态
        ConverterBlockEntity.State stateEnum = converter.getCurrentState();
        int progress = converter.getProgressPercent();
        int gApple = converter.method_5438(ConverterBlockEntity.SLOT_GOLDEN_APPLE).method_7947();
        int potion = converter.method_5438(ConverterBlockEntity.SLOT_POTION).method_7947();
        boolean hasOut = !converter.method_5438(ConverterBlockEntity.SLOT_OUTPUT).method_7960();

        StringBuilder sb = new StringBuilder("§5🧪 村民转化机: §f");
        if (hasOut) {
            sb.append("[状态: §6✨ 治愈完成，请取出骨折村民§f]");
        } else if (stateEnum == ConverterBlockEntity.State.INFECTING) {
            sb.append("[状态: §c僵尸化感染中§f] [进度: ").append(progress).append("%]");
        } else if (stateEnum == ConverterBlockEntity.State.CURING) {
            sb.append("[状态: §a金苹果深度治愈中§f] [进度: ").append(progress).append("%]");
        } else {
            sb.append("[状态: ").append(converter.hasVillager() ? "等待材料" : "空闲")
              .append("] [金苹果: ").append(gApple).append("] [虚弱药水: ").append(potion).append("]");
        }
        player.method_7353(class_2561.method_43470(sb.toString()), true);

        return class_1269.field_5812;
    }

    @Override
    public void method_9536(class_2680 state, class_1937 world, class_2338 pos, class_2680 newState, boolean moved) {
        if (!state.method_27852(newState.method_26204())) {
            class_2586 be = world.method_8321(pos);
            if (be instanceof ConverterBlockEntity converter) {
                class_1264.method_5451(world, pos, converter);
                if (converter.hasVillager()) {
                    class_1799 vil = converter.extractVillager();
                    if (!vil.method_7960()) {
                        class_2248.method_9577(world, pos, vil);
                    }
                }
            }
            super.method_9536(state, world, pos, newState, moved);
        }
    }
}
