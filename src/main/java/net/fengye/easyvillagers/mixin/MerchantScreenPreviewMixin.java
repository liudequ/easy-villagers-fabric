package net.fengye.easyvillagers.mixin;

import net.fengye.easyvillagers.util.VillagerTradePreviewHelper;
import net.minecraft.class_1661;
import net.minecraft.class_1728;
import net.minecraft.class_1914;
import net.minecraft.class_1916;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_465;
import net.minecraft.class_492;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_492.class, remap = false)
public abstract class MerchantScreenPreviewMixin extends class_465<class_1728> {
    @Shadow int field_19163; // 首个可见交易的索引

    protected MerchantScreenPreviewMixin(class_1728 handler, class_1661 inventory, class_2561 title) {
        super(handler, inventory, title);
    }

    @Inject(method = "method_25394", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/class_492;method_2380(Lnet/minecraft/class_332;II)V"))
    private void easyVillagers$shadeLockedRows(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        class_1916 offers = this.field_2797.method_17438();
        context.method_51448().method_22903();
        // 商品绘制在 z=100，工具提示在更高层；灰色遮罩不遮挡附魔/等级提示。
        context.method_51448().method_46416(0.0f, 0.0f, 110.0f);
        for (int row = 0; row < 7 && field_19163 + row < offers.size(); row++) {
            class_1914 offer = offers.get(field_19163 + row);
            if (!VillagerTradePreviewHelper.isLockedOffer(offer)) continue;
            int x = this.field_2776 + 5;
            int y = this.field_2800 + 17 + row * 20;
            context.method_25294(x, y, x + 88, y + 20, 0x66555555);
        }
        context.method_51448().method_22909();
    }
}
