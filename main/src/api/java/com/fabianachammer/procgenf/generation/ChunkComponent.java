package com.fabianachammer.procgenf.generation;

public interface ChunkComponent {

	/**
	 * @return the chunk that this component is contained in
	 */
	ChunkEntity getContainerChunk();
	
	/**
	 * Sets the chunk that this component is contained it, such that getContainerChunk() returns the newly set component afterwards.
	 * @param chunk new container chunk
	 * @return this chunk component
	 */
	ChunkComponent setContainerChunk(ChunkEntity chunk);
}
