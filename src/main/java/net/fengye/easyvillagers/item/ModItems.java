package net.fengye.easyvillagers.item;

import net.minecraft.class_1792;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ModItems {
    public static final String MOD_ID = "easy_villagers";

    public static final class_1792 VILLAGER_ITEM = new VillagerItem(new class_1792.class_1793());

    public static void register() {
        class_2378.method_10230(class_7923.field_41178, new class_2960(MOD_ID, "villager"), VILLAGER_ITEM);
    }
}
