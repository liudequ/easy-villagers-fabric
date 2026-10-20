package net.fengye.easyvillagers.util;

import net.minecraft.class_1299;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1914;
import net.minecraft.class_1916;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3850;
import net.minecraft.class_7923;

/** 未来交易的生成、持久化和开窗入口；不修改村民真实的已解锁交易列表。 */
public final class VillagerTradePreviewHelper {
    public static final String REQUIRED_LEVEL_KEY = "EasyVillagersRequiredLevel";

    private VillagerTradePreviewHelper() {}

    public static void applyCureDiscounts(class_1646 villager) {
        if (villager == null) return;
        class_1916 offers = villager.method_8264();
        if (offers == null) return;
        for (class_1914 offer : offers) {
            applyCureDiscount(offer);
        }
    }

    public static void applyCureDiscount(class_1914 offer) {
        if (offer == null) return;
        class_1799 baseBuy = offer.method_8246();
        if (baseBuy.method_7960()) return;
        int baseCount = baseBuy.method_7947();
        int demand = offer.method_21725();
        float multiplier = offer.method_19278();
        int demandBonus = Math.max(0, net.minecraft.class_3532.method_15375((float) (baseCount * demand) * multiplier));
        int targetSpecialPrice = -(baseCount + demandBonus - 1);
        offer.method_19273(targetSpecialPrice);
    }

    public static void clearCache(class_1646 villager) {
        if (villager instanceof VillagerPreviewAccess access) {
            access.easyVillagers$setPreviewData(new class_2487());
        }
    }

    public static boolean matchesProfession(class_1646 villager, class_2487 data) {
        class_2960 profession = class_7923.field_41195.method_10221(villager.method_7231().method_16924());
        class_2960 type = class_7923.field_41194.method_10221(villager.method_7231().method_16919());
        return profession != null && type != null
                && profession.toString().equals(data.method_10558("Profession"))
                && type.toString().equals(data.method_10558("Type"));
    }

    private static class_2487 getOrGenerateFutureOffers(class_1646 villager) {
        VillagerPreviewAccess access = (VillagerPreviewAccess) villager;
        class_2487 data = access.easyVillagers$getPreviewData();
        if (matchesProfession(villager, data)) return data;

        class_3850 originalData = villager.method_7231();
        class_2960 profession = class_7923.field_41195.method_10221(originalData.method_16924());
        if (profession == null || "none".equals(profession.method_12832())
                || "nitwit".equals(profession.method_12832()) || originalData.method_16925() >= 5) {
            return new class_2487();
        }

        class_1646 probe = class_1299.field_6077.method_5883(villager.method_37908());
        if (probe == null) return new class_2487();
        // 独立实体推演，不暂时升级真实村民、不修改其 Offers 或 DataTracker。
        probe.method_5814(villager.method_23317(), villager.method_23318(), villager.method_23321());
        class_2487 generated = new class_2487();
        generated.method_10582("Profession", profession.toString());
        generated.method_10582("Type", class_7923.field_41194.method_10221(originalData.method_16919()).toString());
        for (int level = originalData.method_16925() + 1; level <= 5; level++) {
            probe.method_7195(originalData.method_16920(level));
            probe.method_16917(new class_1916());
            ((VillagerPreviewAccess) probe).easyVillagers$fillRecipes();
            generated.method_10566("Level" + level, probe.method_8264().method_8268());
        }
        // 全部生成成功后才提交，实体 NBT/收纳物品/展柜存档都会携带这份数据。
        access.easyVillagers$setPreviewData(generated);
        return generated;
    }

    public static class_1916 buildPreviewOffers(class_1646 villager) {
        if (villager instanceof VillagerPreviewAccess access && access.easyVillagers$isCured()) {
            applyCureDiscounts(villager);
        }
        class_1916 display = new class_1916();
        // 真交易保留同一对象引用，交易次数、折扣和经验仍由原版管理。
        display.addAll(villager.method_8264());
        class_2487 future = getOrGenerateFutureOffers(villager);
        boolean isCured = villager instanceof VillagerPreviewAccess access && access.easyVillagers$isCured();
        for (int level = villager.method_7231().method_16925() + 1; level <= 5; level++) {
            if (!future.method_10573("Level" + level, 10)) continue;
            for (class_1914 offer : new class_1916(future.method_10562("Level" + level))) {
                if (isCured) {
                    applyCureDiscount(offer);
                }
                display.add(wrapLockedOffer(offer, level));
            }
        }
        return display;
    }

    private static class_1914 wrapLockedOffer(class_1914 base, int level) {
        class_1799 sell = base.method_8250().method_7972();
        sell.method_7948().method_10569(REQUIRED_LEVEL_KEY, level);
        class_2487 display = sell.method_7911("display");
        class_2499 lore = display.method_10554("Lore", 8);
        String[] titles = {"", "新手", "学徒", "老手", "专家", "大师"};
        class_2561 text = class_2561.method_43470("§c未解锁：需要 Lv." + level + " " + titles[level]);
        lore.add(0, class_2519.method_23256(class_2561.class_2562.method_10867(text)));
        display.method_10566("Lore", lore);
        // 服务端与客户端使用同一份售罄包装，不能通过伪造选择包提前交易。
        return new class_1914(base.method_19272().method_7972(), base.method_8247().method_7972(),
                sell, 1, 1, 0, 0.0f);
    }

    public static boolean isLockedOffer(class_1914 offer) {
        class_2487 tag = offer.method_8250().method_7969();
        return tag != null && tag.method_10550(REQUIRED_LEVEL_KEY) > 0;
    }

    public static void openTradingScreenWithPreview(class_1657 player, class_1646 villager) {
        openTradingScreenWithPreview(player, villager, () -> {});
    }

    public static void openTradingScreenWithPreview(class_1657 player, class_1646 villager, Runnable onChange) {
        if (player == null || villager == null || villager.method_37908().field_9236) return;
        if (villager.method_6109() || !villager.method_5805()
                || (villager.method_8257() != null && villager.method_8257() != player)) return;
        if (villager.method_8264().isEmpty()) return;

        class_1916 offers = buildPreviewOffers(villager);
        ((VillagerPreviewAccess) villager).easyVillagers$updatePrices(player);
        villager.method_8259(player);
        new PreviewMerchant(villager, offers, onChange).method_17449(player, villager.method_5476(), villager.method_7231().method_16925());
        onChange.run();
    }

    /** 虚拟村民不会经历实体 tick，关窗后按原版经验阈值兑现升级。 */
    public static boolean advanceStoredVillager(class_1646 villager) {
        if (villager == null || villager.method_8257() != null) return false;
        int level = villager.method_7231().method_16925();
        if (!class_3850.method_19196(level) || villager.method_19269() < class_3850.method_19195(level)) return false;
        ((VillagerPreviewAccess) villager).easyVillagers$levelUp();
        if (villager instanceof VillagerPreviewAccess access && access.easyVillagers$isCured()) {
            applyCureDiscounts(villager);
        }
        return true;
    }
}
