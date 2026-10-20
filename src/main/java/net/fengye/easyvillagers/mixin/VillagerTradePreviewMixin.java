package net.fengye.easyvillagers.mixin;

import net.fengye.easyvillagers.util.VillagerPreviewAccess;
import net.fengye.easyvillagers.util.VillagerTradePreviewHelper;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1916;
import net.minecraft.class_2487;
import net.minecraft.class_3850;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_1646.class, remap = false)
public abstract class VillagerTradePreviewMixin implements VillagerPreviewAccess {
    @Unique private class_2487 easyVillagers$previewData = new class_2487();

    @Override
    public class_2487 easyVillagers$getPreviewData() {
        return easyVillagers$previewData;
    }

    @Override
    public void easyVillagers$setPreviewData(class_2487 data) {
        easyVillagers$previewData = data;
    }

    @Override @Invoker("method_7237")
    public abstract void easyVillagers$fillRecipes();

    @Override @Invoker("method_19192")
    public abstract void easyVillagers$updatePrices(class_1657 player);

    @Override @Invoker("method_16918")
    public abstract void easyVillagers$levelUp();

    @Inject(method = "method_5652", at = @At("TAIL"))
    private void easyVillagers$writePreview(class_2487 nbt, CallbackInfo ci) {
        if (!easyVillagers$previewData.method_33133()) {
            nbt.method_10566("EasyVillagersTradePreview", easyVillagers$previewData.method_10553());
        }
    }

    @Inject(method = "method_5749", at = @At("TAIL"))
    private void easyVillagers$readPreview(class_2487 nbt, CallbackInfo ci) {
        easyVillagers$previewData = nbt.method_10562("EasyVillagersTradePreview").method_10553();
    }

    @Inject(method = "method_7195", at = @At("HEAD"))
    private void easyVillagers$invalidateProfession(class_3850 data, CallbackInfo ci) {
        class_1646 villager = (class_1646) (Object) this;
        class_3850 previous = villager.method_7231();
        if (previous.method_16924() != data.method_16924() || previous.method_16919() != data.method_16919()) {
            easyVillagers$previewData = new class_2487();
        }
    }

    // 原版升级时直接兑现已预览词条，而不是重新随机生成另一组。
    @Inject(method = "method_7237", at = @At("HEAD"), cancellable = true)
    private void easyVillagers$usePredictedOffers(CallbackInfo ci) {
        class_1646 villager = (class_1646) (Object) this;
        if (!VillagerTradePreviewHelper.matchesProfession(villager, easyVillagers$previewData)) return;
        String key = "Level" + villager.method_7231().method_16925();
        if (easyVillagers$previewData.method_10573(key, 10)) {
            villager.method_8264().addAll(new class_1916(easyVillagers$previewData.method_10562(key)));
            ci.cancel();
        }
    }

    // 只替换原版已经通过存活、成年、非忙碌等校验后的开窗步骤。
    @Inject(method = "method_19191", at = @At("HEAD"), cancellable = true)
    private void easyVillagers$openPreview(class_1657 player, CallbackInfo ci) {
        VillagerTradePreviewHelper.openTradingScreenWithPreview(player, (class_1646) (Object) this);
        ci.cancel();
    }
}
