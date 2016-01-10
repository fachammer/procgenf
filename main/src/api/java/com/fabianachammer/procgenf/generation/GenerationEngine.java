package com.fabianachammer.procgenf.generation;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public interface GenerationEngine {

	/**
	 * Adds a generator to this generation engine. The order in which generators
	 * are added is crucial because this is the order in which they are applied
	 * to a chunk, if more than one generator is applied. When degenerating,
	 * they are applied in the reverse order.
	 * 
	 * @param generator
	 *            generator to be added
	 * @return this generation engine
	 */
	GenerationEngine addGenerator(ChunkGenerator generator);

	GenerationEngine run(PolygonSimple visibilityRegion);

	Chunk getRootChunk();
}
