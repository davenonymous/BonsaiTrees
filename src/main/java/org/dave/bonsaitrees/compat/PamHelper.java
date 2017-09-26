package org.dave.bonsaitrees.compat;

import com.pam.harvestcraft.blocks.growables.BlockPamSapling;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PamHelper {
    public static Object reflectPamSapling(BlockPamSapling obj, String field) {
        return ReflectionHelper.getPrivateValue(BlockPamSapling.class, obj, field);
    }
}
