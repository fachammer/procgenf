package com.fabianachammer.procgenf.main.impl;

import java.util.HashSet;
import java.util.Set;

import org.joml.Matrix3d;
import org.joml.Vector2d;
import org.joml.Vector3d;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class VoronoiNode {

	private VoronoiNode parent;
	private Set<VoronoiNode> children = new HashSet<VoronoiNode>();
	private Site site;
	private PowerDiagram subDiagram;
	private Vector2d localPosition;
	private OpenList subSites;
	private PolygonSimple clipPolygon;
	private Matrix3d localToParentTransform = new Matrix3d();
	private Matrix3d parentToLocalTransform = new Matrix3d();
	private Matrix3d localToWorldTransform = new Matrix3d();
	
	public VoronoiNode() {
		this(new Vector2d());
	}
	
	public VoronoiNode(Vector2d localPosition) {
		this(localPosition, null);
	}
	
	public VoronoiNode(Vector2d localPosition, PolygonSimple clipPolygon) {
		this.clipPolygon = clipPolygon;
		site = new Site(localPosition.x, localPosition.y, 0);
		setLocalPosition(localPosition);
		
		subSites = new OpenList();
		subDiagram = new PowerDiagram();
		subDiagram.setSites(subSites);
		
		if(clipPolygon != null)
			subDiagram.setClipPoly(clipPolygon);
	}
	
	public Set<VoronoiNode> getChildren() {
		return children;
	}
	
	private void reconstructSubSites() {
		subSites.clear();
		for(VoronoiNode child : children) {
			subSites.add(child.site);
		}
	}
	
	private PolygonSimple getClipPolygon() {
		if(site.getPolygon() != null)
			return site.getPolygon();
		
		if(clipPolygon != null)
			return clipPolygon;
		
		return null;
	}
	
	// Refresh must go top-down
	private void refreshSubDiagram() {
		PolygonSimple clipPolygon = getClipPolygon();
		
		if(clipPolygon == null)
			return;
		
		// make sure that clip polygon is in same space as site
		clipPolygon = PolygonTransformer.transformPolygon(clipPolygon, getParentToLocalTransform());
		subDiagram.setClipPoly(clipPolygon);
		subDiagram.computeDiagram();
		
		for(VoronoiNode child : children)
			child.refreshSubDiagram();
	}
	
	public VoronoiNode setClipPolygon(PolygonSimple clipPolygon) {
		this.clipPolygon = clipPolygon;
		refreshSubDiagram();
		return this;
	}
	
	public PolygonSimple getPolygon() {
		PolygonSimple polygon = site.getPolygon() != null ? site.getPolygon() : clipPolygon;
		return polygon;
	}
	
	public VoronoiNode addChild(VoronoiNode child) {
		if(child.parent != null) {
			child.parent.removeChild(child);
		}
		
		if(child != null && children.add(child)) {
			child.parent = this;
			subSites.add(child.site);
			refreshSubDiagram();
		}
		
		return this;
	}
	
	public VoronoiNode removeChild(VoronoiNode child) {
		if(child != null && children.remove(child)) {
			child.parent = null;
			reconstructSubSites();
			refreshSubDiagram();
		}
		
		return this;
	}
	
	public VoronoiNode getParent() {
		return parent;
	}
	
	public VoronoiNode setParent(VoronoiNode parent) {
		if(this.parent != null) {
			this.parent.removeChild(this);
		}
		
		if(parent != null) {
			parent.addChild(this);
		}
		
		return this;
	}
	
	public boolean isParentOf(VoronoiNode child) {
		return children.contains(child);
	}
	
	public Matrix3d getLocalToParentTransform() {
		return localToParentTransform;
	}
	
	public Matrix3d getParentToLocalTransform() {
		return parentToLocalTransform;
	}
	
	public Matrix3d getLocalToWorldTransform() {
		localToWorldTransform.set(getLocalToParentTransform());
		if(getParent() != null) {
			localToWorldTransform.mul(getParent().getLocalToWorldTransform());
		}
		
		return localToWorldTransform;
	}
	
	public Vector2d getLocalPosition() {
		return localPosition;
	}
	
	private void refreshTransformMatrices(Vector2d localPosition) {
		localToParentTransform.m20 = localPosition.x;
		localToParentTransform.m21 = localPosition.y;
		localToParentTransform.invert(parentToLocalTransform);
	}
	
	public VoronoiNode setLocalPosition(Vector2d localPosition) {
		if(localPosition != null) {
			this.localPosition = localPosition;
			this.site.setXY(localPosition.x, localPosition.y);
			refreshTransformMatrices(localPosition);
			
			if(this.parent != null)
				parent.refreshSubDiagram();
		}
		return this;
	}
	
	private static final Vector3d HOMOGENEOUS_ZERO_VECTOR = new Vector3d(0, 0, 1);
	
	public Vector2d getWorldPosition() {
		Vector3d worldPosition = getLocalToWorldTransform().transform(HOMOGENEOUS_ZERO_VECTOR.set(0, 0, 1));
		Vector2d worldPosition2d = new Vector2d(worldPosition.x, worldPosition.y);
		return worldPosition2d;
	}
	
	public int getDepth() {
		return parent == null ? 0 : parent.getDepth() + 1;
	}
}