package org.dave.bonsaitrees.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.block.BlockBonsaiPot;
import org.dave.bonsaitrees.init.Blockss;
import org.dave.bonsaitrees.tile.TileBonsaiPot;

@Mod.EventBusSubscriber
public class CommonProxy {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BlockBonsaiPot(Material.WOOD).setUnlocalizedName("bonsaipot").setRegistryName(BonsaiTrees.MODID, "bonsaipot"));
        GameRegistry.registerTileEntity(TileBonsaiPot.class, "TileBonsaiPot");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(Blockss.bonsaiPot).setRegistryName(Blockss.bonsaiPot.getRegistryName()));
    }

    public void preInit(FMLPreInitializationEvent event) {
        // CompatHandler.registerCompat();
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
