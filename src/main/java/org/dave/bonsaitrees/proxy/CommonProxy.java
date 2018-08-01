package org.dave.bonsaitrees.proxy;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.block.BlockBonsaiPot;
import org.dave.bonsaitrees.compat.CompatHandler;
import org.dave.bonsaitrees.init.Blockss;
import org.dave.bonsaitrees.init.Triggerss;
import org.dave.bonsaitrees.item.ItemBlockPonsaiPot;
import org.dave.bonsaitrees.tile.TileBonsaiPot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber
public class CommonProxy {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BlockBonsaiPot(Material.CLAY).setUnlocalizedName("bonsaipot").setRegistryName(BonsaiTrees.MODID, "bonsaipot"));
        GameRegistry.registerTileEntity(TileBonsaiPot.class, "TileBonsaiPot");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlockPonsaiPot(Blockss.bonsaiPot).setRegistryName(Blockss.bonsaiPot.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        // TODO: Maybe add colorization recipes?
        //event.getRegistry().register(new BonsaiPotRecipe().setRegistryName(BonsaiTrees.MODID, "bonsaipot"));
    }

    void registerTriggers() {
        Method method;
        try {
            method = ReflectionHelper.findMethod(CriteriaTriggers.class, "register", "func_192118_a", ICriterionTrigger.class);
            method.setAccessible(true);
            method.invoke(null, Triggerss.GROW_TREE);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void preInit(FMLPreInitializationEvent event) {
        CompatHandler.registerCompat();
    }

    public void init(FMLInitializationEvent event) {
        registerTriggers();
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
