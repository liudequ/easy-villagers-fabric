package net.fengye.easyvillagers.block.entity;

import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.item.VillagerItem;
import net.minecraft.class_1262;
import net.minecraft.class_1263;
import net.minecraft.class_1278;
import net.minecraft.class_1299;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2371;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;

public class IncubatorBlockEntity extends class_2586 implements class_1278 {
    public static final int INVENTORY_SIZE = 2;
    public static final int INPUT_SLOT = 0;   // 幼年村民输入槽
    public static final int OUTPUT_SLOT = 1;  // 成年村民输出槽

    private static final int[] SLOTS_UP_SIDES = new int[]{INPUT_SLOT};
    private static final int[] SLOTS_DOWN = new int[]{OUTPUT_SLOT};

    public static final int MATURATION_TIME_TOTAL = 600; // 30秒极速催熟

    private final class_2371<class_1799> items = class_2371.method_10213(INVENTORY_SIZE, class_1799.field_8037);
    private int growthProgress = 0;
    private class_1646 cachedBabyVillager = null;

    public IncubatorBlockEntity(class_2338 pos, class_2680 state) {
        super(ModBlocks.INCUBATOR_BLOCK_ENTITY, pos, state);
    }

    public boolean hasBaby() {
        class_1799 input = this.items.get(INPUT_SLOT);
        return !input.method_7960() && VillagerItem.isBaby(input);
    }

    public class_1646 getOrCreateCachedBaby() {
        if (!hasBaby() || this.field_11863 == null) return null;
        if (this.cachedBabyVillager == null) {
            this.cachedBabyVillager = class_1299.field_6077.method_5883(this.field_11863);
            if (this.cachedBabyVillager != null) {
                class_1799 input = this.items.get(INPUT_SLOT);
                class_2487 nbt = input.method_7969();
                if (nbt != null && nbt.method_10545(VillagerItem.ENTITY_TAG)) {
                    this.cachedBabyVillager.method_5651(nbt.method_10562(VillagerItem.ENTITY_TAG));
                }
                this.cachedBabyVillager.method_5614(-24000); // 确保呈现幼年外观
            }
        }
        return this.cachedBabyVillager;
    }

    /**
     * 孵化催熟时钟
     */
    public static void tick(class_1937 world, class_2338 pos, class_2680 state, IncubatorBlockEntity entity) {
        if (world.field_9236) return;

        // 检查输入槽是否有幼体
        if (!entity.hasBaby()) {
            if (entity.growthProgress > 0) {
                entity.growthProgress = 0;
                entity.markUpdated();
            }
            return;
        }

        // 输出槽已有物品，暂停催熟，等待抽取
        if (!entity.items.get(OUTPUT_SLOT).method_7960()) {
            return;
        }

        entity.growthProgress++;
        if (entity.growthProgress >= MATURATION_TIME_TOTAL) {
            entity.growthProgress = 0;
            // 催熟完毕：将幼年村民转化为成年无业村民
            class_1799 babyStack = entity.items.get(INPUT_SLOT);
            class_1799 adultStack = new class_1799(ModItems.VILLAGER_ITEM);
            class_2487 tag = babyStack.method_7969() != null ? babyStack.method_7969().method_10553() : new class_2487();
            // 移除幼年标记与负年龄
            tag.method_10551(VillagerItem.BABY_KEY);
            tag.method_10582(VillagerItem.PROFESSION_KEY, "无业游民");
            tag.method_10582(VillagerItem.LEVEL_KEY, "等级 1");
            if (tag.method_10545(VillagerItem.ENTITY_TAG)) {
                class_2487 entityTag = tag.method_10562(VillagerItem.ENTITY_TAG);
                entityTag.method_10569("Age", 0);
            }
            adultStack.method_7980(tag);

            // 存入输出槽并消耗输入
            entity.items.set(OUTPUT_SLOT, adultStack);
            babyStack.method_7934(1);
            entity.cachedBabyVillager = null;
            entity.markUpdated();
        }
    }

    public int getGrowthProgressPercent() {
        return (int) ((this.growthProgress / (float) MATURATION_TIME_TOTAL) * 100);
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
            this.cachedBabyVillager = null;
            method_5431();
        }
        return result;
    }

    @Override
    public class_1799 method_5441(int slot) {
        this.cachedBabyVillager = null;
        return class_1262.method_5428(this.items, slot);
    }

    @Override
    public void method_5447(int slot, class_1799 stack) {
        this.items.set(slot, stack);
        if (slot == INPUT_SLOT) {
            this.cachedBabyVillager = null;
        }
        method_5431();
    }

    @Override
    public boolean method_5443(class_1657 player) {
        return class_1263.method_49105(this, player);
    }

    @Override
    public boolean method_5437(int slot, class_1799 stack) {
        return slot == INPUT_SLOT && VillagerItem.isBaby(stack);
    }

    @Override
    public void method_5431() {
        markUpdated();
    }

    @Override
    public void method_5448() {
        this.items.clear();
        this.cachedBabyVillager = null;
        method_5431();
    }

    @Override
    public int[] method_5494(class_2350 side) {
        if (side == class_2350.field_11033) {
            return SLOTS_DOWN; // 底部漏斗抽取成年村民
        }
        return SLOTS_UP_SIDES; // 顶部或侧面漏入幼年村民
    }

    @Override
    public boolean method_5492(int slot, class_1799 stack, class_2350 side) {
        return side != class_2350.field_11033 && slot == INPUT_SLOT && VillagerItem.isBaby(stack);
    }

    @Override
    public boolean method_5493(int slot, class_1799 stack, class_2350 side) {
        return side == class_2350.field_11033 && slot == OUTPUT_SLOT;
    }

    // --- NBT 持久化 ---

    @Override
    public void method_11007(class_2487 nbt) {
        super.method_11007(nbt);
        class_1262.method_5426(nbt, this.items);
        nbt.method_10569("GrowthProgress", this.growthProgress);
    }

    @Override
    public void method_11014(class_2487 nbt) {
        super.method_11014(nbt);
        this.items.clear();
        class_1262.method_5429(nbt, this.items);
        this.growthProgress = nbt.method_10550("GrowthProgress");
        this.cachedBabyVillager = null;
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
