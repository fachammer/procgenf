package com.fabianachammer.procgenf.generation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkGenerator;
import com.fabianachammer.procgenf.generation.GenerationEngine;

public class RegeneratingGenerationEngine implements GenerationEngine {

	private Queue<ChunkEntity> generationQueue;
	private List<ChunkGenerator> chunkGenerators;

	public RegeneratingGenerationEngine() {
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
		generateChunkWithMemoization(rootChunk);
		while(!generationQueue.isEmpty()) {
			generateChunkWithMemoization(generationQueue.remove());
		}

		return this;
	}

	private void generateChunkWithMemoization(ChunkEntity chunk) {
		Collection<ChunkEntity> chunksToGenerate = generateChunk(chunk);

		degenerateChunks(chunk.getChildren());

		for(ChunkEntity chunkToGenerate : chunksToGenerate) {
			chunkToGenerate.setParent(chunk);
			chunk.addChild(chunkToGenerate);
			enqueueChunkForGeneration(chunkToGenerate);
		}
	}

	private void degenerateChunks(Collection<ChunkEntity> chunksToDegenerate) {
		Stack<ChunkEntity> degenerationStack = new Stack<>();
		Queue<ChunkEntity> degenerationQueue = new LinkedList<>(chunksToDegenerate);
		while(!degenerationQueue.isEmpty()) {
			ChunkEntity chunkToDegenerate = degenerationQueue.remove();
			degenerationStack.push(chunkToDegenerate);
			degenerationQueue.addAll(chunkToDegenerate.getChildren());
		}

		while(!degenerationStack.isEmpty()) {
			degenerateChunk(degenerationStack.pop());
		}
	}

	private void degenerateChunk(ChunkEntity chunk) {
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
