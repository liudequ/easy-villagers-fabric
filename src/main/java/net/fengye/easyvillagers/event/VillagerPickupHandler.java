package net.fengye.easyvillagers.event;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.item.VillagerItem;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_3966;

public class VillagerPickupHandler {

    public static void register() {
        UseEntityCallback.EVENT.register(VillagerPickupHandler::onUseEntity);
    }

    private static class_1269 onUseEntity(class_1657 player, class_1937 world, class_1268 hand, class_1297 entity, class_3966 hitResult) {
        // 仅处理主手交互，避免副手触发双重事件
        if (hand != class_1268.field_5808) {
            return class_1269.field_5811; // PASS (放行原版处理，切勿返回 field_5814 即 FAIL，否则会导致 Fabric 拦截并 Cancel 原版右键交易)
        }

        // 仅在目标为村民时处理
        if (!(entity instanceof class_1646 villager)) {
            return class_1269.field_5811; // PASS
        }

        // 玩家必须潜行（Shift）
        if (!player.method_5715()) {
            return class_1269.field_5811; // PASS
        }

        // 玩家主手必须为空手
        class_1799 mainStack = player.method_6047();
        if (!mainStack.method_7960()) {
            return class_1269.field_5811; // PASS
        }

        // 客户端直接返回 SUCCESS 阻止原版交易界面弹出
        if (world.field_9236) {
            return class_1269.field_5812; // SUCCESS
        }

        // 服务端执行打包抓取
        class_1799 villagerStack = VillagerItem.createFromVillager(villager, ModItems.VILLAGER_ITEM);

        // 移除世界中的实体
        villager.method_5650(class_1297.class_5529.field_26998); // DISCARDED

        // 将村民物品直接放入玩家主手
        player.method_6122(class_1268.field_5808, villagerStack);

        // 提示玩家
        player.method_7353(class_2561.method_43470("§a✔ 已成功将村民收纳为随身物品！"), true);

        return class_1269.field_5812; // SUCCESS
    }
}
