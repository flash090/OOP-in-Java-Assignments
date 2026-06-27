## Project Title
Earthquake City Map with Interactive Distance Measurement Extension

### screenshot

The distance displayed between 2 markers

### what functionality my program includes over the basic requirements of the project through module 6
Beyond the basic course requirements, my program introduces an interactive distance-measuring tool on the map. The program can dynamically draws a visual line between these two selections and calculates the exact distance in kilometers. This added feature allows users to easily visualize and measure the proximity between different seismic activities and populated cities.  

### the additions/modifications I made to the code to support your extension  
- **Added member variables:** Declared `firstSelected`, `secondSelected` (`CommonMarker`) and `displayDistance` (`double`, sentinel value `-1`) in `EarthquakeCityMap` to track selection state and distance value in a single variable.

- **Refactored `mouseClicked()`:** Extracted `getClickedMarker()` to query the clicked marker without side effects. The method now routes to `checkdrawDistance()` on a marker click, or `resetDistance()` on a blank click, keeping display-reset and distance-state logic independent.

- **Implemented `checkdrawDistance()`:** Manages a three-state machine — idle, first selected, both selected. Distance is computed via `firstSelected.getDistanceTo(secondSelected.getLocation())` only when a valid second marker is chosen. Clicking the same marker twice or clicking blank resets state via `resetDistance()`.

- **Implemented `drawDistanceLabel()`:** Converts both markers' `Location` to screen pixels via `map.getScreenPosition()`, draws a red `line()` between them, and renders the distance label at the midpoint. Wrapped in `pushStyle()`/`popStyle()` to prevent style leakage. Returns early if `displayDistance < 0`, handling the one-marker-selected edge case gracefully.  
