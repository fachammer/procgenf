package com.fabianachammer.procgenf.main.impl;

import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import com.fabianachammer.procgenf.generation.ChunkEntity;
import com.fabianachammer.procgenf.generation.impl.Utility;
import com.fabianachammer.procgenf.generation.impl.components.VoronoiChunkComponent;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

public class VoronoiRenderer {

	private static final int BASE_LINE_WIDTH = 40;
	private static final int BASE_SITE_SIZE = 30;

	private static void renderNodePoint(VoronoiChunkComponent node, Matrix3d viewMatrix) {
		if(node.getContainerChunk().getDepth() <= 0)
			return;
		
		node.getParent().ifPresent(parent -> {
			
			if(node.getPolygon() != null) {
				if(!node.getPolygon().contains(node.getSite().getX(), node.getSite().getY()))
					return;
			}

			GL11.glColor3d(0, 0, 0);
			TEMP.set(viewMatrix);
			TEMP.mul(parent.getLocalToWorldTransform());
			Vector3d parentWorldPosition = TEMP.transform(new Vector3d(0, 0, 1));
			TEMP.mul(node.getLocalToParentTransform());
			Vector3d nodeWorldPosition = TEMP.transform(new Vector3d(0, 0, 1));
			glPointSize(BASE_SITE_SIZE / (node.getContainerChunk().getDepth() + 1));
			glBegin(GL_POINTS);
			{
				glVertex2d(nodeWorldPosition.x, nodeWorldPosition.y);
			}
			glEnd();
			
			if(node.getContainerChunk().getDepth() <= 1)
				return;
			
			GL11.glColor3d(1, 0, 0);
			glLineWidth(BASE_LINE_WIDTH * 0.1f / (node.getContainerChunk().getDepth() + 1));
			glBegin(GL11.GL_LINES);{
				glVertex2d(nodeWorldPosition.x, nodeWorldPosition.y);
				glVertex2d(parentWorldPosition.x, parentWorldPosition.y);
			}
			glEnd();
		});
	}

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

	public void render(VoronoiChunkComponent root, Matrix3d viewMatrix, PolygonSimple clipPolygon) {
		Queue<VoronoiChunkComponent> nodes = new LinkedList<VoronoiChunkComponent>();

		nodes.add(root);
		while(!nodes.isEmpty()) {
			VoronoiChunkComponent node = nodes.remove();
			renderNodePolygon(node, viewMatrix);
			for(ChunkEntity child : node.getContainerChunk().getChildren()) {
				Optional<VoronoiChunkComponent> voronoiChild = Utility.getChunkComponent(child,
						VoronoiChunkComponent.class);
				if(voronoiChild.isPresent())
					nodes.add(voronoiChild.get());
			}

		}

		nodes.clear();
		nodes.add(root);
		while(!nodes.isEmpty()) {
			VoronoiChunkComponent voronoiChunk = nodes.remove();
			ChunkEntity node = voronoiChunk.getContainerChunk();

			if(node.getDepth() > 0) {
				if(voronoiChunk.getPolygon() == null)
					continue;
				GL11.glColor3d(0.0, 0, 0);
				glLineWidth(BASE_LINE_WIDTH / (node.getDepth() + 1));

				TEMP.set(viewMatrix);
				voronoiChunk.getParent().ifPresent(parent -> TEMP.mul(parent.getLocalToWorldTransform()));
				glBegin(GL11.GL_LINE_LOOP);
				{
					renderPolygon(PolygonTransformer.transformPolygon(voronoiChunk.getPolygon(), TEMP));
				}
				glEnd();
			}

			for(ChunkEntity child : node.getChildren()) {
				Optional<VoronoiChunkComponent> voronoiChild = Utility.getChunkComponent(child,
						VoronoiChunkComponent.class);
				if(voronoiChild.isPresent())
					nodes.add(voronoiChild.get());
			}
		}

		nodes.clear();
		nodes.add(root);
		while(!nodes.isEmpty()) {
			VoronoiChunkComponent voronoiChunk = nodes.remove();
			ChunkEntity node = voronoiChunk.getContainerChunk();

			renderNodePoint(voronoiChunk, viewMatrix);
			for(ChunkEntity child : node.getChildren()) {
				Optional<VoronoiChunkComponent> voronoiChild = Utility.getChunkComponent(child,
						VoronoiChunkComponent.class);
				if(voronoiChild.isPresent())
					nodes.add(voronoiChild.get());
			}
		}

		GL11.glColor3d(0, 1, 0);
		TEMP.set(viewMatrix);
		glBegin(GL11.GL_LINE_LOOP);
		renderPolygon(PolygonTransformer.transformPolygon(clipPolygon, TEMP));
		glEnd();
	}
}
