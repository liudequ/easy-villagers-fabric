package net.fengye.easyvillagers.util;

import net.minecraft.class_1657;
import net.minecraft.class_2487;

/** 由村民 Mixin 实现，缓存随实体 NBT 一起保存。 */
public interface VillagerPreviewAccess {
    class_2487 easyVillagers$getPreviewData();
    void easyVillagers$setPreviewData(class_2487 data);
    void easyVillagers$fillRecipes();
    void easyVillagers$updatePrices(class_1657 player);
    void easyVillagers$levelUp();
    boolean easyVillagers$isCured();
    void easyVillagers$setCured(boolean cured);
}
