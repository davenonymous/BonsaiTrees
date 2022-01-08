package com.davenonymous.bonsaitrees3.libnonymous.serialization.nbt;


import com.davenonymous.bonsaitrees3.libnonymous.helper.BlockStateSerializationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NBTFieldHandlers {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Class<?>, Pair<NbtReader, NbtWriter>> nbtHandlers = new HashMap<>();

	static {
		addNBTHandler(boolean[].class, (key, tag, original) -> {
			ListTag listNBT = tag.getList(key, Tag.TAG_SHORT);
			boolean[] result = new boolean[listNBT.size()];
			for(int i = 0; i < result.length; i++) {
				result[i] = listNBT.getShort(i) == 1;
			}

			return null;
		}, (key, booleans, tag) -> {
			ListTag listNBT = new ListTag();
			for(boolean b : booleans) {
				listNBT.add(ShortTag.valueOf(b ? (short) 1 : (short) 0));
			}

			tag.put(key, listNBT);
		});
		addNBTHandler(boolean.class, (key, tag, original) -> tag.getBoolean(key), (key, aBoolean, tag) -> tag.putBoolean(key, aBoolean));
		addNBTHandler(Boolean.class, (key, tag, original) -> tag.getBoolean(key), (key, aBoolean, tag) -> tag.putBoolean(key, aBoolean));

		addNBTHandler(int.class, (key, tag, original) -> tag.getInt(key), (key, val, tag) -> tag.putInt(key, val));
		addNBTHandler(Integer.class, (key, tag, original) -> tag.getInt(key), (key, integer, tag) -> tag.putInt(key, integer));

		addNBTHandler(float.class, (key, tag, original) -> tag.getFloat(key), (key, val, tag) -> tag.putFloat(key, val));
		addNBTHandler(Float.class, (key, tag, original) -> tag.getFloat(key), (key, val, tag) -> tag.putFloat(key, val));

		addNBTHandler(double.class, (key, tag, original) -> tag.getDouble(key), (key, val, tag) -> tag.putDouble(key, val));
		addNBTHandler(Double.class, (key, tag, original) -> tag.getDouble(key), (key, val, tag) -> tag.putDouble(key, val));

		addNBTHandler(long.class, (key, tag, original) -> tag.getLong(key), (key, val, tag) -> tag.putLong(key, val));
		addNBTHandler(Long.class, (key, tag, original) -> tag.getLong(key), (key, val, tag) -> tag.putLong(key, val));

		addNBTHandler(String.class, (key, tag, original) -> tag.contains(key) ? tag.getString(key) : null, (key, s, tag) -> {
			if(s != null) {
				tag.putString(key, s);
			}
		});

		// This is actually covered by INBTSerializable, but our class/interface iteration method is too strict about this.
		addNBTHandler(ItemStack.class, (key, tag, original) -> ItemStack.of(tag.getCompound(key)), (key, itemStack, tag) -> tag.put(key, itemStack.serializeNBT()));

		addNBTHandler(ItemStackHandler.class, (key, tag, original) -> {
			original.deserializeNBT(tag.getCompound(key));
			return original;
		}, (key, itemStackHandler, tag) -> {
			tag.put(key, itemStackHandler.serializeNBT());
		});

		addNBTHandler(Enum.class, ((key, tag, original) -> {
			CompoundTag enumTag = tag.getCompound(key);
			try {
				Class clz = Class.forName(enumTag.getString("class"));
				return Enum.valueOf(clz, enumTag.getString("value"));
			} catch (ClassNotFoundException e) {
				LOGGER.warn("Could not find enum '{}' during NBT deserialization", tag.getString(key));
				e.printStackTrace();
			}
			return null;
		}), (key, anEnum, tag) -> {
			CompoundTag result = new CompoundTag();
			result.putString("class", anEnum.getClass().getName());
			result.putString("value", anEnum.name());

			tag.put(key, result);
		});

		addNBTHandler(Class.class, (key, tag, original) -> {
			if(key.equals("") || !tag.contains(key)) {
				return null;
			}

			try {
				return Class.forName(tag.getString(key));
			} catch (ClassNotFoundException e) {
				LOGGER.warn("Could not find class '{}' during NBT deserialization", tag.getString(key));
				e.printStackTrace();
			}
			return null;
		}, (key, aClass, tag) -> {
			if(aClass != null) {
				tag.putString(key, aClass.getName());
			}
		});

		addNBTHandler(ResourceLocation.class, (key, tag, original) -> {
			if(!tag.contains(key)) {
				return null;
			}

			return new ResourceLocation(tag.getString(key));
		}, (key, resourceLocation, tag) -> {
			if(resourceLocation == null) {
				return;
			}

			tag.putString(key, resourceLocation.toString());
		});

		addNBTHandler(BlockPos.class, (key, tag, original) -> {
			CompoundTag container = tag.getCompound(key);
			return new BlockPos(container.getInt("x"), container.getInt("y"), container.getInt("z"));
		}, (key, pos, tag) -> {
			CompoundTag container = new CompoundTag();
			container.putInt("x", pos.getX());
			container.putInt("y", pos.getY());
			container.putInt("z", pos.getZ());
			tag.put(key, container);
		});

		addNBTHandler(BlockState.class, (key, tag, original) -> BlockStateSerializationHelper.deserializeBlockState(tag.getCompound(key)), (key, blockState, tag) -> tag.put(key, BlockStateSerializationHelper.serializeBlockStateToNBT(blockState)));

		addNBTHandler(UUID.class, (key, tag, original) -> {
			if(!tag.contains(key)) {
				return null;
			}

			CompoundTag containerTag = tag.getCompound(key);
			return containerTag.getUUID("");
		}, (key, uuid, tag) -> {
			if(uuid == null) {
				return;
			}

			CompoundTag containerTag = new CompoundTag();
			containerTag.putUUID("", uuid);
			tag.put(key, containerTag);
		});

		addNBTHandler(INBTSerializable.class, (key, tag, original) -> {
			CompoundTag containerTag = tag.getCompound(key);
			String className = containerTag.getString("class");
			try {
				Class<?> clz = Class.forName(className);
				INBTSerializable obj = (INBTSerializable) clz.getConstructor().newInstance();
				obj.deserializeNBT(containerTag.getCompound("data"));
				return obj;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			return null;
		}, (key, INBTSerializable, tag) -> {
			CompoundTag containerTag = new CompoundTag();
			containerTag.putString("class", INBTSerializable.getClass().getName());
			containerTag.put("data", INBTSerializable.serializeNBT());
			tag.put(key, containerTag);
		});

		addNBTHandler(Map.class, (key, tag, original) -> {
			CompoundTag containerTag = tag.getCompound(key);
			if(!containerTag.contains("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.contains("entries")) {
				return new HashMap();
			}

			Map result = new HashMap();
			try {
				Class keyClass = Class.forName(containerTag.getString("keyClass"));
				if(!hasNBTHandler(keyClass)) {
					LOGGER.warn("No NBT deserialization methods for keys in map (type='{}') exists.", keyClass);
					return new HashMap();
				}

				Class valueClass = Class.forName(containerTag.getString("valueClass"));
				if(!hasNBTHandler(valueClass)) {
					LOGGER.warn("No NBT deserialization methods for values in map (type='{}') exists.", valueClass);
					return new HashMap();
				}

				NbtReader keyReader = getNBTHandler(keyClass).getLeft();
				NbtReader valueReader = getNBTHandler(valueClass).getLeft();

				for(Tag baseTag : containerTag.getList("entries", Tag.TAG_COMPOUND)) {
					CompoundTag entry = (CompoundTag) baseTag;
					Object keyObject = keyReader.read("key", entry, original);
					Object valueObject = valueReader.read("value", entry, original);

					result.put(keyObject, valueObject);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			return result;
		}, (key, map, tag) -> {
			CompoundTag containerTag = new CompoundTag();
			containerTag.putBoolean("isEmpty", map.isEmpty());

			if(!map.isEmpty()) {
				Class keyClass = map.keySet().toArray()[0].getClass();
				if(!hasNBTHandler(keyClass)) {
					LOGGER.warn("No NBT deserialization methods for keys in map (type='{}') exists.", keyClass);
					return;
				}

				Class valueClass = map.values().toArray()[0].getClass();
				if(!hasNBTHandler(valueClass)) {
					LOGGER.warn("No NBT deserialization methods for values in map (type='{}') exists.", valueClass);
					return;
				}

				containerTag.putString("keyClass", keyClass.getName());
				containerTag.putString("valueClass", valueClass.getName());

				NbtWriter keyWriter = getNBTHandler(keyClass).getRight();
				NbtWriter valueWriter = getNBTHandler(valueClass).getRight();

				ListTag data = new ListTag();
				for(Object e : map.entrySet()) {
					CompoundTag entryTag = new CompoundTag();
					Map.Entry entry = (Map.Entry) e;

					keyWriter.write("key", entry.getKey(), entryTag);
					valueWriter.write("value", entry.getValue(), entryTag);

					data.add(entryTag);
				}

				containerTag.put("entries", data);
			}

			tag.put(key, containerTag);
		});

		addNBTHandler(List.class, (key, tag, original) -> {
			List result = new ArrayList();
			CompoundTag containerTag = tag.getCompound(key);
			if(!containerTag.contains("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.contains("values")) {
				return result;
			}

			try {
				Class valueClass = Class.forName(containerTag.getString("valueClass"));
				if(!hasNBTHandler(valueClass)) {
					LOGGER.warn("No NBT deserialization methods for values in list (type='{}') exists.", valueClass);
					return result;
				}

				NbtReader reader = getNBTHandler(valueClass).getLeft();
				for(Tag baseTag : containerTag.getList("values", Tag.TAG_COMPOUND)) {
					CompoundTag entry = (CompoundTag) baseTag;
					Object value = reader.read("data", entry, original);
					result.add(value);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			return result;
		}, (key, list, tag) -> {
			CompoundTag containerTag = new CompoundTag();
			containerTag.putBoolean("isEmpty", list.isEmpty());

			if(!list.isEmpty()) {
				Class valueClass = list.get(0).getClass();
				if(!hasNBTHandler(valueClass)) {
					LOGGER.warn("No NBT serialization methods for values in list (type='{}') exists.", valueClass.getName());
					return;
				}

				containerTag.putString("valueClass", valueClass.getName());

				NbtWriter writer = getNBTHandler(valueClass).getRight();
				ListTag data = new ListTag();
				for(Object e : list) {
					CompoundTag entryContainerTag = new CompoundTag();
					writer.write("data", e, entryContainerTag);
					data.add(entryContainerTag);
				}
				containerTag.put("values", data);
			}

			tag.put(key, containerTag);
		});

		addNBTHandler(Queue.class, (key, tag, original) -> {
			CompoundTag containerTag = tag.getCompound(key);
			if(!containerTag.contains("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.contains("values")) {
				return new ArrayDeque<>();
			}

			Queue result = new ArrayDeque<>();
			try {

				Class valueClass = Class.forName(containerTag.getString("valueClass"));
				if(!hasNBTHandler(valueClass)) {
					LOGGER.warn("No NBT deserialization methods for values in queue (type='{}') exists.", valueClass);
					return new ArrayDeque<>();
				}

				NbtReader reader = getNBTHandler(valueClass).getLeft();

				for(Tag baseTag : containerTag.getList("values", Tag.TAG_COMPOUND)) {
					CompoundTag entry = (CompoundTag) baseTag;
					Object value = reader.read("data", entry, original);
					result.add(value);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			return result;
		}, (key, queue, tag) -> {
			CompoundTag containerTag = new CompoundTag();
			containerTag.putBoolean("isEmpty", queue.isEmpty());

			if(!queue.isEmpty()) {
				Class valueClass = queue.peek().getClass();
				if(!hasNBTHandler(valueClass)) {
					LOGGER.warn("No NBT serialization methods for values in list (type='{}') exists.", valueClass.getName());
					return;
				}

				containerTag.putString("valueClass", valueClass.getName());

				NbtWriter writer = getNBTHandler(valueClass).getRight();

				ListTag data = new ListTag();
				for(Object e : queue) {
					CompoundTag entryContainerTag = new CompoundTag();
					writer.write("data", e, entryContainerTag);
					data.add(entryContainerTag);
				}
				containerTag.put("values", data);
			}

			tag.put(key, containerTag);
		});

	}


	public static <T extends Object> void addNBTHandler(Class<T> type, NbtReader<T> reader, NbtWriter<T> writer) {
		nbtHandlers.put(type, Pair.of(reader, writer));
	}

	public static boolean hasNBTHandler(Class clz) {
		if(nbtHandlers.containsKey(clz)) {
			return true;
		}

		for(Class iface : clz.getInterfaces()) {
			if(nbtHandlers.containsKey(iface)) {
				return true;
			}
		}

		Class superClass = clz.getSuperclass();
		if(superClass == null) {
			return false;
		}

		return hasNBTHandler(superClass);
	}

	public static Pair<NbtReader, NbtWriter> getNBTHandler(Class clz) {
		if(nbtHandlers.containsKey(clz)) {
			return nbtHandlers.get(clz);
		}

		for(Class iface : clz.getInterfaces()) {
			if(nbtHandlers.containsKey(iface)) {
				return nbtHandlers.get(iface);
			}
		}

		Class superClass = clz.getSuperclass();
		if(superClass == null) {
			return null;
		}

		return getNBTHandler(superClass);
	}

	public interface NbtWriter<T extends Object> {
		void write(String key, T t, CompoundTag tag);
	}

	public interface NbtReader<T extends Object> {
		T read(String key, CompoundTag tag, T original);
	}

}
