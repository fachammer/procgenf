package com.fabianachammer.procgenf.main.impl;

import com.fabianachammer.procgenf.generation.Chunk;
import com.fabianachammer.procgenf.generation.GenerationEngine;
import com.fabianachammer.procgenf.generation.impl.ChunkImpl;
import com.fabianachammer.procgenf.generation.impl.GenerationEngineImpl;
import com.fabianachammer.procgenf.generation.impl.RootChunk;
import com.fabianachammer.procgenf.generation.impl.RootVoronoiChunkGenerator;
import com.fabianachammer.procgenf.main.Application;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.Random;

import org.joml.Matrix3d;
import org.joml.Vector2d;

public class ApplicationImpl implements Application {

	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private long window;
	
	private boolean[] keyPressed = new boolean[512];
	private boolean[] previousKeyPressed = new boolean[512];
	private static final int WIDTH = 700;
	private static final int HEIGHT = 700;
	
	private RootChunk rootChunkFeature;
	private VoronoiRenderer voronoiRenderer;
	private GenerationEngine generationEngine;

	public void run(String[] args) {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		try {
			init();
			loop();

			glfwDestroyWindow(window);
			keyCallback.release();
		} finally {
			glfwTerminate();
			errorCallback.release();
		}
	}

	private void init() {
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		if(glfwInit() != GLFW_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
		if(window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, GLFW_TRUE);

				if(action == GLFW_PRESS)
					keyPressed[key] = true;
				else if(action == GLFW_RELEASE)
					keyPressed[key] = false;
			}
		});

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);

		glfwShowWindow(window);

		rootChunkFeature = new RootChunk(null, node(0, 0), 0, RootChunk.GenerationType.Noise, 100, 1);
		Chunk rootChunk = new ChunkImpl().addFeature(rootChunkFeature);
		
		generationEngine = new GenerationEngineImpl(rootChunk, new RootVoronoiChunkGenerator(rootChunkFeature));
		voronoiRenderer = new VoronoiRenderer();
	}

	private static VoronoiNode node(double x, double y, PolygonSimple clipPolygon, VoronoiNode... children) {
		VoronoiNode node = new VoronoiNode(new Vector2d(x, y), clipPolygon);
		for(VoronoiNode n : children) {
			node.addChild(n);
		}
		return node;
	}

	private static VoronoiNode node(double x, double y, VoronoiNode... children) {
		return node(x, y, null, children);
	}

	private double zoomLevel = 0.01;
	private static final double ZOOM_VELOCITY = 400;
	private static final double MIN_ZOOM_LEVEL = Double.MIN_VALUE;
	private static final double MAX_ZOOM_LEVEL = 0.5;
	private static final double CAMERA_SPEED = 10;
	private double deltaTime = 0.0;
	private Vector2d cameraPosition = new Vector2d();
	private Matrix3d viewMatrix = new Matrix3d();
	private boolean fixedVisibility = false;
	private PolygonSimple visibilityPolygon = new PolygonSimple(
			new double[] {1, 1, -1, -1}, 
			new double[] {1, -1, -1, 1});
	private Random random = new Random();
	
	private void loop() {
		GL.createCapabilities();

		glClearColor(0.5f, 0.5f, 0.5f, 0.0f);

		while(glfwWindowShouldClose(window) == GLFW_FALSE) {
			long t0 = System.nanoTime();
			glfwPollEvents();
			
			if(keyPressed[GLFW_KEY_UP])
				zoomLevel *= Math.pow(1.1, ZOOM_VELOCITY * deltaTime);

			if(keyPressed[GLFW_KEY_DOWN])
				zoomLevel /= Math.pow(1.1, ZOOM_VELOCITY * deltaTime);

			if(keyPressed[GLFW_KEY_F] && !previousKeyPressed[GLFW_KEY_F])
				fixedVisibility = !fixedVisibility;
			
			if(keyPressed[GLFW_KEY_R] && !previousKeyPressed[GLFW_KEY_R]) {
				rootChunkFeature.setSeed(random.nextInt());
			}
			
			if(keyPressed[GLFW_KEY_1] && !previousKeyPressed[GLFW_KEY_1]) {
				rootChunkFeature.setGenerationType(RootChunk.GenerationType.Noise);
			}
			
			if(keyPressed[GLFW_KEY_2] && !previousKeyPressed[GLFW_KEY_2]) {
				rootChunkFeature.setGenerationType(RootChunk.GenerationType.Square);
			}
			
			if(keyPressed[GLFW_KEY_3] && !previousKeyPressed[GLFW_KEY_3]) {
				rootChunkFeature.setGenerationType(RootChunk.GenerationType.Hexagon);
			}
			
			System.arraycopy(keyPressed, 0, previousKeyPressed, 0, keyPressed.length);
			
			zoomLevel = clamp(zoomLevel, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);

			double yDirection = Math.signum(keyPressed[GLFW_KEY_W] ? 1 : 0) + (keyPressed[GLFW_KEY_S] ? -1 : 0);
			double xDirection = Math.signum(keyPressed[GLFW_KEY_D] ? 1 : 0) + (keyPressed[GLFW_KEY_A] ? -1 : 0);

			cameraPosition.y += yDirection * CAMERA_SPEED * deltaTime / zoomLevel;
			cameraPosition.x += xDirection * CAMERA_SPEED * deltaTime / zoomLevel;

			Matrix3d cameraTranslation = new Matrix3d();
			cameraTranslation.m20 = -cameraPosition.x;
			cameraTranslation.m21 = -cameraPosition.y;

			viewMatrix.identity()
				.scale(zoomLevel)
				.mul(cameraTranslation)
				.lookAlong(0, 0, -1, 0, 1, 0);

			PolygonSimple visibilityPolygon = this.visibilityPolygon.clone();
			if(!fixedVisibility)
				visibilityPolygon.scale(1 / zoomLevel);
			visibilityPolygon.translate(cameraPosition.x, cameraPosition.y);
			
			generationEngine.run(visibilityPolygon);
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			voronoiRenderer.render(rootChunkFeature.getRootNode(), viewMatrix, visibilityPolygon);

			glfwSwapBuffers(window);			
			
			long t1 = System.nanoTime();
			deltaTime = (t1 - t0) / 10.0e9;
		}
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}
}