package net.fengye.easyvillagers.util;

import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1914;
import net.minecraft.class_1915;
import net.minecraft.class_1916;
import net.minecraft.class_3414;

/** 一次开窗的商人适配器；屏幕两端索引完全一致，原村民只存真实交易。 */
final class PreviewMerchant implements class_1915 {
    private final class_1646 villager;
    private final class_1916 offers;
    private final Runnable onChange;

    PreviewMerchant(class_1646 villager, class_1916 offers, Runnable onChange) {
        this.villager = villager;
        this.offers = offers;
        this.onChange = onChange;
    }

    @Override public void method_8259(class_1657 player) {
        villager.method_8259(player);
        if (player == null) onChange.run();
    }
    @Override public class_1657 method_8257() { return villager.method_8257(); }
    @Override public class_1916 method_8264() { return offers; }
    @Override public void method_8261(class_1916 ignored) {
        throw new UnsupportedOperationException("预览商人不接受客户端覆盖交易列表");
    }
    @Override public void method_8262(class_1914 offer) {
        if (!VillagerTradePreviewHelper.isLockedOffer(offer) && villager.method_8264().contains(offer)) {
            villager.method_8262(offer);
            onChange.run();
        }
    }
    @Override public void method_8258(class_1799 stack) { villager.method_8258(stack); }
    @Override public int method_19269() { return villager.method_19269(); }
    @Override public void method_19271(int experience) {
        villager.method_19271(experience);
        onChange.run();
    }
    @Override public boolean method_19270() { return villager.method_19270(); }
    @Override public class_3414 method_18010() { return villager.method_18010(); }
    @Override public boolean method_20708() { return villager.method_20708(); }
    @Override public boolean method_38069() { return villager.method_38069(); }
}
