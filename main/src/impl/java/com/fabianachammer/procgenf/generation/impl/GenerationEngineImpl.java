package com.fabianachammer.procgenf.generation.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkGenerator;
import com.fabianachammer.procgenf.generation.GenerationEngine;

public class GenerationEngineImpl implements GenerationEngine {

	private Queue<ChunkEntity> generationQueue;
	private List<ChunkGenerator> chunkGenerators;
	private ChunkEntity rootChunk;
	private Set<ChunkEntity> generatedRootChunks;

	public GenerationEngineImpl(ChunkEntity rootChunk) {
		this.rootChunk = rootChunk;
		generationQueue = new LinkedList<>();
		chunkGenerators = new ArrayList<>();
		generatedRootChunks = new HashSet<>();
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
	public GenerationEngine run() {
		setupGenerationQueues();

		while(!generationQueue.isEmpty())
			generateChunk(generationQueue.remove());

		return this;
	}

	private void setupGenerationQueues() {
		Set<ChunkEntity> currentRootChunks = generateChunk(rootChunk);

		currentRootChunks.forEach(chunk -> {
			rootChunk.addChild(chunk);
			chunk.setParent(rootChunk);
		});

		Set<ChunkEntity> degeneratedChunks = degenerateOldChunks(currentRootChunks);
		Set<ChunkEntity> generatedChunks = generateNewChunks(currentRootChunks);

		generatedRootChunks.removeAll(degeneratedChunks);
		generatedRootChunks.addAll(generatedChunks);
	}

	private Set<ChunkEntity> degenerateOldChunks(Set<ChunkEntity> currentRootChunks) {
		Set<ChunkEntity> chunksToBeDegenerated = new HashSet<>();

		chunksToBeDegenerated.addAll(generatedRootChunks);
		chunksToBeDegenerated.removeAll(currentRootChunks);
		System.out.println("degenerated: " + chunksToBeDegenerated.size());
		chunksToBeDegenerated.forEach(this::degenerateChunk);

		return chunksToBeDegenerated;
	}

	private Set<ChunkEntity> generateNewChunks(Set<ChunkEntity> currentRootChunks) {
		Set<ChunkEntity> chunksToBeGenerated = new HashSet<>();
		chunksToBeGenerated.addAll(currentRootChunks);
		chunksToBeGenerated.removeAll(generatedRootChunks);
		System.out.println("generated: " + chunksToBeGenerated.size());
		chunksToBeGenerated.forEach(this::enqueueChunkForGeneration);
		return chunksToBeGenerated;
	}

	private void degenerateChunk(ChunkEntity chunk) {
		List<ChunkGenerator> degenerators = getDegeneratorsForChunk(chunk);
		if(chunk.getChildren() != null) {
			// degenerate children first so that they can depend on their
			// parents while degenerating
			// copy set to avoid ConcurrentModification issues
			new HashSet<>(chunk.getChildren()).forEach(this::degenerateChunk);
		}

		for(int i = degenerators.size() - 1; i >= 0; i--) {
			degenerators.get(i).degenerateChunk(chunk);
		}
		
		if(chunk.getParent() != null) {
			chunk.getParent().removeChild(chunk);
			chunk.setParent(null);
		}
	}

	private List<ChunkGenerator> getGeneratorsForChunk(ChunkEntity chunk) {
		List<ChunkGenerator> generators = new ArrayList<>(chunkGenerators);
		generators.removeIf(g -> !g.willGenerateChunk(chunk));
		return generators;
	}

	private List<ChunkGenerator> getDegeneratorsForChunk(ChunkEntity chunk) {
		List<ChunkGenerator> generators = new ArrayList<>(chunkGenerators);
		generators.removeIf(g -> !g.willDegenerateChunk(chunk));
		return generators;
	}

	private Set<ChunkEntity> generateChunk(ChunkEntity chunk) {
		List<ChunkGenerator> generators = getGeneratorsForChunk(chunk);
		Set<ChunkEntity> subChunks = new HashSet<ChunkEntity>();
		generators.forEach(generator -> {
			Set<ChunkEntity> generatorSubChunks = generator.generateChunk(chunk);
			if(generatorSubChunks != null) {
				generatorSubChunks.forEach(subChunk -> {
					chunk.addChild(subChunk);
					subChunk.setParent(chunk);
					// enqueue chunk so that it can depend on its sibling chunks
					// when generating sub chunks
					enqueueChunkForGeneration(subChunk);
				});
				subChunks.addAll(generatorSubChunks);
			}
		});

		return subChunks;
	}

	@Override
	public ChunkEntity getRootChunk() {
		return rootChunk;
	}
}
