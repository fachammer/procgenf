package com.fabianachammer.procgenf.generation;

import java.util.Set;

public interface ChunkGenerator {

	boolean willGenerateChunk(Chunk chunk);
	
	Set<Chunk> generateChunk(Chunk chunk);
	
	void degenerateChunk(Chunk chunk);
}