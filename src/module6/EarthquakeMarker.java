package module6;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/** Implements a visual marker for earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Han
 *
 */

//TODO: Implement the comparable interface
public abstract class EarthquakeMarker extends CommonMarker implements Comparable<EarthquakeMarker>
{
	
	// Did the earthquake occur on land?  This will be set by the subclasses.
	protected boolean isOnLand;

	// The radius of the Earthquake marker
	// You will want to set this in the constructor, either
	// using the thresholds below, or a continuous function
	// based on magnitude. 
	protected float radius;

	// constants for distance
	protected static final float kmPerMile = 1.6f;
		
	
	/** Greater than or equal to this threshold is a moderate earthquake */
	public static final float THRESHOLD_MODERATE = 5;
	/** Greater than or equal to this threshold is a light earthquake */
	public static final float THRESHOLD_LIGHT = 4;

	/** Greater than or equal to this threshold is an intermediate depth */
	public static final float THRESHOLD_INTERMEDIATE = 70;
	/** Greater than or equal to this threshold is a deep depth */
	public static final float THRESHOLD_DEEP = 300;

	// ADD constants for colors

	
	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
		
	
	// constructor
	public EarthquakeMarker (PointFeature feature) 
	{
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2*magnitude );
		setProperties(properties);
		this.radius = 1.75f*getMagnitude();
	}
	
	// TODO: Add the method:
	public int compareTo(EarthquakeMarker marker) {
		float thisMagnitude = this.getMagnitude();
		float markerMagnitude = marker.getMagnitude();
		if (thisMagnitude < markerMagnitude) {
			return 1;
		}
		else if (thisMagnitude > markerMagnitude) {
			return -1;
		}
		else {
			return 0;
		}
	}
	

	// calls abstract method drawEarthquake and then checks age and draws X if needed
	public void drawMarker(PGraphics pg, float x, float y) {
		// save previous styling
		pg.pushStyle();
			
		// determine color of marker from depth
		colorDetermine(pg);
		
	    if (isSelected()) {
	    	pg.noFill();
	    }
		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);
		
		// OPTIONAL draw X over marker if within past day		
		String age = getStringProperty("age");
		float r = radius / 1.5f;
	    if (age.equals("Past Hour") || age.equals("Past Day")) {
	    	pg.stroke(0,0,0);
	        pg.line(x - r, y - r, x + r, y + r);
	        pg.line(x + r, y - r, x - r, y + r);
	    }

		// reset to previous styling
		pg.popStyle();
		
	}
	
	/** Show the title of the earthquake if this marker is selected */
	@Override
	public void showTitle(PGraphics pg, float x, float y)
	{
		String title = getTitle();
		float w = pg.textWidth(title) + 10;
		// background
	    pg.fill(255, 255, 255);
	    pg.rect(x, y - 10, w, 25);
	    // text
		pg.fill(0);
	    pg.textSize(12);
	    pg.text(title, x + 5, y);
	}

	
	/**
	 * Return the "threat circle" radius, or distance up to 
	 * which this earthquake can affect things, for this earthquake.   
	 * DISCLAIMER: this formula is for illustration purposes
	 *  only and is not intended to be used for safety-critical 
	 *  or predictive applications.
	 */
	public double threatCircle() {	
		double miles = 20.0f * Math.pow(1.8, 2*getMagnitude()-5);
		double km = (miles * kmPerMile);
		return km;
	}
	
	
	// determine color of marker from depth, and set pg's fill color 
	// using the pg.fill method.
	// We suggest: Deep = red, intermediate = blue, shallow = yellow
	// But this is up to you, of course.
	// You might find the getters below helpful.
	private void colorDetermine(PGraphics pg) {
	    float depth = getDepth();
	    
	    if (depth < THRESHOLD_INTERMEDIATE){
	    	pg.fill(255,227,132); // shallow
	    }
	    else if (depth < THRESHOLD_DEEP) {
	    	pg.fill(255,153,18); // intermediate
	    }
	    else {
	    	pg.fill(255,97,3); // deep
	    }
	}
	
	/** toString
	 * Returns an earthquake marker's string representation
	 * @return the string representation of an earthquake marker.
	 */
	public String toString()
	{
		return getTitle();
	}

	
	/*
	 * getters for earthquake properties
	 */
	
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}
	
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}
	
	public String getTitle() {
		return (String) getProperty("title");	
		
	}
	
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}
	
	public boolean isOnLand()
	{
		return isOnLand;
	}
	
}
