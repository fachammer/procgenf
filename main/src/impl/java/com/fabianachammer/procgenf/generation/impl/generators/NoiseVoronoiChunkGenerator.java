package com.fabianachammer.procgenf.generation.impl.generators;

import static com.fabianachammer.procgenf.generation.impl.Utility.getChunkComponent;
import static com.fabianachammer.procgenf.generation.impl.Utility.getRoot;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.joml.Matrix3d;
import org.joml.Vector2d;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkGenerator;
import com.fabianachammer.procgenf.generation.impl.ChunkEntityImpl;
import com.fabianachammer.procgenf.generation.impl.components.GenerationBoundsChunkComponent;
import com.fabianachammer.procgenf.generation.impl.components.SeedChunkComponent;
import com.fabianachammer.procgenf.generation.impl.components.VoronoiChunkComponent;
import com.fabianachammer.procgenf.main.impl.PolygonTransformer;
import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class NoiseVoronoiChunkGenerator implements ChunkGenerator {

	public enum GenerationType {
		Noise, Square, Hexagon
	}

	private int maxDepth;

	public NoiseVoronoiChunkGenerator(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	@Override
	public boolean willGenerateChunk(ChunkEntity chunk) {
		return getChunkComponent(chunk, GenerationBoundsChunkComponent.class).isPresent()
				&& getChunkComponent(getRoot(chunk), SeedChunkComponent.class).isPresent()
				&& getChunkComponent(chunk, VoronoiChunkComponent.class).isPresent() 
				&& chunk.getDepth() < maxDepth;
	}

	@Override
	public Set<ChunkEntity> generateChunk(ChunkEntity chunk) {		
		GenerationBoundsChunkComponent generationBoundsComponent = getChunkComponent(chunk, GenerationBoundsChunkComponent.class).get();
		Rectangle2D.Double generationBounds = generationBoundsComponent.getGenerationBounds();
		double gridSize = generationBoundsComponent.getGridSize();
	
		int seed = getChunkComponent(getRoot(chunk), SeedChunkComponent.class).get().getSeed();
		
		VoronoiChunkComponent parent = getChunkComponent(chunk, VoronoiChunkComponent.class).get();
		Vector2d parentPosition = parent.getWorldPosition();
		List<ChunkEntity> subChunks = new ArrayList<>();
		OpenList sites = new OpenList();
		for(double x = generationBounds.getMinX(); x < generationBounds.getMaxX(); x += gridSize) {
			for(double y = generationBounds.getMinY(); y < generationBounds.getMaxY(); y += gridSize) {
				Rectangle2D.Double gridBounds = new Rectangle2D.Double(x, y, gridSize, gridSize);
				Vector2d generatedPoint = generatePointInBounds(gridBounds, GenerationType.Noise, seed);
				generatedPoint.sub(parentPosition);
				Site site = new Site(generatedPoint.x, generatedPoint.y, 0);
				sites.add(site);
				subChunks.add(new ChunkEntityImpl()
						.addComponent(new VoronoiChunkComponent(site))
						.addComponent(new GenerationBoundsChunkComponent()
								.setGenerationBounds(gridBounds)
								.setGridSize(gridSize / 2)));
			}
		}
		
		
		Matrix3d parentToLocalMatrix = new Matrix3d(parent.getLocalToParentTransform());
		parentToLocalMatrix.invert();
		PolygonSimple clipPolygon = PolygonTransformer.transformPolygon(parent.getPolygon() != null ? parent.getPolygon() : calculatePolygonForRectangle(generationBounds), parentToLocalMatrix);
		
		PowerDiagram powerDiagram = new PowerDiagram(sites, clipPolygon);
		
		powerDiagram.computeDiagram();
		
		return new HashSet<>(subChunks);
	}

	public static PolygonSimple calculatePolygonForRectangle(Rectangle2D.Double rectangle) {
		return new PolygonSimple(
				new double[] { rectangle.getMinX(), rectangle.getMaxX(), rectangle.getMaxX(), rectangle.getMinX() },
				new double[] { rectangle.getMinY(), rectangle.getMinY(), rectangle.getMaxY(), rectangle.getMaxY() });
	}

	@Override
	public boolean willDegenerateChunk(ChunkEntity chunk) {
		return getChunkComponent(chunk, GenerationBoundsChunkComponent.class).isPresent()
				&& getChunkComponent(chunk, VoronoiChunkComponent.class).isPresent()
				&& chunk.getDepth() <= maxDepth;
	}
	
	@Override
	public void degenerateChunk(ChunkEntity chunk) {
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
				NoiseQuality.BEST);
		double y = Noise.valueCoherentNoise3D(bounds.getCenterX(), bounds.getCenterY(), 0, random.nextInt(),
				NoiseQuality.BEST);
		
		x = mapBetweenRanges(x, -1, 1, 0.1, 0.9);
		y = mapBetweenRanges(y, -1, 1, 0.1, 0.9);
		return new Vector2d(bounds.getMinX() + x * bounds.getWidth(), bounds.getMinY() + y * bounds.getHeight());
	}
	
	private static double mapBetweenRanges(double value, double inMin, double inMax, double outMin, double outMax) {
		return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
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
