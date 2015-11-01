# UberClone

Hello everyone, this is a Uber-like application I created. It has some features from the original one, like:
- Login
- Sign up
- Profile
- Add Home and Work location
- Share on social media
- Ask for a ride
- Track your driver position, name, car plate, etc
- Post-ride details (Distance, time, fee)

Of course it is not complete, and maybe implementation is not optimal and has some bugs, so any recommendation is welcomed :)


For deploying the app, you'll need to add certain dependencies to the gradle. In this case I used these ones:
- compile 'com.android.support:appcompat-v7:21.0.3'
- compile 'com.google.android.gms:play-services:6.5.87'

You will also need to run: 
- server https://github.com/david-zhou/uberserver 
- and the driver app https://github.com/david-zhou/UberDriver, 
and put the server IP in the strings file.

For using Google Maps, Google Places and all other Google features, you need an API_KEY, so go to https://console.developers.google.com/project and get one
