package com.davenonymous.bonsaitrees3.libnonymous.serialization.packetbuffer;


import com.davenonymous.bonsaitrees3.libnonymous.serialization.FieldUtils;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.Sync;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PacketBufferUtils {
	private static Map<Class, List<PacketBufferFieldSerializationData>> classByteBufCache = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();


	public static List<PacketBufferFieldSerializationData> initSerializableSyncFields(Class clz) {
		if(classByteBufCache.containsKey(clz)) {
			return classByteBufCache.get(clz);
		}

		List<PacketBufferFieldSerializationData> actionList = new ArrayList<>();

		for(Field field : FieldUtils.getAllDeclaredFields(clz)) {
			Annotation annotation = field.getDeclaredAnnotation(Sync.class);
			if(annotation != null && annotation instanceof Sync) {
				if(!PacketBufferFieldHandlers.hasIOHandler(field.getType())) {
					LOGGER.warn("No PacketBuffer serialization methods for field='{}' (type='{}') in class='{}' exists.", field.getName(), field.getType().getSimpleName(), clz.getSimpleName());
					continue;
				}

				Sync syncAnnotation = (Sync) annotation;
				actionList.add(new PacketBufferFieldSerializationData(field));
				field.setAccessible(true);
			}
		}

		classByteBufCache.put(clz, actionList);
		return actionList;
	}

	public static void writeFieldsToByteBuf(List<PacketBufferFieldSerializationData> ioActions, Object source, FriendlyByteBuf targetBuffer, Predicate<PacketBufferFieldSerializationData> test) {
		for(PacketBufferFieldSerializationData data : ioActions) {
			if(!test.test(data)) {
				continue;
			}

			try {
				Object value = data.field.get(source);
				data.writer.write(value, targetBuffer);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void readFieldsFromByteBuf(List<PacketBufferFieldSerializationData> ioActions, Object target, FriendlyByteBuf sourceBuffer, Predicate<PacketBufferFieldSerializationData> test) {
		for(PacketBufferFieldSerializationData data : ioActions) {
			if(!test.test(data)) {
				continue;
			}

			try {
				Object value = data.reader.read(sourceBuffer);
				data.field.set(target, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
