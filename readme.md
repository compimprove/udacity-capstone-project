# Project Overview
The travel planner application will help user to create their own trip plan at ease. The app will support map feature for user to quickly visit their location

In the future the app will support booking feature for user to make booking easier
# Major Features
- Help the user create a nice looking travel plan for their trip
- Each travel plan will contains many activities, for several days
- The activities can be flighting, take taxi, stay at hotel and the main activity of the trip
- The activity contains the name, time, location and some notes from user (location and note aren't required)
- If user choose location for their activity, a geofence request will be created and show notification when it's been reached. Click on notification will lead user to the detail activity screen, from there user can go back to their plan.
- Additionally, the weather of the location will be updated in the activity to help user aware

<br>

# PROJECT SPECIFICATION

## Android UI/UX

### Build a navigable interface consisting of multiple screens of functionality and data.
- Application includes 5 screens
- The Navigation Controller is used for Fragment-based navigation (fragments in MainActivity) and intents are utilized for Activity-based navigation (switch from MainActivity to LocationReachedActivity)
- An application bundle is built to store data passed between Fragments and Activities.

### Construct interfaces that adhere to Android standards and display appropriately on screens of different size and resolution.
- Application uses ConstraintLayout when possible (list item, create-edit screen), and constains at least 1 vertical constraint.
- Using RecyclerView at screen DayActivityDetail
- Using string* values, drawables, colors, dimensions

### Animate UI components to better utilize screen real estate and create engaging content.
- MotionLayout is used in LocationReachedActivity screen

## Local and Network data

### Connect to and consume data from a remote data source such as a RESTful API.
- Using Retrofit to fetch weather data, handle in appropriate thread
### Load network resources, such as Bitmap Images, dynamically and on-demand.
- Load travel image from Pexel using Glide
### Store data locally on the device for use between application sessions and/or offline use.
- Using Room
- Using SharedPreferences to save specific user data ( to specify new user in order to create sample data)
- Data storage operations are performed on the appropriate threads
## Android system and hardware integration
### Architect application functionality using MVVM.
- Using MVVM and Koin for dependency injection
### Implement logic to handle and respond to hardware and system events that impact the Android Lifecycle.
- Handling interaction to and from the application via Intents ( when open activity in Notification, switch between activity)
- Handling Android Permissions (location, background location)
### Utilize system hardware to provide the user with advanced functionality and features.
- Using location service, background location  and Notification

<br>

# Project Api Key

The application using:
- Google map api service (The key is implemented in Manifest file )
- Pexel service, you can get Pexel api key by follow [this guide](https://help.pexels.com/hc/en-us/articles/900004904026-How-do-I-get-an-API-key-)
- [Open weather api](https://openweathermap.org/api), you can get the key by registering account there and navigate to API section
- Pexel and open weather api key is implemented in gradle.properties file