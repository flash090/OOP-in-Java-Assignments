# UCSDUnfoldingMaps  
  

## Extension 
### Project Title
Earthquake City Map with Interactive Distance Measurement Extension

### screenshot
![Distance measurement between two markers](Preview-W6%20Screenshot.png)

### what functionality my program includes over the basic requirements of the project through module 6
Beyond the basic course requirements, my program introduces an interactive distance-measuring tool on the map. The program can dynamically draws a visual line between these two selections and calculates the exact distance in kilometers. This added feature allows users to easily visualize and measure the proximity between different seismic activities and populated cities.  

### the additions/modifications I made to the code to support your extension  
- **Added member variables:** I declared two new private `CommonMarker` variables, `firstSelected` and `secondSelected`, in the `EarthquakeCityMap` class to store the state of the two markers selected by the user. I also added a `double` variable named `displayDistance`, initialized to `-1`, which tracks whether the distance line is shown or hidden (`-1` means hidden) and stores the computed distance value in kilometers once both markers are selected.

- **Overrode mouse clicked logic:** I fully refactored the `mouseClicked()` method to manually track whether the user is selecting a starting marker, an ending marker, or clicking on empty map space to reset and clear the measurement. When a marker is clicked, the method still runs the existing `showcity()` / `showquack()` threat-circle logic, then delegates distance selection to a helper method `checkdrawDistance()`. On the first click, `firstSelected` is set and no line is drawn yet. On a second click on a different marker, `secondSelected` is set and the distance is calculated using `firstSelected.getDistanceTo(secondSelected.getLocation())`. Clicking the same marker twice clears the distance selection. Clicking blank map space calls `resetAll()`, which unhides all markers and clears the entire selection state.

- **Implemented the distance drawing method:** I created a helper method called `drawDistance()`, called at the end of `draw()`. The method returns early if `displayDistance` is still `-1` or either selected marker is missing, so nothing is drawn when only one marker has been chosen. When both markers are selected, I convert their geographic `Location` objects into pixel-based `ScreenPosition` coordinates using `map.getScreenPosition()`. Then I use Processing's `line()` function to draw a line connecting the two targets and display the precise distance in kilometers (formatted to two decimal places) at the midpoint of the line.   

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

