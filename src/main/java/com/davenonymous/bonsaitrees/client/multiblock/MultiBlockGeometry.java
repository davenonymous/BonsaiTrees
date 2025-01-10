package com.davenonymous.bonsaitrees.client.multiblock;

import com.davenonymous.bonsaitrees.multiblock.MultiBlockGeometryBase;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class MultiBlockGeometry extends MultiBlockGeometryBase implements IUnbakedGeometry<MultiBlockGeometry> {
	public static final MultiBlockGeometry EMPTY = new MultiBlockGeometry(0, Collections.emptyMap(), Collections.emptyList(), 1, 0);
	public static final MapCodec<MultiBlockGeometry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("version").forGetter(MultiBlockGeometry::version),
		Codec.unboundedMap(Codec.STRING, BlockState.CODEC).fieldOf("ref").forGetter(MultiBlockGeometry::ref),
		Codec.list(Codec.list(Codec.STRING)).fieldOf("shape").forGetter(MultiBlockGeometry::shape),
		Codec.INT.optionalFieldOf("scaleToBlocks", 1).forGetter(MultiBlockGeometry::scaleToBlocks),
		Codec.INT.optionalFieldOf("lightEmission", 0).forGetter(MultiBlockGeometry::lightEmission)
	).apply(instance, MultiBlockGeometry::new));

	public static final StreamCodec<ByteBuf, MultiBlockGeometry> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, MultiBlockGeometry::version,
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.fromCodec(BlockState.CODEC)), MultiBlockGeometry::ref,
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()), MultiBlockGeometry::shape,
		ByteBufCodecs.INT, MultiBlockGeometry::scaleToBlocks,
		ByteBufCodecs.INT, MultiBlockGeometry::lightEmission,
		MultiBlockGeometry::new
	);

	public MultiBlockGeometry(int scaleToBlocks, int version, Map<BlockPos, Voxel> blocks, int lightEmission) {
		super(scaleToBlocks, version, blocks, lightEmission);
	}

	public MultiBlockGeometry(int version, Map<String, BlockState> ref, List<List<String>> shape, int scaleToBlocks, int lightEmission) {
		super(version, ref, shape, scaleToBlocks, lightEmission);
	}

	@Override
	public BakedModel bake(IGeometryBakingContext iGeometryBakingContext, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides itemOverrides) {
		return new MultiBlockModel(this);
	}
}
