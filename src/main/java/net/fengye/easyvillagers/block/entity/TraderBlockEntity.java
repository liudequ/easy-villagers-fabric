package net.fengye.easyvillagers.block.entity;

import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.item.VillagerItem;
import net.fengye.easyvillagers.util.VillagerWorkstationHelper;
import net.minecraft.class_1299;
import net.minecraft.class_1646;
import net.minecraft.class_1799;
import net.minecraft.class_1916;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;

public class TraderBlockEntity extends class_2586 {
    public static final String VILLAGER_DATA_KEY = "StoredVillager";
    public static final String WORKSTATION_DATA_KEY = "StoredWorkstation";
    public static final String RESTOCK_TIME_KEY = "LastRestockTime";

    protected class_2487 villagerTag = null;
    protected class_1646 cachedVillager = null;
    protected class_1799 workstation = class_1799.field_8037;
    protected long lastRestockTime = 0;

    public TraderBlockEntity(class_2338 pos, class_2680 state) {
        this(ModBlocks.TRADER_BLOCK_ENTITY, pos, state);
    }

    protected TraderBlockEntity(class_2591<?> type, class_2338 pos, class_2680 state) {
        super(type, pos, state);
    }

    public boolean hasVillager() {
        return this.villagerTag != null;
    }

    public class_2487 getVillagerTag() {
        return this.villagerTag;
    }

    public boolean hasWorkstation() {
        return this.workstation != null && !this.workstation.method_7960();
    }

    public class_1799 getWorkstation() {
        return this.workstation;
    }

    public void setWorkstation(class_1799 stack) {
        this.workstation = stack.method_7972();
        this.workstation.method_7939(1);

        // 如果已经有村民且未锁定职业，立即转职并刷新一级交易
        if (hasVillager()) {
            class_1646 villager = getOrCreateCachedVillager();
            if (villager != null && VillagerWorkstationHelper.canCycleTrade(villager)) {
                String profId = VillagerWorkstationHelper.getProfessionId(this.workstation);
                if (profId != null) {
                    VillagerWorkstationHelper.applyProfession(villager, profId);
                    syncVillagerNbtFromEntity(villager, profId);
                }
            }
        }
        markUpdated();
    }

    public class_1799 extractWorkstation() {
        if (!hasWorkstation()) {
            return class_1799.field_8037;
        }
        class_1799 extracted = this.workstation.method_7972();
        this.workstation = class_1799.field_8037;

        // 如果有村民且未锁定职业，移除工作台后瞬间还原为无业并清空交易（支持洗词条）
        if (hasVillager()) {
            class_1646 villager = getOrCreateCachedVillager();
            if (villager != null && VillagerWorkstationHelper.canCycleTrade(villager)) {
                VillagerWorkstationHelper.applyProfession(villager, "none");
                syncVillagerNbtFromEntity(villager, "none");
            }
        }
        markUpdated();
        return extracted;
    }

    public void setVillagerFromItem(class_1799 stack) {
        class_2487 nbt = stack.method_7969();
        if (nbt != null) {
            this.villagerTag = nbt.method_10553(); // copy
        } else {
            this.villagerTag = new class_2487();
        }
        this.cachedVillager = null;

        // 如果展柜已经插有工作台，且新放入的村民未锁定，自动完成转职
        if (hasWorkstation()) {
            class_1646 villager = getOrCreateCachedVillager();
            if (villager != null && VillagerWorkstationHelper.canCycleTrade(villager)) {
                String profId = VillagerWorkstationHelper.getProfessionId(this.workstation);
                if (profId != null) {
                    VillagerWorkstationHelper.applyProfession(villager, profId);
                    syncVillagerNbtFromEntity(villager, profId);
                }
            }
        }
        markUpdated();
    }

    public class_1799 extractVillagerItem() {
        if (!hasVillager()) {
            return class_1799.field_8037;
        }
        class_1799 stack = new class_1799(ModItems.VILLAGER_ITEM);
        stack.method_7980(this.villagerTag.method_10553());
        this.villagerTag = null;
        this.cachedVillager = null;
        markUpdated();
        return stack;
    }

    protected void syncVillagerNbtFromEntity(class_1646 villager, String profId) {
        if (villager == null || this.villagerTag == null) return;
        class_2487 entityTag = new class_2487();
        villager.method_5647(entityTag);
        this.villagerTag.method_10566(VillagerItem.ENTITY_TAG, entityTag);
        this.villagerTag.method_10582(VillagerItem.PROFESSION_KEY, VillagerItem.getProfessionName(profId));
        this.villagerTag.method_10582(VillagerItem.LEVEL_KEY, VillagerItem.getLevelName(1));
    }

    public class_1916 getVillagerTrades() {
        class_1646 villager = getOrCreateCachedVillager();
        if (villager == null) return null;
        return villager.method_8264();
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

    public static void tick(class_1937 world, class_2338 pos, class_2680 state, TraderBlockEntity be) {
        if (world == null || world.field_9236) return;
        if (!be.hasVillager() || !be.hasWorkstation()) return;

        long time = world.method_8510();
        if (time - be.lastRestockTime >= 2400) {
            class_1646 villager = be.getOrCreateCachedVillager();
            if (villager != null) {
                VillagerWorkstationHelper.restock(villager);
                class_2487 entityTag = new class_2487();
                villager.method_5647(entityTag);
                be.villagerTag.method_10566(VillagerItem.ENTITY_TAG, entityTag);
                be.markUpdated();
            }
            be.lastRestockTime = time;
        }
    }

    public void markUpdated() {
        super.method_5431(); // markDirty()
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

        if (nbt.method_10545(WORKSTATION_DATA_KEY)) {
            this.workstation = class_1799.method_7915(nbt.method_10562(WORKSTATION_DATA_KEY));
        } else {
            this.workstation = class_1799.field_8037;
        }

        this.lastRestockTime = nbt.method_10537(RESTOCK_TIME_KEY);
        this.cachedVillager = null;
    }

    @Override
    protected void method_11007(class_2487 nbt) { // writeNbt
        super.method_11007(nbt);
        if (this.villagerTag != null) {
            nbt.method_10566(VILLAGER_DATA_KEY, this.villagerTag);
        }
        if (hasWorkstation()) {
            nbt.method_10566(WORKSTATION_DATA_KEY, this.workstation.method_7953(new class_2487()));
        }
        nbt.method_10544(RESTOCK_TIME_KEY, this.lastRestockTime);
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
