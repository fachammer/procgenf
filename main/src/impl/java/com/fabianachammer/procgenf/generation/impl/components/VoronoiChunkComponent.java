package com.fabianachammer.procgenf.generation.impl.components;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fabianachammer.procgenf.main.impl.VoronoiNode;

public class VoronoiChunkComponent extends ChunkComponentImpl {

	private VoronoiNode node;
	
	public VoronoiChunkComponent(VoronoiNode node){
		super();
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
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("node", node)
				.build();
	}
}
