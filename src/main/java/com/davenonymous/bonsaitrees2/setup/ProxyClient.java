package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.block.BonsaiPotTileEntity;
import com.davenonymous.bonsaitrees2.block.BonsaiPotTileEntityRenderer;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.gui.TreeCreatorScreen;
import com.davenonymous.bonsaitrees2.render.TreeModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ProxyClient implements IProxy {
    @Override
    public void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(BonsaiPotTileEntity.class, new BonsaiPotTileEntityRenderer());
        TreeModels.init();
        ScreenManager.registerFactory(ModObjects.TREE_CREATOR_CONTAINER, TreeCreatorScreen::new);
    }

    @Override
    public void initClientSetup() {
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
