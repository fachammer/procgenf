package com.fabianachammer.procgenf.generation;

import java.util.Set;

/**
 * Represents a part of the world to be generated. Chunk features can be added
 * to extend the generation process for this chunk
 * 
 * @author fachammer
 *
 */
public interface Chunk {
	
	/**
	 * @return The parent of the chunk or null, if it is the root element.
	 */
	Chunk getParent();

	/**
	 * Sets a new parent for this chunk.
	 * 
	 * @param newParent
	 *            New chunk parent
	 * @return this chunk.
	 */
	Chunk setParent(Chunk newParent);

	/**
	 * @return The siblings of this chunk or the empty set, if the chunk has no
	 *         siblings.
	 */
	Set<Chunk> getSiblings();

	/**
	 * @return All children of this chunk.
	 */
	Set<Chunk> getChildren();

	/**
	 * Adds the given child to the children of this chunk, if it wasn't added
	 * before
	 * 
	 * @param child
	 *            child to be added
	 * @return this chunk
	 */
	Chunk addChild(Chunk child);

	/**
	 * Removes the given child from the children of this chunk, if it was added
	 * before
	 * 
	 * @param child
	 *            child to be removed
	 * @return this chunk
	 */
	Chunk removeChild(Chunk child);

	/**
	 * @return Number of ancestors for this chunk
	 */
	int getDepth();

	/**
	 * @return set of chunk features ordered by insertion order
	 */
	Set<ChunkFeature> getFeatures();

	/**
	 * Adds the given feature to this chunk, if it wasn't added before
	 * 
	 * @param feature
	 *            feature to be added
	 * @return this chunk
	 */
	Chunk addFeature(ChunkFeature feature);

	/**
	 * Removes the given feature from this chunk, if it was added before
	 * 
	 * @param feature
	 *            feature to be removed
	 * @return this chunk
	 */
	Chunk removeFeature(ChunkFeature feature);
}
