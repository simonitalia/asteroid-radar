# Asteroid Radar Android App

This app project consists of 2 screens for displaying Near Earth Objects (aka Asteroids), made avalailable by NASA's Near Earth Objects API Web Service:

1. List Screen: Lists all asteroid objects fetched from the NASA API web service as well as the picture of the day
2. Detail Screen: Displayed when an item on the List Screen is tapped. Displays more information about the specific asteroid tapped
3. Offline support (via Room local database)


### Notes for Assessor

- Asteroid Objects / Data
  - every time asteroid JSON objects are fetched and returned, the database is updated by inserting all new (unique) asteroid objects
  - room database is updated via API fetch request each time the app launches and in the Background via Work Manager 

- Picture of the Day
  - picture of the day is not stored in the database (no requirement to do so)
  - a placeholder image is shown whilst fetching new image from api and i device is offline / has no internet connection
  - since asteroid objects on the main activity are loaded from the app’s local room db, the progress bar is hooked up to the picture of the day fetch request since that will most likely take longer to fetch the image from an api service than load the asteroid objects from the database (after the first launch of the app of course, which requires the first set of asteroid objects to be fetched)

- Overflow Menu and Filtering logic: 
  -  All filtering is handled by querying room database and returning filtered asteroid objects from the room database as follows:
  -  Show Past Week: Shows all asteroids from current date MINUS 7 days ago inclusive
  -  Show Today: Shows all asteroids for current date
  -  Show All Saved: Shows all asteroids in the room database


- Accessibility:
  - Content Description for static Text Views (ie: Titles) have been set using attributes in the xml layout
  - Content Description for dynamic Text and Image Views have been set in Binding Adapter to ensure talkback reflects state. 
  - Note! Default Content Descriptions for dynamic ImageViews have also been set in the xml layout whilst content is downloaded / loaded 

# Core Technologies

- Room (Local App Database)

- Work Manager / Background processes (API fetching and Database update)

- Retrofit (API Web Service integration)

- Moshi and Scalers (JSON De/Serialization)

- Glide (Image loading)

- Interface onClickListeners

- Binding


## Getting Started

This project's repository can be cloned via git or downloaded as a zip file.


### Installation

Once the project files are downloaded, open and run the project using the lastest stable version of Android Studio.


### Dependencies

None. (All project dependencies are already defined in the project gradle files).


# Deployment information

- <strong>Deployment Target (android API / Version):</strong> 30 / Android 11 (R)


# App Versions
- August, 2021 (version 1)


### Licence
  
GNU GENERAL PUBLIC LICENSE 
