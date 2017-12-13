package com.fw.olauberintegration;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.fw.olauberintegration.api.ApiRequestHandler;
import com.fw.olauberintegration.api.request.base.ErrorResponseData;
import com.fw.olauberintegration.api.request.base.Request;
import com.fw.olauberintegration.api.request.uberrideestimate.UberRideEstimateResponseData;
import com.fw.olauberintegration.fragment.OlaRideFragment;
import com.fw.olauberintegration.fragment.UberRideFragment;
import com.fw.olauberintegration.model.OlaRideEstimate;
import com.fw.olauberintegration.model.UberRideEstimate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.etPickLocation) AutoCompleteTextView mEtPickLocation;
    @BindView(R.id.etDropLocation) AutoCompleteTextView mEtDropLocation;
    @BindView(R.id.sliding_tabs) TabLayout mSlidingTabLayout;
    @BindView(R.id.viewpager) ViewPager mRideViewPager;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private LatLng pickLatLng, dropLatLng;
    private List<OlaRideEstimate> olaRideEstimateList = new ArrayList<>();
    private List<UberRideEstimate> uberRideEstimateList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        mRideViewPager.beginFakeDrag();
        mSlidingTabLayout.setupWithViewPager(mRideViewPager);
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

    @OnClick({R.id.btnSubmit})
    public void submitLocation(){


        LatLng fromPosition = pickLatLng;
        LatLng toPosition = dropLatLng;

        Bundle args = new Bundle();
        args.putParcelable("from_position", fromPosition);
        args.putParcelable("to_position", toPosition);

        startActivity(new Intent(MainActivity.this, ShowRouteActivity.class).putExtra("bundle", args));

        /*ApiRequestHandler.fetchAllOlaRideEstimate(pickLatLng, dropLatLng, "",new Request.RequestDelegate<OlaRideEstimateResponseData>() {
            @Override
            public void onSuccess(OlaRideEstimateResponseData result) {
                Log.e("Ola Estimate", result.getRideEstimateList().size()+"");
                olaRideEstimateList = result.getRideEstimateList();
                fetchUberEstimates();
            }

            @Override
            public void onError(ErrorResponseData errorResponse) {

            }

            @Override
            public void onFailed(Throwable t) {

            }
        });*/
    }

    private void fetchUberEstimates(){
        ApiRequestHandler.fetchAllUberRideEstimate(pickLatLng, dropLatLng, new Request.RequestDelegate<UberRideEstimateResponseData>() {
            @Override
            public void onSuccess(UberRideEstimateResponseData result) {
                Log.e("Uber Estimate", result.getRideEstimateList().size()+"");
                uberRideEstimateList = result.getRideEstimateList();

                mRideViewPager.setAdapter(new RidePagerAdapter(getSupportFragmentManager(),
                        MainActivity.this));
            }

            @Override
            public void onError(ErrorResponseData errorResponse) {

            }

            @Override
            public void onFailed(Throwable t) {

            }
        });
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("Places Adapter", "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

//            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,Toast.LENGTH_SHORT).show();
            Log.i("Places Adapter", "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Places Adapter", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.i("Places Adapter", "Place details received: " + place.getName());

            places.release();
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Google Api Client", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private class RidePagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = { "Ola", "Uber" };
        private int[] imageResId = {
                R.mipmap.logomark,
                R.mipmap.logomark,
        };
        private Context context;

        public RidePagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new OlaRideFragment();
                    Bundle bo = new Bundle();
                    bo.putSerializable("ola_ride", (Serializable) olaRideEstimateList);
                    fragment.setArguments(bo);
                    break;
                case 1:
                    fragment = new UberRideFragment();
                    Bundle bu = new Bundle();
                    bu.putSerializable("uber_ride", (Serializable) uberRideEstimateList);
                    fragment.setArguments(bu);
                    break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable image = context.getResources().getDrawable(imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString("   " + tabTitles[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
    }
}
