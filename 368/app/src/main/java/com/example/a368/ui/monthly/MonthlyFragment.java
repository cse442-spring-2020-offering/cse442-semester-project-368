package com.example.a368.ui.monthly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.R;
import com.example.a368.Schedule;
import com.example.a368.ui.monthly.MonthlyAdapter;
import com.example.a368.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by: Jiwon Choi
 * This has the calendar and monthly schedule.
 */

public class MonthlyFragment extends Fragment implements MonthlyAdapter.onClickListener {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/fetch_schedule.php";
    private MonthlyViewModel monthlyViewModel;
    private TextView month;
    private static SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
    protected static Date passDate;

    private RecyclerView sList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Schedule> scheduleList;
    private RecyclerView.Adapter adapter;

    // Calendar Library
    private static CompactCalendarView compactCalendar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        monthlyViewModel =
                ViewModelProviders.of(this).get(MonthlyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Add Schedule Floating Button
        FloatingActionButton fab = (FloatingActionButton)root.findViewById(R.id.fab_add_monthly);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddMonthlySchedule.class));
            }
        });

        compactCalendar = (CompactCalendarView) root.findViewById(R.id.calendar_view);
        compactCalendar.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendar.shouldDrawIndicatorsBelowSelectedDays(true);
        month = (TextView) root.findViewById(R.id.calendar_month);
        month.setText(dateFormatMonth.format(compactCalendar.getFirstDayOfCurrentMonth()));

        long currentTime = System.currentTimeMillis();
        Date today = new Date(currentTime);
        passDate = today;

        sList = (RecyclerView)root.findViewById(R.id.monthly_schedule_list);
        scheduleList = new ArrayList<>();
        adapter = new MonthlyAdapter(getContext(), scheduleList, this);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(sList.getContext(), linearLayoutManager.getOrientation());

        sList.setHasFixedSize(true);
        sList.setLayoutManager(linearLayoutManager);
        sList.addItemDecoration(dividerItemDecoration);
        sList.setAdapter(adapter);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                month.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        return root;
    }

    // Updates view schedule list
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    // Fetch JSON data to display schedule
    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                scheduleList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // Get today
                        Date today = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String formatted_today = dateFormat.format(today);

                        // Get current time
                        Date now = Calendar.getInstance().getTime();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String formatted_time = timeFormat.format(now);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("email").equals(User.getInstance().getEmail())) {
//                                jsonObject.getString("start_date").equals(formatted_today)) {
//
//                            if ((jsonObject.getString("end_date").equals(formatted_today) &&
//                                    check_timings(formatted_time, jsonObject.getString("end_time"))) ||
//                                    (!(jsonObject.getString("end_date").equals(formatted_today)))) {

                                Schedule schedule = new Schedule();

                                schedule.setID((jsonObject.getInt("id")));
                                schedule.setName(jsonObject.getString("title"));
                                schedule.setStart_date(jsonObject.getString("start_date"));
                                schedule.setStart_date(date_parsing(schedule.getStart_date().substring(0, 5),
                                        "MM/dd", "MMM d"));

                                schedule.setStart_time(jsonObject.getString("start_time"));
                                schedule.setStart_time(date_parsing(schedule.getStart_time(),
                                        "hh:mm aaa", "HH:mm"));

                                schedule.setEnd_date(jsonObject.getString("end_date"));
                                schedule.setEnd_date(date_parsing(schedule.getEnd_date().substring(0, 5),
                                        "MM/dd", "MMM d"));

                                schedule.setEnd_time(jsonObject.getString("end_time"));
                                schedule.setEnd_time(date_parsing(schedule.getEnd_time(),
                                        "hh:mm aaa", "HH:mm"));

                                schedule.setDescription(jsonObject.getString("description"));

                                // empty description disregarded
                                if (schedule.getDescription().equals("Exception: No Text Applied")) {
                                    schedule.setDescription("");
                                }
                                scheduleList.add(schedule);
//                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                // Sort by start time
                sortArray(scheduleList);

                // Re-format time
                for (int i = 0; i < scheduleList.size(); i++) {
                    // start time
                    scheduleList.get(i).setStart_time(date_parsing(scheduleList.get(i).getStart_time(),
                            "HH:mm", "hh:mm aaa"));

                    // end time
                    scheduleList.get(i).setEnd_time(date_parsing(scheduleList.get(i).getEnd_time(),
                            "HH:mm", "hh:mm aaa"));
                }

                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    // TODO
    @Override
    public void onClickSchedule(int position) {

    }

    // Check for passed time
    private boolean check_timings(String time, String endtime) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String time2 = date_parsing(endtime, "hh:mm aaa", "HH:mm");

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(time2);

            if(date1.before(date2)) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e){
            e.printStackTrace();
        }

        return false;
    }

    private void sortArray(List<Schedule> arrayList) {
        if (arrayList != null) {
            Collections.sort(arrayList, new Comparator<Schedule>() {
                @Override
                public int compare(Schedule o1, Schedule o2) {
                    return o1.getStart_time().compareTo(o2.getStart_time()); }
            });
        }
    }

    // Converts date format
    public String date_parsing (String old_date, String input_format, String output_format) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(input_format);
        SimpleDateFormat outputFormat = new SimpleDateFormat(output_format);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(old_date);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}