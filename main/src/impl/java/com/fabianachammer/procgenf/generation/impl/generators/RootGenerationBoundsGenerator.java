package com.fabianachammer.procgenf.generation.impl.generators;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkGenerator;
import com.fabianachammer.procgenf.generation.impl.ChunkEntityImpl;
import com.fabianachammer.procgenf.generation.impl.components.GenerationBoundsChunkComponent;
import com.fabianachammer.procgenf.generation.impl.components.VisibilityChunkComponent;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

import static com.fabianachammer.procgenf.generation.impl.Utility.getChunkComponent;

public class RootGenerationBoundsGenerator implements ChunkGenerator {

	private double gridSize;
	private int visibilityOffset;
	
	public RootGenerationBoundsGenerator(double gridSize, int visibilityOffset) {
		this.gridSize = gridSize;
		this.visibilityOffset = visibilityOffset;
	}
	
	
	public boolean willGenerateChunk(ChunkEntity chunk) {
		return getChunkComponent(chunk, VisibilityChunkComponent.class).isPresent();
	}
	
	@Override
	public Collection<ChunkEntity> generateChunkChildren(ChunkEntity chunk) {
		if(!willGenerateChunk(chunk))
			return null;
		
		VisibilityChunkComponent visibilityComponent = getChunkComponent(chunk, VisibilityChunkComponent.class).get();
		Rectangle2D.Double generationBounds = calculateGenerationBoundsFromVisibilityRegion(visibilityComponent.getVisibilityPolygon());
		
		List<ChunkEntity> subChunks = new ArrayList<>();
		subChunks.add(new ChunkEntityImpl()
				.addComponent(new GenerationBoundsChunkComponent()
						.setGenerationBounds(generationBounds)
						.setGridSize(gridSize)));
		
		return subChunks;
	}

	private Rectangle2D.Double calculateGenerationBoundsFromVisibilityRegion(PolygonSimple visibilityRegion) {
		Rectangle2D bounds = visibilityRegion.getBounds2D();
		int leftGridX = (int) Math.floor(bounds.getMinX() / gridSize) - visibilityOffset;
		int rightGridX = (int) Math.floor(bounds.getMaxX() / gridSize) + visibilityOffset;

		int topGridY = (int) Math.floor(bounds.getMinY() / gridSize) - visibilityOffset;
		int bottomGridY = (int) Math.floor(bounds.getMaxY() / gridSize) + visibilityOffset;
		
		int numXGrids = rightGridX - leftGridX + 1;
		int numYGrids = bottomGridY - topGridY + 1;
		
		Rectangle2D.Double generationBounds = new Rectangle2D.Double(
				leftGridX * gridSize, 
				topGridY * gridSize, 
				numXGrids * gridSize,
				numYGrids * gridSize
		);
		return generationBounds;
	}

	@Override
	public void degenerateChunk(ChunkEntity chunk) {
	}
}
