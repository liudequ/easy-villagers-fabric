package net.fengye.easyvillagers.block;

import net.fengye.easyvillagers.block.entity.TraderBlockEntity;
import net.fengye.easyvillagers.item.ModItems;
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
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2741;
import net.minecraft.class_2753;
import net.minecraft.class_3965;

public class TraderBlock extends class_2237 {
    public static final class_2753 FACING = class_2741.field_12545;

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
    public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) { // onUse
        if (world.field_9236) {
            return class_1269.field_5812; // SUCCESS
        }

        class_2586 be = world.method_8321(pos);
        if (!(be instanceof TraderBlockEntity trader)) {
            return class_1269.field_5814;
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

        // 2. 潜行空手从交易展柜取出村民
        if (trader.hasVillager() && player.method_5715() && mainStack.method_7960()) {
            class_1799 extracted = trader.extractVillagerItem();
            player.method_6122(hand, extracted);
            player.method_7353(class_2561.method_43470("§e已取出交易展柜中的村民！"), true);
            return class_1269.field_5812;
        }

        // 3. 普通右键打开村民交易面板
        if (trader.hasVillager()) {
            class_1646 villager = trader.getOrCreateCachedVillager();
            if (villager != null) {
                villager.method_8259(player); // setCustomer
                villager.method_17449(player, villager.method_5477(), villager.method_7231().method_16925()); // sendOffers
                return class_1269.field_5812;
            }
        }

        return class_1269.field_5814;
    }

    @Override
    public void method_9536(class_2680 state, class_1937 world, class_2338 pos, class_2680 newState, boolean moved) { // onStateReplaced
        if (!state.method_27852(newState.method_26204())) {
            class_2586 be = world.method_8321(pos);
            if (be instanceof TraderBlockEntity trader && trader.hasVillager()) {
                class_1799 stack = trader.extractVillagerItem();
                class_2248.method_9577(world, pos, stack);
            }
            super.method_9536(state, world, pos, newState, moved);
        }
    }
}
