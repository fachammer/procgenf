package com.fabianachammer.procgenf.generation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.fabianachammer.procgenf.generation.Chunk;
import com.fabianachammer.procgenf.generation.ChunkGenerator;
import com.fabianachammer.procgenf.generation.GenerationEngine;
import com.fabianachammer.procgenf.generation.RootChunkGenerator;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class GenerationEngineImpl implements GenerationEngine {

	private Queue<Chunk> generationQueue;
	private Queue<Chunk> degenerationQueue;
	private List<ChunkGenerator> chunkGenerators;
	private Chunk rootChunk;
	private Set<Chunk> generatedRootChunks;
	private RootChunkGenerator rootChunkGenerator;
	
	public GenerationEngineImpl(Chunk rootChunk, RootChunkGenerator rootChunkGenerator) {
		this.rootChunk = rootChunk;
		this.rootChunkGenerator = rootChunkGenerator;
		generationQueue = new LinkedList<>();
		degenerationQueue = new LinkedList<>();
		chunkGenerators = new ArrayList<>();
		generatedRootChunks = new HashSet<>();	
	}
	
	private GenerationEngine enqueueChunkForGeneration(Chunk chunk) {
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
		Set<Chunk> currentRootChunks = rootChunkGenerator.generateChunksFromVisibilityRegion(visibilityRegion);
		
		currentRootChunks.forEach(chunk -> chunk.setParent(rootChunk));
		
		degenerateOldChunks(currentRootChunks);
		generateNewChunks(currentRootChunks);
		
		generatedRootChunks = currentRootChunks;
	}

	private void generateNewChunks(Set<Chunk> currentRootChunks) {
		Set<Chunk> chunksToBeGenerated = new HashSet<>();
		chunksToBeGenerated.addAll(currentRootChunks);
		chunksToBeGenerated.removeAll(generatedRootChunks);
		chunksToBeGenerated.forEach(this::enqueueChunkForGeneration);
	}

	private void degenerateOldChunks(Set<Chunk> currentRootChunks) {
		Set<Chunk> chunksToBeDegenerated = new HashSet<>();
		chunksToBeDegenerated.addAll(generatedRootChunks);
		chunksToBeDegenerated.removeAll(currentRootChunks);
		rootChunkGenerator.degenerateChunks(chunksToBeDegenerated);
	}
	
	private List<ChunkGenerator> getGeneratorsForChunk(Chunk chunk) {
		List<ChunkGenerator> generators = new ArrayList<>(chunkGenerators);
		generators.removeIf(g -> !g.willGenerateChunk(chunk));
		return generators;
	}
	
	private void degenerateChunk(Chunk chunk) {
		List<ChunkGenerator> generators = getGeneratorsForChunk(chunk);
		if(chunk.getChildren() != null) {
			// degenerate children first so that they can depend on their parents while degenerating
			chunk.getChildren().forEach(childChunk -> {
				degenerateChunk(childChunk);
			});
		}
		
		Collections.reverse(generators);
		generators.forEach(generator -> generator.degenerateChunk(chunk));
	}
	
	private void generateChunk(Chunk chunk) {
		List<ChunkGenerator> generators = getGeneratorsForChunk(chunk);
		
		generators.forEach(generator -> {
			Set<Chunk> generatorSubChunks = generator.generateChunk(chunk);
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
	public Chunk getRootChunk() {
		return rootChunk;
	}
}
