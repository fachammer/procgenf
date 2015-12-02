package com.fabianachammer.procgenf.main;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;

public class Main {
    public static void main(String[] args) {
    	PowerDiagram diagram = new PowerDiagram();
    	OpenList sites = new OpenList();
    	diagram.setSites(sites);
    	System.out.println("Hello World with PowerDiagram");
    }
}
