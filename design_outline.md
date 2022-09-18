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