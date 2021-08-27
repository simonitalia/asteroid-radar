# Asteroid Radar Android App

This app project consists of 2 screens for displaying Near Earth Objects (aka Asteroids), made avalailable by NASA's Near Earth Objects API Web Service:

1. List Screen: Lists all asteroid objects fetched from the NASA API web service as well as the picture of the day
2. Detail Screen: Displayed when an item on the List Screen is tapped. Displays more information about the specific asteroid tapped
3. Offline support (via Room local database)


### Notes for Assessor

- Asteroid Objects / Data
  - every time asteroid JSON objects are fetched and returned, the database is purged of all existing records and new asteroid objects are inserted
  - therefore database always only holds 7 days worth of asteroid objects

- Picture of the Day
  - picture of the day is not stored in the database (no requirement to do so)
  - a placeholder image is shown whilst fetching new image from api and i device is offline / has no internet connection
  - since asteroid objects on the main activity are loaded from the app’s local room db, the progress bar is hooked up to the picture of the day fetch request since that will most likely take longer to fetch the image from an api service than load the asteroid objects from the database (after the first launch of the app of course, which requires the first set of asteroid objects to be fetched)

- Options Menu 
  - has not been implemented for this submission since this is not a rubric requirement


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
