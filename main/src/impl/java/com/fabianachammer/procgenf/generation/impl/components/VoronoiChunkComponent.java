package com.fabianachammer.procgenf.generation.impl.components;

import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joml.Matrix3d;
import org.joml.Vector2d;
import org.joml.Vector3d;

import com.fabianachammer.procgenf.generation.ChunkComponent;
import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.impl.Utility;

import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class VoronoiChunkComponent extends ChunkComponentImpl {

	private Site site;
	private Matrix3d localToParentTransform;

	public VoronoiChunkComponent(Site site) {
		super();
		this.site = site;
		this.localToParentTransform = new Matrix3d();
		this.localToParentTransform.m20 = site.x;
		this.localToParentTransform.m21 = site.y;
	}

	public Site getSite() {
		return site;
	}

	public Matrix3d getLocalToParentTransform() {
		return localToParentTransform;
	}

	public PolygonSimple getPolygon() {
		return site.getPolygon();
	}

	public Optional<VoronoiChunkComponent> getParent() {
		ChunkEntity container = getContainerChunk();
		if(container == null)
			return Optional.empty();

		ChunkEntity parent = container.getParent();
		if(parent == null)
			return Optional.empty();

		return Utility.getChunkComponent(parent, VoronoiChunkComponent.class);
	}

	public Matrix3d getLocalToWorldTransform() {
		Matrix3d localToWorldTransform = new Matrix3d();
		localToWorldTransform.set(getLocalToParentTransform());

		getParent().ifPresent(p -> localToWorldTransform.mul(p.getLocalToWorldTransform()));

		return localToWorldTransform;
	}

	public Vector2d getSitePosition() {
		return new Vector2d(site.getX(), site.getY());
	}

	public Vector2d getSiteInParentPosition() {
		Vector3d intermediate = new Vector3d(site.getX(), site.getY(), 1);
		intermediate.mul(getLocalToParentTransform());
		return new Vector2d(intermediate.x, intermediate.y);
	}

	public Vector2d getWorldPosition() {
		Vector3d intermediate = new Vector3d(0, 0, 1);
		intermediate.mul(getLocalToWorldTransform());
		return new Vector2d(intermediate.x, intermediate.y);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof VoronoiChunkComponent))
			return false;

		if(this == obj)
			return true;

		VoronoiChunkComponent rhs = (VoronoiChunkComponent) obj;
		return new EqualsBuilder().append(site, rhs.site).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(site).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("site", site).build();
	}

	@Override
	public ChunkComponent clone() {
		return new VoronoiChunkComponent(site.clone());
	}
}
