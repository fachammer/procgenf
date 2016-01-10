package com.fabianachammer.procgenf.generation;

import java.util.Set;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public interface RootChunkGenerator {

	Set<Chunk> generateChunksFromVisibilityRegion(PolygonSimple visibilityRegion);
	
	void degenerateChunks(Set<Chunk> chunks);
}
