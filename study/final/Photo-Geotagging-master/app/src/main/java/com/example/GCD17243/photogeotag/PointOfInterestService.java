package com.example.GCD17243.photogeotag.CreatePOIActivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class PointOfInterestService extends Service {
  
  public static final String BROADCAST_POI = "com.alex.poi_service";
  
  public PointOfInterestService() {
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
  }
  
  @Override
  public int onStartCommand(final Intent intent, int flags, int startId) {
    
    //Get the user's position
    LatLng userPosition = (LatLng) intent.getParcelableExtra("userPosition");
    
    //Query for POIs within +- 111km (roughly) of the user's position
    FirebaseManager.getInstance().getPOIRef().orderByChild("longitude").startAt(userPosition.longitude - 1.0).endAt(userPosition.longitude + 1.0).addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        PointOfInterest poi = dataSnapshot.getValue(PointOfInterest.class);
        Intent intent = new Intent(BROADCAST_POI);
        //Pass the coordinates to the intent
        intent.putExtra("poi", poi);
        intent.putExtra("action", "add");
  
        //Broadcast the intent to MapActivity
        sendBroadcast(intent);
      }
  
      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    
      }
  
      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        PointOfInterest poi = dataSnapshot.getValue(PointOfInterest.class);
        Intent intent = new Intent(BROADCAST_POI);
        //Pass the coordinates to the intent
        intent.putExtra("poi", poi);
        intent.putExtra("action", "remove");
  
        //Broadcast the intent to MapActivity
        sendBroadcast(intent);
      }
  
      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    
      }
  
      @Override
      public void onCancelled(DatabaseError databaseError) {
    
      }
    });
    
    return START_STICKY;
  }
}
