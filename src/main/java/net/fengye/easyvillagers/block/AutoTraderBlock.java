package net.fengye.easyvillagers.block;

import net.fengye.easyvillagers.block.entity.AutoTraderBlockEntity;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.util.VillagerWorkstationHelper;
import net.minecraft.class_1264;
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
import net.minecraft.class_2960;
import net.minecraft.class_3965;
import net.minecraft.class_5558;
import net.minecraft.class_7923;

public class AutoTraderBlock extends class_2237 {
    public static final class_2753 FACING = class_2741.field_12481;

    public AutoTraderBlock(class_2248.class_2251 settings) {
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
        return new AutoTraderBlockEntity(pos, state);
    }

    @Override
    public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) { // getTicker
        return world.field_9236 ? null : method_31618(type, ModBlocks.AUTO_TRADER_BLOCK_ENTITY, AutoTraderBlockEntity::autoTick);
    }

    private boolean isTradeSelectorItem(class_1799 stack) {
        if (stack == null || stack.method_7960()) return false;
        class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
        if (id == null) return false;
        String path = id.method_12832(); // 修正为 getPath()
        return path.equals("redstone") || path.equals("redstone_torch") || path.equals("stick");
    }

    @Override
    public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) { // onUse
        if (world.field_9236) {
            return class_1269.field_5812;
        }

        class_2586 be = world.method_8321(pos);
        if (!(be instanceof AutoTraderBlockEntity autoTrader)) {
            return class_1269.field_5814;
        }

        class_1799 mainStack = player.method_6047();

        // 1. 手持村民物品放入空的自动交易展柜
        if (!autoTrader.hasVillager() && mainStack.method_7909() == ModItems.VILLAGER_ITEM) {
            autoTrader.setVillagerFromItem(mainStack);
            if (!player.method_7337()) {
                mainStack.method_7934(1);
            }
            player.method_7353(class_2561.method_43470("§a已将村民放入全自动交易展柜！"), true);
            return class_1269.field_5812;
        }

        // 2. 手持工作台放入展柜
        if (VillagerWorkstationHelper.isWorkstation(mainStack)) {
            if (!autoTrader.hasWorkstation()) {
                autoTrader.setWorkstation(mainStack);
                if (!player.method_7337()) {
                    mainStack.method_7934(1);
                }
                player.method_7353(class_2561.method_43470("§a已将工作台插入全自动交易展柜！"), true);
                return class_1269.field_5812;
            } else {
                player.method_7353(class_2561.method_43470("§c展柜内已有工作台，潜行空手右键可取出！"), true);
                return class_1269.field_5812;
            }
        }

        // 3. 潜行空手取出：优先取出工作台，若无工作台则取出村民
        if (player.method_5715() && mainStack.method_7960()) {
            if (autoTrader.hasWorkstation()) {
                class_1799 ws = autoTrader.extractWorkstation();
                if (!player.method_31548().method_7394(ws)) {
                    class_2248.method_9577(world, pos, ws);
                }
                player.method_7353(class_2561.method_43470("§e已取出工作台！村民已恢复无业（交易重置）。"), true);
                return class_1269.field_5812;
            } else if (autoTrader.hasVillager()) {
                class_1799 vil = autoTrader.extractVillagerItem();
                if (!player.method_31548().method_7394(vil)) {
                    class_2248.method_9577(world, pos, vil);
                }
                player.method_7353(class_2561.method_43470("§e已取出全自动交易展柜中的村民！"), true);
                return class_1269.field_5812;
            }
        }

        // 4. 手持红石 / 红石火把 / 木棍：切换自动交易锁定目标
        if (isTradeSelectorItem(mainStack)) {
            autoTrader.cycleTrade(player, player.method_5715());
            return class_1269.field_5812;
        }

        // 5. 普通右键打开村民交易面板
        if (autoTrader.hasVillager()) {
            class_1646 villager = autoTrader.getOrCreateCachedVillager();
            if (villager != null) {
                villager.method_8259(player); // setCustomer
                villager.method_17449(player, villager.method_5477(), villager.method_7231().method_16925()); // sendOffers
                player.method_7353(class_2561.method_43470("§7[提示: 手持红石粉或木棍右键可切换自动交易锁定项]"), true);
                return class_1269.field_5812;
            }
        }

        return class_1269.field_5814;
    }

    @Override
    public void method_9536(class_2680 state, class_1937 world, class_2338 pos, class_2680 newState, boolean moved) { // onStateReplaced
        if (!state.method_27852(newState.method_26204())) {
            class_2586 be = world.method_8321(pos);
            if (be instanceof AutoTraderBlockEntity autoTrader) {
                // 1. 散落输入槽与输出槽的所有物品
                class_1264.method_5451(world, pos, autoTrader);
                // 2. 掉落村民物品
                if (autoTrader.hasVillager()) {
                    class_1799 stack = autoTrader.extractVillagerItem();
                    class_2248.method_9577(world, pos, stack);
                }
                // 3. 掉落工作台
                if (autoTrader.hasWorkstation()) {
                    class_1799 ws = autoTrader.extractWorkstation();
                    class_2248.method_9577(world, pos, ws);
                }
            }
            super.method_9536(state, world, pos, newState, moved);
        }
    }
}
