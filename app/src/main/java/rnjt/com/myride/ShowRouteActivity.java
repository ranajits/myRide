package rnjt.com.myride;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import rnjt.com.myride.DrawRoute.DrawMarker;
import rnjt.com.myride.DrawRoute.DrawRouteMaps;
import rnjt.com.myride.DrawRoute.PlaceAutocompleteAdapter;
import rnjt.com.myride.model.CustomArray;

public class ShowRouteActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    double latStart = 19.240330, longStart = 73.130539;

    RelativeLayout layCarInfo, layLocation;
    TextView txtCarType, txtCarNumber, txtArTime;

    AutoCompleteTextView mEtPickLocation;
    AutoCompleteTextView mEtDropLocation;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private LatLng pickLatLng, dropLatLng;
    boolean isRouteClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mEtPickLocation = (AutoCompleteTextView) findViewById(R.id.etPickLocation);
        mEtDropLocation = (AutoCompleteTextView) findViewById(R.id.etDropLocation);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();


        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null, null);
        mEtPickLocation.setAdapter(mAdapter);
        mEtPickLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final AutocompletePrediction item = mAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                fetchLatitudeLong(placeId, "pick");

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
                fetchLatitudeLong(placeId, "drop");

                final CharSequence primaryText = item.getPrimaryText(null);
                Log.i("Places Adapter", "Autocomplete item selected: " + primaryText);
            }
        });


        layCarInfo = (RelativeLayout) findViewById(R.id.layCarInfo);
        layLocation = (RelativeLayout) findViewById(R.id.layLocation);
        layLocation.setVisibility(View.VISIBLE);
        txtArTime = (TextView) findViewById(R.id.txtArTime);
        txtCarNumber = (TextView) findViewById(R.id.txtCarNumber);
        txtCarType = (TextView) findViewById(R.id.txtCarType);

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

                if (pickLatLng == null || dropLatLng == null) {
                    Toast.makeText(ShowRouteActivity.this, "Please select proper Locations", Toast.LENGTH_SHORT).show();
                } else {
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

                CustomArray customArray= new CustomArray();
                customArray.setCarModel(""+txtCarType.getText().toString());
                Random rand = new Random();
                int pickedNumber = rand.nextInt(500) + 120;
                customArray.setPrice(""+pickedNumber);
                customArray.setTimestamp(System.currentTimeMillis());
                ArrayList<CustomArray> customArrays= new ArrayList<CustomArray>();
                customArrays.add(customArray);
                storeArrayVal(customArrays, ShowRouteActivity.this);
                showBookingDialog();
            }
        });


        findViewById(R.id.imgRoute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layLocation.setVisibility(View.VISIBLE);
                isRouteClicked=true;
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(isRouteClicked){
            isRouteClicked=false;
            layLocation.setVisibility(View.GONE);
        }else {
            finish();
        }
    }


    private void showPatientDialog() {


        final Dialog dialog = new Dialog(ShowRouteActivity.this);

// Set the title
        dialog.setTitle("Dialog Title");

// inflate the layout
        dialog.setContentView(R.layout.dialog_patient_detail);

        // Set the dialog text -- this is better done in the XML
        Button text = (Button) dialog.findViewById(R.id.btnSubmit);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(ShowRouteActivity.this, "Patient Details Added Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


// Display the dialog
        dialog.show();

    }

    private void showBookingDialog() {


        final Dialog dialog = new Dialog(ShowRouteActivity.this);

// Set the title
        dialog.setCancelable(false);

        dialog.setTitle("Dialog Title");

// inflate the layout
        dialog.setContentView(R.layout.dialog_booking_detail);

        // Set the dialog text -- this is better done in the XML
        TextView text = (TextView) dialog.findViewById(R.id.txtinfo);
        TextView txtAddlater = (TextView) dialog.findViewById(R.id.txtAddlater);

        txtAddlater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
               showPatientDialog();
            }
        });

        text.setText("Arriving : 10 Mins\n\n" +
                "Vehicle: Swift Dezire\n\n" +
                "Cost: 127INR\n\n" +
                "Driver: Ram Pandey\n\n"+
                "Dr. Assigned: -- ");

// Display the dialog
        dialog.show();

    }



    private void showDialog() {

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


    private void fetchLatitudeLong(String placeId, final String type) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            if (type.equals("pick")) {
                                pickLatLng = myPlace.getLatLng();
                            } else if (type.equals("drop")) {
                                dropLatLng = myPlace.getLatLng();
                            }
                        }
                        places.release();
                    }
                });
    }


    private void drawRoute() {
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


    public static void storeArrayVal(ArrayList<CustomArray> inArrayList, Context context)
    {
        SharedPreferences shref = context.getSharedPreferences("currArrayValues", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = shref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(inArrayList);

        prefEditor.putString("currArray", json);
        prefEditor.commit();

    }
    public static ArrayList getArrayVal(Context ctx)
    {
        SharedPreferences shref = ctx.getSharedPreferences("currArrayValues", Context.MODE_PRIVATE);

        ArrayList<CustomArray> rArray;
        Gson gson;
        gson = new GsonBuilder().create();
        String response = shref.getString("currArray", null);

        if (response!=null){

            Type type = new TypeToken<ArrayList<CustomArray>>(){}.getType();
            rArray = gson.fromJson(response, type);

        }else {
            rArray = new ArrayList<>();
        }

        return rArray;

    }



}