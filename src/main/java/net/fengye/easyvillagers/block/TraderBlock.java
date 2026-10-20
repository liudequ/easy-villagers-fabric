package net.fengye.easyvillagers.block;

import net.fengye.easyvillagers.block.entity.TraderBlockEntity;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.util.VillagerWorkstationHelper;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1646;
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

public class TraderBlock extends class_2237 {
    public static final class_2753 FACING = class_2741.field_12481;

    public TraderBlock(class_2248.class_2251 settings) {
        super(settings);
        this.method_9590(this.method_9564().method_11657(FACING, class_2350.field_11043));
    }

    @Override
    protected void method_9515(class_2689.class_2690<class_2248, class_2680> builder) { // appendProperties
        builder.method_11667(FACING);
    }

    @Override
    public class_2680 method_9605(class_1750 ctx) { // getPlacementState
        return this.method_9564().method_11657(FACING, ctx.method_8042().method_10153());
    }

    @Override
    public class_2464 method_9604(class_2680 state) { // getRenderType
        return class_2464.field_11458; // MODEL
    }

    @Override
    public class_2586 method_10123(class_2338 pos, class_2680 state) { // createBlockEntity
        return new TraderBlockEntity(pos, state);
    }

    @Override
    public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) { // getTicker
        return world.field_9236 ? null : method_31618(type, ModBlocks.TRADER_BLOCK_ENTITY, TraderBlockEntity::tick);
    }

    @Override
    public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) { // onUse
        if (world.field_9236) {
            return class_1269.field_5812; // SUCCESS
        }

        class_2586 be = world.method_8321(pos);
        if (!(be instanceof TraderBlockEntity trader)) {
            return class_1269.field_5811;
        }

        class_1799 mainStack = player.method_6047();

        // 1. 手持村民放入空的交易展柜
        if (!trader.hasVillager() && mainStack.method_7909() == ModItems.VILLAGER_ITEM) {
            trader.setVillagerFromItem(mainStack);
            if (!player.method_7337()) {
                mainStack.method_7934(1);
            }
            player.method_7353(class_2561.method_43470("§a已将村民放入交易展柜！"), true);
            return class_1269.field_5812;
        }

        // 2. 手持工作台放入展柜
        if (VillagerWorkstationHelper.isWorkstation(mainStack)) {
            if (!trader.hasWorkstation()) {
                trader.setWorkstation(mainStack);
                if (!player.method_7337()) {
                    mainStack.method_7934(1);
                }
                player.method_7353(class_2561.method_43470("§a已将工作台插入交易展柜！"), true);
                return class_1269.field_5812;
            } else {
                player.method_7353(class_2561.method_43470("§c展柜内已有工作台，潜行空手右键可取出！"), true);
                return class_1269.field_5812;
            }
        }

        // 3. 潜行空手取出：优先取出工作台，若无工作台则取出村民
        if (player.method_5715() && mainStack.method_7960()) {
            if (trader.hasWorkstation()) {
                class_1799 ws = trader.extractWorkstation();
                if (!player.method_31548().method_7394(ws)) { // 尝试放入背包，放不下则掉落地上
                    class_2248.method_9577(world, pos, ws);
                }
                player.method_7353(class_2561.method_43470("§e已取出工作台！村民已恢复无业（交易重置）。"), true);
                return class_1269.field_5812;
            } else if (trader.hasVillager()) {
                class_1799 vil = trader.extractVillagerItem();
                if (!player.method_31548().method_7394(vil)) {
                    class_2248.method_9577(world, pos, vil);
                }
                player.method_7353(class_2561.method_43470("§e已取出交易展柜中的村民！"), true);
                return class_1269.field_5812;
            }
        }

        // 4. 普通右键打开村民交易面板
        if (trader.hasVillager()) {
            class_1646 villager = trader.getOrCreateCachedVillager();
            if (villager != null) {
                villager.method_8259(player); // setCustomer
                villager.method_17449(player, villager.method_5477(), villager.method_7231().method_16925()); // sendOffers
                trader.markUpdated();
                return class_1269.field_5812;
            }
        }

        return class_1269.field_5811;
    }

    @Override
    public void method_9536(class_2680 state, class_1937 world, class_2338 pos, class_2680 newState, boolean moved) { // onStateReplaced
        if (!state.method_27852(newState.method_26204())) {
            class_2586 be = world.method_8321(pos);
            if (be instanceof TraderBlockEntity trader) {
                if (trader.hasVillager()) {
                    class_1799 stack = trader.extractVillagerItem();
                    class_2248.method_9577(world, pos, stack);
                }
                if (trader.hasWorkstation()) {
                    class_1799 ws = trader.extractWorkstation();
                    class_2248.method_9577(world, pos, ws);
                }
            }
            super.method_9536(state, world, pos, newState, moved);
        }
    }
}
