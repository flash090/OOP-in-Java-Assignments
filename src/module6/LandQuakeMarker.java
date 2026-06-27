package module6;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Implements a visual marker for land earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Han 
 *
 */
public class LandQuakeMarker extends EarthquakeMarker {
	
	
	public LandQuakeMarker(PointFeature quake) {
		
		// calling EarthquakeMarker constructor
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = true;
	}


	/** Draw the earthquake as an ellipse */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		// TA version：pg.ellipse(x, y, 2*radius, 2*radius);
		// my version:
		float r = radius * 2.0f;
		pg.rect(x - r/2, y - r/2, r, r);
	}
	


	// Get the country the earthquake is in
	public String getCountry() {
		return (String) getProperty("country");
	}

		
}