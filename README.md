# UCSDUnfoldingMaps  
  

## Extension 
### Project Title
Earthquake City Map with Interactive Distance Measurement Extension

### screenshot
![Distance measurement between two markers](Preview-W6%20Screenshot.png)

### what functionality my program includes over the basic requirements of the project through module 6
Beyond the basic course requirements, my program introduces an interactive distance-measuring tool on the map. The program can dynamically draws a visual line between these two selections and calculates the exact distance in kilometers. This added feature allows users to easily visualize and measure the proximity between different seismic activities and populated cities.  

### the additions/modifications I made to the code to support your extension  
- **Added member variables:** Declared `firstSelected`, `secondSelected` (`CommonMarker`) and `displayDistance` (`double`, sentinel value `-1`) in `EarthquakeCityMap` to track selection state and distance value in a single variable.

- **Refactored `mouseClicked()`:** Extracted `getClickedMarker()` to query the clicked marker without side effects. The method now routes to `checkdrawDistance()` on a marker click, or `resetDistance()` on a blank click, keeping display-reset and distance-state logic independent.

- **Implemented `checkdrawDistance()`:** Manages a three-state machine — idle, first selected, both selected. Distance is computed via `firstSelected.getDistanceTo(secondSelected.getLocation())` only when a valid second marker is chosen. Clicking the same marker twice or clicking blank resets state via `resetDistance()`.

- **Implemented `drawDistanceLabel()`:** Converts both markers' `Location` to screen pixels via `map.getScreenPosition()`, draws a red `line()` between them, and renders the distance label at the midpoint. Wrapped in `pushStyle()`/`popStyle()` to prevent style leakage. Returns early if `displayDistance < 0`, handling the one-marker-selected edge case gracefully.  

### Module previews

![W5](Preview-W5%20Screenshot.png)
![W4](Preview-W4%20Screenshot.png)
![W3](Preview-W3%20Screenshot.png)

## Notes

My notes:  

Applications/Eclipse/oop/UCSDUnfoldingMaps

module4:  

```
Marker (interface)  
    └── AbstractMarker (abstract)  
            └── SimplePointMarker  
                    ├── EarthquakeMarker (abstract)  
                    │       ├── LandQuakeMarker  
                    │       └── OceanQuakeMarker  
                    └── CityMarker  
```
                    
                    
@startuml

interface Marker

abstract class AbstractMarker implements Marker

class SimplePointMarker extends AbstractMarker

abstract class EarthquakeMarker extends SimplePointMarker

class CityMarker extends SimplePointMarker

class LandQuakeMarker extends EarthquakeMarker

class OceanQuakeMarker extends EarthquakeMarker

@enduml  

module5:

```
Marker (interface)
└── AbstractMarker (abstract)
    └── SimplePointMarker
        └── CommonMarker (abstract)
            ├── EarthquakeMarker (abstract)
            │   ├── LandQuakeMarker
            │   └── OceanQuakeMarker
            └── CityMarker
```
@startuml

abstract class CommonMarker extends SimplePointMarker

abstract class EarthquakeMarker extends CommonMarker

class CityMarker extends CommonMarker

class LandQuakeMarker extends EarthquakeMarker

class OceanQuakeMarker extends EarthquakeMarker

@enduml



## by UC San Diego/Coursera 
### unfolding_app_template and UC San Diego/Coursera MOOC starter code


This is a skeleton to use Unfolding in Eclipse as well as some starter
code for the Object Oriented Programming in Java course offered by 
UC San Diego through Coursera.

A very basic Unfolding demo you'll find in the source folder in the default package. 
For more examples visit http://unfoldingmaps.org, or download the template with
examples.

The module folders contain the starter code for the programming assignments
associated with the MOOC.

Get excited and make things!


INSTALLATION

Import this folder in Eclipse ('File' -> 'Import' -> 'Existing Projects into
Workspace', Select this folder, 'Finish')


MANUAL INSTALLATION

If the import does not work follow the steps below.

- Create new Java project
- Copy+Paste all files into project
- Add all lib/*.jars to build path
- Set native library location for jogl.jar. Choose appropriate folder for your OS.
- Add data/ as src


TROUBLE SHOOTING

Switch Java Compiler to 1.6 if you get VM problems. (Processing should work with Java 1.6, and 1.7)

