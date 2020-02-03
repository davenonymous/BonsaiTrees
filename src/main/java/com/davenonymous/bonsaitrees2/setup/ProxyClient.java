package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.BonsaiPotTileEntityRenderer;
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
        ClientRegistry.bindTileEntityRenderer(Registration.BONSAIPOT_TILE.get(), tileEntityRendererDispatcher -> new BonsaiPotTileEntityRenderer(tileEntityRendererDispatcher));
        ClientRegistry.bindTileEntityRenderer(Registration.HOPPING_BONSAIPOT_TILE.get(), tileEntityRendererDispatcher -> new BonsaiPotTileEntityRenderer(tileEntityRendererDispatcher));

        TreeModels.init();
        ScreenManager.registerFactory(Registration.TREE_CREATOR_CONTAINER.get(), TreeCreatorScreen::new);

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
