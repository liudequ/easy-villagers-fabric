package net.fengye.easyvillagers.smoke;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public final class ClientMixinSmoke implements PreLaunchEntrypoint {
    @Override public void onPreLaunch() {
        try {
            Class.forName("net.minecraft.class_492", false, getClass().getClassLoader());
            System.out.println("[EV-CLIENT-MIXIN PASS] MerchantScreen transformed and verified");
            System.exit(0);
        } catch (Throwable error) {
            error.printStackTrace();
            System.exit(1);
        }
    }
}
