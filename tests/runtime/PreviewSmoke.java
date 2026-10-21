package net.fengye.easyvillagers.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.block.entity.TraderBlockEntity;
import net.fengye.easyvillagers.block.entity.AutoTraderBlockEntity;
import net.fengye.easyvillagers.item.ModItems;
import net.fengye.easyvillagers.item.VillagerItem;
import com.mojang.authlib.GameProfile;
import java.util.OptionalInt;
import java.util.UUID;

public final class PreviewSmoke implements ModInitializer {
    private static void check(boolean value, String name) {
        if (!value) throw new AssertionError(name);
        System.out.println("[EV-SMOKE PASS] " + name);
    }
    @Override public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try { run(server); }
            catch (Throwable error) {
                System.out.println("[EV-SMOKE FAILED]");
                error.printStackTrace();
            }
            System.out.println("[EV-SMOKE FINISHED]");
        });
    }
    private static final class TestPlayer extends class_1657 {
        class_1916 receivedOffers;
        TestPlayer(MinecraftServer server) {
            super(server.method_30002(), new class_2338(0, 100, 0), 0.0f,
                    new GameProfile(UUID.randomUUID(), "PreviewSmoke"));
        }
        @Override public boolean method_7325() { return false; }
        @Override public boolean method_7337() { return false; }
        @Override public OptionalInt method_17355(class_3908 factory) {
            this.field_7512 = factory.createMenu(1, method_31548(), this);
            return OptionalInt.of(1);
        }
        @Override public void method_17354(int syncId, class_1916 offers, int level, int xp, boolean leveled, boolean refreshable) {
            this.receivedOffers = offers;
        }
    }
    private static void run(MinecraftServer server) {
        class_1646 v = class_1299.field_6077.method_5883(server.method_30002());
        v.method_7195(v.method_7231().method_16921(class_7923.field_41195.method_10223(new class_2960("minecraft:librarian"))).method_16920(1));
        class_2487 initialOffers = v.method_8264().method_8268().method_10553();
        int count = v.method_8264().size();
        class_1916 display = VillagerTradePreviewHelper.buildPreviewOffers(v);
        check(count == 2 && display.size() > count, "level 1 shows future offers");
        check(initialOffers.equals(v.method_8264().method_8268()), "preview does not mutate real offers");
        check(v.method_7231().method_16925() == 1, "preview does not change real level");
        check(display.method_8268().equals(VillagerTradePreviewHelper.buildPreviewOffers(v).method_8268()), "reopening retains identical future offers");
        for (int i = count; i < display.size(); i++) {
            check(display.get(i).method_8255() && VillagerTradePreviewHelper.isLockedOffer(display.get(i)), "future offer " + i + " locked on server");
        }
        PreviewMerchant merchant = new PreviewMerchant(v, display, () -> {});
        class_1725 inventory = new class_1725(merchant);
        inventory.method_7643(count);
        inventory.method_5447(0, display.get(count).method_8246().method_7972());
        inventory.method_5447(1, display.get(count).method_8247().method_7972());
        inventory.method_7645();
        check(inventory.method_5438(2).method_7960(), "locked offer cannot produce output with valid ingredients");
        inventory.method_5448();
        inventory.method_7643(0);
        inventory.method_5447(0, display.get(0).method_8246().method_7972());
        inventory.method_5447(1, display.get(0).method_8247().method_7972());
        inventory.method_7645();
        check(!inventory.method_5438(2).method_7960(), "real offer still produces output");
        int uses = display.get(0).method_8249();
        merchant.method_8262(display.get(0));
        check(v.method_8264().get(0).method_8249() == uses + 1, "successful trade updates original villager");
        class_2487 nbt = new class_2487();
        v.method_5647(nbt);
        check(nbt.method_10573("EasyVillagersTradePreview", 10), "preview saved in entity NBT");
        class_1646 restored = class_1299.field_6077.method_5883(server.method_30002());
        restored.method_5651(nbt);
        check(display.method_8268().equals(VillagerTradePreviewHelper.buildPreviewOffers(restored).method_8268()), "save/load retains exact previews and real offers");
        class_1916 predictedLevel2 = new class_1916(((VillagerPreviewAccess) restored).easyVillagers$getPreviewData().method_10562("Level2"));
        restored.method_19625(10);
        check(VillagerTradePreviewHelper.advanceStoredVillager(restored), "virtual villager upgrades at vanilla XP threshold");
        check(restored.method_7231().method_16925() == 2, "upgraded to level 2");
        class_1916 unlocked = new class_1916();
        unlocked.addAll(restored.method_8264().subList(count, restored.method_8264().size()));
        check(unlocked.method_8268().equals(predictedLevel2.method_8268()), "upgrade unlocks exact previously predicted recipes");
        for (class_1914 offer : unlocked) check(!offer.method_8255() && !VillagerTradePreviewHelper.isLockedOffer(offer), "unlocked offer is usable and has no preview lore");
        check(!VillagerTradePreviewHelper.advanceStoredVillager(restored), "cannot upgrade twice without sufficient XP");
        restored.method_7195(restored.method_7231().method_16921(class_7923.field_41195.method_10223(new class_2960("minecraft:farmer"))).method_16920(1));
        check(((VillagerPreviewAccess) restored).easyVillagers$getPreviewData().method_33133(), "changing profession invalidates prediction");
        for (boolean automated : new boolean[]{false, true}) {
            class_2338 pos = new class_2338(automated ? 2 : 0, 100, 0);
            class_2680 state = (automated ? ModBlocks.AUTO_TRADER_BLOCK : ModBlocks.TRADER_BLOCK).method_9564();
            server.method_30002().method_8652(pos, state, 3);
            TraderBlockEntity trader = (TraderBlockEntity) server.method_30002().method_8321(pos);
            class_1799 villagerItem = VillagerItem.createFromVillager(v, ModItems.VILLAGER_ITEM);
            trader.setVillagerFromItem(villagerItem);
            class_1646 stored = trader.getOrCreateCachedVillager();
            class_1916 preview = VillagerTradePreviewHelper.buildPreviewOffers(stored);
            class_1916 nextLevel = new class_1916(((VillagerPreviewAccess) stored).easyVillagers$getPreviewData().method_10562("Level2"));
            stored.method_19625(10);
            if (automated) AutoTraderBlockEntity.tick(server.method_30002(), pos, state, (AutoTraderBlockEntity) trader);
            else TraderBlockEntity.tick(server.method_30002(), pos, state, trader);
            check(stored.method_7231().method_16925() == 2, (automated ? "auto" : "manual") + " trader ticker upgrades cached villager");
            class_1799 extracted = trader.extractVillagerItem();
            class_1646 released = class_1299.field_6077.method_5883(server.method_30002());
            released.method_5651(extracted.method_7969().method_10562(VillagerItem.ENTITY_TAG));
            check(released.method_7231().method_16925() == 2 && released.method_19269() == 10, "extraction preserves upgraded level and XP");
            class_1916 releasedLevel2 = new class_1916();
            releasedLevel2.addAll(released.method_8264().subList(count, released.method_8264().size()));
            check(nextLevel.method_8268().equals(releasedLevel2.method_8268()), "extraction preserves promised recipes");
            check(!((VillagerPreviewAccess) released).easyVillagers$getPreviewData().method_33133(), "villager item retains remaining future previews");
        }
        TestPlayer player = new TestPlayer(server);
        class_1646 normal = class_1299.field_6077.method_5883(server.method_30002());
        normal.method_7195(v.method_7231());
        int normalCount = normal.method_8264().size();
        normal.method_5992(player, class_1268.field_5808);
        check(player.field_7512 instanceof class_1728 && player.receivedOffers.size() > normalCount, "vanilla entity interaction opens preview screen through mixin");
        class_1728 screen = (class_1728) player.field_7512;
        check(screen.method_17438().size() == player.receivedOffers.size(), "server menu indices match transmitted offer list");
        check(normal.method_8257() == player, "preview screen owns the original villager customer");
        screen.method_7595(player);
        check(normal.method_8257() == null, "closing preview screen clears villager busy state");

        // 测试治愈村民在交易柜中交易、关窗、升级、自动化交易的打折持久性
        class_1646 curedV = class_1299.field_6077.method_5883(server.method_30002());
        curedV.method_7195(curedV.method_7231().method_16921(class_7923.field_41195.method_10223(new class_2960("minecraft:weaponsmith"))).method_16920(1));
        class_1799 curedItem = VillagerItem.createFromVillager(curedV, ModItems.VILLAGER_ITEM);
        curedItem.method_7969().method_10582("DiscountTitle", "§6✨ 治愈骨折特惠 (1绿宝石)");
        curedItem.method_7969().method_10556("EasyVillagersCured", true);
        curedItem.method_7969().method_10562(VillagerItem.ENTITY_TAG).method_10556("EasyVillagersCured", true);

        class_2338 traderPos = new class_2338(10, 100, 0);
        server.method_30002().method_8652(traderPos, ModBlocks.TRADER_BLOCK.method_9564(), 3);
        TraderBlockEntity traderBe = (TraderBlockEntity) server.method_30002().method_8321(traderPos);
        traderBe.setVillagerFromItem(curedItem);

        class_1646 cachedCured = traderBe.getOrCreateCachedVillager();
        VillagerTradePreviewHelper.openTradingScreenWithPreview(player, cachedCured, () -> {
            traderBe.updateVillagerTagFromCached();
            traderBe.markUpdated();
        });

        // 测试交易柜中 Shift 批量交易（快捷移动产物槽）：验证不会抛出 ClassCastException 且批量交易正常
        class_1728 handler = (class_1728) player.field_7512;
        class_1914 r0 = handler.method_17438().get(0);
        player.method_31548().method_5442();
        player.method_31548().method_5447(0, new class_1799(r0.method_19272().method_7909(), 64));
        handler.method_20215(0);
        check(handler.method_7611(0).method_7677().method_7947() > 0, "recipe 0 fills input slot with items");
        check(!handler.method_7611(2).method_7677().method_7960(), "recipe 0 produces output in slot 2");

        // 模拟玩家按住 Shift 点击产物槽（SlotActionType.QUICK_MOVE）
        handler.method_7593(2, 0, class_1713.field_7794, player);
        check(player.method_31548().method_7379(r0.method_8250()), "player inventory received traded items via shift-click");
        check(r0.method_8249() > 0, "villager offer uses increased after shift-click trades");

        // 模拟关闭交易窗口
        cachedCured.method_8259(null);
        traderBe.updateVillagerTagFromCached();

        // 关窗后检查打折是否依然有效（buy1 必须为 1）
        for (class_1914 offer : cachedCured.method_8264()) {
            check(offer.method_19272().method_7947() == 1, "cured trade buy1 is discounted to 1 after close");
        }

        // 测试补货与需求激增后的价格：即使 demand 增加，治愈价格依然被压制在 1
        class_1914 firstTrade = cachedCured.method_8264().get(0);
        for (int i = 0; i < 5; i++) {
            firstTrade.method_8244(); // 模拟多次交易增加需求
        }
        VillagerWorkstationHelper.restock(cachedCured);
        check(firstTrade.method_19272().method_7947() == 1, "cured trade buy1 remains 1 after restock and demand surge");

        // 测试升级后新解锁的 2 级交易是否同样享有打折
        cachedCured.method_19625(10);
        VillagerTradePreviewHelper.advanceStoredVillager(cachedCured);
        check(cachedCured.method_7231().method_16925() == 2, "cured villager upgraded to level 2");
        for (class_1914 offer : cachedCured.method_8264()) {
            check(offer.method_19272().method_7947() == 1, "level 2 unlocked trade also has 1 emerald discount");
        }

        // 测试自动交易柜扣除的原料是否为打折后的数量（1 个而非原价）
        class_2338 autoTraderPos = new class_2338(11, 100, 0);
        server.method_30002().method_8652(autoTraderPos, ModBlocks.AUTO_TRADER_BLOCK.method_9564(), 3);
        AutoTraderBlockEntity autoTraderBe = (AutoTraderBlockEntity) server.method_30002().method_8321(autoTraderPos);
        class_1799 curedItem2 = traderBe.extractVillagerItem();
        autoTraderBe.setVillagerFromItem(curedItem2);
        class_1646 autoCached = autoTraderBe.getOrCreateCachedVillager();
        class_1914 autoOffer = autoCached.method_8264().get(0);
        check(autoOffer.method_19272().method_7947() == 1, "auto trader offer is discounted to 1");

        // 测试放出世界后其他玩家交互是否依然享受打折
        class_1799 extractedItem = autoTraderBe.extractVillagerItem();
        class_1646 worldVillager = class_1299.field_6077.method_5883(server.method_30002());
        worldVillager.method_5651(extractedItem.method_7969().method_10562(VillagerItem.ENTITY_TAG));
        TestPlayer player2 = new TestPlayer(server);
        ((VillagerPreviewAccess) worldVillager).easyVillagers$updatePrices(player2);
        for (class_1914 offer : worldVillager.method_8264()) {
            check(offer.method_19272().method_7947() == 1, "another player also enjoys 1 emerald discount in world");
        }


        for (String profession : new String[]{"none", "nitwit"}) {
            class_1646 empty = class_1299.field_6077.method_5883(server.method_30002());
            empty.method_7195(empty.method_7231().method_16921(class_7923.field_41195.method_10223(new class_2960("minecraft:" + profession))));
            check(VillagerTradePreviewHelper.buildPreviewOffers(empty).isEmpty(), profession + " has no generated trades");
        }
    }
}
