package com.fabianachammer.procgenf.main.impl;

import static org.lwjgl.opengl.GL11.GL_POINTS;
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
		if (node.getParent() == null)
			return;

		GL11.glColor3d(0, 0, 0);
		glPointSize(BASE_SITE_SIZE / (node.getDepth() + 1));
		glBegin(GL_POINTS);
		{
			TEMP.set(viewMatrix);
			TEMP.mul(node.getLocalToWorldTransform());
			Vector3d worldPosition = TEMP.transform(new Vector3d(0, 0, 1));
			glVertex2d(worldPosition.x, worldPosition.y);
		}
		glEnd();
	}

	private static final Matrix3d TEMP = new Matrix3d();

	private static void renderNodePolygon(VoronoiNode node, Matrix3d viewMatrix) {
		if (node.getDepth() <= 1)
			return;

		TEMP.set(viewMatrix);
		if (node.getParent() != null)
			TEMP.mul(node.getParent().getLocalToWorldTransform());

		double intensity = node.getDepth() / 3.0;
		GL11.glColor3d(1.0, 0, 0);
		glLineWidth(BASE_LINE_WIDTH / (node.getDepth() + 1));
		glBegin(GL11.GL_TRIANGLE_FAN);{
			glVertex2d(node.getLocalPosition().x, node.getLocalPosition().y);
			renderPolygon(PolygonTransformer.transformPolygon(node.getPolygon(), TEMP));
		}glEnd();
	}

	private static void renderPolygon(PolygonSimple polygon) {
		for (int i = 0; i < polygon.getNumPoints(); i++) {
			double x = polygon.getXPoints()[i];
			double y = polygon.getYPoints()[i];
			glVertex2d(x, y);
		}
	}

	public void render(Matrix3d viewMatrix, PolygonSimple clipPolygon) {
		Queue<VoronoiNode> nodes = new LinkedList<VoronoiNode>();
		root.setClipPolygon(clipPolygon);

		nodes.add(root);
		while (!nodes.isEmpty()) {
			VoronoiNode node = nodes.remove();
			renderNodePolygon(node, viewMatrix);
			for (VoronoiNode child : node.getChildren())
				nodes.add(child);
		}

		nodes.clear();
		nodes.add(root);
		while (!nodes.isEmpty()) {
			VoronoiNode node = nodes.remove();
			
			GL11.glColor3d(0.0, 0, 0);
			glLineWidth(BASE_LINE_WIDTH / (node.getDepth() + 1));
			TEMP.set(viewMatrix);
			if (node.getParent() != null)
				TEMP.mul(node.getParent().getLocalToWorldTransform());
			glBegin(GL11.GL_LINE_LOOP);{
				renderPolygon(PolygonTransformer.transformPolygon(node.getPolygon(), TEMP));
			}glEnd();
			for (VoronoiNode child : node.getChildren())
				nodes.add(child);
		}
		
		nodes.clear();
		nodes.add(root);
		while (!nodes.isEmpty()) {
			VoronoiNode node = nodes.remove();
			renderNodePoint(node, viewMatrix);
			for (VoronoiNode child : node.getChildren())
				nodes.add(child);
		}
	}
}
