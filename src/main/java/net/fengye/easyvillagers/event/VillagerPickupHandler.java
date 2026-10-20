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
        // 普通交互一律 PASS，保留原版命名、刷怪蛋、忙碌检查及其他模组事件。
        if (hand != class_1268.field_5808 || !(entity instanceof class_1646 villager)
                || !player.method_5715() || !player.method_6047().method_7960()
                || player.method_7325()) {
            return class_1269.field_5811;
        }
        // 交易中不允许收纳，防止窗口仍持有已移除的村民引用。
        if (villager.method_8257() != null) return class_1269.field_5811;
        if (world.field_9236) return class_1269.field_5812;

        class_1799 stack = VillagerItem.createFromVillager(villager, ModItems.VILLAGER_ITEM);
        villager.method_5650(class_1297.class_5529.field_26998);
        player.method_6122(class_1268.field_5808, stack);
        player.method_7353(class_2561.method_43470("§a✔ 已成功将村民收纳为随身物品！"), true);
        return class_1269.field_5812;
    }
}
