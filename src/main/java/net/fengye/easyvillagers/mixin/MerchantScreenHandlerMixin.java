package net.fengye.easyvillagers.mixin;

import net.fengye.easyvillagers.util.PreviewMerchant;
import net.minecraft.class_1297;
import net.minecraft.class_1646;
import net.minecraft.class_1728;
import net.minecraft.class_1915;
import net.minecraft.class_3419;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_1728.class, remap = false)
public abstract class MerchantScreenHandlerMixin {
    @Shadow @Final private class_1915 field_7863;

    @Inject(method = "method_20595", at = @At("HEAD"), cancellable = true)
    private void easyVillagers$safePlayYesSound(CallbackInfo ci) {
        if (this.field_7863 instanceof PreviewMerchant preview) {
            class_1646 villager = preview.getVillager();
            if (villager != null && !villager.method_38069()) {
                villager.method_37908().method_8486(
                    villager.method_23317(),
                    villager.method_23318(),
                    villager.method_23321(),
                    this.field_7863.method_18010(),
                    class_3419.field_15254,
                    1.0f,
                    1.0f,
                    false
                );
            }
            ci.cancel();
        } else if (!(this.field_7863 instanceof class_1297)) {
            ci.cancel();
        }
    }
}
