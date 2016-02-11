package com.fabianachammer.procgenf.generation.impl.components;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fabianachammer.procgenf.generation.ChunkComponent;
import com.fabianachammer.procgenf.generation.impl.generators.NoiseVoronoiChunkGenerator.GenerationType;

public class SeedChunkComponent extends ChunkComponentImpl {

	private int seed;
	private GenerationType type;
	
	public SeedChunkComponent(int seed, GenerationType type) {
		super();
		this.seed = seed;
		this.type = type;
	}

	public int getSeed() {
		return seed;
	}
	
	public SeedChunkComponent setSeed(int seed){
		this.seed = seed;
		return this;
	}
	
	public GenerationType getType() {
		return type;
	}
	
	public SeedChunkComponent setType(GenerationType type) {
		this.type = type;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SeedChunkComponent))
			return false;
		
		if(obj == this)
			return true;
		
		SeedChunkComponent rhs = (SeedChunkComponent) obj;
		return new EqualsBuilder()
				.append(seed, rhs.seed)
				.append(type, rhs.type)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(seed)
				.append(type)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("seed", seed)
				.build();			
	}

	@Override
	public ChunkComponent clone() {
		return new SeedChunkComponent(seed, type);
	}
}
