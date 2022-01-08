package com.davenonymous.bonsaitrees3.libnonymous.serialization.nbt;


import com.davenonymous.bonsaitrees3.libnonymous.serialization.FieldUtils;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.Store;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class NBTFieldUtils {
	private static Map<Class, List<NBTFieldSerializationData>> classNbtCache = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	public static CompoundTag writeFieldsToNBT(List<NBTFieldSerializationData> NBTActions, Object source, CompoundTag targetCompound, Predicate<NBTFieldSerializationData> test) {
		for(NBTFieldSerializationData data : NBTActions) {
			if(!test.test(data)) {
				continue;
			}

			try {
				Object value = data.field.get(source);
				data.writer.write(data.key, value, targetCompound);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return targetCompound;
	}

	public static void readFieldsFromNBT(List<NBTFieldSerializationData> NBTActions, Object target, CompoundTag sourceCompound, Predicate<NBTFieldSerializationData> test) {
		for(NBTFieldSerializationData data : NBTActions) {
			if(!test.test(data)) {
				continue;
			}

			try {
				Object value = data.reader.read(data.key, sourceCompound, data.field.get(target));
				data.field.set(target, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<NBTFieldSerializationData> initSerializableStoreFields(Class clz) {
		if(classNbtCache.containsKey(clz)) {
			return classNbtCache.get(clz);
		}

		List<NBTFieldSerializationData> actionList = new ArrayList<>();

		for(Field field : FieldUtils.getAllDeclaredFields(clz)) {
			Annotation annotation = field.getDeclaredAnnotation(Store.class);
			if(annotation != null) {
				if(!NBTFieldHandlers.hasNBTHandler(field.getType())) {
					LOGGER.warn("No NBT serialization methods for field='{}' (type='{}') in class='{}' exists.", field.getName(), field.getType().getSimpleName(), clz.getSimpleName());
					continue;
				}

				Store storeAnnotation = (Store) annotation;
				String key = storeAnnotation.key();
				if(key.equals("")) {
					key = field.getName();
				}
				actionList.add(new NBTFieldSerializationData(field, key, storeAnnotation.storeWithItem(), storeAnnotation.sendInUpdatePackage()));
				field.setAccessible(true);
			}
		}

		classNbtCache.put(clz, actionList);
		return actionList;
	}
}
