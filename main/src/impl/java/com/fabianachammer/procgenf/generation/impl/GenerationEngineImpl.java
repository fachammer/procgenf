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
	private Set<ChunkEntity> previouslyGeneratedFirstLevelChunks;

	public GenerationEngineImpl(ChunkEntity rootChunk) {
		this.rootChunk = rootChunk;
		generationQueue = new LinkedList<>();
		chunkGenerators = new ArrayList<>();
		previouslyGeneratedFirstLevelChunks = new HashSet<>();
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
			generateChunk(generationQueue.remove(), true, true);

		return this;
	}

	private void setupGenerationQueues() {
		Set<ChunkEntity> currentlyGeneratedFirstLevelChunks = generateChunk(rootChunk, false, true);

		Set<ChunkEntity> degeneratedChunks = degenerateOldChunks(currentlyGeneratedFirstLevelChunks);
		Set<ChunkEntity> generatedChunks = generateNewChunks(currentlyGeneratedFirstLevelChunks);
		previouslyGeneratedFirstLevelChunks = new HashSet<>(previouslyGeneratedFirstLevelChunks);

		previouslyGeneratedFirstLevelChunks.removeAll(degeneratedChunks);
		previouslyGeneratedFirstLevelChunks.addAll(generatedChunks);
	}

	private Set<ChunkEntity> degenerateOldChunks(Set<ChunkEntity> currentRootChunks) {
		Set<ChunkEntity> chunksToBeDegenerated = new HashSet<>();

		chunksToBeDegenerated.addAll(previouslyGeneratedFirstLevelChunks);
		chunksToBeDegenerated.removeAll(currentRootChunks);
		chunksToBeDegenerated.forEach(this::degenerateChunk);

		System.out.println("old chunks degenerated: " + chunksToBeDegenerated.size());
		
		return new HashSet<>(chunksToBeDegenerated);
	}

	private Set<ChunkEntity> generateNewChunks(Set<ChunkEntity> currentRootChunks) {
		Set<ChunkEntity> chunksToBeGenerated = new HashSet<>();
		chunksToBeGenerated.addAll(currentRootChunks);
		chunksToBeGenerated.removeAll(previouslyGeneratedFirstLevelChunks);
		chunksToBeGenerated.forEach(this::enqueueChunkForGeneration);
		System.out.println("new chunks generated: " + chunksToBeGenerated.size());
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

	private Set<ChunkEntity> generateChunk(ChunkEntity chunk, boolean enqueueChildren, boolean parentChildren) {
		List<ChunkGenerator> generators = getGeneratorsForChunk(chunk);
		Set<ChunkEntity> subChunks = new HashSet<>();
		generators.forEach(generator -> {
			Set<ChunkEntity> generatorSubChunks = generator.generateChunk(chunk);
			if(generatorSubChunks != null) {
				generatorSubChunks.forEach(subChunk -> {
					if(parentChildren) {
						chunk.addChild(subChunk);
						subChunk.setParent(chunk);
					}
					// enqueue chunk so that it can depend on its sibling chunks
					// when generating sub chunks
					if(enqueueChildren)
						enqueueChunkForGeneration(subChunk);
				});
				subChunks.addAll(generatorSubChunks);
			}
		});
		
		chunk.onGenerated();

		return subChunks;
	}

	@Override
	public ChunkEntity getRootChunk() {
		return rootChunk;
	}
}
