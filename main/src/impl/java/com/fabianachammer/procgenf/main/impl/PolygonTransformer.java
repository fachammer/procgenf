package com.fabianachammer.procgenf.main.impl;

import org.joml.Matrix3d;
import org.joml.Vector3d;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class PolygonTransformer {
	public static PolygonSimple transformPolygon(PolygonSimple polygon, Matrix3d transform) {
		PolygonSimple transformedPolygon = new PolygonSimple();

		Vector3d vertex = new Vector3d();
		for(int i = 0; i < polygon.getNumPoints(); i++) {
			vertex.set(polygon.getXPoints()[i], polygon.getYPoints()[i], 1);
			transform.transform(vertex);
			transformedPolygon.add(vertex.x, vertex.y);
		}

		return transformedPolygon;
	}
}
