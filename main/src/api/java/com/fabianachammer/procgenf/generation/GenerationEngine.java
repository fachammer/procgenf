package com.fabianachammer.procgenf.generation;

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

	/**
	 * Runs the generation loop by starting at the root chunk, generating it and
	 * then continue with the process on the child chunks until no more chunks
	 * are generated
	 * 
	 * @return this generation engine
	 */
	GenerationEngine run(ChunkEntity rootChunk);
}
