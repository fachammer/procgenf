package com.fabianachammer.procgenf.generation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkGeneratorSystem;
import com.fabianachammer.procgenf.generation.GenerationEngine;
import com.fabianachammer.procgenf.generation.RootChunkGenerator;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class GenerationEngineImpl implements GenerationEngine {

	private Queue<ChunkEntity> generationQueue;
	private Queue<ChunkEntity> degenerationQueue;
	private List<ChunkGeneratorSystem> chunkGenerators;
	private ChunkEntity rootChunk;
	private Set<ChunkEntity> generatedRootChunks;
	private RootChunkGenerator rootChunkGenerator;
	
	public GenerationEngineImpl(ChunkEntity rootChunk, RootChunkGenerator rootChunkGenerator) {
		this.rootChunk = rootChunk;
		this.rootChunkGenerator = rootChunkGenerator;
		generationQueue = new LinkedList<>();
		degenerationQueue = new LinkedList<>();
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
	public GenerationEngine addGenerator(ChunkGeneratorSystem generator) {
		if(generator == null)
			throwNull("generator must not be null");
		
		chunkGenerators.add(generator);
		return this;
	}
	
	private static void throwNull(String message){
		throw new IllegalArgumentException(message);
	}
	
	@Override
	public GenerationEngine run(PolygonSimple visibilityRegion) {
		setupGenerationQueues(visibilityRegion);
		
		while(!degenerationQueue.isEmpty())
			degenerateChunk(degenerationQueue.remove());
		
		while(!generationQueue.isEmpty())
			generateChunk(generationQueue.remove());
		
		return this;
	}

	private void setupGenerationQueues(PolygonSimple visibilityRegion) {
		Set<ChunkEntity> currentRootChunks = rootChunkGenerator.generateChunksFromVisibilityRegion(visibilityRegion);
		
		currentRootChunks.forEach(chunk -> chunk.setParent(rootChunk));
		
		degenerateOldChunks(currentRootChunks);
		generateNewChunks(currentRootChunks);
		
		generatedRootChunks = currentRootChunks;
	}

	private void generateNewChunks(Set<ChunkEntity> currentRootChunks) {
		Set<ChunkEntity> chunksToBeGenerated = new HashSet<>();
		chunksToBeGenerated.addAll(currentRootChunks);
		chunksToBeGenerated.removeAll(generatedRootChunks);
		chunksToBeGenerated.forEach(this::enqueueChunkForGeneration);
	}

	private void degenerateOldChunks(Set<ChunkEntity> currentRootChunks) {
		Set<ChunkEntity> chunksToBeDegenerated = new HashSet<>();
		chunksToBeDegenerated.addAll(generatedRootChunks);
		chunksToBeDegenerated.removeAll(currentRootChunks);
		rootChunkGenerator.degenerateChunks(chunksToBeDegenerated);
	}
	
	private List<ChunkGeneratorSystem> getGeneratorsForChunk(ChunkEntity chunk) {
		List<ChunkGeneratorSystem> generators = new ArrayList<>(chunkGenerators);
		generators.removeIf(g -> !g.willGenerateChunk(chunk));
		return generators;
	}
	
	private void degenerateChunk(ChunkEntity chunk) {
		List<ChunkGeneratorSystem> generators = getGeneratorsForChunk(chunk);
		if(chunk.getChildren() != null) {
			// degenerate children first so that they can depend on their parents while degenerating
			chunk.getChildren().forEach(childChunk -> {
				degenerateChunk(childChunk);
			});
		}
		
		Collections.reverse(generators);
		generators.forEach(generator -> generator.degenerateChunk(chunk));
	}
	
	private void generateChunk(ChunkEntity chunk) {
		List<ChunkGeneratorSystem> generators = getGeneratorsForChunk(chunk);
		
		generators.forEach(generator -> {
			Set<ChunkEntity> generatorSubChunks = generator.generateChunk(chunk);
			if(generatorSubChunks != null){
				generatorSubChunks.forEach(subChunk -> {
					chunk.addChild(subChunk);
					subChunk.setParent(chunk);
					// enqueue chunk so that it can depend on its sibling chunks when generating sub chunks
					enqueueChunkForGeneration(chunk);
				});
			}
		});
	}
	
	@Override
	public ChunkEntity getRootChunk() {
		return rootChunk;
	}
}
