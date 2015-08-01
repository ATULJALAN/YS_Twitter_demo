# YS_Twitter_demo
An android application with Twitter login. After Authentication user can search for tweet images using hashtags and Location.

To compile and run the app, follow the steps.

1) Download the Zip.

2) import it in your eclipse.

3) after importing clean the project using Project->Clean.

4) Now run the application as an Android Application.

5) In order to use the application one will need an active internet and location service on.

6) open the app, login via twitter, app will display your profile and will automatically look for your location.

7) Enter the hashtag, you want to look for and the app will display all the related images. currently i have set the limit of images to 20, you can change it to any number by going to project src->com->example->ysdemo->TweetImageFragment.java-> line no. 29 and set "final int count =20" to number of tweet image you want to retrieve.
