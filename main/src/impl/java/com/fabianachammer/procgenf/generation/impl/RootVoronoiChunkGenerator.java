package com.fabianachammer.procgenf.generation.impl;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.joml.Vector2d;

import com.fabianachammer.procgenf.generation.Chunk;
import com.fabianachammer.procgenf.generation.RootChunkGenerator;
import com.fabianachammer.procgenf.generation.impl.RootChunk.GenerationType;
import com.fabianachammer.procgenf.main.impl.VoronoiNode;
import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import static com.fabianachammer.procgenf.generation.impl.Utility.getChunkFeature;

public class RootVoronoiChunkGenerator implements RootChunkGenerator {
	private RootChunk root;

	public RootVoronoiChunkGenerator(RootChunk root) {
		this.root = root;
	}

	@Override
	public Set<Chunk> generateChunksFromVisibilityRegion(PolygonSimple visibilityRegion) {
		float gridSize = root.getGridSize();
		int visibilityOffset = root.getVisibilityOffset();
		Rectangle2D bounds = visibilityRegion.getBounds2D();
		int leftGridX = (int) Math.floor(bounds.getMinX() / gridSize) - visibilityOffset;
		int rightGridX = (int) Math.floor(bounds.getMaxX() / gridSize) + visibilityOffset;

		int bottomGridY = (int) Math.floor(bounds.getMinY() / gridSize) - visibilityOffset;
		int topGridY = (int) Math.floor(bounds.getMaxY() / gridSize) + visibilityOffset;

		int numberOfXGrids = rightGridX - leftGridX + 1;
		int numberOfYGrids = topGridY - bottomGridY + 1;
		
		Rectangle2D generationBounds = new Rectangle2D.Double(leftGridX * gridSize, bottomGridY * gridSize, numberOfXGrids * gridSize, numberOfYGrids * gridSize);
		root.getRootNode().setClipPolygon(calculateClipPolygonForVisibleBounds(generationBounds));
		Set<Chunk> subChunks = new HashSet<>();
		for(int x = leftGridX; x <= rightGridX; x++) {
			for(int y = bottomGridY; y <= topGridY; y++) {
				double xMin = x * gridSize;
				double yMin = y * gridSize;
				Vector2d generatedPoint = generatePointInBounds(new Rectangle2D.Double(xMin, yMin, gridSize, gridSize),
						root.getGenerationType(), root.getSeed());
				VoronoiNode node = new VoronoiNode(generatedPoint).setParent(root.getRootNode());
				Chunk chunk = new ChunkImpl();
				VoronoiChunk voronoiFeature = new VoronoiChunk(chunk, node);
				chunk.addFeature(voronoiFeature);
				subChunks.add(chunk);
			}
		}
		
		root.getRootNode().recomputeSubDiagram();

		return subChunks;
	}
	
	@Override
	public void degenerateChunks(Set<Chunk> chunks) {
		chunks.forEach(chunk -> {
			Optional<VoronoiChunk> voronoiFeature = getChunkFeature(chunk, VoronoiChunk.class);
			if(voronoiFeature.isPresent())
				voronoiFeature.get().getNode().setParent(null);
		});
	}
	
	private static PolygonSimple calculateClipPolygonForVisibleBounds(Rectangle2D generationBounds) {
		return new PolygonSimple(new double[]{
			generationBounds.getMinX(), generationBounds.getMaxX(), generationBounds.getMaxX(), generationBounds.getMinX()
		}, new double[]{
			generationBounds.getMinY(), generationBounds.getMinY(), generationBounds.getMaxY(), generationBounds.getMaxY()
		});
	}

	private Vector2d generatePointInBounds(Rectangle2D.Double bounds, GenerationType type, int seed) {
		switch(type) {
		case Noise:
			return generateNoisePointInBounds(bounds, seed);
		case Square:
			return generateSquarePointInBounds(bounds);
		case Hexagon:
			return generateHexagonPointInBounds(bounds, bounds.getWidth());
		}

		return null;
	}

	private static Random random = new Random();
	public static Vector2d generateNoisePointInBounds(Rectangle2D.Double bounds, int seed) {
		random.setSeed(seed);
		double x = Noise.valueCoherentNoise3D(bounds.getCenterX(), bounds.getCenterY(), 0, random.nextInt(),
				NoiseQuality.BEST) / 2 + 0.5;
		double y = Noise.valueCoherentNoise3D(bounds.getCenterX(), bounds.getCenterY(), 0, random.nextInt(),
				NoiseQuality.BEST) / 2 + 0.5;
		return new Vector2d(bounds.getMinX() + x * bounds.getWidth(), bounds.getMinY() + y * bounds.getHeight());
	}

	public static Vector2d generateSquarePointInBounds(Rectangle2D.Double bounds) {
		double x = bounds.getCenterX();
		double y = bounds.getCenterY();
		return new Vector2d(x, y);
	}

	public static Vector2d generateHexagonPointInBounds(Rectangle2D.Double bounds, double gridSize) {
		int x = (int) Math.floor(bounds.getMinY() / gridSize);
		int offSetSign = x % 2 == 0 ? -1 : 1;
		double offset = offSetSign * bounds.getWidth() / 4;

		return new Vector2d(bounds.getCenterX() + offset, bounds.getCenterY());
	}

	
}
