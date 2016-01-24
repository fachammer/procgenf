package com.fabianachammer.procgenf.generation.impl;

import java.awt.geom.Rectangle2D;
import java.util.Optional;

import com.fabianachammer.procgenf.generation.ChunkEntity;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

import com.fabianachammer.procgenf.generation.ChunkComponent;

public class Utility {

	@SuppressWarnings("unchecked")
	public static <T extends ChunkComponent> Optional<T> getChunkComponent(ChunkEntity chunk, Class<T> componentClass) {
		return chunk.getComponents().stream().filter(c -> componentClass.isAssignableFrom(c.getClass())).map(f -> (T) f).findFirst();
	}
	
	public static ChunkEntity getRoot(ChunkEntity chunkEntity) {
		if(chunkEntity == null)
			return null;
		
		ChunkEntity root = chunkEntity;
		while(root.getParent() != null)
			root = root.getParent();
		
		return root;
	}
	
	public static PolygonSimple calculatePolygonForRectangle(Rectangle2D.Double rectangle) {
		return new PolygonSimple(new double[]{
				rectangle.getMinX(), rectangle.getMaxX(), rectangle.getMaxX(), rectangle.getMinX()
		}, new double[]{
				rectangle.getMinY(), rectangle.getMinY(), rectangle.getMaxY(), rectangle.getMaxY()
		});
	}
}
