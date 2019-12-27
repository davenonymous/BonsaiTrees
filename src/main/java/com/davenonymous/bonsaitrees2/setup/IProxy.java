package com.davenonymous.bonsaitrees2.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {

    void init();
    void initClientSetup();

    World getClientWorld();

    PlayerEntity getClientPlayer();
}