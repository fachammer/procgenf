package com.fabianachammer.procgenf.generation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkGenerator;
import com.fabianachammer.procgenf.generation.GenerationEngine;

public class GenerationEngineImpl implements GenerationEngine {

	private Queue<ChunkEntity> generationQueue;
	private List<ChunkGenerator> chunkGenerators;
	private ChunkEntity previousRootChunk;
	private ChunkEntity rootChunk;
	private Map<ChunkEntity, Set<ChunkEntity>> alreadyGeneratedChunks;

	public GenerationEngineImpl(ChunkEntity rootChunk) {
		this.rootChunk = rootChunk;
		generationQueue = new LinkedList<>();
		chunkGenerators = new ArrayList<>();
		alreadyGeneratedChunks = new HashMap<>();
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
		if(!rootChunk.equals(previousRootChunk)) {
			System.out.println("NEW GENERATION!");

			Set<ChunkEntity> previousRootChildren = alreadyGeneratedChunks.remove(previousRootChunk);
			previousRootChildren = previousRootChildren != null ? previousRootChildren : new HashSet<>();
			alreadyGeneratedChunks.put(rootChunk, previousRootChildren);
			
			generateChunkWithMemoization(rootChunk);

			while(!generationQueue.isEmpty())
				generateChunkWithMemoization(generationQueue.remove());

			previousRootChunk = rootChunk.clone();
			alreadyGeneratedChunks.put(previousRootChunk, alreadyGeneratedChunks.get(rootChunk));
		}

		return this;
	}

	private void generateChunkWithMemoization(ChunkEntity chunk) {
		Set<ChunkEntity> newlyGenerated = generateChunk(chunk);
		Set<ChunkEntity> alreadyGenerated = alreadyGeneratedChunks.getOrDefault(chunk, new HashSet<>());

		System.out.println("newly generated chunks: " + newlyGenerated.size());
		System.out.println("already generated chunks: " + alreadyGenerated.size());
		
		Set<ChunkEntity> chunksToDegenerate = new HashSet<>();
		Set<ChunkEntity> chunksToGenerate = new HashSet<>();

		chunksToDegenerate.addAll(alreadyGenerated);
		chunksToDegenerate.removeAll(newlyGenerated);
		chunksToDegenerate.forEach(this::degenerateChunk);

		System.out.println("old chunks to degenerate: " + chunksToDegenerate.size());

		chunksToGenerate.addAll(newlyGenerated);
		chunksToGenerate.removeAll(alreadyGenerated);
		chunksToGenerate.forEach(this::enqueueChunkForGeneration);

		System.out.println("new chunks to generate: " + chunksToGenerate.size());

		alreadyGenerated.removeAll(chunksToDegenerate);
		alreadyGenerated.addAll(chunksToGenerate);
		alreadyGeneratedChunks.put(chunk.clone(), alreadyGenerated);
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

		alreadyGeneratedChunks.remove(chunk);
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
		Set<ChunkEntity> subChunks = new HashSet<>();
		generators.forEach(generator -> {
			Set<ChunkEntity> generatorSubChunks = generator.generateChunk(chunk);
			if(generatorSubChunks != null) {
				generatorSubChunks.forEach(subChunk -> {
					chunk.addChild(subChunk);
					subChunk.setParent(chunk);
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
