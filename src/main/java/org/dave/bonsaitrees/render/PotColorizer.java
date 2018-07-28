package org.dave.bonsaitrees.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.bonsaitrees.init.Blockss;
import org.dave.bonsaitrees.tile.TileBonsaiPot;

public class PotColorizer {
    public static final EnumDyeColor DEFAULT_COLOR = EnumDyeColor.SILVER;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void init(ColorHandlerEvent.Block event) {
        final IBlockColor potColorHandler = (state, blockAccess, pos, tintIndex) -> {
            TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
            if(te == null || !(te instanceof TileBonsaiPot)) {
                return PotColorizer.DEFAULT_COLOR.getColorValue();
            }

            TileBonsaiPot bonsaiPot = (TileBonsaiPot)te;
            return bonsaiPot.getColor().getColorValue();
        };

        event.getBlockColors().registerBlockColorHandler(potColorHandler, Blockss.bonsaiPot);

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void init(ColorHandlerEvent.Item event) {
        final IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
            if(!stack.hasTagCompound()) {
                return PotColorizer.DEFAULT_COLOR.getColorValue();
            }

            NBTTagCompound stackTag = stack.getTagCompound();
            if(!stackTag.hasKey("color")) {
                return PotColorizer.DEFAULT_COLOR.getColorValue();
            }

            EnumDyeColor color = EnumDyeColor.byMetadata(stackTag.getInteger("color"));
            return color.getColorValue();
        };

        event.getItemColors().registerItemColorHandler(itemBlockColourHandler, Blockss.bonsaiPot);
    }

    public static TextFormatting textFormattingForDye(EnumDyeColor color) {
        switch (color) {
            case WHITE:
                return TextFormatting.WHITE;
            case ORANGE:
                return TextFormatting.GOLD;
            case MAGENTA:
                return TextFormatting.DARK_PURPLE;
            case LIGHT_BLUE:
                return TextFormatting.DARK_AQUA;
            case YELLOW:
                return TextFormatting.YELLOW;
            case LIME:
                return TextFormatting.GREEN;
            case PINK:
                return TextFormatting.LIGHT_PURPLE;
            case GRAY:
                return TextFormatting.DARK_GRAY;
            case SILVER:
                return TextFormatting.GRAY;
            case CYAN:
                return TextFormatting.AQUA;
            case PURPLE:
                return TextFormatting.DARK_PURPLE;
            case BLUE:
                return TextFormatting.BLUE;
            case BROWN:
                return TextFormatting.GOLD;
            case GREEN:
                return TextFormatting.DARK_GREEN;
            case RED:
                return TextFormatting.DARK_RED;
            case BLACK:
                return TextFormatting.DARK_GRAY;
            default:
                return TextFormatting.WHITE;
        }
    }
}
