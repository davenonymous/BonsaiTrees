package com.davenonymous.bonsaitrees3.libnonymous.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseDataProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();


	private final DataGenerator generator;
	private final Type type;

	private Map<String, JsonObject> values = new HashMap<>();

	public abstract void addValues();

	public abstract String getModId();

	public void add(String path, JsonObject value) {
		this.values.put(path, value);
	}

	public enum Type {
		ASSETS, DATA
	}

	;

	public float getRounded(double d) {
		var df = new DecimalFormat("#.##");
		var symbols = df.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);

		return Float.parseFloat(df.format(d));
	}

	public BaseDataProvider(DataGenerator generator, Type type) {
		this.type = type;
		this.generator = generator;
	}


	@Override
	public void run(HashCache cache) throws IOException {
		addValues();

		values.forEach((s, jsonObject) -> {
			saveValue(cache, s, jsonObject);
		});
	}

	private void saveValue(HashCache cache, String key, JsonObject jsonObject) {
		Path mainOutput = generator.getOutputFolder();
		String pathSuffix = (type == Type.ASSETS ? "assets" : "data") + "/" + getModId() + "/" + key + ".json";

		Path outputPath = mainOutput.resolve(pathSuffix);
		try {
			DataProvider.save(GSON, cache, jsonObject, outputPath);
		} catch (IOException e) {
			LOGGER.error("Couldn't save {} to {}", getName(), outputPath, e);
		}
	}
}