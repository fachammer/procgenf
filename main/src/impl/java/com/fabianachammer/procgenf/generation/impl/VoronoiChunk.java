package com.fabianachammer.procgenf.generation.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fabianachammer.procgenf.generation.Chunk;
import com.fabianachammer.procgenf.generation.ChunkFeature;
import com.fabianachammer.procgenf.main.impl.VoronoiNode;

public class VoronoiChunk implements ChunkFeature {

	private VoronoiNode node;
	private Chunk chunk;
	
	public VoronoiChunk(Chunk chunk, VoronoiNode node){
		this.chunk = chunk;
		setNode(node);
	}
	
	public VoronoiChunk setNode(VoronoiNode node){
		this.node = node;
		return this;
	}
	
	public VoronoiNode getNode(){
		return node;
	}

	@Override
	public Chunk getContainerChunk() {
		return chunk;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof VoronoiChunk))
			return false;
		
		if(this == obj)
			return true;
		
		VoronoiChunk rhs = (VoronoiChunk) obj;
		return new EqualsBuilder()
				.append(node, rhs.node)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(node)
				.toHashCode();
	}
}
