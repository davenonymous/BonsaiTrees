package org.dave.bonsaitrees.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.dave.bonsaitrees.utility.Logz;

public class CompatHandler {
    public static void registerCompat() {
        registerTheOneProbe();
        registerWaila();
    }

    private static void registerTheOneProbe() {
        if (Loader.isModLoaded("theoneprobe")) {
            Logz.info("Trying to tell The One Probe about us");
            TopCompatibility.register();
        }
    }

    private static void registerWaila() {
        if (Loader.isModLoaded("waila")) {
            Logz.info("Trying to tell Waila about us");
            FMLInterModComms.sendMessage("waila", "register", "org.dave.bonsaitrees.compat.WailaProvider.register");
        }
    }
}
