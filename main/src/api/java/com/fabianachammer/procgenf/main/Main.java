package com.fabianachammer.procgenf.main;

import java.util.Arrays;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class Main extends Application {
	
    public static void main(String[] args) {
    	launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Pane root = new Pane();
		VoronoiGroup voronoiRoot = new VoronoiGroup();
		Scene scene = new Scene(root, 700, 700);
		
		root.getChildren().add(voronoiRoot);
		
		final Camera camera = new ParallelCamera();
		scene.setCamera(camera);
		scene.setFill(Color.ALICEBLUE);
		Group cameraGroup = new Group();
		
		cameraGroup.getChildren().add(camera);
		voronoiRoot.getChildren().add(cameraGroup);
		
		voronoiRoot.setClipPolygon(sceneClipPolygon(scene));
		
		VoronoiGroup region1 = new VoronoiGroup();
		region1.setTranslateX(250);
		region1.setTranslateY(100);
		voronoiRoot.getChildren().add(region1);
		
		VoronoiGroup region2 = new VoronoiGroup();
		region2.setTranslateX(350);
		region2.setTranslateY(250);
		voronoiRoot.getChildren().add(region2);
				
		VoronoiGroup region3 = new VoronoiGroup();
		region3.setTranslateX(50);
		region3.setTranslateY(500);
		voronoiRoot.getChildren().add(region3);
		
		VoronoiGroup region4 = new VoronoiGroup();
		region4.setTranslateX(750);
		region4.setTranslateY(300);
		voronoiRoot.getChildren().add(region4);
		
		scene.widthProperty().addListener((sceneWidth, oldWidth, newWidth) -> {
			voronoiRoot.setClipPolygon(sceneClipPolygon(scene));
		});
		scene.heightProperty().addListener((sceneHeight, oldHeight, newHeight) -> {
	    	voronoiRoot.setClipPolygon(sceneClipPolygon(scene));
		});
		cameraGroup.translateXProperty().addListener(x -> {
			voronoiRoot.setClipPolygon(sceneClipPolygon(scene));
		});
		cameraGroup.translateYProperty().addListener(x -> {
			voronoiRoot.setClipPolygon(sceneClipPolygon(scene));
		});
		primaryStage.setTitle("Recursive on-line Voronoi region generation");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		final KeyCode[] validKeys = new KeyCode[]{ KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT };
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if(!Arrays.asList(validKeys).contains(event.getCode()))
				event.consume();
		});
		
		final double speed = 10;
		scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			switch(event.getCode()){
			case UP:
				cameraGroup.setTranslateY(cameraGroup.getTranslateY() - speed);
				break;
			case DOWN:
				cameraGroup.setTranslateY(cameraGroup.getTranslateY() + speed);
				break;
			case LEFT:
				cameraGroup.setTranslateX(cameraGroup.getTranslateX() - speed);
				break;
			case RIGHT:
				cameraGroup.setTranslateX(cameraGroup.getTranslateX() + speed);
				break;
			default:
					
			}
		});
	}
	
	private static Polygon sceneClipPolygon(Scene scene) {
		double width = scene.getWidth();
		double height = scene.getHeight();
		Camera camera = scene.getCamera();
		Point2D scenePosition = camera.localToScene(camera.getTranslateX(), camera.getTranslateY());
		double x = scenePosition.getX();
		double y = scenePosition.getY();
		return new Polygon(x - width, y - height, x - width, y + height, x + width, y + height, x + width, y - height);
	}
}

//package com.fabianachammer.procgenf.main;
//
//import java.awt.Canvas;
//import java.awt.Graphics;
//import java.awt.Polygon;
//import java.util.Random;
//
//import javax.swing.JFrame;
//
//import kn.uni.voronoitreemap.datastructure.OpenList;
//import kn.uni.voronoitreemap.diagram.PowerDiagram;
//import kn.uni.voronoitreemap.j2d.PolygonSimple;
//import kn.uni.voronoitreemap.j2d.Site;
//
//public class Main extends Canvas {
//	
//	private static final long serialVersionUID = -1609623198152343250L;
//	private PowerDiagram diagram;
//	
//	public Main() {
//		diagram = new PowerDiagram();
//		OpenList sites = new OpenList();
//
//		
//    	sites.add(new Site(250, 100, 0));
//    	sites.add(new Site(350, 250, 0));
//    	sites.add(new Site(50, 500, 0));
//    	diagram.setSites(sites);
//    	PolygonSimple clipPolygon = new PolygonSimple(new double[]{0, 0, 700, 700}, new double[] {0, 700, 700, 0});
//    	diagram.setClipPoly(clipPolygon);
//    	diagram.computeDiagram();
//	}
//	
//    public static void main(String[] args) {
//    	JFrame frame = new JFrame();
//    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(700, 700);
//    	frame.setVisible(true);
//    	
//    	Main mainCanvas = new Main();
//    	mainCanvas.setSize(700, 700);
//    	frame.add(mainCanvas);	
//    }
//    
//    @Override
//    public void paint(Graphics graphics) {
//    	for(Site s : diagram.getSites()) {
//    		PolygonSimple sitePolygon = s.getPolygon();
//    		Polygon polygon = new Polygon(sitePolygon.getXpointsClosed(), sitePolygon.getYpointsClosed(), sitePolygon.length);
//    		graphics.drawPolygon(polygon);
//    		graphics.fillOval((int) Math.round(s.x), (int) Math.round(s.y), 5, 5);
//    	}
//    }
//}
