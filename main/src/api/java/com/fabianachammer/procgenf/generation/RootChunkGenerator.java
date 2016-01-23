package com.fabianachammer.procgenf.generation;

import java.util.Set;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public interface RootChunkGenerator {

	Set<ChunkEntity> generateChunksFromVisibilityRegion(PolygonSimple visibilityRegion);
	
	void degenerateChunks(Set<ChunkEntity> chunks);
}
