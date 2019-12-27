package com.davenonymous.bonsaitrees2.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ProxyServer implements IProxy {
    @Override
    public void init() {

    }

    @Override
    public void initClientSetup() {
        throw new IllegalStateException("Only run this on the client!");
    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Only run this on the client!");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Only run this on the client!");
    }
}
