package com.fabianachammer.procgenf.generation;

import java.util.Set;

/**
 * Represents a part of the world to be generated. Chunk components can be added
 * to extend the generation process for this chunk
 * 
 * @author fachammer
 *
 */
public interface ChunkEntity extends Cloneable {

	/**
	 * @return The parent of the chunk or null, if it is the root element.
	 */
	ChunkEntity getParent();

	/**
	 * Sets a new parent for this chunk.
	 * 
	 * @param newParent
	 *            New chunk parent
	 * @return this chunk.
	 */
	ChunkEntity setParent(ChunkEntity newParent);

	/**
	 * @return The siblings of this chunk or the empty set, if the chunk has no
	 *         siblings.
	 */
	Set<ChunkEntity> getSiblings();

	/**
	 * @return All children of this chunk.
	 */
	Set<ChunkEntity> getChildren();

	/**
	 * Adds the given child to the children of this chunk, if it wasn't added
	 * before
	 * 
	 * @param child
	 *            child to be added
	 * @return this chunk
	 */
	ChunkEntity addChild(ChunkEntity child);

	/**
	 * Removes the given child from the children of this chunk, if it was added
	 * before
	 * 
	 * @param child
	 *            child to be removed
	 * @return this chunk
	 */
	ChunkEntity removeChild(ChunkEntity child);

	/**
	 * @return Number of ancestors for this chunk
	 */
	int getDepth();

	/**
	 * @return set of chunk components ordered by insertion order
	 */
	Set<ChunkComponent> getComponents();

	/**
	 * Adds the given component to this chunk, if it wasn't added before
	 * 
	 * @param component
	 *            component to be added
	 * @return this chunk
	 */
	ChunkEntity addComponent(ChunkComponent component);

	/**
	 * Removes the given component from this chunk, if it was added before
	 * 
	 * @param coponent
	 *            component to be removed
	 * @return this chunk
	 */
	ChunkEntity removeComponent(ChunkComponent coponent);

	/**
	 * Called by the generation engine to tell the chunk that its generation has
	 * finished
	 */
	void onGenerated();
	
	ChunkEntity clone();
}
