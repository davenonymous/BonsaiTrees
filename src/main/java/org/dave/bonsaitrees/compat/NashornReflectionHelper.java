package org.dave.bonsaitrees.compat;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class NashornReflectionHelper {
    public static Object getPrivateValue(Class classs, Object obj, String field) {
        return ReflectionHelper.getPrivateValue(classs, obj, field);
    }
}
