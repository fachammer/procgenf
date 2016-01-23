package com.fabianachammer.procgenf.generation.impl;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkComponent;
import com.fabianachammer.procgenf.main.impl.VoronoiNode;

public class RootChunkComponent implements ChunkComponent {
	
	public enum GenerationType {
		Noise,
		Square,
		Hexagon
	}
	
	private VoronoiNode rootNode;
	private int seed;
	private GenerationType generationType;
	private ChunkEntity chunk;
	private float gridSize;
	private int visibilityOffset;
	
	public RootChunkComponent(ChunkEntity chunk, VoronoiNode rootNode, int seed, GenerationType generationType, float gridSize, int visibilityOffset) {
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
	
	public RootChunkComponent setRootNode(VoronoiNode rootNode){
		this.rootNode = rootNode;
		return this;
	}
	
	public int getSeed(){
		return seed;
	}
	
	public RootChunkComponent setSeed(int seed){
		this.seed = seed;
		return this;
	}
	
	public GenerationType getGenerationType(){
		return generationType;
	}
	
	public RootChunkComponent setGenerationType(GenerationType generationType){
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
	public ChunkEntity getContainerChunk() {
		return chunk;
	}
}
