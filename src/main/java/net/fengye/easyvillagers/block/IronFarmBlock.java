package net.fengye.easyvillagers.block;

import net.fengye.easyvillagers.block.entity.IronFarmBlockEntity;
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

public class IronFarmBlock extends class_2237 {
    public static final class_2753 FACING = class_2741.field_12481;

    public IronFarmBlock(class_2248.class_2251 settings) {
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
        return new IronFarmBlockEntity(pos, state);
    }

    @Override
    public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) {
        return world.field_9236 ? null : method_31618(type, ModBlocks.IRON_FARM_BLOCK_ENTITY, IronFarmBlockEntity::tick);
    }

    @Override
    public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) {
        if (world.field_9236) return class_1269.field_5812;

        class_2586 be = world.method_8321(pos);
        if (!(be instanceof IronFarmBlockEntity ironFarm)) return class_1269.field_5811;

        class_1799 held = player.method_5998(hand);

        // 1. 潜行空手取出村民
        if (player.method_5715() && held.method_7960()) {
            if (ironFarm.hasVillager()) {
                class_1799 extracted = ironFarm.extractVillager();
                if (!player.method_31548().method_7394(extracted)) {
                    class_2248.method_9577(world, pos, extracted);
                }
                player.method_7353(class_2561.method_43470("§e[刷铁机] §a村民已安全取出"), true);
                return class_1269.field_5812;
            }
        }

        // 2. 手持村民放入
        if (held.method_7909() == ModItems.VILLAGER_ITEM) {
            if (VillagerItem.isBaby(held)) {
                player.method_7353(class_2561.method_43470("§c⚠️ 幼年村民无法激活刷铁机！"), true);
                return class_1269.field_5812;
            }
            if (!ironFarm.hasVillager()) {
                class_1799 insertStack = held.method_7972();
                insertStack.method_7939(1);
                ironFarm.addVillager(insertStack);
                if (!player.method_7337()) {
                    held.method_7934(1);
                }
                player.method_7353(class_2561.method_43470("§e[刷铁机] §a村民已安置，铁傀儡处决时钟启动！"), true);
                return class_1269.field_5812;
            }
        }

        // 3. 收集内部已有产物
        boolean collectedAny = false;
        for (int i = 0; i < IronFarmBlockEntity.INVENTORY_SIZE; i++) {
            class_1799 stack = ironFarm.method_5438(i);
            if (!stack.method_7960()) {
                class_1799 extracted = ironFarm.method_5441(i);
                if (!player.method_31548().method_7394(extracted)) {
                    class_2248.method_9577(world, pos, extracted);
                }
                collectedAny = true;
            }
        }
        if (collectedAny) {
            player.method_7353(class_2561.method_43470("§e[刷铁机] §a已收取仓储中的铁锭与虞美人！"), true);
            return class_1269.field_5812;
        }

        // 4. 显示当前运行状态
        if (ironFarm.hasVillager()) {
            int progressPct = (ironFarm.getProgress() * 100) / IronFarmBlockEntity.MAX_PROGRESS;
            player.method_7353(class_2561.method_43470("§e[刷铁机] §a运行中 §7| 生产进度: §b" + progressPct + "% §7(漏斗可从底部抽取)"), true);
        } else {
            player.method_7353(class_2561.method_43470("§e[刷铁机] §c空置中 §7- 手持村民右键即可安置！"), true);
        }

        return class_1269.field_5812;
    }

    @Override
    public void method_9536(class_2680 state, class_1937 world, class_2338 pos, class_2680 newState, boolean moved) {
        if (!state.method_27852(newState.method_26204())) {
            class_2586 be = world.method_8321(pos);
            if (be instanceof IronFarmBlockEntity ironFarm) {
                class_1264.method_5451(world, pos, ironFarm);
                if (ironFarm.hasVillager()) {
                    class_1799 villager = ironFarm.extractVillager();
                    if (!villager.method_7960()) {
                        class_2248.method_9577(world, pos, villager);
                    }
                }
            }
            super.method_9536(state, world, pos, newState, moved);
        }
    }
}
