package com.davenonymous.bonsaitrees.client.multiblock;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.NotNull;

public class MultiBlockModelLoader implements IGeometryLoader<MultiBlockGeometry> {
	public static final MultiBlockModelLoader INSTANCE = new MultiBlockModelLoader();
	public static final ResourceLocation ID = BonsaiTrees.resource("multiblockmodel");

	private MultiBlockModelLoader() {
	}

	@Override
	public @NotNull MultiBlockGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
		return MultiBlockGeometry.CODEC.codec().parse(JsonOps.INSTANCE, jsonObject).result().orElse(MultiBlockGeometry.EMPTY);
	}
}