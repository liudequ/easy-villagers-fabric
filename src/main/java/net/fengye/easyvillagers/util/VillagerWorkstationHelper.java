package net.fengye.easyvillagers.util;

import net.minecraft.class_1646;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_3850;
import net.minecraft.class_3852;
import net.minecraft.class_7923;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class VillagerWorkstationHelper {
    private static final Map<class_1792, String> ITEM_TO_PROFESSION = new HashMap<>();
    private static Method fillRecipesMethod;

    static {
        registerWorkstation("blast_furnace", "armorer");
        registerWorkstation("smoker", "butcher");
        registerWorkstation("cartography_table", "cartographer");
        registerWorkstation("brewing_stand", "cleric");
        registerWorkstation("composter", "farmer");
        registerWorkstation("barrel", "fisherman");
        registerWorkstation("fletching_table", "fletcher");
        registerWorkstation("cauldron", "leatherworker");
        registerWorkstation("lectern", "librarian");
        registerWorkstation("stonecutter", "mason");
        registerWorkstation("loom", "shepherd");
        registerWorkstation("smithing_table", "toolsmith");
        registerWorkstation("grindstone", "weaponsmith");

        try {
            fillRecipesMethod = class_1646.class.getDeclaredMethod("method_7237");
            fillRecipesMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerWorkstation(String itemId, String professionId) {
        class_1792 item = class_7923.field_41178.method_10223(new class_2960("minecraft", itemId));
        if (item != null) {
            ITEM_TO_PROFESSION.put(item, professionId);
        }
    }

    public static boolean isWorkstation(class_1799 stack) {
        if (stack == null || stack.method_7960()) return false;
        return ITEM_TO_PROFESSION.containsKey(stack.method_7909());
    }

    public static String getProfessionId(class_1799 stack) {
        if (stack == null || stack.method_7960()) return null;
        return ITEM_TO_PROFESSION.get(stack.method_7909());
    }

    public static class_3852 getProfession(class_1799 stack) {
        String profId = getProfessionId(stack);
        if (profId == null) return null;
        return class_7923.field_41195.method_10223(new class_2960("minecraft", profId));
    }

    public static class_3852 getNoneProfession() {
        return class_7923.field_41195.method_10223(new class_2960("minecraft", "none"));
    }

    public static boolean canCycleTrade(class_1646 villager) {
        if (villager == null) return false;
        // 未获得过经验，即未被玩家锁定
        int exp = villager.method_19269();
        if (exp > 0) return false;

        class_3850 data = villager.method_7231();
        class_2960 profId = class_7923.field_41195.method_10221(data.method_16924());
        if (profId != null && "nitwit".equals(profId.method_12832())) {
            return false; // 傻子村民无法转职
        }
        return true;
    }

    public static boolean applyProfession(class_1646 villager, String professionId) {
        if (villager == null || !canCycleTrade(villager)) return false;

        class_3852 profession = class_7923.field_41195.method_10223(new class_2960("minecraft", professionId));
        if (profession == null) return false;

        // 清空之前旧交易
        villager.method_8264().clear();

        // 设置新职业和初始等级 1 (Novice)
        class_3850 newData = villager.method_7231().method_16921(profession).method_16920(1);
        villager.method_7195(newData);

        // 如果不是 none/nitwit，重新生成交易词条
        if (!"none".equals(professionId) && !"nitwit".equals(professionId)) {
            try {
                if (fillRecipesMethod != null) {
                    fillRecipesMethod.invoke(villager);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 补货初始化
        villager.method_19182();
        return true;
    }

    public static void restock(class_1646 villager) {
        if (villager != null) {
            villager.method_19182();
        }
    }
}
