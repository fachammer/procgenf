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
	private Map<ChunkEntity, Set<ChunkEntity>> alreadyGeneratedChunks;

	public GenerationEngineImpl() {
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
	public GenerationEngine run(ChunkEntity rootChunk) {
		if(!rootChunk.equals(previousRootChunk)) {
			Set<ChunkEntity> previousRootChildren = alreadyGeneratedChunks.remove(previousRootChunk);
			if(previousRootChildren != null)
				alreadyGeneratedChunks.put(rootChunk.clone(), previousRootChildren);
			
			generateChunkWithMemoization(rootChunk);

			int generatedChunksCount = 1;
			while(!generationQueue.isEmpty()) {
				generateChunkWithMemoization(generationQueue.remove());
				generatedChunksCount++;
			}
			System.out.println("newly generated chunks: " + generatedChunksCount);
			
			previousRootChunk = rootChunk.clone();
		}

		return this;
	}

	private void generateChunkWithMemoization(ChunkEntity chunk) {
		Set<ChunkEntity> newlyGenerated = generateChunk(chunk);
		Set<ChunkEntity> alreadyGenerated = alreadyGeneratedChunks.getOrDefault(chunk, new HashSet<>());

		Set<ChunkEntity> chunksToDegenerate = new HashSet<>();
		Set<ChunkEntity> chunksToGenerate = new HashSet<>();

		chunksToDegenerate.addAll(alreadyGenerated);
		chunksToDegenerate.removeAll(newlyGenerated);
		chunksToDegenerate.forEach(this::degenerateChunk);

		chunksToGenerate.addAll(newlyGenerated);
		chunksToGenerate.removeAll(alreadyGenerated);
		chunksToGenerate.forEach(this::enqueueChunkForGeneration);

		alreadyGenerated.removeAll(chunksToDegenerate);
		alreadyGenerated.addAll(chunksToGenerate);
		ChunkEntity clone = chunk.clone();
		alreadyGeneratedChunks.put(clone, alreadyGenerated);
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
}
