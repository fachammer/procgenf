package com.fabianachammer.procgenf.generation.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkComponent;
import com.fabianachammer.procgenf.main.impl.VoronoiNode;

public class VoronoiChunkComponent implements ChunkComponent {

	private VoronoiNode node;
	private ChunkEntity chunk;
	
	public VoronoiChunkComponent(ChunkEntity chunk, VoronoiNode node){
		this.chunk = chunk;
		setNode(node);
	}
	
	public VoronoiChunkComponent setNode(VoronoiNode node){
		this.node = node;
		return this;
	}
	
	public VoronoiNode getNode(){
		return node;
	}

	@Override
	public ChunkEntity getContainerChunk() {
		return chunk;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof VoronoiChunkComponent))
			return false;
		
		if(this == obj)
			return true;
		
		VoronoiChunkComponent rhs = (VoronoiChunkComponent) obj;
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
