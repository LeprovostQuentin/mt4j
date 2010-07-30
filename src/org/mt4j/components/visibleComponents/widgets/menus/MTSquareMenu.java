package org.mt4j.components.visibleComponents.widgets.menus;


import java.util.ArrayList;
import java.util.List;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.css.style.CSSFont;
import org.mt4j.css.style.CSSStyle;
import org.mt4j.css.util.CSSFontManager;
import org.mt4j.css.util.CSSKeywords.CSSFontWeight;
import org.mt4j.css.util.CSSStylableComponent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;


import processing.core.PConstants;
import processing.core.PImage;

public class MTSquareMenu extends MTRectangle implements CSSStylableComponent {

	MTApplication app;
	List<MTRectangle> menuContents = new ArrayList<MTRectangle>();
	List<ArrayList<MTRectangle>> layout = new ArrayList<ArrayList<MTRectangle>>();
	float size;
	int current = 0;
	int maxPerLine = 0;
	float bezel = 10f;

	public MTSquareMenu(MTApplication app, Vector3D position,
			List<MenuItem> menuItems, float size) {
		super(position.x, position.y, (float) (int) Math
				.sqrt(menuItems.size() + 1) * size, (float) (int) Math
				.sqrt(menuItems.size() + 1) * size, app);
		this.app = app;
		this.size = size;
		this.setCssForceDisable(true);

		this.setNoFill(true);
		this.setNoStroke(true);



		//List of the Child Polygons and their IGestureEventListeners
		List<PolygonListeners> pl = new ArrayList<PolygonListeners>();
		
		
		for (MenuItem s : menuItems) {
			
			if (s != null && s.getType() == MenuItem.TEXT) {
				MTRectangle container = new MTRectangle(0, 0, size, size, app);
				this.addChild(container);


				for (String t : s.getMenuText().split("\n")) {
					MTTextArea menuItem = new MTTextArea(app);
					menuItem.setText(t);
					menuItem.setCssForceDisable(true);
					menuItem.setFillColor(new MTColor(0, 0, 0, 0));
					menuItem.setStrokeColor(new MTColor(0, 0, 0, 0));
					menuItem.setPickable(false);
					container.addChild(menuItem);

				}

				container.setChildClip(new Clip(container));
				container.setPickable(false);
				
				menuContents.add(container);
			} else if (s != null && s.getType() == MenuItem.PICTURE) {

				if (s.getMenuImage() != null) {
					PImage texture = null;
					if (s.getMenuImage().width != (int) size
							|| s.getMenuImage().height != (int) size) {
						texture = cropImage(s.getMenuImage(), (int) size, true);
					} else {
						texture = s.getMenuImage();
					}

					MTRectangle container = new MTRectangle(0, 0, size, size,
							app);
					this.addChild(container);
					container.setTexture(texture);

					container.setChildClip(new Clip(container));
					container.setPickable(false);
					
					menuContents.add(container);

				}

			}

		}
		//Register the TapProcessor
		this.setGestureAllowance(TapProcessor.class, true);
		this.registerInputProcessor(new TapProcessor(app));
		this.addGestureListener(TapProcessor.class, new TapListener(pl));
		this.setCssForceDisable(true);
		
		//Apply Style to Children
		this.styleChildren(getNecessaryFontSize(menuItems, size));

	}

	private PImage cropImage(PImage image, int size, boolean resize) {
		PImage returnImage = app.createImage(size, size, PConstants.RGB);
		if (resize || image.width < size || image.height < size) {
			if (image.width < image.height) {
				image.resize(
						size,
						(int) ((float) image.height / ((float) image.width / (float) size)));
			} else {
				image.resize(
						(int) ((float) image.width / ((float) image.height / (float) size)),
						size);
			}

		}
		int x = (image.width / 2) - (size / 2);
		int y = (image.height / 2) - (size / 2);

		if (x + size > image.width)
			x = image.width - size;
		if (x < 0)
			x = 0;
		if (x + size > image.width)
			size = image.width - x;
		if (y + size > image.height)
			x = image.height - size;
		if (y < 0)
			y = 0;
		if (y + size > image.height)
			size = image.height - y;

		returnImage.copy(image, x, y, size, size, 0, 0, size, size);

		return returnImage;
	}



	private void styleChildren(int fontsize) {
		organizeRectangles();
		CSSStyle vss = this.getCssHelper().getVirtualStyleSheet();
		CSSFont cf = this.getCssHelper().getVirtualStyleSheet().getCssfont();
		cf.setFontsize(fontsize);
		cf.setWeight(CSSFontWeight.BOLD);
		CSSFontManager cfm = new CSSFontManager(app);
		IFont font = cfm.selectFont(cf);

		// System.out.println("Fill Color: " + vss.getBackgroundColor());
		for (MTRectangle c : menuContents) {

			MTRectangle rect = c;

			c.setWidthLocal(size);
			c.setHeightLocal(size);

			rect.setStrokeColor(vss.getBorderColor());
			// System.out.println("Border Color: " + vss.getBorderColor());
			if (((MTRectangle) c).getTexture() == null) {
				rect.setFillColor(vss.getBackgroundColor());
				for (MTComponent d : c.getChildren()) {
					if (d instanceof MTTextArea) {
						MTTextArea ta = (MTTextArea) d;
						// System.out.println("Setting Font for Part " +
						// ta.getText());
						ta.setFont(font);
					}
				}

				float height = calcTotalHeight(c.getChildren());
				float ypos = size / 2f - height / 2f;
				for (MTComponent d : c.getChildren()) {
					if (d instanceof MTTextArea) {
						MTTextArea ta = (MTTextArea) d;

						ta.setPositionRelativeToParent(new Vector3D(size / 2f,
								ypos + ta.getHeightXY(TransformSpace.LOCAL)
										/ 2f));
						ypos += ta.getHeightXY(TransformSpace.LOCAL);

					}

				}
			} else {
				rect.setFillColor(MTColor.WHITE);
			}

		}

		//Min/Max Values of the Children
		float minx = 16000, maxx = -16000, miny = 16000, maxy = -16000;
		
		int currentRow = 0;
		for (List<MTRectangle> lr : layout) {
			int currentColumn = 0;
			for (MTRectangle r : lr) {
				r.setPositionRelativeToParent((new Vector3D(this
						.getVerticesLocal()[0].x
						+ (size / 2f)
						+ (bezel / 2f)
						+ currentColumn++
						* (size + bezel)
						+ (maxPerLine - lr.size()) * (size / 2f + bezel / 2f),
						this.getVerticesLocal()[0].x + (size / 2 + bezel / 2f)
								+ currentRow * (size + bezel))));
				//Determine Min/Max-Positions
				for (Vertex v: r.getVerticesGlobal()) {
					if (v.x < minx) minx = v.x;
					if (v.x > maxx) maxx = v.x;
					if (v.y < miny) miny = v.y;
					if (v.y > maxy) maxy = v.y;
				}
			}
			currentRow++;
		}
		//Set Vertices to include all children
		this.setVertices(new Vertex[] {new Vertex(minx,miny), new Vertex(maxx,miny), new Vertex(maxx,maxy), new Vertex(minx,maxy),new Vertex(minx,miny)});
	}

	private void organizeRectangles() {
		layout.clear();
		layout.add(new ArrayList<MTRectangle>());
		layout.add(new ArrayList<MTRectangle>());
		layout.add(new ArrayList<MTRectangle>());
		layout.add(new ArrayList<MTRectangle>());
		current = 0;
		switch (menuContents.size()) {
		case 0:
		case -1:
			break;

		case 1:
			layout.get(0).addAll(next(1));
			maxPerLine = 1;
			break;
		case 2:
			layout.get(0).addAll(next(2));
			maxPerLine = 2;
			break;
		case 3:
			layout.get(0).addAll(next(1));
			layout.get(1).addAll(next(2));
			maxPerLine = 2;
			break;
		case 4:
			layout.get(0).addAll(next(2));
			layout.get(1).addAll(next(2));
			maxPerLine = 2;
			break;
		case 5:
			layout.get(0).addAll(next(2));
			layout.get(1).addAll(next(3));
			maxPerLine = 3;
			break;
		case 6:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(3));
			maxPerLine = 3;
			break;
		case 7:
			layout.get(0).addAll(next(2));
			layout.get(1).addAll(next(3));
			layout.get(2).addAll(next(2));
			maxPerLine = 3;
			break;
		case 8:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(2));
			layout.get(2).addAll(next(3));
			maxPerLine = 3;
			break;
		case 9:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(3));
			layout.get(2).addAll(next(3));
			maxPerLine = 3;
			break;
		case 10:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(4));
			layout.get(2).addAll(next(3));
			maxPerLine = 4;
			break;
		case 11:
			layout.get(0).addAll(next(4));
			layout.get(1).addAll(next(3));
			layout.get(2).addAll(next(4));
			maxPerLine = 4;
			break;
		case 12:
			layout.get(0).addAll(next(4));
			layout.get(1).addAll(next(4));
			layout.get(2).addAll(next(4));
			maxPerLine = 4;
			break;
		case 13:
			layout.get(0).addAll(next(4));
			layout.get(1).addAll(next(5));
			layout.get(2).addAll(next(4));
			maxPerLine = 5;
			break;
		case 14:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(4));
			layout.get(2).addAll(next(4));
			layout.get(3).addAll(next(3));
			maxPerLine = 4;
			break;
		case 15:
			layout.get(0).addAll(next(5));
			layout.get(1).addAll(next(5));
			layout.get(2).addAll(next(5));
			maxPerLine = 5;
			break;
		case 16:
			layout.get(0).addAll(next(4));
			layout.get(1).addAll(next(4));
			layout.get(2).addAll(next(4));
			layout.get(3).addAll(next(4));
			maxPerLine = 4;
			break;

		}

	}

	private List<MTRectangle> next(int next) {
		List<MTRectangle> returnValues = new ArrayList<MTRectangle>();
		for (int i = 0; i < next; i++) {
			returnValues.add(menuContents.get(current++));
		}

		return returnValues;
	}

	private float calcTotalHeight(MTComponent[] components) {
		float height = 0;
		for (MTComponent c : components) {
			if (c instanceof MTTextArea)
				height += ((MTTextArea) c).getHeightXY(TransformSpace.LOCAL);
		}

		return height;
	}

	private int getNecessaryFontSize(List<MenuItem> strings, float size) {
		int maxNumberCharacters = 0;

		for (MenuItem s : strings) {

			if (s.getType() == MenuItem.TEXT) {
				if (s.getMenuText().contains("\n")) {
					for (String t : s.getMenuText().split("\n")) {

						if (t.length() > maxNumberCharacters)
							maxNumberCharacters = t.length();

					}
				} else {

					if (s.getMenuText().length() > maxNumberCharacters)
						maxNumberCharacters = s.getMenuText().length();

				}
			}
		}

		float spc = size / (float) maxNumberCharacters; // Space Per Character
		int returnValue = (int)(-0.5 + 1.725 * spc); //Determined using Linear Regression
		return returnValue;
	}
	public class TapListener implements IGestureEventListener {
		//Tap Listener to reach through TapListeners to children
		
		List<PolygonListeners> children;
		public TapListener(List<PolygonListeners> children) {
			this.children = children;
		}
		
		
		@Override
		public boolean processGestureEvent(MTGestureEvent ge) {
			if (ge instanceof TapEvent) {
				
				TapEvent te = (TapEvent)ge;
				if (te.getTapID() == TapEvent.BUTTON_CLICKED) {
					Vector3D w = Tools3D.project(app, app.getCurrentScene().getSceneCam(), te.getLocationOnScreen());
					for (PolygonListeners pl: children) {
						pl.component.setPickable(true);
						if (pl.component.getIntersectionGlobal(Tools3D.getCameraPickRay(app, pl.component, w.getX(), w.getY())) != null) {
							pl.listener.processGestureEvent(ge);
						} else {
					
						}
						pl.component.setPickable(false);
					}
				}
			}
			return false;
		}
		
		
		
		
	}
	

	
	
	public class PolygonListeners {
		public MTPolygon component;
		public IGestureEventListener listener;
		
		public PolygonListeners(MTPolygon component, IGestureEventListener listener) {
			this.component = component;
			this.listener = listener;
		}
		
	}
	
	
	
}

