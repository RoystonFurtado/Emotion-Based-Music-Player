# Mood_Music
An Android Music player that detects the face of the user and determines his mood and plays songs that suit his current mood.
This is an android music player app that plays music based on the current mood of the user. When you start the app it will capture a photo of you using the front camera secretly i.e.without accessing the camera app.Based on the picture captured, your current mood will be determined and it will play the songs according to your mood.Firstly,you need to store the songs that you would like to listen to depending on your mood in a "Happy" and "Sad" folder anywhere in your local storage.The app will be able to automatically locate this folder then based on your mood it will play these songs when you open the app.

API's used to create this app:

android.hardware.camera2-To capture images using the mobile device.

com.google.android.gms.vision.face-To detect faces in an image.

android.graphics-To create a bitmap for processing the image.

android.media-To play music.
