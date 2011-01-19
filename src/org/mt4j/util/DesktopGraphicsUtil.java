package org.mt4j.util;

import org.mt4j.IMTApplication;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL20;

import processing.core.PGraphics;
import processing.core.PGraphics3D;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;

public class DesktopGraphicsUtil implements IGraphicsUtil {
	private final IMTApplication app;

	public DesktopGraphicsUtil(IMTApplication app){
		this.app = app;
	}

	public PGraphics getPGraphics(){
		return app.getPGraphics();
	}
	
	public PMatrix3D getModelView() {
		return ((PGraphics3D)app.getPGraphics()).modelview;
	}
	
	public PMatrix3D getModelViewInv() {
		return ((PGraphics3D)app.getPGraphics()).modelviewInv;
	}

//	public GL10 getGL() {
//		return ((PGraphicsOpenGL)app.getPGraphics()).gl;
//	}
//
//	public GL10 beginGL() {
//		((PGraphicsOpenGL)app.getPGraphics()).beginGL();
//		return app.getGLCommon();
//	}
//    
//    public void endGL(){
//    	((PGraphicsOpenGL)app.getPGraphics()).endGL();
//    }
	
	public GL10 getGL() {
		return app.getGL10(); //FIXME DOES THE CAST TO kronos.GL10 work!??
	}
	
	@Override
	public GL11 getGL11() {
		return app.getGL11(); 
	}

	@Override
	public GL20 getGL20() {
		return app.getGL20(); 
	}

	public GL10 beginGL() {
//		((PGraphicsOpenGL)app.getPGraphics()).beginGL();
		app.beginGL();
		return app.getGL10();
	}
    
    public void endGL(){
//    	((PGraphicsOpenGL)app.getPGraphics()).endGL();
    	app.endGL();
    }

}
