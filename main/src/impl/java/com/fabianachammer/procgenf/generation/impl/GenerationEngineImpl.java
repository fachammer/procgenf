package com.fabianachammer.procgenf.generation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkGenerator;
import com.fabianachammer.procgenf.generation.GenerationEngine;

public class GenerationEngineImpl implements GenerationEngine {

	private Queue<ChunkEntity> generationQueue;
	private List<ChunkGenerator> chunkGenerators;
	private ChunkEntity previousRootChunk;

	public GenerationEngineImpl() {
		generationQueue = new LinkedList<>();
		chunkGenerators = new ArrayList<>();
	}

	private GenerationEngine enqueueChunkForGeneration(ChunkEntity chunk) {
		if(chunk == null)
			throwNull("chunk must not be null");

		generationQueue.add(chunk);
		return this;
	}

	@Override
	public GenerationEngine addGenerator(ChunkGenerator generator) {
		if(generator == null)
			throwNull("generator must not be null");

		chunkGenerators.add(generator);
		return this;
	}

	private static void throwNull(String message) {
		throw new IllegalArgumentException(message);
	}

	@Override
	public GenerationEngine run(ChunkEntity rootChunk) {
		if(!rootChunk.equals(previousRootChunk)) {
			generateChunkWithMemoization(rootChunk, true);

			int generatedChunksCount = 1;
			while(!generationQueue.isEmpty()) {
				generateChunkWithMemoization(generationQueue.remove(), false);
				generatedChunksCount++;
			}
			System.out.println("newly generated chunks: " + generatedChunksCount);
			
			previousRootChunk = rootChunk.clone();
		}

		return this;
	}

	private void generateChunkWithMemoization(ChunkEntity chunk, boolean isRootChunk) {
		Collection<ChunkEntity> alreadyGenerated = new ArrayList<>(chunk.getChildren());
		Collection<ChunkEntity> newlyGenerated = generateChunk(chunk);
		
		Collection<ChunkEntity> chunksToDegenerate = new ArrayList<>(alreadyGenerated);
		Collection<ChunkEntity> chunksToGenerate = new ArrayList<>(newlyGenerated);

		chunksToDegenerate.removeAll(newlyGenerated);
		chunksToDegenerate.forEach(this::degenerateChunk);
		
		chunksToGenerate.removeAll(alreadyGenerated);
		for(ChunkEntity chunkToGenerate : chunksToGenerate) {
			chunkToGenerate.setParent(chunk);
			chunk.addChild(chunkToGenerate);
			enqueueChunkForGeneration(chunkToGenerate);
		}
	}

	private void degenerateChunk(ChunkEntity chunk) {
		if(chunk.getChildren() != null) {
			// degenerate children first so that they can depend on their
			// parents while degenerating
			new ArrayList<>(chunk.getChildren()).forEach(this::degenerateChunk);
		}

		List<ChunkGenerator> degenerators = getDegeneratorsForChunk(chunk);
		for(int i = degenerators.size() - 1; i >= 0; i--)
			degenerators.get(i).degenerateChunk(chunk);
		
		if(chunk.getParent() != null) {
			chunk.getParent().removeChild(chunk);
			chunk.setParent(null);
		}
	}

	private List<ChunkGenerator> getGeneratorsForChunk(ChunkEntity chunk) {
		return chunkGenerators;
	}

	private List<ChunkGenerator> getDegeneratorsForChunk(ChunkEntity chunk) {
		return chunkGenerators;
	}

	private Collection<ChunkEntity> generateChunk(ChunkEntity chunk) {
		List<ChunkGenerator> generators = getGeneratorsForChunk(chunk);
		List<ChunkEntity> subChunks = new ArrayList<>();
		for(ChunkGenerator generator : generators) {
			Collection<ChunkEntity> generatorSubChunks = generator.generateChunkChildren(chunk);
			if(generatorSubChunks != null) 
				subChunks.addAll(generatorSubChunks);
		}
		
		return subChunks;
	}
}
