package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;


public class MainActivity extends AppCompatActivity implements Session.SearchListener, CameraListener {

    private final String MAPKIT_API_KEY = "48fafd2b-6dbe-4eec-9216-688d45b7595c";


    private MapView mapView;
    //private final Point TARGET_LOCATION = new Point(59.945933, 30.320045);
    private SearchManager searchManager;
    private Session searchSession;

    private EditText searchEdit;


    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                (Session.SearchListener) this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.setLocale("RU_ru");
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);

        searchEdit = (EditText)findViewById(R.id.search_edit);
    }


    public void onMyButtonClick(View view)
    {
        submitQuery(searchEdit.getText().toString());
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }


    @Override
    public void onCameraPositionChanged(
        Map map,
        CameraPosition cameraPosition,
        CameraUpdateSource cameraUpdateSource,
        boolean finished) {
        if (finished) {

        }
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();

        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                        resultLocation,
                        ImageProvider.fromResource(this, R.drawable.search_result));
                mapView.getMap().move(new CameraPosition(resultLocation, 18.0f, 0.0f, 0.0f));
            }
        }
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }






}
