package com.fabianachammer.procgenf.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class Main extends Application {
	
    public static void main(String[] args) {
    	launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		VoronoiGroup root = new VoronoiGroup();
		
		VoronoiGroup region1 = new VoronoiGroup();
		region1.setTranslateX(100);
		region1.setTranslateY(200);
		root.getChildren().add(region1);
		
		VoronoiGroup region2 = new VoronoiGroup();
		region2.setTranslateX(200);
		region2.setTranslateY(100);
		root.getChildren().add(region2);
		
		VoronoiGroup region3 = new VoronoiGroup();
		region3.setTranslateX(300);
		region3.setTranslateY(300);
		root.getChildren().add(region3);
		
		
		Scene scene = new Scene(root, 400, 400);
		root.setClippingPolygon(sceneClipPolygon(scene));

		scene.widthProperty().addListener(x -> {
			root.setClippingPolygon(sceneClipPolygon(scene));
		});
		scene.heightProperty().addListener(x -> {
	    	root.setClippingPolygon(sceneClipPolygon(scene));
		});
		primaryStage.setTitle("Recursive on-line Voronoi region generation");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private static Polygon sceneClipPolygon(Scene scene) {
		double width = scene.getWidth();
		double height = scene.getHeight();
		return new Polygon(0.0, 0.0, 0.0, height, width, height, width, 0.0);
	}
}
