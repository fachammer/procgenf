package com.fabianachammer.procgenf.generation;

public interface ChunkComponent {

	/**
	 * @return the chunk that this component is contained in
	 */
	ChunkEntity getContainerChunk();
}
