package rnjt.com.myride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rnjt.com.myride.model.CustomArray;

import static rnjt.com.myride.ShowRouteActivity.getArrayVal;

public class BookingHistoryActivity extends AppCompatActivity {

    ArrayList<CustomArray> customArrays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        Log.d(" BookingHisty ", " data: " + getArrayVal(BookingHistoryActivity.this));

        customArrays = new ArrayList<>();
        customArrays = getArrayVal(BookingHistoryActivity.this);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        CountryAdapter ca = new CountryAdapter(customArrays);
        rv.setAdapter(ca);

    }

    public class CountryAdapter extends
            RecyclerView.Adapter<CountryAdapter.MyViewHolder> {

        private List<CustomArray> countryList;

        /**
         * View holder class
         */
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView countryText;
            public TextView popText, time;

            public MyViewHolder(View view) {
                super(view);
                countryText = (TextView) view.findViewById(R.id.countryName);
                popText = (TextView) view.findViewById(R.id.pop);
                time = (TextView) view.findViewById(R.id.time);
            }
        }

        public CountryAdapter(List<CustomArray> countryList) {
            this.countryList = countryList;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CustomArray c = countryList.get(position);
            holder.countryText.setText(c.getCarModel());
            holder.popText.setText("Amount: " + String.valueOf(c.getPrice() + " INR"));
            holder.time.setText("Booking Time: " + getDate(c.getTimestamp(), "dd/MMM/yyyy hh:mm:ss"));
        }

        @Override
        public int getItemCount() {
            return countryList.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row, parent, false);
            return new MyViewHolder(v);
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }






}
