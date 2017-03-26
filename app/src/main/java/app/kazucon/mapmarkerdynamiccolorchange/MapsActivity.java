package app.kazucon.mapmarkerdynamiccolorchange;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;

    private Map<String, LatLng> locations = new HashMap<String, LatLng>() {
        { put("場所1", new LatLng(35.658725884775244, 139.74541783332825)); }
        { put("場所2", new LatLng(35.66625720662894,  139.7578203678131));  }
        { put("場所3", new LatLng(35.685082403076336, 139.75284218788147)); }
        { put("場所4", new LatLng(35.636162536113574, 139.76434350013733)); }
        { put("場所5", new LatLng(35.690519966404246, 139.6994125843048));  }
    };

    private BitmapDescriptor iconNotYet;
    private BitmapDescriptor iconDisplayed;
    private BitmapDescriptor iconDisplaying;

    private Marker lastDisplayedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.iconNotYet     = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        this.iconDisplayed  = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        this.iconDisplaying = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;

        // Add a marker in Sydney and move the camera
        for (Map.Entry<String, LatLng> location : locations.entrySet()) {
            addNotYetMarker(map, location.getKey(), location.getValue());
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom((LatLng) locations.values().toArray()[0], 12));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (lastDisplayedMarker == null) {
                    // マーカー初めて選択
                    lastDisplayedMarker = replaceMarker(map, marker, iconDisplaying);

                } else if (    marker.getPosition().latitude  != lastDisplayedMarker.getPosition().latitude
                            || marker.getPosition().longitude != lastDisplayedMarker.getPosition().longitude) {

                    // 異なるマーカーを選択
                    replaceMarker(map, lastDisplayedMarker, iconDisplayed);
                    lastDisplayedMarker = replaceMarker(map, marker, iconDisplaying);
                }

                map.animateCamera(CameraUpdateFactory.newLatLng(lastDisplayedMarker.getPosition()));
                lastDisplayedMarker.showInfoWindow();

                return true;
            }
        });
    }

    private void addNotYetMarker(GoogleMap map, String name, LatLng latLng) {
        map.addMarker(newMarkerOptions(name, latLng, iconNotYet));
    }

    private Marker replaceMarker(GoogleMap map, Marker oldMarker, BitmapDescriptor newIcon) {
        oldMarker.hideInfoWindow();
        oldMarker.remove();

        // setIconは即時反映されないため、Marker作り直し
        return map.addMarker(newMarkerOptions(
                oldMarker.getTitle(),
                oldMarker.getPosition(),
                newIcon
        ));
    }

    private MarkerOptions newMarkerOptions(String title, LatLng position, BitmapDescriptor icon) {
        return new MarkerOptions()
                .title(title)
                .position(position)
                .icon(icon);
    }
}
