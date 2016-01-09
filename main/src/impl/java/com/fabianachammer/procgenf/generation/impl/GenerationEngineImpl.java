package com.fabianachammer.procgenf.generation.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.fabianachammer.procgenf.generation.Chunk;
import com.fabianachammer.procgenf.generation.ChunkGenerator;
import com.fabianachammer.procgenf.generation.GenerationEngine;

public class GenerationEngineImpl implements GenerationEngine {

	private Queue<Chunk> generationQueue;
	private Queue<Chunk> degenerationQueue;
	private Map<Class<? extends Chunk>, ChunkGenerator> chunkGenerators;
	
	public GenerationEngineImpl() {
		generationQueue = new LinkedList<>();
		degenerationQueue = new LinkedList<>();
		chunkGenerators = new HashMap<>();
	}
	
	private void throwIfNoGeneratorDefinedForChunk(Chunk chunk){
		if(!chunkGenerators.containsKey(chunk.getClass()))
			throw new IllegalArgumentException("no generator found for chunk class '" + chunk.getClass().getName() + "'. Make sure you set up the generators using the setGeneratorForChunk method");
	}
	
	@Override
	public GenerationEngine enqueueChunkForGeneration(Chunk chunk) {
		throwIfNoGeneratorDefinedForChunk(chunk);
		generationQueue.add(chunk);
		return this;
	}

	@Override
	public GenerationEngine enqueueChunkForDegeneration(Chunk chunk) {
		throwIfNoGeneratorDefinedForChunk(chunk);
		degenerationQueue.add(chunk);
		return this;
	}
	
	@Override
	public GenerationEngine setGeneratorForChunk(ChunkGenerator generator, Class<? extends Chunk> chunkClass) {
		throwIfNull(generator, "generator must not be null");
		throwIfNull(chunkClass, "chunkClass must not be null");
		chunkGenerators.put(chunkClass, generator);
		return this;
	}
	
	private static void throwIfNull(Object object, String message){
		if(object == null)
			throw new IllegalArgumentException(message);
	}
	
	@Override
	public GenerationEngine run() {
		while(!degenerationQueue.isEmpty()){
			Chunk chunk = degenerationQueue.remove();
			ChunkGenerator generator = getGeneratorForChunk(chunk);
			degenerateChunk(generator, chunk);
		}
		
		while(!generationQueue.isEmpty()){
			Chunk chunk = generationQueue.remove();
			ChunkGenerator generator = chunkGenerators.get(chunk.getClass());
			generateChunk(generator, chunk);
		}
		
		return this;
	}
	
	private ChunkGenerator getGeneratorForChunk(Chunk chunk) {
		return chunkGenerators.get(chunk.getClass());
	}
	
	private void degenerateChunk(ChunkGenerator generator, Chunk chunk) {
		if(chunk.getChildren() != null) {
			// degenerate children first so that they can depend on their parents while degenerating
			chunk.getChildren().forEach(childChunk -> {
				ChunkGenerator childGenerator = getGeneratorForChunk(childChunk);
				degenerateChunk(childGenerator, childChunk);
			});
		}
		
		generator.degenerateChunk(chunk);
	}
	
	private void generateChunk(ChunkGenerator generator, Chunk chunk) {
		Set<Chunk> subChunks = generator.generateChunk(chunk);
		if(subChunks == null)
			return;
		
		subChunks.forEach(subChunk -> {
			subChunk.setParent(chunk);
			// enqueue chunk so that it can depend on its sibling chunk when generating sub chunks
			enqueueChunkForGeneration(chunk);
		});
	}
}
