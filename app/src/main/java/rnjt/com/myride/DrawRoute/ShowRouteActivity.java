package rnjt.com.myride.DrawRoute;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import rnjt.com.myride.R;

public class ShowRouteActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    double latStart= 19.240330,longStart= 73.130539;

    RelativeLayout layCarInfo, layLocation;
    TextView txtCarType,txtCarNumber,txtArTime;

    AutoCompleteTextView mEtPickLocation;
    AutoCompleteTextView mEtDropLocation;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private LatLng pickLatLng, dropLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mEtPickLocation= (AutoCompleteTextView) findViewById(R.id.etPickLocation);
        mEtDropLocation= (AutoCompleteTextView) findViewById(R.id.etDropLocation);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();


        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,null);
        mEtPickLocation.setAdapter(mAdapter);
        mEtPickLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final AutocompletePrediction item = mAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                fetchLatitudeLong(placeId,"pick");

                final CharSequence primaryText = item.getPrimaryText(null);
                Log.i("Places Adapter", "Autocomplete item selected: " + primaryText);
            }
        });
        mEtDropLocation.setAdapter(mAdapter);
        mEtDropLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final AutocompletePrediction item = mAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                fetchLatitudeLong(placeId,"drop");

                final CharSequence primaryText = item.getPrimaryText(null);
                Log.i("Places Adapter", "Autocomplete item selected: " + primaryText);
            }
        });






        layCarInfo= (RelativeLayout) findViewById(R.id.layCarInfo);
        layLocation= (RelativeLayout) findViewById(R.id.layLocation);
        layLocation.setVisibility(View.VISIBLE);
        txtArTime= (TextView) findViewById(R.id.txtArTime);
        txtCarNumber= (TextView) findViewById(R.id.txtCarNumber);
        txtCarType= (TextView) findViewById(R.id.txtCarType);

        layCarInfo.setVisibility(View.GONE);

        findViewById(R.id.txtCar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCarType.setText("Maruti Suzuki Swift");
                txtCarNumber.setText("MH 16 CD 2365");
                txtArTime.setText("Arriving: 10 Mins");
                layCarInfo.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.txtExc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCarType.setText("Toyota Traveler XL");
                txtCarNumber.setText("MH 11 VY 8754");
                txtArTime.setText("Arriving: 3 Mins");
                layCarInfo.setVisibility(View.VISIBLE);

            }
        });
        findViewById(R.id.txtAuto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtCarType.setText("Maruti Suzuki Swift");
                txtCarNumber.setText("MH 05 CD 4566");
                txtArTime.setText("Arriving: 8 Mins");
                layCarInfo.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng fromPosition = pickLatLng;
                LatLng toPosition = dropLatLng;

                //mMap.clear();

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                if(pickLatLng==null||dropLatLng==null){
                    Toast.makeText(ShowRouteActivity.this, "Please select proper Locations", Toast.LENGTH_SHORT).show();
                }else {
                    layLocation.setVisibility(View.GONE);
                    drawRoute();
                }


            }
        });
        layLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        findViewById(R.id.txtBookCar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });




    }

    private void showDialog(){

        final AlertDialog alertDialog = new AlertDialog.Builder(
                ShowRouteActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Ambulance Booked!");

        // Setting Dialog Message
        alertDialog.setMessage("Arriving : 10 Mins\n" +
                "Vehicle: Swift Dezire\n" +
                "Cost: 127INR\n" +
                "Driver: Ram Pandey"
        );

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_logo_ambula);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }


    private void fetchLatitudeLong(String placeId, final String type){
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            if (type.equals("pick")){
                                pickLatLng = myPlace.getLatLng();
                            }else if (type.equals("drop")){
                                dropLatLng = myPlace.getLatLng();
                            }
                        }
                        places.release();
                    }
                });
    }


    private void drawRoute(){
        //Bundle bundle = getIntent().getParcelableExtra("bundle");
        //LatLng toPosition = bundle.getParcelable("to_position");
        //LatLng destination = toPosition;
        mMap.clear();
        LatLng origin = pickLatLng;
        LatLng destination = dropLatLng;
        DrawRouteMaps.getInstance(this)
                .draw(origin, destination, mMap);
        DrawMarker.getInstance(this).draw(mMap, origin, R.drawable.marker, "Origin Location");
        DrawMarker.getInstance(this).draw(mMap, destination, R.drawable.marker, "Destination Location");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination).build();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Bundle bundle = getIntent().getParcelableExtra("bundle");
        //  LatLng fromPosition = bundle.getParcelable("from_position");
        // LatLng toPosition = bundle.getParcelable("to_position");

        /*LatLng origin =new LatLng(-7.788969, 110.338382);//fromPosition;
        LatLng destination =new LatLng(-7.781200, 110.349709);//toPosition;
        DrawRouteMaps.getInstance(this)
                .draw(origin, destination, mMap);

        DrawMarker.getInstance(this).draw(mMap, origin,R.drawable.marker, "Origin Location");
        DrawMarker.getInstance(this).draw(mMap, destination, R.drawable.marker, "Destination Location");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination).build();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
