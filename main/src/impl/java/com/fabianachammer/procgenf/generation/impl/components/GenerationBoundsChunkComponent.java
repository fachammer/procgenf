package com.fabianachammer.procgenf.generation.impl.components;

import java.awt.geom.Rectangle2D;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fabianachammer.procgenf.generation.ChunkComponent;

public class GenerationBoundsChunkComponent extends ChunkComponentImpl {

	private Rectangle2D.Double generationBounds;
	private double gridSize;
	
	public Rectangle2D.Double getGenerationBounds() {
		return generationBounds;
	}
	
	public GenerationBoundsChunkComponent setGenerationBounds(Rectangle2D.Double generationBounds) {
		this.generationBounds = generationBounds;
		return this;
	}
	
	public double getGridSize() {
		return gridSize;
	}
	
	public GenerationBoundsChunkComponent setGridSize(double gridSize) {
		this.gridSize = gridSize;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof GenerationBoundsChunkComponent))
			return false;
		
		if(obj == this)
			return true;
		
		GenerationBoundsChunkComponent rhs = (GenerationBoundsChunkComponent) obj;
		return new EqualsBuilder()
				.append(generationBounds, rhs.generationBounds)
				.append(gridSize, rhs.gridSize)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(generationBounds)
				.append(gridSize)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("generationBounds", generationBounds)
				.append("gridSize", gridSize)
				.build();
	}
	
	@Override
	public ChunkComponent clone() {
		return new GenerationBoundsChunkComponent()
				.setGenerationBounds((Rectangle2D.Double) generationBounds.clone())
				.setGridSize(gridSize);
	}
}
