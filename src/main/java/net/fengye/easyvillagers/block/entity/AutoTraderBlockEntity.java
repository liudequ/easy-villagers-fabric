package net.fengye.easyvillagers.block.entity;

import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.item.VillagerItem;
import net.fengye.easyvillagers.util.VillagerWorkstationHelper;
import net.fengye.easyvillagers.util.VillagerTradePreviewHelper;
import net.fengye.easyvillagers.util.VillagerPreviewAccess;
import net.minecraft.class_1262;
import net.minecraft.class_1263;
import net.minecraft.class_1278;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1914;
import net.minecraft.class_1916;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2371;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_3419;
import net.minecraft.class_3443;
import net.minecraft.class_3532;

import java.lang.reflect.Method;

public class AutoTraderBlockEntity extends TraderBlockEntity implements class_1278 {
    public static final int INVENTORY_SIZE = 8;
    public static final int INPUT_SLOTS_START = 0;
    public static final int INPUT_SLOTS_END = 3;   // 0, 1, 2, 3 为输入槽
    public static final int OUTPUT_SLOTS_START = 4;
    public static final int OUTPUT_SLOTS_END = 7;  // 4, 5, 6, 7 为输出槽

    private static final int[] SLOTS_UP_SIDES = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_DOWN = new int[]{4, 5, 6, 7};

    private static final int DEFAULT_TRADE_COOLDOWN = 100; // 5秒执行一次自动交易
    private static final int RESTOCK_INTERVAL = 1200;      // 1分钟补货一次

    private final class_2371<class_1799> items = class_2371.method_10213(INVENTORY_SIZE, class_1799.field_8037);
    private int selectedTradeIndex = 0;
    private int tradeCooldown = DEFAULT_TRADE_COOLDOWN;

    public AutoTraderBlockEntity(class_2338 pos, class_2680 state) {
        super(ModBlocks.AUTO_TRADER_BLOCK_ENTITY, pos, state);
    }

    public int getSelectedTradeIndex() {
        return this.selectedTradeIndex;
    }

    public void setSelectedTradeIndex(int index) {
        this.selectedTradeIndex = Math.max(0, index);
        markUpdated();
    }

    /**
     * 切换下一个/上一个交易条目，并向玩家发送操作栏反馈
     */
    public void cycleTrade(class_1657 player, boolean backwards) {
        class_1916 trades = getVillagerTrades();
        if (trades == null || trades.isEmpty()) {
            player.method_7353(class_2561.method_30163("§c⚠️ 当前村民暂无可用交易条目"), true);
            return;
        }

        int total = trades.size();
        if (backwards) {
            this.selectedTradeIndex = (this.selectedTradeIndex - 1 + total) % total;
        } else {
            this.selectedTradeIndex = (this.selectedTradeIndex + 1) % total;
        }
        markUpdated();

        class_1914 offer = trades.get(this.selectedTradeIndex);
        class_1799 buy1 = offer.method_19272(); // getAdjustedFirstBuyItem() 采用打折后的实际价格
        class_1799 buy2 = offer.method_8247();
        class_1799 sell = offer.method_8250();

        StringBuilder sb = new StringBuilder("§6⚡ 自动交易锁定: §f");
        sb.append(buy1.method_7947()).append("x ").append(buy1.method_7964().getString());
        if (!buy2.method_7960()) {
            sb.append(" + ").append(buy2.method_7947()).append("x ").append(buy2.method_7964().getString());
        }
        sb.append(" §e➔ §a").append(sell.method_7947()).append("x ").append(sell.method_7964().getString());
        sb.append(" §7(").append(this.selectedTradeIndex + 1).append("/").append(total).append(")");

        player.method_7353(class_2561.method_30163(sb.toString()), true);
    }

    /**
     * 服务端周期时钟：自动补货 + 自动执行交易
     */
    public static void autoTick(class_1937 world, class_2338 pos, class_2680 state, AutoTraderBlockEntity entity) {
        if (world.field_9236) { // 仅在服务端运行
            return;
        }

        if (!entity.hasVillager()) {
            return;
        }

        if (VillagerTradePreviewHelper.advanceStoredVillager(entity.getOrCreateCachedVillager())) {
            entity.updateVillagerTagFromCached();
            entity.markUpdated();
        }
        // 玩家手动交易期间暂停自动化，避免同一交易并发消耗或补货。
        if (entity.getOrCreateCachedVillager() != null && entity.getOrCreateCachedVillager().method_8257() != null) return;

        // 1. 自动补货检查
        long currentTime = world.method_8510();
        if (currentTime - entity.lastRestockTime >= RESTOCK_INTERVAL) {
            class_1646 villager = entity.getOrCreateCachedVillager();
            if (villager != null) {
                VillagerWorkstationHelper.restock(villager);
                if (villager instanceof VillagerPreviewAccess access && access.easyVillagers$isCured()) {
                    VillagerTradePreviewHelper.applyCureDiscounts(villager);
                }
                entity.lastRestockTime = currentTime;
                String profId = VillagerWorkstationHelper.getProfessionId(entity.workstation);
                if (profId != null) {
                    entity.syncVillagerNbtFromEntity(villager, profId);
                }
                entity.markUpdated();
            }
        }

        // 2. 自动交易倒计时
        if (entity.tradeCooldown > 0) {
            entity.tradeCooldown--;
            return;
        }

        // 重置倒计时，尝试执行一次交易
        entity.tradeCooldown = DEFAULT_TRADE_COOLDOWN;
        entity.tryExecuteTrade(world, pos);
    }

    /**
     * 核心：检查原料并自动完成交易
     */
    private boolean tryExecuteTrade(class_1937 world, class_2338 pos) {
        class_1916 trades = getVillagerTrades();
        if (trades == null || trades.isEmpty()) {
            return false;
        }

        if (this.selectedTradeIndex >= trades.size()) {
            this.selectedTradeIndex = 0;
        }

        class_1914 offer = trades.get(this.selectedTradeIndex);
        if (offer.method_8255()) { // 已售罄，等待补货
            return false;
        }

        class_1799 buy1 = offer.method_19272(); // getAdjustedFirstBuyItem() 扣除打折后的实际原料
        class_1799 buy2 = offer.method_8247();
        class_1799 sell = offer.method_8250();

        // 检查输入槽是否有足够的原料
        if (!hasEnoughItems(buy1)) {
            return false;
        }
        if (!buy2.method_7960() && !hasEnoughItems(buy2)) {
            return false;
        }

        // 检查输出槽是否有空间容纳产物
        if (!canFitOutput(sell)) {
            return false;
        }

        // 扣除输入原料
        consumeItems(buy1);
        if (!buy2.method_7960()) {
            consumeItems(buy2);
        }

        // 存入产出物品
        insertOutput(sell.method_7972()); // copy

        // 记录交易次数与经验
        offer.method_8244(); // use()
        class_1646 villager = getOrCreateCachedVillager();
        if (villager != null) {
            try {
                Method afterUsing = class_1646.class.getDeclaredMethod("method_18008", class_1914.class);
                afterUsing.setAccessible(true);
                afterUsing.invoke(villager, offer);
            } catch (Throwable ignored) {
            }
            String profId = VillagerWorkstationHelper.getProfessionId(this.workstation);
            if (profId != null) {
                syncVillagerNbtFromEntity(villager, profId);
            }
        }

        // 播放效果与状态保存
        markUpdated();
        return true;
    }

    private boolean matchesRequirement(class_1799 stack, class_1799 requirement) {
        if (stack.method_7960()) return false;
        if (stack.method_7909() != requirement.method_7909()) return false;
        if (requirement.method_7942()) {
            return class_1799.method_31577(stack, requirement);
        }
        return true;
    }

    private boolean hasEnoughItems(class_1799 requirement) {
        int needed = requirement.method_7947();
        int count = 0;
        for (int i = INPUT_SLOTS_START; i <= INPUT_SLOTS_END; i++) {
            class_1799 stack = this.items.get(i);
            if (matchesRequirement(stack, requirement)) {
                count += stack.method_7947();
                if (count >= needed) {
                    return true;
                }
            }
        }
        return false;
    }

    private void consumeItems(class_1799 requirement) {
        int remaining = requirement.method_7947();
        for (int i = INPUT_SLOTS_START; i <= INPUT_SLOTS_END && remaining > 0; i++) {
            class_1799 stack = this.items.get(i);
            if (matchesRequirement(stack, requirement)) {
                int toTake = Math.min(remaining, stack.method_7947());
                stack.method_7934(toTake); // decrement
                remaining -= toTake;
            }
        }
    }

    private boolean canFitOutput(class_1799 product) {
        int remaining = product.method_7947();
        for (int i = OUTPUT_SLOTS_START; i <= OUTPUT_SLOTS_END; i++) {
            class_1799 stack = this.items.get(i);
            if (stack.method_7960()) {
                return true;
            }
            if (class_1799.method_31577(stack, product)) {
                int space = stack.method_7914() - stack.method_7947();
                remaining -= space;
                if (remaining <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void insertOutput(class_1799 product) {
        // 先堆叠到已有同类物品槽位
        for (int i = OUTPUT_SLOTS_START; i <= OUTPUT_SLOTS_END && !product.method_7960(); i++) {
            class_1799 stack = this.items.get(i);
            if (!stack.method_7960() && class_1799.method_31577(stack, product)) {
                int space = stack.method_7914() - stack.method_7947();
                int toAdd = Math.min(space, product.method_7947());
                stack.method_7939(toAdd); // increment
                product.method_7934(toAdd);
            }
        }
        // 若还有剩余，放入空槽
        for (int i = OUTPUT_SLOTS_START; i <= OUTPUT_SLOTS_END && !product.method_7960(); i++) {
            class_1799 stack = this.items.get(i);
            if (stack.method_7960()) {
                this.items.set(i, product.method_7972());
                product.method_7934(product.method_7947());
                break;
            }
        }
    }

    // --- SidedInventory 实现 ---

    @Override
    public int method_5439() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean method_5442() {
        for (class_1799 stack : this.items) {
            if (!stack.method_7960()) return false;
        }
        return true;
    }

    @Override
    public class_1799 method_5438(int slot) {
        return this.items.get(slot);
    }

    @Override
    public class_1799 method_5434(int slot, int amount) {
        class_1799 result = class_1262.method_5430(this.items, slot, amount);
        if (!result.method_7960()) {
            method_5431();
        }
        return result;
    }

    @Override
    public class_1799 method_5441(int slot) {
        return class_1262.method_5428(this.items, slot);
    }

    @Override
    public void method_5447(int slot, class_1799 stack) {
        this.items.set(slot, stack);
        if (stack.method_7947() > method_5444()) {
            stack.method_7939(method_5444() - stack.method_7947());
        }
        method_5431();
    }

    @Override
    public boolean method_5443(class_1657 player) {
        return class_1263.method_49105(this, player);
    }

    @Override
    public boolean method_5437(int slot, class_1799 stack) {
        // 只有输入槽允许放入原料
        return slot >= INPUT_SLOTS_START && slot <= INPUT_SLOTS_END;
    }

    @Override
    public void method_5431() {
        markUpdated();
    }

    @Override
    public void method_5448() {
        this.items.clear();
        method_5431();
    }

    @Override
    public int[] method_5494(class_2350 side) {
        if (side == class_2350.field_11033) { // DOWN 底部漏斗抽出产物
            return SLOTS_DOWN;
        }
        // UP 顶部或四周侧面漏入原料
        return SLOTS_UP_SIDES;
    }

    @Override
    public boolean method_5492(int slot, class_1799 stack, class_2350 side) {
        // 漏斗输入：仅允许输入到 0-3 槽位，且不允许从底部输入
        return side != class_2350.field_11033 && slot >= INPUT_SLOTS_START && slot <= INPUT_SLOTS_END;
    }

    @Override
    public boolean method_5493(int slot, class_1799 stack, class_2350 side) {
        // 漏斗抽取：仅允许从底部抽取 4-7 输出槽位
        return side == class_2350.field_11033 && slot >= OUTPUT_SLOTS_START && slot <= OUTPUT_SLOTS_END;
    }

    // --- NBT 持久化 ---

    @Override
    public void method_11007(class_2487 nbt) {
        super.method_11007(nbt);
        class_1262.method_5426(nbt, this.items);
        nbt.method_10569("SelectedTradeIndex", this.selectedTradeIndex);
        nbt.method_10569("TradeCooldown", this.tradeCooldown);
    }

    @Override
    public void method_11014(class_2487 nbt) {
        super.method_11014(nbt);
        this.items.clear();
        class_1262.method_5429(nbt, this.items);
        this.selectedTradeIndex = nbt.method_10550("SelectedTradeIndex");
        this.tradeCooldown = nbt.method_10550("TradeCooldown");
    }
}
