package com.davenonymous.bonsaitrees3.libnonymous.serialization.nbt;


import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;

public class NBTFieldSerializationData {
	public NBTFieldHandlers.NbtReader reader;
	public NBTFieldHandlers.NbtWriter writer;
	public Field field;
	public String key;
	public boolean storeWithItem;
	public boolean sendInUpdatePackage;

	public NBTFieldSerializationData(Field field, String key, boolean storeWithItem, boolean sendInUpdatePackage) {
		this.field = field;
		this.key = key;
		this.storeWithItem = storeWithItem;
		this.sendInUpdatePackage = sendInUpdatePackage;

		Pair<NBTFieldHandlers.NbtReader, NBTFieldHandlers.NbtWriter> pair = NBTFieldHandlers.getNBTHandler(field.getType());
		this.reader = pair.getLeft();
		this.writer = pair.getRight();
	}
}
