package com.davenonymous.bonsaitrees3.libnonymous.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class TeleporterTools {
	public static void teleport(ServerPlayer entity, ServerLevel destination, BlockPos pos, boolean findTop) {
		entity.changeDimension(destination, new ITeleporter() {
			@Override
			public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
				entity = repositionEntity.apply(false);
				int y = pos.getY();
				if(findTop) {
					y = destination.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
				}
				entity.teleportTo(pos.getX(), y, pos.getZ());
				return entity;
			}
		});
	}
}