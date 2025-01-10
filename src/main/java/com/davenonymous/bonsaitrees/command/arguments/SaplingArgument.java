package com.davenonymous.bonsaitrees.command.arguments;

import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SaplingArgument implements ArgumentType<ResourceLocation> {
	private static final List<String> EXAMPLES = Arrays.asList("minecraft:oak_sapling", "minecraft:acacia_sapling");

	private SaplingArgument() {
	}

	public static SaplingArgument saplingArgument() {
		return new SaplingArgument();
	}

	@Override
	public ResourceLocation parse(final StringReader reader) throws CommandSyntaxException {
		return ResourceLocation.read(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(
			BonsaiCache.BONSAI_BY_ITEM.keySet().stream()
				.map(BuiltInRegistries.ITEM::getKey)
				.map(ResourceLocation::toString),
			builder
		);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
