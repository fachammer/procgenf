package com.fabianachammer.procgenf.main;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.Site;

public class VoronoiGroup extends Group {

	private Polygon polygon;
	private Site site;
	private PowerDiagram diagram;
	
	public VoronoiGroup() {
		Point2D sitePosition = localToScene(getTranslateX(), getTranslateY());
		site = new Site(sitePosition.getX(), sitePosition.getY());
		diagram = new PowerDiagram();
	}
	
	public Polygon getPolygon() {
		return polygon;
	}
	
	public Site getSite() {
		return site;
	}
	
	public VoronoiGroup setClippingPolygon(Polygon newPolygon) {
		this.polygon = newPolygon;
		
		if(this.polygon.getParent() != this)
			this.getChildren().add(this.polygon);
		
		return this;
	}
}
