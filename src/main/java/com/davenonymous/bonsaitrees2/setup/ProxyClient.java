package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.BonsaiPotTileEntityRenderer;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.config.Config;
import com.davenonymous.bonsaitrees2.gui.TreeCreatorScreen;
import com.davenonymous.bonsaitrees2.render.TreeModels;
import com.davenonymous.libnonymous.gui.config.WidgetGuiConfig;
import com.davenonymous.libnonymous.setup.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ProxyClient implements IProxy {
    @Override
    public void init() {
        ClientRegistry.bindTileEntityRenderer(ModObjects.BONSAIPOT_TILE, tileEntityRendererDispatcher -> new BonsaiPotTileEntityRenderer(tileEntityRendererDispatcher));
        ClientRegistry.bindTileEntityRenderer(ModObjects.HOPPING_BONSAIPOT_TILE, tileEntityRendererDispatcher -> new BonsaiPotTileEntityRenderer(tileEntityRendererDispatcher));

        TreeModels.init();
        ScreenManager.registerFactory(ModObjects.TREE_CREATOR_CONTAINER, TreeCreatorScreen::new);

        ModList.get().getModContainerById(BonsaiTrees2.MODID).ifPresent(c -> c.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, parent) -> {
            return new WidgetGuiConfig(parent, Config.COMMON_CONFIG, Config.CLIENT_CONFIG);
        }));
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
