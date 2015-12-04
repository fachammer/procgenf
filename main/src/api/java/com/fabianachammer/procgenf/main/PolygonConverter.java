package com.fabianachammer.procgenf.main;

import javafx.scene.shape.Polygon;
import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class PolygonConverter {

	public static Polygon toJavaFxPolygon(PolygonSimple polygonSimple) {
		if(polygonSimple == null)
			return null;
		
		Polygon polygon = new Polygon();
		for(int i = 0; i < polygonSimple.getNumPoints(); i++){
			polygon.getPoints().add(polygonSimple.getXPoints()[i]);
			polygon.getPoints().add(polygonSimple.getYPoints()[i]);
		}
		return polygon;
	}
	
	public static PolygonSimple toPolygonSimple(Polygon javaFxPolygon){
		if(javaFxPolygon == null)
			return null;
		
		int size = javaFxPolygon.getPoints().size() / 2;
		double[] x = new double[size];
		double[] y = new double[size];
		for(int i = 0; i < javaFxPolygon.getPoints().size() - 1; i += 2){
			x[i / 2] = javaFxPolygon.getPoints().get(i);
			y[i / 2] = javaFxPolygon.getPoints().get(i + 1);
		}
		
		return new PolygonSimple(x, y);
	}
}
