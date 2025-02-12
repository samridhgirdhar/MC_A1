My Flight Tracker  – XML & Compose
The Journey App simulates a multi-stop journey, displaying distances, time, visa requirements, and progress. It exists in two versions:
XML Version: Uses Kotlin + XML (RecyclerView, TextViews).
Jetpack Compose Version: Uses Kotlin + Compose (LazyColumn, Text, etc.).
1. Data & File Structure
stops.txt (in res/raw/stops.txt):
Format: <CityName>,<VisaRequired(true/false)>,<DistanceInKm>,<TimeInMinutes>
Example: Toronto,true,0,0
Chicago,false,700,150
London,true,6300,800
Dubai,true,5500,750
Sydney,false,12000,1600

Both versions read this file to populate the list of stops.

2. XML Version (Traditional UI)
Languages & Frameworks: Kotlin + XML + RecyclerView.
A. Activity & Layout
activity_main.xml:
Displays either TextViews (if ≤ 3 stops) or RecyclerView (if > 3 stops).
Has TextViews for distance covered, distance remaining, time until next stop, and a ProgressBar for journey progress.
B. MainActivity.kt
Reads Data: stops = readStopsFromResource(this)
 This function splits each line into 4 parts and creates Stop objects.
Display Logic:
If 3 or fewer stops: uses simple TextViews in a LinearLayout.
If more than 3 stops: uses a RecyclerView with a StopsAdapter.
Toggle Button: Switches between km and miles by updating a conversion factor and recalculating the UI.
Next Stop Button: Increments the currentStopIndex, adds the next stop’s distance & time to the totals, and updates the UI.
Progress Bar: Shows percentage of distance covered (distanceCoveredKm / totalDistanceKm * 100).
C. StopsAdapter.kt
Binds each stop’s name, visa requirement, and distance (converted if needed) to built-in simple layouts.

3. Jetpack Compose Version
Languages & Frameworks: Kotlin + Jetpack Compose.
A. MainActivity.kt
Same data reading logic from stops.txt.
Calls a composable JourneyScreen(stopsList).
B. Composables
JourneyScreen:
State variables:
currentStopIndex, distanceCoveredKm, timeSpentMinutes, isKilometers, etc.
If ≤ 3 stops: displays them using simple Column + Text.
If > 3 stops: displays them using a LazyColumn.
Shows distance covered, distance remaining, time until next stop, and a LinearProgressIndicator.
A button toggles units between km & miles.
Another button increments currentStopIndex and updates distanceCoveredKm & timeSpentMinutes.
StopText / StopItem:
Display a single stop’s data (converted distance, time, etc.).

4. Key Features in Both Versions
Distance Toggle: Switch km ↔ miles via a button.
Next Stop Button: Moves to the next stop, updates distance/time covered.
Progress Bar / Indicator: Shows how much of the journey is completed.
Lazy List Logic:
XML: RecyclerView for >3 stops, TextViews if ≤3.
Compose: LazyColumn for >3 stops, Column + Text if ≤3.
Data from stops.txt: Each stop’s name, visa, distance, and time is parsed.

5. Running the App
Load in Android Studio:
Open either JourneyXMLApp or JourneyComposeApp project.
Build & Run:
Select a device or emulator.
Press Run.
Or Generate APK:
Build > Build APK(s).
Find in app/build/outputs/apk/debug/app-debug.apk.
Transfer/install on a real device.

6. Repository & Branches
xml-version branch: Contains the XML version code.
compose-version branch: Contains the Compose version code.
# Switch to XML version
git checkout xml-version

# Switch to Compose version
git checkout compose-version
Submission: Provide both branches in a private GitHub repo, plus a README & the APK files.

✅ Done!
This README explains how both XML and Compose versions are implemented, covering:
Data handling (stops.txt)
UI layout logic (traditional vs. lazy)
Distance/time toggles
Basic instructions to run on an emulator or device
You have a complete multi-stop journey app in two different UI frameworks. Enjoy!
