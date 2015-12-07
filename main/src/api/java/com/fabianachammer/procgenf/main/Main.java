package com.fabianachammer.procgenf.main;

public class Main {
	
	private static final String APPLICATION_IMPLEMENTATION = "com.fabianachammer.procgenf.main.impl.ApplicationImpl";
	
    public static void main(String[] args) {
    	Application application;
		try {
			application = (Application) Class.forName(APPLICATION_IMPLEMENTATION).newInstance();
			application.run(args);
		} catch (Exception e) {
			System.err.println(e);
		}
    }
}