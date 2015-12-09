package com.fabianachammer.procgenf.main.impl;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import org.joml.Vector2d;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class VoronoiGenerator {

	private VoronoiNode root;
	
	public enum GenerationType {
		Noise,
		Square,
		Hexagon
	}
	
	public VoronoiGenerator(VoronoiNode root) {
		this.root = root;
	}
	
	public PolygonSimple generate(PolygonSimple visibilityPolygon, GenerationType type, int seed) {
		return generatePointsIn(root, visibilityPolygon, type, seed);
	}
	
	private static final double GRID_SIZE = 100.0;
	private PolygonSimple generatePointsIn(VoronoiNode node, PolygonSimple visibilityPolygon, GenerationType type, int seed) {	
		
		Rectangle2D generationBounds = calculateGenerationBounds(visibilityPolygon.getBounds2D(), GRID_SIZE);
		Vector2d[] generatedPoints = generatePointsForBounds(generationBounds, GRID_SIZE, type, seed);
		
		for(int i = 0; i < generatedPoints.length; i++) {
			if(!node.containsChildAt(generatedPoints[i]))
				node.addChild(new VoronoiNode(generatedPoints[i]));
		}
		
		for(VoronoiNode child : node.getChildren().toArray(new VoronoiNode[0])) {
			Vector2d position = child.getWorldPosition();
			if(!generationBounds.contains(position.x, position.y))
				node.removeChild(child);
		}
		
		return calculateClipPolygonForVisibleBounds(visibilityPolygon.getBounds2D(), GRID_SIZE);
	}
	
	private static PolygonSimple calculateClipPolygonForVisibleBounds(Rectangle2D visibilityBounds, double gridSize) {
		Rectangle2D generationBounds = calculateGenerationBounds(visibilityBounds, gridSize);
		return new PolygonSimple(new double[]{
			generationBounds.getMinX(), generationBounds.getMaxX(), generationBounds.getMaxX(), generationBounds.getMinX()
		}, new double[]{
			generationBounds.getMinY(), generationBounds.getMinY(), generationBounds.getMaxY(), generationBounds.getMaxY()
		});
	}
	
	private static final int VISIBILITY_OFFSET = 1;
	private static Rectangle2D calculateGenerationBounds(Rectangle2D visibilityBounds, double gridSize) {
		int leftGridX = (int) Math.floor(visibilityBounds.getMinX() / gridSize);
		int rightGridX = (int) Math.floor(visibilityBounds.getMaxX() / gridSize);
		int numberOfXGrids = (rightGridX - leftGridX) + 1;
		
		int bottomGridY = (int) Math.floor(visibilityBounds.getMinY() / gridSize);
		int topGridY = (int) Math.floor(visibilityBounds.getMaxY() / gridSize);
		int numberOfYGrids = (topGridY - bottomGridY) + 1;
		
		return new Rectangle2D.Double(
				(leftGridX - VISIBILITY_OFFSET) * gridSize, 
				(bottomGridY - VISIBILITY_OFFSET) * gridSize, 
				(numberOfXGrids + 2 * VISIBILITY_OFFSET) * gridSize, 
				(numberOfYGrids + 2 * VISIBILITY_OFFSET) * gridSize
			);
	}
	
	public static Vector2d[] generatePointsForBounds(Rectangle2D bounds, double gridSize, GenerationType type, int seed) {
		int leftGridX = (int) Math.floor(bounds.getMinX() / gridSize);
		int rightGridX = (int) Math.floor(bounds.getMaxX() / gridSize);
		int numberOfXGrids = (rightGridX - leftGridX) + 1;
		
		int bottomGridY = (int) Math.floor(bounds.getMinY() / gridSize);
		int topGridY = (int) Math.floor(bounds.getMaxY() / gridSize);
		int numberOfYGrids = (topGridY - bottomGridY) + 1;
		
		Vector2d[] generatedPoints = new Vector2d[numberOfXGrids * numberOfYGrids];
		int i = 0;
		for(int x = 0; x < numberOfXGrids; x++) {
			for(int y = 0; y < numberOfYGrids; y++){
				double xMin = (leftGridX + x) * gridSize;
				double yMin = (bottomGridY + y) * gridSize;
				switch(type) {
				case Noise:
					generatedPoints[i++] = generateNoisePointInBounds(new Rectangle2D.Double(xMin, yMin, gridSize, gridSize), seed);
					break;
				case Square:
					generatedPoints[i++] = generateSquarePointInBounds(new Rectangle2D.Double(xMin, yMin, gridSize, gridSize));
					break;
				case Hexagon:
					generatedPoints[i++] = generateHexagonPointInBounds(new Rectangle2D.Double(xMin, yMin, gridSize, gridSize), gridSize);
				}
				
			}
		}
		
		return generatedPoints;
	}
	
	private static Random random = new Random();
	public static Vector2d generateNoisePointInBounds(Rectangle2D.Double bounds, int seed) {
		random.setSeed(seed);
		double x = Noise.valueCoherentNoise3D(bounds.getCenterX(), bounds.getCenterY(), 0, random.nextInt(), NoiseQuality.BEST) / 2 + 0.5;
		double y = Noise.valueCoherentNoise3D(bounds.getCenterX(), bounds.getCenterY(), 0, random.nextInt(), NoiseQuality.BEST) / 2 + 0.5;
		return new Vector2d(bounds.getMinX() + x * bounds.getWidth(),bounds.getMinY() + y * bounds.getHeight());
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
