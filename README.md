# YS_Twitter_demo
An android application with Twitter login. After Authentication user can search for tweet images using hashtags and Location.

To compile and run the app, follow the steps.

1) Download the Zip.

2) Import it in your eclipse.

3) After importing clean the project using Project->Clean.

4) Now run the application as an Android Application.

5) In order to use the application, one will need an active internet and location service on.

6) Open the app, login via twitter, app will display your profile and will automatically look for your location.

7) Enter the hashtag, you want to look for and the app will display all the related images. currently i have set the limit of images to 20, you can change it to any number by going to project src->com->example->ysdemo->TweetImageFragment.java-> line no. 30 and set "final int count =20" to number of tweet image you want to retrieve.

8) Currently i am searching for a area with radius 100 km from current location. to change the value go to src->com->example->ysdemo->TweetImageFragment.java->line no. 31 and set "final int radius =100" to number as your wish.

9) Library used for the application twitter4j :)
