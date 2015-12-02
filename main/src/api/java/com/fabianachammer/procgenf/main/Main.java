package com.fabianachammer.procgenf.main;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Random;

import javax.swing.JFrame;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class Main extends Canvas {
	
	private static final long serialVersionUID = -1609623198152343250L;
	private PowerDiagram diagram;
	
	public Main() {
		diagram = new PowerDiagram();
		OpenList sites = new OpenList();
    	Random random = new Random();
    	for(int i = 0; i < 10; i++){
    		double x = random.nextDouble() * 400;
    		double y = random.nextDouble() * 400;
    		Site s = new Site(x, y, 0);
    		sites.add(s);
    	}
    	diagram.setSites(sites);
    	PolygonSimple clipPolygon = new PolygonSimple(new double[]{0, 0, 400, 400}, new double[] {0, 400, 400, 0});
    	diagram.setClipPoly(clipPolygon);
    	diagram.computeDiagram();
	}
	
    public static void main(String[] args) {
    	JFrame frame = new JFrame();
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
    	frame.setVisible(true);
    	
    	Main mainCanvas = new Main();
    	mainCanvas.setSize(400, 400);
    	frame.add(mainCanvas);	
    }
    
    @Override
    public void paint(Graphics graphics) {
    	for(Site s : diagram.getSites()) {
    		PolygonSimple sitePolygon = s.getPolygon();
    		Polygon polygon = new Polygon(sitePolygon.getXpointsClosed(), sitePolygon.getYpointsClosed(), sitePolygon.length);
    		graphics.drawPolygon(polygon);
    		graphics.fillOval((int) Math.round(s.x), (int) Math.round(s.y), 5, 5);
    	}
    }
}
