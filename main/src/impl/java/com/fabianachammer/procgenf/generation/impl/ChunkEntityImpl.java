package com.fabianachammer.procgenf.generation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.ChunkComponent;

public class ChunkEntityImpl implements ChunkEntity {

	private ChunkEntity parent;
	private List<ChunkEntity> children;
	private List<ChunkComponent> components;
	
	public ChunkEntityImpl(){
		children = new ArrayList<>();
		components = new ArrayList<>();	
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
	public Collection<ChunkEntity> getChildren() {
		return Collections.unmodifiableCollection(children);
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
	public Collection<ChunkComponent> getComponents() {
		return Collections.unmodifiableCollection(components);
	}

	@Override
	public ChunkEntity addComponent(ChunkComponent component) {
		components.add(component);		
		component.setContainerChunk(this);
		return this;
	}

	@Override
	public ChunkEntity removeComponent(ChunkComponent component) {
		components.remove(component);
		component.setContainerChunk(null);
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
				.append(components, rhs.components)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(components)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("components", components)
				.build();
	}
	
	@Override
	public ChunkEntity clone() {
		ChunkEntityImpl clone = new ChunkEntityImpl();
		for(ChunkComponent component : components)
			clone.addComponent(component.clone());
		
		return clone;
	}
}
