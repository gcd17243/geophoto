package com.example.GCD17243.photogeotag.CreatePOIActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends android.app.Fragment{
  
  private ImageView imageView;
  private TextView userText;
  private TextView title;
  private PointOfInterest poi;
  
  
  public DetailsFragment() {
    // Required empty public constructor
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_details, container, false);
    
    imageView = (ImageView) rootView.findViewById(R.id.markerImageView);
    title = (TextView) rootView.findViewById(R.id.title_text_view);
    userText = (TextView) rootView.findViewById(R.id.user_text_view);
  
    return rootView;
  }
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Retain the instance of the fragment on orientation changes
    //setRetainInstance(true);
  }
  
  
  @Override
  public void onStart() {
    super.onStart();
    
    if(this.getArguments() == null) return;
  
    //Get the poi from the bundle
    final PointOfInterest poi = this.getArguments().getParcelable("poi");
  
    /*
      Images are stored under a different reference in database so that they are not downloaded along
      with the POI object. Instead the image is only downloaded when needed (e.g. when the detail
      fragment requires it). The custom listener notifies us when the image has been updated. Since
      all POI objects are passed as parcelables on the details activity or fragment, when the image is
      downloaded, it will be "saved" in the object. Any subsequent clicks on marker will not
      re-download the image from firebase.
     */
    
    //Get the encoded image of the POI
    if (poi.getEncodedImage() == null || poi.getEncodedImage().isEmpty()) {
      //If the String is not set, request the image from firebase
      //Register the custom adapter
      poi.registerCallbackInterface(new PointOfInterest.POIInteface() {
        @Override
        public void onImageUpdate() {
          //When the image has been fetched and updated in the poi
          String encodedImage = poi.getEncodedImage();
          //Set it in the image view of the fragment
          try {
            //Decode it into a bitmap
            imageView.setImageBitmap(FirebaseManager.getInstance().decodeFromBase64(encodedImage));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
      //Query for the encoded string (image) from firebase. Listener notifies when this is done
      poi.queryImage();
    } else {
      //If the encoded image has already been fetched for this POI
      try {
        imageView.setImageBitmap(FirebaseManager.getInstance().decodeFromBase64(poi.getEncodedImage()));
      } catch (IOException e) {
        e.printStackTrace();
      }
      
    }
  
    userText.setText("Created by: " + poi.getUser());
    title.setText(poi.getName());
  }
}
