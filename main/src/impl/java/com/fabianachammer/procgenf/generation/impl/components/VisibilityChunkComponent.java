package com.fabianachammer.procgenf.generation.impl.components;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class VisibilityChunkComponent extends ChunkComponentImpl {

	private PolygonSimple visibilityPolygon;
	
	public VisibilityChunkComponent(PolygonSimple visibilityPolygon) {
		super();
		this.visibilityPolygon = visibilityPolygon;
	}

	public PolygonSimple getVisibilityPolygon() {
		return visibilityPolygon;
	}
	
	public VisibilityChunkComponent setVisibilityPolygon(PolygonSimple visiblityPolygon) {
		this.visibilityPolygon = visiblityPolygon;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof VisibilityChunkComponent))
			return false;
		
		if(obj == this)
			return true;
		
		VisibilityChunkComponent rhs = (VisibilityChunkComponent) obj;
		return new EqualsBuilder()
				.append(visibilityPolygon, rhs.visibilityPolygon)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(visibilityPolygon)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("visibilityPolygon", visibilityPolygon)
				.build();
	}
}
