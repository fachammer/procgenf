package com.fabianachammer.procgenf.generation;

import java.util.Set;

public interface ChunkGenerator {

	boolean willGenerateChunk(ChunkEntity chunk);
	
	boolean willDegenerateChunk(ChunkEntity chunk);
	
	Set<ChunkEntity> generateChunk(ChunkEntity chunk);
	
	void degenerateChunk(ChunkEntity chunk);
}
