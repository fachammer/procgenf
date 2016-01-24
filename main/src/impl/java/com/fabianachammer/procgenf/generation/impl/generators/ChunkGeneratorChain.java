package com.fabianachammer.procgenf.generation.impl.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkGenerator;

public class ChunkGeneratorChain implements ChunkGenerator {

	private List<ChunkGenerator> chain = new ArrayList<>();
	
	public ChunkGeneratorChain addGenerator(ChunkGenerator generator) {
		chain.add(generator);
		return this;
	}
	
	@Override
	public boolean willGenerateChunk(ChunkEntity chunk) {
		return !chain.isEmpty() && chain.get(0).willGenerateChunk(chunk);
	}

	@Override
	public Set<ChunkEntity> generateChunk(ChunkEntity chunk) {
		if(chain.isEmpty())
			return null;
		
		Set<ChunkEntity> subChunks = new HashSet<ChunkEntity>();
		for(ChunkGenerator generator : chain){
			Set<ChunkEntity> generatorChunks = generator.generateChunk(chunk);
			if(generatorChunks != null) {
				subChunks.addAll(generatorChunks);
			}
		}
		
		return subChunks;
	}

	@Override
	public boolean willDegenerateChunk(ChunkEntity chunk) {
		return !chain.isEmpty() && chain.get(chain.size() - 1).willDegenerateChunk(chunk);
	}
	
	@Override
	public void degenerateChunk(ChunkEntity chunk) {
		for(int i = chain.size(); i >= 0; i--) {
			chain.get(i).degenerateChunk(chunk);
		}
	}

}
