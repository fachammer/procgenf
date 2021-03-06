package com.fabianachammer.procgenf.main.impl;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.util.LinkedList;
import java.util.Queue;

import org.joml.Matrix3d;
import org.lwjgl.opengl.GL11;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.impl.Utility;
import com.fabianachammer.procgenf.generation.impl.components.VoronoiChunkComponent;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class VoronoiRenderer {

	private static final int BASE_LINE_WIDTH = 40;

	private static final Matrix3d TEMP = new Matrix3d();

	private static void renderNodePolygon(VoronoiChunkComponent node, Matrix3d viewMatrix) {

		if(node.getContainerChunk().getDepth() <= 0 || node.getPolygon() == null)
			return;

		TEMP.set(viewMatrix);
		node.getParent().ifPresent(parent -> TEMP.mul(parent.getLocalToWorldTransform()));

		double value = node.getContainerChunk().getDepth() / 2.0;
		double red = 0.25 * value;
		double green = 0.25 * value;
		double blue = 0.25 * value;
		GL11.glColor3d(red, green, blue);
		glBegin(GL11.GL_TRIANGLE_FAN);
		{
			PolygonSimple transformedPolygon = PolygonTransformer.transformPolygon(node.getPolygon(), TEMP);

			renderPolygon(transformedPolygon);
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

	public void render(ChunkEntity root, Matrix3d viewMatrix, PolygonSimple clipPolygon) {
		Queue<ChunkEntity> nodes = new LinkedList<ChunkEntity>();

		nodes.add(root);
		while(!nodes.isEmpty()) {
			ChunkEntity node = nodes.remove();
			Utility.getChunkComponent(node, VoronoiChunkComponent.class)
					.ifPresent(n -> renderNodePolygon(n, viewMatrix));
			for(ChunkEntity child : node.getChildren()) {
				nodes.add(child);
			}
		}

		nodes.clear();
		nodes.add(root);
		while(!nodes.isEmpty()) {
			ChunkEntity node = nodes.remove();

			if(node.getDepth() > 0) {
				if(!Utility.getChunkComponent(node, VoronoiChunkComponent.class).map(voronoiChunk -> {
					if(voronoiChunk.getPolygon() == null)
						return false;
					GL11.glColor3d(0.0, 0, 0);
					glLineWidth(BASE_LINE_WIDTH / (node.getDepth() + 1));

					TEMP.set(viewMatrix);
					voronoiChunk.getParent().ifPresent(parent -> TEMP.mul(parent.getLocalToWorldTransform()));
					glBegin(GL11.GL_LINE_LOOP);
					{
						renderPolygon(PolygonTransformer.transformPolygon(voronoiChunk.getPolygon(), TEMP));
					}
					glEnd();
					
					return true;
				}).orElse(true)){
					continue;
				}
			}

			for(ChunkEntity child : node.getChildren()) {
				nodes.add(child);
			}
		}

		GL11.glColor3d(0, 1, 0);
		TEMP.set(viewMatrix);
		glBegin(GL11.GL_LINE_LOOP);
		renderPolygon(PolygonTransformer.transformPolygon(clipPolygon, TEMP));
		glEnd();
	}
}
