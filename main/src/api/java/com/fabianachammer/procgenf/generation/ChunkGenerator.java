package com.fabianachammer.procgenf.generation;

import java.util.Collection;

/**
 * Represents a generator that creates the children chunks of a given chunk with
 * all its components
 * 
 * @author fachammer
 *
 */
public interface ChunkGenerator {

	/**
	 * Generates the children of the given chunk, but doesn't add them as
	 * children (done by the GenerationEngine). When generating, the generator
	 * may not change the chunk itself, but only its children.
	 * 
	 * @param chunk
	 *            chunk whose children need to be generated
	 * @return the generated children for the given chunk
	 */
	Collection<ChunkEntity> generateChunkChildren(ChunkEntity chunk);

	/**
	 * Degenerates the given chunk (not its children). This is called by the
	 * GenerationEngine when a chunk does not have to be kept generated because
	 * it is not part of the immediate surroundings. During this call this
	 * Generator can still access the parents of this chunk, but not its
	 * children, as they are already degenerated and removed from the Generation
	 * tree
	 * 
	 * @param chunk
	 *            chunk to be degenerated
	 */
	void degenerateChunk(ChunkEntity chunk);
}
