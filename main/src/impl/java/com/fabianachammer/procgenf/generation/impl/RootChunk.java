package com.fabianachammer.procgenf.generation.impl;

import com.fabianachammer.procgenf.generation.Chunk;
import com.fabianachammer.procgenf.generation.ChunkFeature;
import com.fabianachammer.procgenf.main.impl.VoronoiNode;

public class RootChunk implements ChunkFeature {
	
	public enum GenerationType {
		Noise,
		Square,
		Hexagon
	}
	
	private VoronoiNode rootNode;
	private int seed;
	private GenerationType generationType;
	private Chunk chunk;
	private float gridSize;
	private int visibilityOffset;
	
	public RootChunk(Chunk chunk, VoronoiNode rootNode, int seed, GenerationType generationType, float gridSize, int visibilityOffset) {
		this.chunk = chunk;
		this.rootNode = rootNode;
		this.seed = seed;
		this.generationType = generationType;
		this.gridSize = gridSize;
		this.visibilityOffset = visibilityOffset;
	}
	
	public VoronoiNode getRootNode() {
		return rootNode;
	}
	
	public RootChunk setRootNode(VoronoiNode rootNode){
		this.rootNode = rootNode;
		return this;
	}
	
	public int getSeed(){
		return seed;
	}
	
	public RootChunk setSeed(int seed){
		this.seed = seed;
		return this;
	}
	
	public GenerationType getGenerationType(){
		return generationType;
	}
	
	public RootChunk setGenerationType(GenerationType generationType){
		this.generationType = generationType;
		return this;
	}
	
	public float getGridSize(){
		return gridSize;
	}
	
	public int getVisibilityOffset(){
		return visibilityOffset;
	}

	@Override
	public Chunk getContainerChunk() {
		return chunk;
	}
}
