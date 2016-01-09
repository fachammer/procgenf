package com.fabianachammer.procgenf.generation;

import java.util.Set;

public interface Chunk {

	Chunk getParent();
	
	Chunk setParent(Chunk newParent);
	
	Set<Chunk> getChildren();
	
	int getDepth();
}
