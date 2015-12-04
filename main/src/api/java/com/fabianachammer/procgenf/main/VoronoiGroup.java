package com.fabianachammer.procgenf.main;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class VoronoiGroup extends Group {

	private Polygon polygon;
	private Polygon clipPolygon;
	private Site site;
	private PowerDiagram subDiagram;
	
	public VoronoiGroup(Polygon clipPolygon) {
		site = new Site(getTranslateX(), getTranslateY(), 0);
		subDiagram = new PowerDiagram();
		this.clipPolygon = clipPolygon;
		polygon = new Polygon();
		polygon.setStroke(Color.BLACK);
		polygon.setFill(Color.NAVAJOWHITE.deriveColor(0, 1, 1, 0.5));
		polygon.setClip(null);
		getChildren().add(polygon);

		getChildren().add(new Circle(2, Color.BLACK));
		
		getChildren().addListener((ListChangeListener.Change<? extends Node> change) -> {
			refreshSubDiagram();
		});
		
		translateXProperty().addListener((observable, oldX, newX) -> {
			refreshSite();
		});
		translateYProperty().addListener((observable, oldY, newY) -> {
			refreshSite();
		});
		
		refreshRegionPolygon();
	}
	
	public VoronoiGroup(){
		this(null);
	}
	
	public Polygon getRegionPolygon() {
		return polygon;
	}
	
	private Site getSite() {
		return site;
	}
	
	public VoronoiGroup setClipPolygon(Polygon clipPolygon){
		this.clipPolygon = clipPolygon;
		refreshSubDiagram();
		return this;
	}
	
	private void refreshSite() {
		site.setXY(getTranslateX(), getTranslateY());
		polygon.setTranslateX(-getTranslateX());
		polygon.setTranslateY(-getTranslateY());
		polygon.setClip(null);
		
		if(getVoronoiGroupParent() != null)
			getVoronoiGroupParent().refreshSubDiagram();
	}
	
	private VoronoiGroup getVoronoiGroupParent() {
		if(!(getParent() instanceof VoronoiGroup))
			return null;
		return (VoronoiGroup) getParent();
	}
	
	private void refreshRegionPolygon() {
		if(getVoronoiGroupParent() == null)
			return;
			
		polygon.getPoints().clear();
		if(site.getPolygon() != null)
			polygon.getPoints().addAll(PolygonConverter.toJavaFxPolygon(site.getPolygon()).getPoints());
		
		refreshSubDiagram();
	}

	private void refreshSubDiagram() {
		subDiagram.setSites(getChildSites());
		PolygonSimple clipPoly = site.getPolygon() != null ? site.getPolygon() : PolygonConverter.toPolygonSimple(clipPolygon);
		if(clipPoly != null) {
			subDiagram.setClipPoly(clipPoly);
			
			if(subDiagram.getSites().size > 0) {
				subDiagram.computeDiagram();
				refreshChildPolygons();
			}
		}
	}
	
	private void refreshChildPolygons() {
		getVoronoiGroupChildren().forEach(node -> node.refreshRegionPolygon());
	}
	
	private List<VoronoiGroup> getVoronoiGroupChildren() {
		List<VoronoiGroup> groups = new ArrayList<VoronoiGroup>();
		getChildren().filtered(n -> n instanceof VoronoiGroup).forEach(node -> {
			groups.add((VoronoiGroup) node);
		});
		return groups;
	}
	
	private OpenList getChildSites() {
		OpenList sites = new OpenList();
		getVoronoiGroupChildren().forEach(node -> {
			sites.add(node.getSite());
		});
		
		return sites;
	}
}
