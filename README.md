# FitnessTracker

A simple app built as a test for Human Project Inc. The app will track the distance walked based on GPS location. It also has a SQLite database for local user account functionality. Notifications will be given at intervals of 1000ft walked, and office mode can be entered in order to recieve a notification to get up and walk around every hour.

To use it, open the app, and enter a username and password. Since the username isn't in the database yet, they a new user account will be added to the database along with it's associated password, and starting distance of 0.

***********************************************************

Once a username is made, password checking is enabled for that user. Log in to view the walking distance associated with that specific user. Hit "Start Walking" when you will begin a walk session, and "Stop Walking" when you are finished. There is also buttons for leaderboard and OfficeMode notifications.

Please be sure to enable location permissions on your local device for this app after installing. You can do this from Settings > Apps > FitnessTracker > Permissions

***********************************************************

(NOTE ON CURRENT VERSION):

Current version has a button to view the entire database and its contents. This is made for the reviewer and for the ease of development.

This version also shows the latitude/longitude updating every 10s on the user's activity, as well as distance in meters.. Leaderboard still needs to be implemented. 

POSSIBLE FUTURE IMPROVEMENTS:

possibly alter on pause method to work in background. The app will currently stop giving updates until onResume is called, or the phone is woken up. 

Turning off Office mode may not be currently done in an optimal way.

display daily stats on user activity.

requesting location permission automatically

encrypting the database passwords

sleeping and waking up phone quickly bugs it out.

problems with the accuracy of GPS. checking accelerometer on standing still could be useful to becoming more accurate. Longer intervals may help with this in the context of walking.

