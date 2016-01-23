package com.fabianachammer.procgenf.generation.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkComponent;

public class ChunkEntityImpl implements ChunkEntity {

	private ChunkEntity parent;
	private Set<ChunkEntity> children;
	private Set<ChunkComponent> components;
	
	public ChunkEntityImpl(){
		children = new HashSet<>();
		components = new HashSet<>();	
	}
	
	@Override
	public ChunkEntity getParent() {
		return parent;
	}

	@Override
	public ChunkEntity setParent(ChunkEntity newParent) {
		this.parent = newParent;
		return this;
	}

	@Override
	public Set<ChunkEntity> getSiblings() {
		if(parent == null)
			return null;		
		
		Set<ChunkEntity> siblings = new HashSet<>(parent.getChildren());
		siblings.remove(this);
		return siblings;
	}
	
	@Override
	public Set<ChunkEntity> getChildren() {
		return children;
	}
	
	@Override
	public ChunkEntity addChild(ChunkEntity child) {
		children.add(child);
		return this;
	}

	@Override
	public ChunkEntity removeChild(ChunkEntity child) {
		children.remove(child);
		return this;
	}

	@Override
	public int getDepth() {
		return parent != null ? parent.getDepth() + 1 : 0;
	}

	@Override
	public Set<ChunkComponent> getComponents() {
		return components;
	}

	@Override
	public ChunkEntity addComponent(ChunkComponent component) {
		components.add(component);		
		return this;
	}

	@Override
	public ChunkEntity removeComponent(ChunkComponent component) {
		components.remove(component);
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ChunkEntityImpl))
			return false;
		
		if(this == obj)
			return true;
		
		ChunkEntityImpl rhs = (ChunkEntityImpl) obj;
		return new EqualsBuilder()
				//.append(parent, rhs.parent)
				.append(children, rhs.children)
				.append(components, rhs.components)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				//.append(parent)
				.append(children)
				.append(components)
				.toHashCode();
	}
}