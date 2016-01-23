package com.fabianachammer.procgenf.generation;

import java.util.Set;

public interface ChunkGeneratorSystem {

	boolean willGenerateChunk(ChunkEntity chunk);
	
	Set<ChunkEntity> generateChunk(ChunkEntity chunk);
	
	void degenerateChunk(ChunkEntity chunk);
}
