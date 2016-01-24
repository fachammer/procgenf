package com.fabianachammer.procgenf.generation.impl.components;

import com.fabianachammer.procgenf.generation.ChunkComponent;
import com.fabianachammer.procgenf.generation.ChunkEntity;

public class ChunkComponentImpl implements ChunkComponent {

	private ChunkEntity containerChunk;
	
	public ChunkComponentImpl() {
		this.containerChunk = null;
	}
	
	@Override
	public ChunkEntity getContainerChunk() {
		return containerChunk;
	}

	@Override
	public ChunkComponent setContainerChunk(ChunkEntity chunk) {
		this.containerChunk = chunk;
		return this;
	}
}
