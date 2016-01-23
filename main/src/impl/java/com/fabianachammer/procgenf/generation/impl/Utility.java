package com.fabianachammer.procgenf.generation.impl;

import java.util.Optional;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkComponent;

public class Utility {

	@SuppressWarnings("unchecked")
	public static <T extends ChunkComponent> Optional<T> getChunkComponent(ChunkEntity chunk, Class<T> componentClass) {
		return chunk.getComponents().stream().filter(c -> componentClass.isAssignableFrom(c.getClass())).map(f -> (T) f).findFirst();
	}
}
