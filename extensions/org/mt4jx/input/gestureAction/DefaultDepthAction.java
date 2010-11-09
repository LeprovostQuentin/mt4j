package org.mt4jx.input.gestureAction;

import org.mt4j.components.MTComponent;


import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.input.inputProcessors.ICollisionAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.Cluster;
import org.mt4jx.input.inputProcessors.componentProcessors.depthProcessor.DepthGestureEvent;

public class DefaultDepthAction implements IGestureEventListener,ICollisionAction {

	private IMTComponent3D dragDepthTarget;
	
	private boolean gestureAborted = false;
	private MTGestureEvent lastEvent;
	
	public DefaultDepthAction(IMTComponent3D dragDepthObject)
	{
		this.dragDepthTarget = dragDepthObject;			
	}
	
	public boolean processGestureEvent(MTGestureEvent ge) {
		DepthGestureEvent depthEv;
		if(ge instanceof DepthGestureEvent)
		{
			lastEvent = ge;
			depthEv = (DepthGestureEvent)ge;
		}
		else
		{
			return false;
		}
		
		switch(depthEv.getId())
		{
			case MTGestureEvent.GESTURE_DETECTED:
			{
				if (dragDepthTarget instanceof MTComponent){
					MTComponent baseComp = (MTComponent)dragDepthTarget;	
					baseComp.sendToFront();					
					
				}
				Vector3D zVector = new Vector3D(0.0f,0.0f,depthEv.getTranslationVect().z);
				
				if(!(dragDepthTarget instanceof Cluster))
				{
					dragDepthTarget.translateGlobal(zVector);
				}else
				{
					//only move children, not cluster itself
					//cause it should stay on the floor
					Cluster cl = (Cluster)dragDepthTarget;
					for(MTComponent comp : cl.getChildren())
					{
						if(!(comp instanceof MTPolygon))
						{
							comp.translateGlobal(zVector);							
						}
					}
				}
				break;
			}
			case MTGestureEvent.GESTURE_UPDATED:
			{
				Vector3D zVector = new Vector3D(0.0f,0.0f,depthEv.getTranslationVect().z);
				
				if(!(dragDepthTarget instanceof Cluster)&&!gestureAborted)
				{
					dragDepthTarget.translateGlobal(zVector);					
				}else
				{
					//only move children, not cluster itself
					//cause it should stay on the floor
					Cluster cl = (Cluster)dragDepthTarget;
					//remove
															
					cl.translateGlobal(zVector);
					//remove end
					/*for(MTComponent comp : cl.getChildren())
					{
						if(!(comp instanceof MTPolygon))
						{
							comp.translateGlobal(zVector);
						}
					}*/
				}
				break;
			}
			case MTGestureEvent.GESTURE_ENDED:
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public boolean gestureAborted() {
		return this.gestureAborted;		
	}

	@Override
	public void setGestureAborted(boolean aborted) {
		this.gestureAborted = aborted;
	}

	@Override
	public MTGestureEvent getLastEvent() {
		return this.lastEvent;
	}

}
