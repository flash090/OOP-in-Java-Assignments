package module6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import parsing.ParseFeed;
import processing.core.PApplet;

import de.fhpotsdam.unfolding.providers.Microsoft;
// new for m6 extension
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import de.fhpotsdam.unfolding.geo.Location;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Han
 * Date: June,2026 
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	// new for MODULE 6 extension
	private CommonMarker firstSelected = null;
	private CommonMarker secondSelected = null;
	private double displayDistance = -1;
	
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(1000, 600);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			// map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			map = new UnfoldingMap(this, 0, 0, 1000, 500, new Microsoft.RoadProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		// earthquakesURL = "test1.atom";
		// earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
		// earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    // printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	    
	    sortAndPrint(10);
	    
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		drawDistance();
		
	}
	
	
	// TODO: Add the method:
	// and then call that method from setUp
	private void sortAndPrint(int numToPrint) {
		EarthquakeMarker[] sortquakes = quakeMarkers.toArray(new EarthquakeMarker[0]);
		java.util.Arrays.sort(sortquakes);
		if (numToPrint > sortquakes.length) {
			numToPrint = sortquakes.length;
		}
		for (int i = 0; i < numToPrint; i++) {
		    System.out.println(sortquakes[i]);
		}
	}

	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
	    // clicked on an earthquake marker
	    for (Marker emark : quakeMarkers) {
	        if (emark.isInside(map, mouseX, mouseY)) {
	            // reset display before showing new selection
	            if (lastClicked != null) {
	                unhideMarkers();
	            }
	            lastClicked = (CommonMarker) emark;
	            showcity(emark);
	        	// new for MODULE 6 extension
	            checkdrawDistance((CommonMarker) emark);
	            return;
	        }
	    }

	    // clicked on a city marker
	    for (Marker cmark : cityMarkers) {
	        if (cmark.isInside(map, mouseX, mouseY)) {
	            // reset display before showing new selection
	            if (lastClicked != null) {
	                unhideMarkers();
	            }
	            lastClicked = (CommonMarker) cmark;
	            showquack(cmark);
	            checkdrawDistance((CommonMarker) cmark);
	            return;
	        }
	    }

	    // clicked on blank area — reset everything
	    resetAll();
	}
	
	// new for MODULE 6 extension
	
	// reset all state to initial
	private void resetAll() {
	    unhideMarkers();
	    lastClicked = null;
	    firstSelected = null;
	    secondSelected = null;
	    displayDistance = -1;
	}
	
	    
	// handle distance feature logic
	private void checkdrawDistance(CommonMarker clicked) {
	    if (firstSelected == null) {
	        // first click — set start point
	        firstSelected = clicked;
	        displayDistance = -1;
	    } else if (clicked != firstSelected) {
	        // second click on different marker — set end point and calculate
	        secondSelected = clicked;
	        displayDistance = firstSelected.getDistanceTo(
	            secondSelected.getLocation()
	        );
	    } else {
	        // clicked same marker again — reset distance selection
	        firstSelected = null;
	        secondSelected = null;
	        displayDistance = -1;
	    }
	}
	
	// helper by me 1 at m5
	private void showcity(Marker emark) {
		double threat = ((EarthquakeMarker) emark).threatCircle();
		for (Marker marker: cityMarkers) {
			double distance = marker.getDistanceTo(emark.getLocation());
			if (distance > threat){ 
				marker.setHidden(true);
			}
	    }
		for (Marker marker : quakeMarkers) {
	        if (marker != emark) {
	        	marker.setHidden(true);
	        }
		}
	}
	
	// helper by me 2 at m5
	private void showquack(Marker cmark) {
		for (Marker marker: quakeMarkers) {
			double threat = ((EarthquakeMarker) marker).threatCircle();
			double distance = marker.getDistanceTo(cmark.getLocation());
			if (distance > threat){ 
				marker.setHidden(true);
			}
	    }
		for(Marker marker : cityMarkers) {
			if (marker != cmark){
				marker.setHidden(true);
		    }
		}
	}

		
	// new for MODULE 6 extension 
    // draw distance label on map — call at end of draw()
	private void drawDistance() {
	    if (displayDistance < 0 || firstSelected == null || secondSelected == null) return;

	    ScreenPosition pos1 = map.getScreenPosition(firstSelected.getLocation());
	    ScreenPosition pos2 = map.getScreenPosition(secondSelected.getLocation());

	    float midX = (pos1.x + pos2.x) / 2;
	    float midY = (pos1.y + pos2.y) / 2;

	    pushStyle();    // save current style
	    
	    stroke(0, 0, 0);
	    strokeWeight(1);
	    line(pos1.x, pos1.y, pos2.x, pos2.y);
	    
	    fill(255, 255, 255);
	    rect(midX - 70, midY - 20, 140, 25);
	    fill(0);
	    textSize(13);
	    textAlign(CENTER);
	    text(String.format("%.2f km", displayDistance), midX, midY - 2);
	    
	    popStyle();     // restore original style
	}


	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 255, 255);
		rect(25, 50, 175, 270);
		
		// title
	    fill(0);
	    textAlign(LEFT, CENTER);
	    textSize(12);
	    text("Earthquake Key", 40, 75);

	    // 1 City Marker
	    fill(64, 224, 208);
	    triangle(50, 102, 43, 115, 57, 115);
	    fill(0, 0, 0);
	    text("City Marker", 70, 108);

	    // 2 Land Quake
	    fill(255, 255, 255);
	    ellipse(50, 135, 12, 12);
	    fill(0, 0, 0);
	    text("Land Quake", 70, 135);

	    // 3 Ocean Quake
	    fill(255, 255, 255);
	    rect(44, 156, 12, 12);
	    fill(0, 0, 0);
	    text("Ocean Quake", 70, 162);

	    // text
	    text("Size ~ Magnitude", 40, 190);

	    // newline

	    // 4 shallow color
	    fill(255, 227, 132);
	    ellipse(50, 215, 12, 12);
	    fill(0, 0, 0);
	    text("Shallow", 70, 215);

	    // 5 intermediate color
	    fill(255, 153, 18);
	    ellipse(50, 240, 12, 12);
	    fill(0, 0, 0);
	    text("Intermediate", 70, 240);

	    // 6 deep color
	    fill(255, 97, 3);
	    ellipse(50, 265, 12, 12);
	    fill(0, 0, 0);
	    text("Deep", 70, 265);
	    
		// 7 past mark
	    fill(255, 255, 255);
	    ellipse(50, 290, 12, 12);
	    line(44, 284, 56, 296);
	    line(56, 284, 44, 296);
	    fill(0, 0, 0);
	    text("Past Hour", 70, 290);
		
	}

	

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
