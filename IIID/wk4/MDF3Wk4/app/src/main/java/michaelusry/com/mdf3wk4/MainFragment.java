package michaelusry.com.mdf3wk4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends MapFragment implements OnInfoWindowClickListener, OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private String TAG = "MainFragment.TAG";
    String cTitle;
    static public String cLocation = null;
    static public double clat;
    static public double clng;

    GoogleMap mMap;

    double latitude, longitude;
    private JSONObject arrayElement;

    Double lat = null;
    Double lng = null;
    String title = null;
    String path = null;
    String desc = null;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();

        latitude = bundle.getDouble("lat");
        longitude = bundle.getDouble("long");


        Log.i(TAG, "latitude: " + latitude);
        Log.i(TAG, "longitude: " + longitude);


        mMap = getMap();

        Log.i(TAG, "loadedJSonArray.length: " + MainActivity.loadedJSonArray.length());

        if (MainActivity.loadedJSonArray.length() > 0) {

            Log.i(TAG, "loadedJSonArray > 0");
            for (int i = 0; i < MainActivity.loadedJSonArray.length(); i++) {
                try {
                    arrayElement = MainActivity.loadedJSonArray.getJSONObject(i);
                } catch (JSONException e) {
                    Log.i(TAG, "Problem getting the object at position: \n");
                    e.printStackTrace();
                }

                path = null;
                lat = null;
                lng = null;
                title = null;
                desc = null;

                try {
                    JSONObject json_data = MainActivity.loadedJSonArray.getJSONObject(i);
                    path = json_data.getString("path");
                    lat = json_data.getDouble("lat");
                    lng = json_data.getDouble("lng");
                    title = json_data.getString("title");
                    desc = json_data.getString("desc");

                    Log.i(TAG, "path: " + path);
                    Log.i(TAG, "lat: " + lat);
                    Log.i(TAG, "lng: " + lng);
                    Log.i(TAG, "title: " + title);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title));

            }


        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here"));

        mMap.setInfoWindowAdapter(new MarkerAdapter());
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG, "onMarkerClick");


                Intent i = new Intent(getActivity(), DetailsActivity.class);

                Log.i(TAG, "path: " + path);
                Log.i(TAG, "title: " + title);
                Log.i(TAG, "desc: " + desc);

                i.putExtra("path", path);
                i.putExtra("desc", desc);
                i.putExtra("title", title);
                startActivity(i);

                return false;
            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Marker Clicked")
                .setMessage("You clicked on: " + marker.getTitle())
                .setPositiveButton("Close", null)
                .setNegativeButton("Remove", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.remove();
                    }
                })
                .show();
    }

    @Override
    public void onMapClick(final LatLng location) {
        Log.i(TAG, "onMapClick");
        new AlertDialog.Builder(getActivity())
                .setTitle("Map Clicked")
                .setMessage("Add new marker here?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title("New Marker")
                                        .snippet(location.toString())
                        );
                        Log.i(TAG, "addMarker: " + location);
                        cLocation = location.toString();
                        clat = location.latitude;
                        clng = location.longitude;

                    }
                })
                .show();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.i(TAG,"onMapLongClick");

        mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("New Marker")
        );
        Log.i(TAG, "addMarker: " + latLng);
        clat = latLng.latitude;
        clng = latLng.longitude;
        Intent i = new Intent(getActivity(), CameraActivity.class);
        startActivity(i);

    }

//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        Log.i(TAG,"*******[onMarkerClick]*******");
//        Log.i(TAG,"title: " + marker.getTitle());
//        Log.i(TAG,"description: " + marker.)
//        Intent i = new Intent(getActivity(),DetailsActivity.class);
//        i.putExtra("lat",marker.)
//        startActivity(i);


//        return false;
//    }

    private class MarkerAdapter implements InfoWindowAdapter {

        TextView mText;

        public MarkerAdapter() {
            mText = new TextView(getActivity());
        }

        @Override
        public View getInfoContents(Marker marker) {
            Log.i(TAG, "*****getInfoContents*****");

            ViewGroup vg = (ViewGroup) getView();
            View v = getActivity().getLayoutInflater().inflate(R.layout.location_window, vg, false);
            TextView desc = (TextView) v.findViewById(R.id.location_description);
            desc.setText(marker.getTitle());
            cTitle = marker.getTitle();
            Log.i(TAG, "getTitle: " + marker.getTitle());
            Log.i(TAG, "cTitle: " + cTitle);
            Log.i(TAG, "getId: " + marker.getId());
            Log.i(TAG, "getSnippet: " + marker.getSnippet());
            Log.i(TAG, "getPosition: " + marker.getPosition());
            Log.i(TAG, "getClass: " + marker.getClass());

            return v;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            Log.i(TAG, "getInfoWindow");
            return null;
        }
    }

}