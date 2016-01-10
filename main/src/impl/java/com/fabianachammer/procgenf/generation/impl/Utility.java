package com.fabianachammer.procgenf.generation.impl;

import java.util.Optional;

import com.fabianachammer.procgenf.generation.Chunk;
import com.fabianachammer.procgenf.generation.ChunkFeature;

public class Utility {

	@SuppressWarnings("unchecked")
	public static <T extends ChunkFeature> Optional<T> getChunkFeature(Chunk chunk, Class<T> featureClass) {
		return chunk.getFeatures().stream().filter(f -> featureClass.isAssignableFrom(f.getClass())).map(f -> (T) f).findFirst();
	}
}
