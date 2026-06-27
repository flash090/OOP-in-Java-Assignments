package module5;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Han
 * Date: May,2026 
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
		
		// WHEN TAKING THIS QUIZ: Uncomment the next line
		// earthquakesURL = "quiz1.atom";
		
		
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
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		
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
	}
	
	// If there is a marker under the cursor, and lastSelected is null 
	// set the lastSelected to be the first marker found under the cursor
	// Make sure you do not select two markers.
	// 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// TODO: Implement this method
		if (lastSelected != null) 
			return;
		
		for (Marker mark: markers) {
			if (mark.isInside(map, mouseX, mouseY)) {
				lastSelected = (CommonMarker) mark;
				mark.setSelected(true);
				break;
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
		// TODO: Implement this method
		// Hint: You probably want a helper method or two to keep this code
		// from getting too long/disorganized
		
		if (lastClicked != null) {
			unhideMarkers();       
			lastClicked = null;    
			return;
		}
		
		
		// When an earthquake’s marker is selected
		for (Marker emark: quakeMarkers) {
			if (emark.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker) emark;
				hideMarkers(emark);
				showcity(emark);
				return;
		    }
		} 
		// When a city’s marker is selected
		for (Marker cmark: cityMarkers) {
			if (cmark.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker) cmark;
				hideMarkers(cmark);
				showquack(cmark);
				return;
			}
		}
		
	}
	
	// helper by me 1 
	private void showcity(Marker emark) {
		double threat = ((EarthquakeMarker) emark).threatCircle();
		for (Marker marker: cityMarkers) {
			double distance = marker.getDistanceTo(emark.getLocation());
			if (distance <= threat){ 
				marker.setHidden(false);
			}
			else {
				marker.setHidden(true);
			}
	    }
	}
	
	// helper by me 2
	private void showquack(Marker cmark) {
		for (Marker marker: quakeMarkers) {
			double threat = ((EarthquakeMarker) marker).threatCircle();
			double distance = marker.getDistanceTo(cmark.getLocation());
			if (distance <= threat){ 
				marker.setHidden(false);
			}
			else {
				marker.setHidden(true);
			}
	    }
	}
	
	// helper by me 3 
	private void hideMarkers(Marker themark) {
		
		for(Marker marker : quakeMarkers) {
			if (!marker.equals(themark)){
				marker.setHidden(true);
			}
		}
			
		for(Marker marker : cityMarkers) {
			if (!marker.equals(themark)){
				marker.setHidden(true);
		    }
		}
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
	// TODO: Update this method as appropriate
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
		
		
		// Loop over all the country markers.  
		// For each, check if the earthquake PointFeature is in the 
		// country in m.  Notice that isInCountry takes a PointFeature
		// and a Marker as input.  
		// If isInCountry ever returns true, isLand should return true.
		for (Marker m : countryMarkers) {
			// TODO: Finish this method using the helper method isInCountry
			if (isInCountry(earthquake,m)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	/* prints countries with number of earthquakes as
	 * Country1: numQuakes1
	 * Country2: numQuakes2
	 * ...
	 * OCEAN QUAKES: numOceanQuakes
	 * */
	private void printQuakes() 
	{
		// TODO: Implement this method
		// One (inefficient but correct) approach is to:
		//   Loop over all of the countries, e.g. using 
		//        for (Marker cm : countryMarkers) { ... }
		//        
		//      Inside the loop, first initialize a quake counter.
		//      Then loop through all of the earthquake
		//      markers and check to see whether (1) that marker is on land
		//     	and (2) if it is on land, that its country property matches 
		//      the name property of the country marker.   If so, increment
		//      the country's counter.
		
		// Here is some code you will find useful:
		// 
		//  * To get the name of a country from a country marker in variable cm, use:
		//     String name = (String)cm.getProperty("name");
		//  * If you have a reference to a Marker m, but you know the underlying object
		//    is an EarthquakeMarker, you can cast it:
		//       EarthquakeMarker em = (EarthquakeMarker)m;
		//    Then em can access the methods of the EarthquakeMarker class 
		//       (e.g. isOnLand)
		//  * If you know your Marker, m, is a LandQuakeMarker, then it has a "country" 
		//      property set.  You can get the country with:
		//        String country = (String)m.getProperty("country");
		for (Marker cm : countryMarkers) {
			int qcounter = 0;

			for (Marker qm : quakeMarkers) {
				EarthquakeMarker em = (EarthquakeMarker) qm;
				if (em.isOnLand) {
					if (em.getStringProperty("country").equals(cm.getStringProperty("name"))) {
					    qcounter++;
				    }
				}
			}
			if (qcounter > 0) {
				String name = (String)cm.getProperty("name");
				System.out.println(name + ":" + qcounter);
			}
		}
		
		int ocounter = 0;
		for (Marker qm : quakeMarkers) {
		    EarthquakeMarker em = (EarthquakeMarker) qm;
		    if (!em.isOnLand) {
		    	ocounter++;
		    }
		}
		
		System.out.println( "Ocean Quakes:" + ocounter);
	}
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake 
	// feature if it's in one of the countries.
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
