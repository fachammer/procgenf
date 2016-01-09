package com.fabianachammer.procgenf.generation;

public interface GenerationEngine {
	
	GenerationEngine enqueueChunkForGeneration(Chunk chunk);
	
	GenerationEngine enqueueChunkForDegeneration(Chunk chunk);
	
	GenerationEngine setGeneratorForChunk(ChunkGenerator generator, Class<? extends Chunk> chunkClass);

	GenerationEngine run();
}
