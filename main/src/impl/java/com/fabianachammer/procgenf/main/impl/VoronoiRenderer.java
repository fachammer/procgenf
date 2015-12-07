package com.fabianachammer.procgenf.main.impl;

import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.util.LinkedList;
import java.util.Queue;

import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class VoronoiRenderer {

	private static final int BASE_LINE_WIDTH = 20;
	private static final int BASE_SITE_SIZE = 30;
	private VoronoiNode root;

	public VoronoiRenderer(VoronoiNode root) {
		this.root = root;
	}

	private static void renderNodePoint(VoronoiNode node, Matrix3d viewMatrix) {
		if(node.getParent() == null)
			return;
		
		GL11.glColor3d(0, 0, 0);
		glPointSize(BASE_SITE_SIZE / (node.getDepth() + 1));
		glBegin(GL_POINTS);
		{
			Matrix3d localToWorldTransform = node.getLocalToWorldTransform();
			viewMatrix.mul(localToWorldTransform, localToWorldTransform);
			Vector3d worldPosition = localToWorldTransform.transform(new Vector3d(0, 0, 1));
			glVertex2d(worldPosition.x, worldPosition.y);
		}
		glEnd();
	}

	private static void renderNodePolygon(VoronoiNode node, Matrix3d viewMatrix) {
		Matrix3d transform = node.getParent() != null ? node.getParent().getLocalToWorldTransform() : new Matrix3d();
		viewMatrix.mul(transform, transform);
		
		double intensity = node.getDepth() / 3.0;
		GL11.glColor3d(intensity, 0, 0);
		glLineWidth(BASE_LINE_WIDTH / (node.getDepth() + 1));
		renderPolygon(transformPolygon(node.getPolygon(), transform));
	}

	public static PolygonSimple transformPolygon(PolygonSimple polygon, Matrix3d transform) {
		PolygonSimple transformedPolygon = new PolygonSimple();

		Vector3d vertex = new Vector3d();
		for (int i = 0; i < polygon.getNumPoints(); i++) {
			vertex.set(polygon.getXPoints()[i], polygon.getYPoints()[i], 1);
			vertex.mul(transform);
			transformedPolygon.add(vertex.x, vertex.y);
		}

		return transformedPolygon;
	}

	private static void renderPolygon(PolygonSimple polygon) {
		glBegin(GL11.GL_LINE_LOOP);
		{
			for (int i = 0; i < polygon.getNumPoints(); i++) {
				double x = polygon.getXPoints()[i];
				double y = polygon.getYPoints()[i];
				glVertex2d(x, y);
			}
		}
		glEnd();
	}

	public void render(Matrix3d viewMatrix) {
		Queue<VoronoiNode> nodes = new LinkedList<VoronoiNode>();
		nodes.add(root);
		while (!nodes.isEmpty()) {
			VoronoiNode node = nodes.remove();
			renderNodePoint(node, viewMatrix);
			for (VoronoiNode child : node.getChildren())
				nodes.add(child);
		}
		nodes.clear();
		nodes.add(root);
		while (!nodes.isEmpty()) {
			VoronoiNode node = nodes.remove();
			renderNodePolygon(node, viewMatrix);
			for (VoronoiNode child : node.getChildren())
				nodes.add(child);
		}
	}
}
