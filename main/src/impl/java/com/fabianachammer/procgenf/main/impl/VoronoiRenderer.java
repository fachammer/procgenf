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

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;

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
			TEMP.set(viewMatrix);
			TEMP.mul(node.getLocalToWorldTransform());
			Vector3d worldPosition = TEMP.transform(new Vector3d(0, 0, 1));
			glVertex2d(worldPosition.x, worldPosition.y);
		}
		glEnd();
	}

	private static final Matrix3d TEMP = new Matrix3d();

	private static void renderNodePolygon(VoronoiNode node, Matrix3d viewMatrix) {
		if(node.getDepth() <= 0 || node.getPolygon() == null)
			return;

		TEMP.set(viewMatrix);
		if(node.getParent() != null)
			TEMP.mul(node.getParent().getLocalToWorldTransform());

		double red = Noise.valueCoherentNoise3D(node.getWorldPosition().x, node.getWorldPosition().y, 0, 1234, NoiseQuality.BEST) / 2 + 0.5;
		double green = Noise.valueCoherentNoise3D(node.getWorldPosition().x, node.getWorldPosition().y, 0, 4321, NoiseQuality.BEST) / 2 + 0.5;
		double blue = Noise.valueCoherentNoise3D(node.getWorldPosition().x, node.getWorldPosition().y, 0, 3412, NoiseQuality.BEST) / 2 + 0.5;
		GL11.glColor3d(red, green, blue);
		glBegin(GL11.GL_TRIANGLE_FAN);
		{
			
			PolygonSimple transformedPolygon = PolygonTransformer.transformPolygon(node.getPolygon(), TEMP);
			
			//glVertex2d(node.getWorldPosition().x, node.getWorldPosition().y);
			renderPolygon(transformedPolygon);
			//glVertex2d(transformedPolygon.getXPoints()[0], transformedPolygon.getYPoints()[0]);
		}
		glEnd();
	}

	private static void renderPolygon(PolygonSimple polygon) {
		if(polygon.getNumPoints() == 0)
			return;

		for(int i = 0; i < polygon.getNumPoints(); i++) {
			double x = polygon.getXPoints()[i];
			double y = polygon.getYPoints()[i];
			glVertex2d(x, y);
		}
	}

	public void render(Matrix3d viewMatrix, PolygonSimple clipPolygon) {
		Queue<VoronoiNode> nodes = new LinkedList<VoronoiNode>();

		nodes.add(root);
		while(!nodes.isEmpty()) {
			VoronoiNode node = nodes.remove();
			renderNodePolygon(node, viewMatrix);
			for(VoronoiNode child : node.getChildren())
				nodes.add(child);
		}

		nodes.clear();
		nodes.add(root);
		while(!nodes.isEmpty()) {
			VoronoiNode node = nodes.remove();

			if(node.getDepth() > 0) {
				if(node.getPolygon() == null)
					continue;
				GL11.glColor3d(0.0, 0, 0);
				glLineWidth(BASE_LINE_WIDTH / (node.getDepth() + 1));
				
				TEMP.set(viewMatrix);
				if(node.getParent() != null)
					TEMP.mul(node.getParent().getLocalToWorldTransform());
				glBegin(GL11.GL_LINE_LOOP);
				{
					renderPolygon(PolygonTransformer.transformPolygon(node.getPolygon(), TEMP));
				}
				glEnd();
			}
			for(VoronoiNode child : node.getChildren())
				nodes.add(child);
		}

		nodes.clear();
		nodes.add(root);
		while(!nodes.isEmpty()) {
			VoronoiNode node = nodes.remove();
			renderNodePoint(node, viewMatrix);
			for(VoronoiNode child : node.getChildren())
				nodes.add(child);
		}
		
		
		GL11.glColor3d(0, 1, 0);
		TEMP.set(viewMatrix);
		glBegin(GL11.GL_LINE_LOOP);
		renderPolygon(PolygonTransformer.transformPolygon(clipPolygon, TEMP));
		glEnd();
	}
}
