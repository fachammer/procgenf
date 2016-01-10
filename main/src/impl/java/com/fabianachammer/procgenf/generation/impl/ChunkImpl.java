package com.fabianachammer.procgenf.generation.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fabianachammer.procgenf.generation.Chunk;
import com.fabianachammer.procgenf.generation.ChunkFeature;

public class ChunkImpl implements Chunk {

	private Chunk parent;
	private Set<Chunk> children;
	private Set<ChunkFeature> features;
	
	public ChunkImpl(){
		children = new HashSet<>();
		features = new HashSet<>();	
	}
	
	@Override
	public Chunk getParent() {
		return parent;
	}

	@Override
	public Chunk setParent(Chunk newParent) {
		this.parent = newParent;
		return this;
	}

	@Override
	public Set<Chunk> getSiblings() {
		if(parent == null)
			return null;		
		
		Set<Chunk> siblings = new HashSet<>(parent.getChildren());
		siblings.remove(this);
		return siblings;
	}
	
	@Override
	public Set<Chunk> getChildren() {
		return children;
	}
	
	@Override
	public Chunk addChild(Chunk child) {
		children.add(child);
		return this;
	}

	@Override
	public Chunk removeChild(Chunk child) {
		children.remove(child);
		return this;
	}

	@Override
	public int getDepth() {
		return parent != null ? parent.getDepth() + 1 : 0;
	}

	@Override
	public Set<ChunkFeature> getFeatures() {
		return features;
	}

	@Override
	public Chunk addFeature(ChunkFeature feature) {
		features.add(feature);		
		return this;
	}

	@Override
	public Chunk removeFeature(ChunkFeature feature) {
		features.remove(feature);
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ChunkImpl))
			return false;
		
		if(this == obj)
			return true;
		
		ChunkImpl rhs = (ChunkImpl) obj;
		return new EqualsBuilder()
				//.append(parent, rhs.parent)
				.append(children, rhs.children)
				.append(features, rhs.features)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				//.append(parent)
				.append(children)
				.append(features)
				.toHashCode();
	}
}
