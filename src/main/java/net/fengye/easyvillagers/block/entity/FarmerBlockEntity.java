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
import net.minecraft.class_1792;
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
import net.minecraft.class_2960;
import net.minecraft.class_7923;

import java.util.Random;

public class FarmerBlockEntity extends class_2586 implements class_1278 {
    public static final int TOTAL_SLOTS = 5;
    public static final int SLOT_SEED = 0;
    public static final int OUTPUT_SLOT_START = 1;

    private static final int[] SLOTS_TOP = new int[]{SLOT_SEED};
    private static final int[] SLOTS_BOTTOM = new int[]{1, 2, 3, 4};
    private static final int[] SLOTS_SIDES = new int[]{0, 1, 2, 3, 4};

    private final class_2371<class_1799> items = class_2371.method_10213(TOTAL_SLOTS, class_1799.field_8037);
    private class_2487 villagerTag = null;

    private int progress = 0;
    public static final int MAX_PROGRESS = 140; // 7秒 (140刻)
    private final Random random = new Random();

    // 客户端渲染实体
    private class_1646 cachedVillager = null;

    public FarmerBlockEntity(class_2338 pos, class_2680 state) {
        super(ModBlocks.FARMER_BLOCK_ENTITY, pos, state);
    }

    public boolean hasVillager() {
        return this.villagerTag != null;
    }

    public void addVillager(class_1799 stack) {
        if (stack.method_7942()) {
            this.villagerTag = stack.method_7969().method_10553();
        } else {
            this.villagerTag = new class_2487();
        }
        this.cachedVillager = null;
        markUpdated();
    }

    public class_1799 extractVillager() {
        if (!hasVillager()) return class_1799.field_8037;
        if (this.cachedVillager != null && this.villagerTag != null) {
            class_2487 entityTag = new class_2487();
            this.cachedVillager.method_5647(entityTag);
            this.villagerTag.method_10566(VillagerItem.ENTITY_TAG, entityTag);
        }
        class_1799 stack = new class_1799(ModItems.VILLAGER_ITEM);
        if (this.villagerTag != null) {
            stack.method_7980(this.villagerTag.method_10553());
        }
        this.villagerTag = null;
        this.cachedVillager = null;
        this.progress = 0;
        markUpdated();
        return stack;
    }

    public int getProgress() {
        return this.progress;
    }

    public void markUpdated() {
        super.method_5431();
        if (this.field_11863 != null && !this.field_11863.field_9236) {
            this.field_11863.method_8413(this.field_11867, this.method_11010(), this.method_11010(), 3);
        }
    }

    public static void tick(class_1937 world, class_2338 pos, class_2680 state, FarmerBlockEntity be) {
        if (world.field_9236) return;

        if (!be.hasVillager()) {
            if (be.progress != 0) {
                be.progress = 0;
                be.markUpdated();
            }
            return;
        }

        class_1799 seed = be.items.get(SLOT_SEED);
        if (seed.method_7960()) {
            if (be.progress != 0) {
                be.progress = 0;
                be.markUpdated();
            }
            return;
        }

        be.progress++;
        if (be.progress >= MAX_PROGRESS) {
            be.progress = 0;
            be.harvestCrop();
            be.markUpdated();
        }
    }

    private void harvestCrop() {
        class_1799 seedStack = this.items.get(SLOT_SEED);
        if (seedStack.method_7960()) return;

        class_2960 seedId = class_7923.field_41178.method_10221(seedStack.method_7909());
        if (seedId == null) return;

        String path = seedId.method_12832();

        if (path.contains("wheat_seed") || path.equals("wheat_seeds")) {
            insertOutput(createStack("wheat", 1 + this.random.nextInt(2)));
            insertOutput(createStack("wheat_seeds", 1));
        } else if (path.equals("carrot") || path.equals("carrots")) {
            insertOutput(createStack("carrot", 2 + this.random.nextInt(3)));
        } else if (path.equals("potato") || path.equals("potatoes")) {
            insertOutput(createStack("potato", 2 + this.random.nextInt(3)));
        } else if (path.contains("beetroot_seed") || path.equals("beetroot_seeds")) {
            insertOutput(createStack("beetroot", 1 + this.random.nextInt(2)));
            insertOutput(createStack("beetroot_seeds", 1));
        } else {
            insertOutput(new class_1799(seedStack.method_7909(), 1));
        }
    }

    private class_1799 createStack(String name, int count) {
        class_1792 item = class_7923.field_41178.method_10223(new class_2960("minecraft", name));
        if (item != null) {
            return new class_1799(item, count);
        }
        return class_1799.field_8037;
    }

    private void insertOutput(class_1799 stack) {
        if (stack.method_7960()) return;

        for (int i = OUTPUT_SLOT_START; i < TOTAL_SLOTS; i++) {
            class_1799 slot = this.items.get(i);
            if (!slot.method_7960() && slot.method_7909() == stack.method_7909()) {
                int space = slot.method_7914() - slot.method_7947();
                if (space > 0) {
                    int toAdd = Math.min(space, stack.method_7947());
                    slot.method_7939(slot.method_7947() + toAdd);
                    stack.method_7934(toAdd);
                    if (stack.method_7960()) return;
                }
            }
        }

        for (int i = OUTPUT_SLOT_START; i < TOTAL_SLOTS; i++) {
            if (this.items.get(i).method_7960()) {
                this.items.set(i, stack.method_7972());
                stack.method_7939(0);
                return;
            }
        }
    }

    public class_1646 getOrCreateCachedVillager() {
        if (!hasVillager() || this.field_11863 == null) return null;
        if (this.cachedVillager == null) {
            this.cachedVillager = class_1299.field_6077.method_5883(this.field_11863);
            if (this.cachedVillager != null && this.villagerTag != null) {
                this.cachedVillager.method_5651(this.villagerTag);
            }
        }
        return this.cachedVillager;
    }

    @Override
    public int[] method_5494(class_2350 side) {
        if (side == class_2350.field_11036) {
            return SLOTS_TOP;
        } else if (side == class_2350.field_11033) {
            return SLOTS_BOTTOM;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean method_5492(int slot, class_1799 stack, class_2350 dir) {
        return slot == SLOT_SEED;
    }

    @Override
    public boolean method_5493(int slot, class_1799 stack, class_2350 dir) {
        return slot >= OUTPUT_SLOT_START;
    }

    @Override
    public int method_5439() {
        return TOTAL_SLOTS;
    }

    @Override
    public boolean method_5442() {
        for (class_1799 s : this.items) {
            if (!s.method_7960()) return false;
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
        if (!result.method_7960()) markUpdated();
        return result;
    }

    @Override
    public class_1799 method_5441(int slot) {
        class_1799 result = class_1262.method_5428(this.items, slot);
        if (!result.method_7960()) markUpdated();
        return result;
    }

    @Override
    public void method_5447(int slot, class_1799 stack) {
        this.items.set(slot, stack);
        markUpdated();
    }

    @Override
    public boolean method_5443(class_1657 player) {
        return class_1263.method_49105(this, player);
    }

    @Override
    public void method_5448() {
        this.items.clear();
        markUpdated();
    }

    @Override
    public void method_11007(class_2487 nbt) {
        super.method_11007(nbt);
        if (this.villagerTag != null) {
            nbt.method_10566("Villager", this.villagerTag);
        }
        class_1262.method_5426(nbt, this.items);
        nbt.method_10569("Progress", this.progress);
    }

    @Override
    public void method_11014(class_2487 nbt) {
        super.method_11014(nbt);
        this.villagerTag = nbt.method_10545("Villager") ? nbt.method_10562("Villager") : null;
        this.items.clear();
        class_1262.method_5429(nbt, this.items);
        this.progress = nbt.method_10550("Progress");
        this.cachedVillager = null;
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
