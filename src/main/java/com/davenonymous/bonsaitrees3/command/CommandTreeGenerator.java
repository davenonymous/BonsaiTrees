package com.davenonymous.bonsaitrees3.command;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.datagen.server.DatagenSaplings;
import com.davenonymous.libnonymous.commands.SimpleCommandReply;
import com.davenonymous.libnonymous.reflections.AbstractTreeGrowerReflection;
import com.davenonymous.libnonymous.reflections.SaplingBlockReflection;
import com.davenonymous.libnonymous.serialization.MultiblockBlockModel;
import com.davenonymous.libnonymous.utils.ComponentUtils;
import com.davenonymous.libnonymous.utils.TeleporterTools;
import com.davenonymous.bonsaitrees3.setup.Registration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Unit;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.command.ModIdArgument;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

public class CommandTreeGenerator implements Command<CommandSourceStack> {
	private static final CommandTreeGenerator CMD = new CommandTreeGenerator(false, false);
	private static final CommandTreeGenerator CMD_WITH_FILTER = new CommandTreeGenerator(true, false);
	private static final CommandTreeGenerator CMD_WITH_GROUND = new CommandTreeGenerator(true, true);

	public CommandTreeGenerator(boolean hasModFilter, boolean setsCustomGround) {
		this.hasModFilter = hasModFilter;
		this.setsCustomGround = setsCustomGround;
	}

	boolean hasModFilter;
	boolean setsCustomGround;

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext pContext) {
		return Commands.literal("generator").requires(cs -> cs.hasPermission(0))
				.executes(SimpleCommandReply.error("This command can be used to mass generate sapling and model files.\nIt should not be used on actual game worlds!\nIf you know what you are doing, continue by appending 'yes' to the command!"))
				.then(Commands.argument("confirm", StringArgumentType.word()).executes(CMD)
						.then(Commands.argument("mod", ModIdArgument.modIdArgument()).executes(CMD_WITH_FILTER)
								.then(Commands.argument("ground", BlockStateArgument.block(pContext)).executes(CMD_WITH_GROUND))
						));
	}

	private int doit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();

		if(!player.getLevel().dimension().equals(Registration.GROWTOWN)) {
			ServerLevel destination = player.getServer().getLevel(Registration.GROWTOWN);
			TeleporterTools.teleport(player, destination, BlockPos.ZERO, true);
			context.getSource().sendFailure(Component.literal("Wrong dimension. Needed to teleport you there first. Please run the command again."));
			context.getSource().sendFailure(Component.literal("To get back to another dimension run: /execute in <dimension> run teleport <coordinates>"));
			return 0;
		}

		var mc = Minecraft.getInstance();
		var generatorSaveDir = new File(mc.gameDirectory, "bonsai-generated");
		generatorSaveDir.delete();
		generatorSaveDir.mkdirs();

		var modelSaveDir = new File(generatorSaveDir, "models");
		var saplingSaveDir = new File(generatorSaveDir, "saplings");
		modelSaveDir.mkdirs();
		saplingSaveDir.mkdirs();

		Map<Item, ConfiguredFeature<?, ?>> saplingMap = new HashMap<>();

		int count = 0;
		int xOffset = 0;
		var saplingBlocks = ForgeRegistries.BLOCKS.getValues().stream().filter(b -> b instanceof SaplingBlock).map(b -> (SaplingBlock) b).toList();
		for(var saplingBlock : saplingBlocks) {
			if(hasModFilter) {
				var saplingMod = ForgeRegistries.BLOCKS.getKey(saplingBlock).getNamespace();
				var wantedMod = context.getArgument("mod", String.class);

				if(!saplingMod.equals(wantedMod)) {
					BonsaiTrees3.LOGGER.info("Skipping sapling: {}, wrong mod ({} != {})", saplingBlock, saplingMod, wantedMod);
					continue;
				}
			}

			context.getSource().sendSuccess(ComponentUtils.format("Found sapling block: %s", ForgeRegistries.BLOCKS.getKey(saplingBlock)), false);

			// Prepare the area for growth, i.e. clear the chunk, make sure the ground is the desired ground
			BlockPos growPos = new BlockPos(7 + xOffset, 4, 7);
			resetChunks(context.getSource(), growPos, 0, false);

			if(setsCustomGround) {
				var blockInfo = BlockStateArgument.getBlock(context, "ground");
				clearArea(player.getLevel(), xOffset, blockInfo.getState());
			} else {
				clearArea(player.getLevel(), xOffset);
			}


			// Actually grow the tree feature
			AbstractTreeGrower grower = SaplingBlockReflection.getTreeGrowerFromSaplingBlock(saplingBlock);
			grower.growTree(player.getLevel(), player.getLevel().getChunkSource().getGenerator(), growPos, player.getLevel().getBlockState(growPos), player.level.random);

			var feature = AbstractTreeGrowerReflection.getConfiguredFeature(grower, player.getRandom(), false);
			try {
				BonsaiTrees3.LOGGER.info("Looking at configured feature: {}", feature);
				if(feature != null && feature.config() instanceof TreeConfiguration tc) {
					BonsaiTrees3.LOGGER.info(" --> TreeConfiguration: {}", tc);
					var saplingItem = saplingBlock.asItem();
					saplingMap.put(saplingItem, feature);
					BonsaiTrees3.LOGGER.info(" --> Sapling: {}", saplingItem);

					// Clear the ground afterwards, so we have no contact to the ground
					for(int x = 0; x < 16; x++) {
						for(int z = 0; z < 16; z++) {
							player.getLevel().setBlock(new BlockPos(x + xOffset, 3, z), Blocks.AIR.defaultBlockState(), UPDATE_ALL);
							player.getLevel().setBlock(new BlockPos(x + xOffset, 2, z), Blocks.AIR.defaultBlockState(), UPDATE_ALL);
						}
					}

					// Flood fill to get the model
					var featureName = BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(feature).get().location();
					BonsaiTrees3.LOGGER.info(" --> Feature name: {}", featureName);
					MultiblockBlockModel model = new MultiblockBlockModel(new ResourceLocation(BonsaiTrees3.MODID, "sapling/" + featureName.getNamespace() + "/" + featureName.getPath()));
					model.setBlocksByFloodFill(context.getSource().getLevel(), growPos.above());

					// Write model to disk
					var modDir = new File(modelSaveDir, ForgeRegistries.BLOCKS.getKey(saplingBlock).getNamespace());
					modDir.mkdirs();

					var modelFile = new File(modDir, featureName.getPath() + ".json");
					try {
						var writer = new FileWriter(modelFile);
						writer.write(model.serializePretty());
						writer.close();

						count++;
					} catch (IOException e) {
						context.getSource().sendFailure(ComponentUtils.format("Unable to write model for: %s: %s", featureName, e.getLocalizedMessage()));
					}
				}
			} catch (Exception e) {
				BonsaiTrees3.LOGGER.info("Something went wrong during generation of: {}", ForgeRegistries.BLOCKS.getKey(saplingBlock).toString());
				BonsaiTrees3.LOGGER.error("{}", e.getLocalizedMessage());
				e.printStackTrace();
			}

			xOffset += 16;
		}

		// Use the already existing data generator to generate sapling jsons
		var dataGen = new DataGenerator(saplingSaveDir.toPath(), Collections.emptySet(), SharedConstants.getCurrentVersion(), true);
		dataGen.addProvider(true, new DatagenSaplings(dataGen) {
			@Override
			public void addValues() {
				for(Item saplingItem : saplingMap.keySet()) {
					var feature = saplingMap.get(saplingItem);
					addSapling(saplingItem, getAsTreeConfiguration(feature));
				}
			}
		});

		try {
			dataGen.run();
		} catch (IOException e) {
			BonsaiTrees3.LOGGER.error("IO Error during data gen: {}", e.getLocalizedMessage());
			e.printStackTrace();
		}

		context.getSource().sendSuccess(ComponentUtils.format("Done! Wrote %d sapling and model files", count), false);

		return 0;
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if(!context.getArgument("confirm", String.class).equals("yes")) {
			context.getSource().sendFailure(ComponentUtils.format("No confirmation received. Append 'yes' to the command if you know what you are doing!"));
			return 0;
		}

		return doit(context);
	}

	private static void clearArea(ServerLevel level, int xOffset) {
		clearArea(level, xOffset, Blocks.GRASS.defaultBlockState());
	}

	private static void clearArea(ServerLevel level, int xOffset, BlockState groundState) {
		for(int x = 0; x < 16; x++) {
			for(int y = 0; y < 32; y++) {
				for(int z = 0; z < 16; z++) {
					BlockState wanted = Blocks.AIR.defaultBlockState();
					if(y == 0) {
						wanted = Blocks.BEDROCK.defaultBlockState();
					}
					if(y == 1) {
						wanted = Blocks.DIRT.defaultBlockState();
					}
					if(y == 2) {
						wanted = groundState;
					}
					var pos = new BlockPos(x + xOffset, y, z);
					level.setBlock(pos, wanted, UPDATE_ALL);

				}
			}
		}
	}

	private static int resetChunks(CommandSourceStack p_183685_, BlockPos pos, int p_183686_, boolean p_183687_) {
		ServerLevel serverlevel = p_183685_.getLevel();
		ServerChunkCache serverchunkcache = serverlevel.getChunkSource();
		serverchunkcache.chunkMap.debugReloadGenerator();

		ChunkPos chunkpos = new ChunkPos(pos);
		int i = chunkpos.z - p_183686_;
		int j = chunkpos.z + p_183686_;
		int k = chunkpos.x - p_183686_;
		int l = chunkpos.x + p_183686_;

		for(int i1 = i; i1 <= j; ++i1) {
			for(int j1 = k; j1 <= l; ++j1) {
				ChunkPos chunkpos1 = new ChunkPos(j1, i1);
				LevelChunk levelchunk = serverchunkcache.getChunk(j1, i1, false);
				if(levelchunk != null && (!p_183687_ || !levelchunk.isOldNoiseGeneration())) {
					for(BlockPos blockpos : BlockPos.betweenClosed(chunkpos1.getMinBlockX(), serverlevel.getMinBuildHeight(), chunkpos1.getMinBlockZ(), chunkpos1.getMaxBlockX(), serverlevel.getMaxBuildHeight() - 1, chunkpos1.getMaxBlockZ())) {
						serverlevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 16);
					}
				}
			}
		}

		ProcessorMailbox<Runnable> processormailbox = ProcessorMailbox.create(Util.backgroundExecutor(), "worldgen-resetchunks");

		for(ChunkStatus chunkstatus : ImmutableList.of(ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.LIQUID_CARVERS, ChunkStatus.FEATURES)) {
			CompletableFuture<Unit> completablefuture = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, processormailbox::tell);

			for(int i2 = chunkpos.z - p_183686_; i2 <= chunkpos.z + p_183686_; ++i2) {
				for(int j2 = chunkpos.x - p_183686_; j2 <= chunkpos.x + p_183686_; ++j2) {
					ChunkPos chunkpos2 = new ChunkPos(j2, i2);
					LevelChunk levelchunk1 = serverchunkcache.getChunk(j2, i2, false);
					if(levelchunk1 != null && (!p_183687_ || !levelchunk1.isOldNoiseGeneration())) {
						List<ChunkAccess> list = Lists.newArrayList();
						int k2 = Math.max(1, chunkstatus.getRange());

						for(int l2 = chunkpos2.z - k2; l2 <= chunkpos2.z + k2; ++l2) {
							for(int i3 = chunkpos2.x - k2; i3 <= chunkpos2.x + k2; ++i3) {
								ChunkAccess chunkaccess = serverchunkcache.getChunk(i3, l2, chunkstatus.getParent(), true);
								ChunkAccess chunkaccess1;
								if(chunkaccess instanceof ImposterProtoChunk) {
									chunkaccess1 = new ImposterProtoChunk(((ImposterProtoChunk) chunkaccess).getWrapped(), true);
								} else if(chunkaccess instanceof LevelChunk) {
									chunkaccess1 = new ImposterProtoChunk((LevelChunk) chunkaccess, true);
								} else {
									chunkaccess1 = chunkaccess;
								}

								list.add(chunkaccess1);
							}
						}

						completablefuture = completablefuture.thenComposeAsync((p_183678_) -> {
							return chunkstatus.generate(processormailbox::tell, serverlevel, serverchunkcache.getGenerator(), serverlevel.getStructureManager(), serverchunkcache.getLightEngine(), (p_183691_) -> {
								throw new UnsupportedOperationException("Not creating full chunks here");
							}, list, true).thenApply((p_183681_) -> {
								if(chunkstatus == ChunkStatus.NOISE) {
									p_183681_.left().ifPresent((p_183671_) -> {
										Heightmap.primeHeightmaps(p_183671_, ChunkStatus.POST_FEATURES);
									});
								}

								return Unit.INSTANCE;
							});
						}, processormailbox::tell);
					}
				}
			}

			p_183685_.getServer().managedBlock(completablefuture::isDone);
		}

		for(int i4 = chunkpos.z - p_183686_; i4 <= chunkpos.z + p_183686_; ++i4) {
			for(int l1 = chunkpos.x - p_183686_; l1 <= chunkpos.x + p_183686_; ++l1) {
				ChunkPos chunkpos3 = new ChunkPos(l1, i4);
				LevelChunk levelchunk2 = serverchunkcache.getChunk(l1, i4, false);
				if(levelchunk2 != null && (!p_183687_ || !levelchunk2.isOldNoiseGeneration())) {
					for(BlockPos blockpos1 : BlockPos.betweenClosed(chunkpos3.getMinBlockX(), serverlevel.getMinBuildHeight(), chunkpos3.getMinBlockZ(), chunkpos3.getMaxBlockX(), serverlevel.getMaxBuildHeight() - 1, chunkpos3.getMaxBlockZ())) {
						serverchunkcache.blockChanged(blockpos1);
					}
				}
			}
		}

		return 1;
	}
}