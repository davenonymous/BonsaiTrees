package org.dave.bonsaitrees.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.dave.bonsaitrees.compat.CraftTweaker2.CraftTweakerCompatibility;
import org.dave.bonsaitrees.compat.TheOneProbe.TopCompatibility;
import org.dave.bonsaitrees.utility.Logz;

public class CompatHandler {
    public static void registerCompat() {
        registerTheOneProbe();
        registerWaila();
        registerCraftTweaker();
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
            FMLInterModComms.sendMessage("waila", "register", "org.dave.bonsaitrees.compat.Waila.WailaProvider.register");
        }
    }

    private static void registerCraftTweaker() {
        if (Loader.isModLoaded("crafttweaker")) {
            Logz.info("Trying to tell CraftTweaker2 about us");
            CraftTweakerCompatibility.register();
        }
    }
}
