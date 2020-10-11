package com.davenonymous.bonsaitrees2.misc;

import com.davenonymous.bonsaitrees2.setup.Registration;
import com.davenonymous.libnonymous.misc.ColorProperty;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PotColorizer {
    public static final DyeColor DEFAULT_COLOR = DyeColor.LIGHT_GRAY;

    @SubscribeEvent
    public static void init(ColorHandlerEvent.Block event) {
        final IBlockColor potColorHandler = (state, blockAccess, pos, tintIndex) -> {
            if(!state.hasProperty(ColorProperty.COLOR)) {
                return PotColorizer.DEFAULT_COLOR.getColorValue();
            }

            int color = state.get(ColorProperty.COLOR);
            int rgb = DyeColor.byId(color).getColorValue();
            return rgb;
        };

        event.getBlockColors().register(potColorHandler, Registration.BONSAIPOT.get());
        event.getBlockColors().register(potColorHandler, Registration.HOPPING_BONSAIPOT.get());
    }

    @SubscribeEvent
    public static void init(ColorHandlerEvent.Item event) {
        final IItemColor potColorHandler = (stack, tintIndex) -> {
            if(!stack.hasTag()) {
                return PotColorizer.DEFAULT_COLOR.getColorValue();
            }

            CompoundNBT tag = stack.getTag();
            if(!tag.contains("bonsaitrees2:color")) {
                return PotColorizer.DEFAULT_COLOR.getColorValue();
            }

            int color = tag.getInt("bonsaitrees2:color");
            int rgb = DyeColor.byId(color).getColorValue();
            return rgb;
        };

        event.getItemColors().register(potColorHandler, Registration.BONSAIPOT.get());
        event.getItemColors().register(potColorHandler, Registration.HOPPING_BONSAIPOT.get());
    }
}
