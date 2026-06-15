# UCSDUnfoldingMaps  
  

unfolding_app_template and UC San Diego/Coursera MOOC starter code
==================================================================

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


My notes
======================
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

