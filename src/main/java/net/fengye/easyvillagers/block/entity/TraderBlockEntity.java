package net.fengye.easyvillagers.block.entity;

import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.item.VillagerItem;
import net.minecraft.class_1299;
import net.minecraft.class_1646;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;

public class TraderBlockEntity extends class_2586 {
    public static final String VILLAGER_DATA_KEY = "StoredVillager";
    private class_2487 villagerTag = null;
    private class_1646 cachedVillager = null;

    public TraderBlockEntity(class_2338 pos, class_2680 state) {
        super(ModBlocks.TRADER_BLOCK_ENTITY, pos, state);
    }

    public boolean hasVillager() {
        return this.villagerTag != null;
    }

    public class_2487 getVillagerTag() {
        return this.villagerTag;
    }

    public void setVillagerFromItem(class_1799 stack) {
        class_2487 nbt = stack.method_7969();
        if (nbt != null) {
            this.villagerTag = nbt.method_10553(); // copy
        } else {
            this.villagerTag = new class_2487();
        }
        this.cachedVillager = null;
        markUpdated();
    }

    public class_1799 extractVillagerItem() {
        if (!hasVillager()) {
            return class_1799.field_8037; // ItemStack.EMPTY
        }
        class_1799 stack = new class_1799(ModItems.VILLAGER_ITEM);
        stack.method_7980(this.villagerTag.method_10553());
        this.villagerTag = null;
        this.cachedVillager = null;
        markUpdated();
        return stack;
    }

    public class_1646 getOrCreateCachedVillager() {
        if (!hasVillager() || this.field_11863 == null) {
            return null;
        }
        if (this.cachedVillager == null) {
            this.cachedVillager = class_1299.field_6077.method_5883(this.field_11863);
            if (this.cachedVillager != null && this.villagerTag.method_10545(VillagerItem.ENTITY_TAG)) {
                this.cachedVillager.method_5651(this.villagerTag.method_10562(VillagerItem.ENTITY_TAG));
            }
        }
        return this.cachedVillager;
    }

    private void markUpdated() {
        this.method_5431(); // markDirty()
        if (this.field_11863 != null && !this.field_11863.field_9236) {
            this.field_11863.method_8413(this.field_11867, this.method_11010(), this.method_11010(), 3);
        }
    }

    @Override
    public void method_11014(class_2487 nbt) { // readNbt
        super.method_11014(nbt);
        if (nbt.method_10545(VILLAGER_DATA_KEY)) {
            this.villagerTag = nbt.method_10562(VILLAGER_DATA_KEY);
        } else {
            this.villagerTag = null;
        }
        this.cachedVillager = null;
    }

    @Override
    protected void method_11007(class_2487 nbt) { // writeNbt
        super.method_11007(nbt);
        if (this.villagerTag != null) {
            nbt.method_10566(VILLAGER_DATA_KEY, this.villagerTag);
        }
    }

    @Override
    public class_2596<class_2602> method_38235() { // toUpdatePacket
        return class_2622.method_38585(this);
    }

    @Override
    public class_2487 method_16887() { // toInitialChunkDataNbt
        return method_38242();
    }
}
