package org.dave.bonsaitrees.misc;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class HoppingMustBeEnabledConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        boolean value = JsonUtils.getBoolean(json , "value", true);
        return () -> ConfigurationHandler.GeneralSettings.disableHoppingBonsaiPot != value;
    }
}
