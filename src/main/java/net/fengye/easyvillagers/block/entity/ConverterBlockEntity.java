package net.fengye.easyvillagers.block.entity;

import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.item.VillagerItem;
import net.minecraft.class_1262;
import net.minecraft.class_1263;
import net.minecraft.class_1278;
import net.minecraft.class_1299;
import net.minecraft.class_1528;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1914;
import net.minecraft.class_1916;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2371;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

import java.util.UUID;

public class ConverterBlockEntity extends class_2586 implements class_1278 {
    public static final int INVENTORY_SIZE = 3;
    public static final int SLOT_GOLDEN_APPLE = 0; // 金苹果输入槽
    public static final int SLOT_POTION = 1;       // 虚弱药水输入槽
    public static final int SLOT_OUTPUT = 2;       // 治愈产出槽

    private static final int[] SLOTS_UP_SIDES = new int[]{0, 1};
    private static final int[] SLOTS_DOWN = new int[]{2};

    public static final int INFECT_TIME = 60;   // 3秒感染
    public static final int CURE_TIME = 300;    // 15秒治愈

    public enum State {
        IDLE,
        INFECTING,
        CURING
    }

    private class_2487 villagerTag = null;
    private final class_2371<class_1799> items = class_2371.method_10213(INVENTORY_SIZE, class_1799.field_8037);

    private State currentState = State.IDLE;
    private int timer = 0;

    private class_1646 cachedVillager = null;
    private class_1528 cachedZombieVillager = null;

    public ConverterBlockEntity(class_2338 pos, class_2680 state) {
        super(ModBlocks.CONVERTER_BLOCK_ENTITY, pos, state);
    }

    public boolean hasVillager() {
        return this.villagerTag != null;
    }

    public State getCurrentState() {
        return this.currentState;
    }

    public int getTimer() {
        return this.timer;
    }

    public boolean addVillager(class_1799 stack) {
        if (!hasVillager()) {
            this.villagerTag = stack.method_7969() != null ? stack.method_7969().method_10553() : new class_2487();
            this.cachedVillager = null;
            this.cachedZombieVillager = null;
            this.currentState = State.IDLE;
            this.timer = 0;
            markUpdated();
            return true;
        }
        return false;
    }

    public class_1799 extractVillager() {
        if (hasVillager() && this.currentState == State.IDLE) {
            class_1799 stack = new class_1799(ModItems.VILLAGER_ITEM);
            stack.method_7980(this.villagerTag.method_10553());
            this.villagerTag = null;
            this.cachedVillager = null;
            this.cachedZombieVillager = null;
            this.timer = 0;
            markUpdated();
            return stack;
        }
        return class_1799.field_8037;
    }

    public static boolean isGoldenApple(class_1799 stack) {
        if (stack == null || stack.method_7960()) return false;
        class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
        return id != null && (id.method_12832().equals("golden_apple") || id.method_12832().equals("enchanted_golden_apple"));
    }

    public static boolean isWeaknessPotion(class_1799 stack) {
        if (stack == null || stack.method_7960()) return false;
        class_2487 tag = stack.method_7969();
        if (tag == null) return false;
        String potion = tag.method_10558("Potion");
        return potion.contains("weakness");
    }

    public boolean insertGoldenApple(class_1799 stack) {
        if (!isGoldenApple(stack)) return false;
        class_1799 slotStack = this.items.get(SLOT_GOLDEN_APPLE);
        if (slotStack.method_7960()) {
            this.items.set(SLOT_GOLDEN_APPLE, stack.method_7972());
            stack.method_7934(stack.method_7947());
            markUpdated();
            return true;
        } else if (class_1799.method_31577(slotStack, stack) && slotStack.method_7947() < slotStack.method_7914()) {
            int toAdd = Math.min(slotStack.method_7914() - slotStack.method_7947(), stack.method_7947());
            slotStack.method_7933(toAdd); // increment 增量
            stack.method_7934(toAdd);     // decrement 扣除
            markUpdated();
            return true;
        }
        return false;
    }

    public boolean insertWeaknessPotion(class_1799 stack) {
        if (!isWeaknessPotion(stack)) return false;
        class_1799 slotStack = this.items.get(SLOT_POTION);
        if (slotStack.method_7960()) {
            this.items.set(SLOT_POTION, stack.method_7972());
            stack.method_7934(stack.method_7947());
            markUpdated();
            return true;
        }
        return false;
    }

    public class_1646 getOrCreateCachedVillager() {
        if (!hasVillager() || this.field_11863 == null) return null;
        if (this.cachedVillager == null) {
            this.cachedVillager = class_1299.field_6077.method_5883(this.field_11863);
            if (this.cachedVillager != null && this.villagerTag.method_10545(VillagerItem.ENTITY_TAG)) {
                this.cachedVillager.method_5651(this.villagerTag.method_10562(VillagerItem.ENTITY_TAG));
            }
        }
        return this.cachedVillager;
    }

    public class_1528 getOrCreateCachedZombieVillager() {
        if (!hasVillager() || this.field_11863 == null) return null;
        if (this.cachedZombieVillager == null) {
            this.cachedZombieVillager = class_1299.field_6119.method_5883(this.field_11863);
        }
        return this.cachedZombieVillager;
    }

    /**
     * 治愈转化状态机 Ticker
     */
    public static void tick(class_1937 world, class_2338 pos, class_2680 state, ConverterBlockEntity entity) {
        if (world.field_9236) return;

        if (!entity.hasVillager()) {
            if (entity.currentState != State.IDLE) {
                entity.currentState = State.IDLE;
                entity.timer = 0;
                entity.markUpdated();
            }
            return;
        }

        switch (entity.currentState) {
            case IDLE:
                // 检查材料：需要 1 个金苹果和 1 瓶虚弱药水，并且输出槽为空
                if (!entity.items.get(SLOT_GOLDEN_APPLE).method_7960() &&
                    !entity.items.get(SLOT_POTION).method_7960() &&
                    entity.items.get(SLOT_OUTPUT).method_7960()) {
                    entity.currentState = State.INFECTING;
                    entity.timer = 0;
                    entity.markUpdated();
                }
                break;

            case INFECTING:
                entity.timer++;
                if (entity.timer >= INFECT_TIME) {
                    // 感染完成，进入治愈倒计时，并消耗药水和金苹果
                    entity.currentState = State.CURING;
                    entity.timer = 0;
                    entity.items.get(SLOT_GOLDEN_APPLE).method_7934(1);
                    entity.items.get(SLOT_POTION).method_7934(1);
                    entity.markUpdated();
                }
                break;

            case CURING:
                entity.timer++;
                if (entity.timer >= CURE_TIME) {
                    // 治愈完成！写入巨额骨折特惠折扣并产出
                    entity.applyCureDiscounts();
                    class_1799 curedVillager = new class_1799(ModItems.VILLAGER_ITEM);
                    curedVillager.method_7980(entity.villagerTag.method_10553());

                    // 移入输出槽
                    entity.items.set(SLOT_OUTPUT, curedVillager);
                    entity.villagerTag = null;
                    entity.cachedVillager = null;
                    entity.cachedZombieVillager = null;
                    entity.currentState = State.IDLE;
                    entity.timer = 0;
                    entity.markUpdated();
                }
                break;
        }
    }

    /**
     * 为村民注入治愈特惠折扣
     */
    private void applyCureDiscounts() {
        if (this.villagerTag == null) return;
        class_2487 entityData;
        if (this.villagerTag.method_10545(VillagerItem.ENTITY_TAG)) {
            entityData = this.villagerTag.method_10562(VillagerItem.ENTITY_TAG);
        } else {
            entityData = new class_2487();
            this.villagerTag.method_10566(VillagerItem.ENTITY_TAG, entityData);
        }

        // 标记已治愈特惠
        this.villagerTag.method_10582("DiscountTitle", "§6✨ 治愈骨折特惠 (1绿宝石)");

        // 深入 Offers 列表为每一个已有交易植入 specialPrice 减免
        if (entityData.method_10545("Offers")) {
            class_2487 offersTag = entityData.method_10562("Offers");
            if (offersTag.method_10545("Recipes")) {
                class_2499 recipesList = offersTag.method_10554("Recipes", 10);
                for (int i = 0; i < recipesList.size(); i++) {
                    class_2487 recipe = recipesList.method_10602(i);
                    // 给买入物品 1 赋予巨额负数 specialPrice，使其直接降为最低 1 个
                    if (recipe.method_10545("buy")) {
                        class_2487 buy = recipe.method_10562("buy");
                        int count = buy.method_10550("Count");
                        if (count > 1) {
                            recipe.method_10569("specialPrice", -(count - 1));
                        } else {
                            recipe.method_10569("specialPrice", -50);
                        }
                    }
                }
            }
        }
    }

    public int getProgressPercent() {
        if (this.currentState == State.INFECTING) {
            return (int) ((this.timer / (float) INFECT_TIME) * 100);
        } else if (this.currentState == State.CURING) {
            return (int) ((this.timer / (float) CURE_TIME) * 100);
        }
        return 0;
    }

    public void markUpdated() {
        super.method_5431();
        if (this.field_11863 != null && !this.field_11863.field_9236) {
            this.field_11863.method_8413(this.field_11867, this.method_11010(), this.method_11010(), 3);
        }
    }

    // --- SidedInventory ---

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
        method_5431();
    }

    @Override
    public boolean method_5443(class_1657 player) {
        return class_1263.method_49105(this, player);
    }

    @Override
    public boolean method_5437(int slot, class_1799 stack) {
        if (slot == SLOT_GOLDEN_APPLE) return isGoldenApple(stack);
        if (slot == SLOT_POTION) return isWeaknessPotion(stack);
        return false;
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
        if (side == class_2350.field_11033) {
            return SLOTS_DOWN; // 底部抽取治愈村民
        }
        return SLOTS_UP_SIDES; // 顶部或侧面输入金苹果和药水
    }

    @Override
    public boolean method_5492(int slot, class_1799 stack, class_2350 side) {
        if (side == class_2350.field_11033) return false;
        return method_5437(slot, stack);
    }

    @Override
    public boolean method_5493(int slot, class_1799 stack, class_2350 side) {
        return side == class_2350.field_11033 && slot == SLOT_OUTPUT;
    }

    // --- NBT ---

    @Override
    public void method_11007(class_2487 nbt) {
        super.method_11007(nbt);
        if (this.villagerTag != null) {
            nbt.method_10566("Villager", this.villagerTag);
        }
        class_1262.method_5426(nbt, this.items);
        nbt.method_10582("CurrentState", this.currentState.name());
        nbt.method_10569("Timer", this.timer);
    }

    @Override
    public void method_11014(class_2487 nbt) {
        super.method_11014(nbt);
        this.villagerTag = nbt.method_10545("Villager") ? nbt.method_10562("Villager") : null;
        this.items.clear();
        class_1262.method_5429(nbt, this.items);
        if (nbt.method_10545("CurrentState")) {
            try {
                this.currentState = State.valueOf(nbt.method_10558("CurrentState"));
            } catch (Exception ignored) {
                this.currentState = State.IDLE;
            }
        }
        this.timer = nbt.method_10550("Timer");
        this.cachedVillager = null;
        this.cachedZombieVillager = null;
    }

    @Override
    public class_2596<class_2602> method_38235() {
        return class_2622.method_38585(this);
    }

    @Override
    public class_2487 method_16887() {
        return method_38242();
    }
}
