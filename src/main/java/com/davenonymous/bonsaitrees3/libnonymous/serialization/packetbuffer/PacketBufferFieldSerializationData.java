package com.davenonymous.bonsaitrees3.libnonymous.serialization.packetbuffer;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;

public class PacketBufferFieldSerializationData {
	public PacketBufferFieldHandlers.Reader reader;
	public PacketBufferFieldHandlers.Writer writer;
	public Field field;

	public PacketBufferFieldSerializationData(Field field) {
		this.field = field;

		Pair<PacketBufferFieldHandlers.Reader, PacketBufferFieldHandlers.Writer> pair = PacketBufferFieldHandlers.getIOHandler(field.getType());
		this.reader = pair.getLeft();
		this.writer = pair.getRight();
	}
}
