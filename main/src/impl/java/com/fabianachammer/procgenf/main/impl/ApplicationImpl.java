package com.fabianachammer.procgenf.main.impl;

import com.fabianachammer.procgenf.main.Application;

import kn.uni.voronoitreemap.j2d.PolygonSimple;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MathUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.math.MathContext;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class ApplicationImpl implements Application {
	
	private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    private long window;
    private VoronoiNode root;
    private VoronoiRenderer voronoiRenderer;
    private boolean[] keyPressed = new boolean[512];
    private long time = System.nanoTime();
    
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
 
        if ( glfwInit() != GLFW_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); 
 
        int WIDTH = 300;
        int HEIGHT = 300;
 
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
 
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GLFW_TRUE);
                
                if(action == GLFW_PRESS)
                	keyPressed[key] = true;
                else if(action == GLFW_RELEASE)
                	keyPressed[key] = false;
            }
        });
 
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
            window,
            (vidmode.width() - WIDTH) / 2,
            (vidmode.height() - HEIGHT) / 2
        );
 
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwShowWindow(window);
        
        PolygonSimple clipPolygon = new PolygonSimple(
        		new double[] { -1.1, -1.1, 1.1, 1.1 }, 
        		new double[] { 1.1, -1.1, -1.1, 1.1 }
        	);
        root = node(0, 0, clipPolygon,
        		node(0.5, 0.5, 
        				node(0.1, 0.1), 
        				node(-0.1, 0.1),
        				node(-0.1, -0.1)),
				node(-0.5, -0.5,
						node(0.1, 0.1), 
        				node(-0.1, 0.2),
        				node(-0.1, -0.1)),
				node(0.5, -0.5,
						node(0.1, 0.1), 
        				node(-0.1, 0.1),
        				node(-0.1, -0.1)),
				node(-0.5, 0.5,
						node(0.1, 0.1), 
        				node(-0.1, 0.1),
        				node(-0.1, -0.1)));
        voronoiRenderer = new VoronoiRenderer(root);
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
    
    private double zoomLevel = 0.1;
    private static final double ZOOM_VELOCITY = 400;
    private static final double MIN_ZOOM_LEVEL = 0.01;
    private static final double MAX_ZOOM_LEVEL = 10;
    private static final double CAMERA_SPEED = 10;
    private double deltaTime = 0.0;
    private Vector2d cameraPosition = new Vector2d();
    
    private void loop() {
        GL.createCapabilities();
 
        glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
 
        while (glfwWindowShouldClose(window) == GLFW_FALSE) {
        	long t0 = System.nanoTime();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            if(keyPressed[GLFW_KEY_UP])
            	zoomLevel *= Math.pow(1.1, ZOOM_VELOCITY * deltaTime);
            
            if(keyPressed[GLFW_KEY_DOWN])
            	zoomLevel /= Math.pow(1.1, ZOOM_VELOCITY * deltaTime);
            
            zoomLevel = clamp(zoomLevel, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);
            
            double yDirection = Math.signum(keyPressed[GLFW_KEY_W] ? 1 : 0) + (keyPressed[GLFW_KEY_S] ? -1 : 0);
            double xDirection = Math.signum(keyPressed[GLFW_KEY_D] ? 1 : 0) + (keyPressed[GLFW_KEY_A] ? -1 : 0);
            
            cameraPosition.y += yDirection * CAMERA_SPEED * deltaTime / zoomLevel;
            cameraPosition.x += xDirection * CAMERA_SPEED * deltaTime / zoomLevel;
            
            Matrix3d cameraTranslation = new Matrix3d();
            cameraTranslation.m20 = cameraPosition.x;
            cameraTranslation.m21 = cameraPosition.y;
            
            Matrix3d viewMatrix = new Matrix3d()
            		.mul(cameraTranslation)
            		.scale(zoomLevel)
            		.lookAlong(new Vector3d(0, 0, 1), new Vector3d(0, 1, 0));
            voronoiRenderer.render(viewMatrix);
            
            glfwSwapBuffers(window);
 
            glfwPollEvents();
            long t1 = System.nanoTime();
            deltaTime = (t1 - t0) / 10.0e9;
        }
    }
    
    private static double clamp(double value, double min, double max) {
    	return Math.max(min, Math.min(max, value));
    }
}