package net.fengye.easyvillagers.block.entity;

import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.item.VillagerItem;
import net.minecraft.class_1262;
import net.minecraft.class_1263;
import net.minecraft.class_1264;
import net.minecraft.class_1278;
import net.minecraft.class_1299;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2371;
import net.minecraft.class_2394;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class BreederBlockEntity extends class_2586 implements class_1278 {
    public static final int INVENTORY_SIZE = 5;
    public static final int FOOD_SLOTS_START = 0;
    public static final int FOOD_SLOTS_END = 3;   // 0~3 为食物输入槽
    public static final int OUTPUT_SLOT = 4;        // 4 为幼年村民产出槽

    private static final int[] SLOTS_UP_SIDES = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_DOWN = new int[]{4};

    public static final int BREED_TIME_TOTAL = 400; // 20秒繁殖一次

    private class_2487 villagerTag1 = null;
    private class_2487 villagerTag2 = null;
    private class_1646 cachedVillager1 = null;
    private class_1646 cachedVillager2 = null;

    private final class_2371<class_1799> items = class_2371.method_10213(INVENTORY_SIZE, class_1799.field_8037);
    private int breedProgress = 0;

    public BreederBlockEntity(class_2338 pos, class_2680 state) {
        super(ModBlocks.BREEDER_BLOCK_ENTITY, pos, state);
    }

    public boolean hasVillager1() {
        return this.villagerTag1 != null;
    }

    public boolean hasVillager2() {
        return this.villagerTag2 != null;
    }

    public boolean hasTwoVillagers() {
        return hasVillager1() && hasVillager2();
    }

    public int getVillagerCount() {
        int count = 0;
        if (hasVillager1()) count++;
        if (hasVillager2()) count++;
        return count;
    }

    public boolean addVillager(class_1799 stack) {
        if (!hasVillager1()) {
            this.villagerTag1 = stack.method_7969() != null ? stack.method_7969().method_10553() : new class_2487();
            this.cachedVillager1 = null;
            markUpdated();
            return true;
        } else if (!hasVillager2()) {
            this.villagerTag2 = stack.method_7969() != null ? stack.method_7969().method_10553() : new class_2487();
            this.cachedVillager2 = null;
            markUpdated();
            return true;
        }
        return false;
    }

    public class_1799 extractVillager() {
        if (hasVillager2()) {
            if (this.cachedVillager2 != null && this.villagerTag2 != null) {
                class_2487 entityTag = new class_2487();
                this.cachedVillager2.method_5647(entityTag);
                this.villagerTag2.method_10566(VillagerItem.ENTITY_TAG, entityTag);
            }
            class_1799 stack = new class_1799(ModItems.VILLAGER_ITEM);
            stack.method_7980(this.villagerTag2.method_10553());
            this.villagerTag2 = null;
            this.cachedVillager2 = null;
            markUpdated();
            return stack;
        } else if (hasVillager1()) {
            if (this.cachedVillager1 != null && this.villagerTag1 != null) {
                class_2487 entityTag = new class_2487();
                this.cachedVillager1.method_5647(entityTag);
                this.villagerTag1.method_10566(VillagerItem.ENTITY_TAG, entityTag);
            }
            class_1799 stack = new class_1799(ModItems.VILLAGER_ITEM);
            stack.method_7980(this.villagerTag1.method_10553());
            this.villagerTag1 = null;
            this.cachedVillager1 = null;
            markUpdated();
            return stack;
        }
        return class_1799.field_8037;
    }

    public class_1646 getOrCreateCachedVillager1() {
        if (!hasVillager1() || this.field_11863 == null) return null;
        if (this.cachedVillager1 == null) {
            this.cachedVillager1 = class_1299.field_6077.method_5883(this.field_11863);
            if (this.cachedVillager1 != null && this.villagerTag1.method_10545(VillagerItem.ENTITY_TAG)) {
                this.cachedVillager1.method_5651(this.villagerTag1.method_10562(VillagerItem.ENTITY_TAG));
            }
        }
        return this.cachedVillager1;
    }

    public class_1646 getOrCreateCachedVillager2() {
        if (!hasVillager2() || this.field_11863 == null) return null;
        if (this.cachedVillager2 == null) {
            this.cachedVillager2 = class_1299.field_6077.method_5883(this.field_11863);
            if (this.cachedVillager2 != null && this.villagerTag2.method_10545(VillagerItem.ENTITY_TAG)) {
                this.cachedVillager2.method_5651(this.villagerTag2.method_10562(VillagerItem.ENTITY_TAG));
            }
        }
        return this.cachedVillager2;
    }

    public static boolean isVillagerFood(class_1799 stack) {
        if (stack == null || stack.method_7960()) return false;
        class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
        if (id == null) return false;
        String path = id.method_12832(); // 必须使用 getPath() 即 method_12832
        return path.equals("bread") || path.equals("carrot") || path.equals("potato") || path.equals("beetroot");
    }

    public static int getFoodPoints(class_1799 stack) {
        if (stack == null || stack.method_7960()) return 0;
        class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
        if (id == null) return 0;
        String path = id.method_12832(); // 必须使用 getPath() 即 method_12832
        if (path.equals("bread")) return 3;
        if (path.equals("carrot") || path.equals("potato") || path.equals("beetroot")) return 1;
        return 0;
    }

    public int getTotalFoodPoints() {
        int total = 0;
        for (int i = FOOD_SLOTS_START; i <= FOOD_SLOTS_END; i++) {
            class_1799 stack = this.items.get(i);
            total += getFoodPoints(stack) * stack.method_7947();
        }
        return total;
    }

    public boolean insertFood(class_1799 stack) {
        if (!isVillagerFood(stack)) return false;
        for (int i = FOOD_SLOTS_START; i <= FOOD_SLOTS_END; i++) {
            class_1799 existing = this.items.get(i);
            if (existing.method_7960()) {
                this.items.set(i, stack.method_7972());
                stack.method_7934(stack.method_7947());
                markUpdated();
                return true;
            } else if (class_1799.method_31577(existing, stack) && existing.method_7947() < existing.method_7914()) {
                int space = existing.method_7914() - existing.method_7947();
                int toAdd = Math.min(space, stack.method_7947());
                existing.method_7933(toAdd); // increment 增量
                stack.method_7934(toAdd);   // decrement 扣除
                markUpdated();
                return true;
            }
        }
        return false;
    }

    private boolean consumeFood(int requiredPoints) {
        if (getTotalFoodPoints() < requiredPoints) return false;
        int remaining = requiredPoints;
        for (int i = FOOD_SLOTS_START; i <= FOOD_SLOTS_END && remaining > 0; i++) {
            class_1799 stack = this.items.get(i);
            if (!stack.method_7960()) {
                int pts = getFoodPoints(stack);
                if (pts > 0) {
                    while (!stack.method_7960() && remaining > 0) {
                        stack.method_7934(1);
                        remaining -= pts;
                    }
                }
            }
        }
        markUpdated();
        return true;
    }

    /**
     * 繁殖时钟逻辑
     */
    public static void tick(class_1937 world, class_2338 pos, class_2680 state, BreederBlockEntity entity) {
        if (world.field_9236) return;

        // 必须有 2 只村民
        if (!entity.hasTwoVillagers()) {
            if (entity.breedProgress > 0) {
                entity.breedProgress = 0;
                entity.markUpdated();
            }
            return;
        }

        // 输出槽必须为空才能产出幼年村民
        if (!entity.items.get(OUTPUT_SLOT).method_7960()) {
            return;
        }

        // 食物储备检查（需要至少 6 点食物值，例如 2 个面包）
        if (entity.getTotalFoodPoints() < 6) {
            return;
        }

        entity.breedProgress++;
        if (entity.breedProgress >= BREED_TIME_TOTAL) {
            entity.breedProgress = 0;
            // 消耗食物并产出幼体
            if (entity.consumeFood(6)) {
                class_1799 baby = VillagerItem.createBabyVillager(ModItems.VILLAGER_ITEM);
                entity.items.set(OUTPUT_SLOT, baby);
                entity.markUpdated();
            }
        }
    }

    public int getBreedProgressPercent() {
        return (int) ((this.breedProgress / (float) BREED_TIME_TOTAL) * 100);
    }

    public void markUpdated() {
        super.method_5431();
        if (this.field_11863 != null && !this.field_11863.field_9236) {
            this.field_11863.method_8413(this.field_11867, this.method_11010(), this.method_11010(), 3);
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
            stack.method_7939(method_5444());
        }
        method_5431();
    }

    @Override
    public boolean method_5443(class_1657 player) {
        return class_1263.method_49105(this, player);
    }

    @Override
    public boolean method_5437(int slot, class_1799 stack) {
        return slot >= FOOD_SLOTS_START && slot <= FOOD_SLOTS_END && isVillagerFood(stack);
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
        if (side == class_2350.field_11033) { // 底部漏斗抽取幼年村民
            return SLOTS_DOWN;
        }
        // 顶部或侧面漏入食物
        return SLOTS_UP_SIDES;
    }

    @Override
    public boolean method_5492(int slot, class_1799 stack, class_2350 side) {
        return side != class_2350.field_11033 && slot >= FOOD_SLOTS_START && slot <= FOOD_SLOTS_END && isVillagerFood(stack);
    }

    @Override
    public boolean method_5493(int slot, class_1799 stack, class_2350 side) {
        return side == class_2350.field_11033 && slot == OUTPUT_SLOT;
    }

    // --- NBT 持久化 ---

    @Override
    public void method_11007(class_2487 nbt) {
        super.method_11007(nbt);
        if (this.villagerTag1 != null) {
            nbt.method_10566("Villager1", this.villagerTag1);
        }
        if (this.villagerTag2 != null) {
            nbt.method_10566("Villager2", this.villagerTag2);
        }
        class_1262.method_5426(nbt, this.items);
        nbt.method_10569("BreedProgress", this.breedProgress);
    }

    @Override
    public void method_11014(class_2487 nbt) {
        super.method_11014(nbt);
        this.villagerTag1 = nbt.method_10545("Villager1") ? nbt.method_10562("Villager1") : null;
        this.villagerTag2 = nbt.method_10545("Villager2") ? nbt.method_10562("Villager2") : null;
        this.items.clear();
        class_1262.method_5429(nbt, this.items);
        this.breedProgress = nbt.method_10550("BreedProgress");
        this.cachedVillager1 = null;
        this.cachedVillager2 = null;
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
