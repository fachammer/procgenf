package com.fabianachammer.procgenf.generation;

/**
 * Represents a component of a chunk that holds data that extends the chunk
 * 
 * @author fachammer
 *
 */
public interface ChunkComponent extends Cloneable {

	/**
	 * @return the chunk that this component is contained in
	 */
	ChunkEntity getContainerChunk();

	/**
	 * Sets the chunk that this component is contained it, such that
	 * getContainerChunk() returns the newly set component afterwards.
	 * 
	 * @param chunk
	 *            new container chunk
	 * @return this chunk component
	 */
	ChunkComponent setContainerChunk(ChunkEntity chunk);

	ChunkComponent clone();
}
