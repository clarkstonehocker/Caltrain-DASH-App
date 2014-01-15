package com.seniorproject.caltraindashmobileapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import com.seniorproject.caltraindashmobileapp.R;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.text.format.Time;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ViewDashActivity extends AbstractNavigationDrawerActivity implements OnItemSelectedListener
{
	private Spinner dashDepartures;
	private Spinner dashArrivals;
	private ListView dashTrips;
	private TextView scheduleError;
	private TextView fareDisplay;
	//private static final String DASH_ROUTE_ID = "201";
	//private static ArrayList<String> westTripIDs = new ArrayList<String>();
	//private static ArrayList<String> eastTripIDs = new ArrayList<String>();
	//private static ArrayList<String> stopIDs = new ArrayList<String>();
	//private static ArrayList<String> stopNames = new ArrayList<String>();
	//private static ArrayList<Float> stopLatitudes = new ArrayList<Float>();
	//private static ArrayList<Float> stopLongitudes = new ArrayList<Float>();
	private static String lastDepartSelection = "";
	private static String lastArriveSelection = "";
	
	private Time time;
	private String currentTime;
	
	private static final String NEXT_TRIP_MESSAGE = "Next available trip highlighted.";
	//private static boolean showTutorial = true;
	
	private NavigationDrawerActivityConfiguration navDrawerActivityConfiguration = new NavigationDrawerActivityConfiguration();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		dashDepartures = (Spinner) findViewById(R.id.dash_departure);
		dashArrivals = (Spinner) findViewById(R.id.dash_arrival);
		/*try
		{
			loadDashTrips();
			loadDashStopIDs();
			
			if(stopNames.size() < 4)
			{
				loadDashStopNames();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}*/
		//ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, westTripIDs );//MainActivity.dashStops);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MainActivity.stopNames);
		
		//adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dashDepartures.setAdapter(adapter2);
		dashDepartures.setOnItemSelectedListener(this);
		dashArrivals.setAdapter(adapter2);
		dashArrivals.setOnItemSelectedListener(this);
		lastDepartSelection = MainActivity.savedValues.getString("lastDashDepart", "");
		lastArriveSelection = MainActivity.savedValues.getString("lastDashArrive", "");
		if(lastDepartSelection != null)
		{
			dashDepartures.setSelection(getIndex(dashDepartures, lastDepartSelection ));
		}
		if(lastArriveSelection != null)
		{
			dashArrivals.setSelection(getIndex(dashArrivals, lastArriveSelection));
		}
		
		dashTrips = (ListView) findViewById(R.id.dash_trip_schedules);
		scheduleError = (TextView) findViewById(R.id.dash_schedule_error);
		fareDisplay = (TextView) findViewById(R.id.dash_trip_fare);
		DashScheduleAdapter tripsSchedule;
		try
		{
			String from = (String) dashDepartures.getSelectedItem();
			String to = (String) dashArrivals.getSelectedItem();
			ArrayList<DashTripDetails> times = getDashTripsForInputStops(from, to);
			time = new Time(Time.getCurrentTimezone());
			time.setToNow();
			currentTime = formatCurrentTime(time);
			int index = getTripIndexClosestToCurrentTime(times, currentTime);
			if(index == times.size())
			{	
				index = 0;
			}
			tripsSchedule = new DashScheduleAdapter(this, R.layout.dash_row, times, index);
			dashTrips.setAdapter(tripsSchedule);
			dashTrips.setSelection(index);
			if(from.equals(to))
			{
				dashTrips.setVisibility(View.GONE);
				scheduleError.setVisibility(View.VISIBLE);
				//scheduleError.setText("Departure and arrival stops are the same." + "\n" + "Please select another stop.");
				scheduleError.setText(getResources().getText(R.string.same_depart_and_arrive_selections));
				scheduleError.append("\n");
				scheduleError.append(getResources().getText(R.string.change_location));
				fareDisplay.setVisibility(View.INVISIBLE);
			}
			else
			{
				fareDisplay.setVisibility(View.VISIBLE);
				scheduleError.setVisibility(View.GONE);
				dashTrips.setVisibility(View.VISIBLE);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	private String formatCurrentTime(Time t)
	{
		String hour = "";
		String minute = "";
		if(t.hour < 10)
		{
			hour = "0" + t.hour; 
		}
		if(t.minute < 10)
		{
			minute = "0" + t.minute;
		}
		if(t.hour >= 10)
		{
			hour = hour + t.hour;
		}
		if(t.minute >= 10)
		{
			minute = minute + t.minute;
		}
		return hour + ":" + minute;
	}
	
	private int getTripIndexClosestToCurrentTime(ArrayList<DashTripDetails> array, String time)
	{
		int index = 0;
		boolean found = false;
		while(index < array.size() && !found)
		{
			if(array.get(index).getDepartTime().compareTo(time) > 0)
			{
				found = true;
			}
			else
			{
				index++;
			}
		}
		return index;
	}
	
	@Override
    protected NavigationDrawerActivityConfiguration getNavDrawerConfiguration() {
        
        NavigationDrawerItem[] menu = new NavigationDrawerItem[] {
                NavigationDrawerSection.create( 100, "Public Transportation"),
                NavigationDrawerMenuItem.create(101,"Home", R.drawable.home_icon, false, this),
                NavigationDrawerMenuItem.create(102, "Caltrain Schedule", R.drawable.caltrain_icon, true, this),
                NavigationDrawerMenuItem.create(103, "Dash Schedule", R.drawable.shuttle_icon, true, this),
                NavigationDrawerSection.create(200, "Twitter"),
                NavigationDrawerMenuItem.create(202, "@Caltrain_News", R.drawable.bird_black, false, this),
                NavigationDrawerMenuItem.create(203, "@GoCaltrain", R.drawable.bird_black, false, this), 
                NavigationDrawerMenuItem.create(204, "@VTA", R.drawable.bird_black, false, this)};
        
        
        navDrawerActivityConfiguration.setMainLayout(R.layout.activity_view_dash);
        navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
        navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);
        navDrawerActivityConfiguration.setNavItems(menu);
        navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);       
        navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
        navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.drawer_close);
        navDrawerActivityConfiguration.setBaseAdapter(
            new NavigationDrawerAdapter(this, R.layout.navigation_drawer_menu_item, menu ));
        return navDrawerActivityConfiguration;
    }
    
    @Override
    protected void onNavItemSelected(int id) 
    {
        switch (id) 
        {
        	case 101:
        	{
        		Intent intent = new Intent(this, MainActivity.class);
        		startActivity(intent);
        		break;
        	}
        	case 102:
        	{    
        		Intent intent = new Intent(this, ViewCaltrainActivity.class);
        		startActivity(intent);
        		break;
        	}
        	case 202:
        	{
        		Intent intent = new Intent(this, CaltrainNewsTwitterActivity.class);
        		startActivity(intent);
        		break;
        	}
        	case 203:
        	{
        		Intent intent = new Intent(this, GoCaltrainTwitterActivity.class);
        		startActivity(intent);
        		break;
        	}
        	case 204:
        	{
        		Intent intent = new Intent(this, DashTwitterActivity.class);
        		startActivity(intent);
        		break;
        	}
            
        }
    }
	
	/**
	 * Listener for spinner objects.
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		//take action only for the desired spinners
		if(parent.equals(dashDepartures) || parent.equals(dashArrivals))
		{
			//get currently selected values from spinners
			String departure = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf((String) dashDepartures.getSelectedItem() ));
			lastDepartSelection = (String) dashDepartures.getSelectedItem();
			String arrival = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf((String) dashArrivals.getSelectedItem()));
			lastArriveSelection = (String) dashArrivals.getSelectedItem();
			MainActivity.edit.putString("lastDashDepart", lastDepartSelection);
			MainActivity.edit.putString("lastDashArrive", lastArriveSelection);
			MainActivity.edit.commit();
			
			try{
				//get the routes between departure and arrival  locations based on schedule type and display
				ArrayList<DashTripDetails> times = getDashTripsForInputStops(departure, arrival);
				time.setToNow();
				
				currentTime = formatCurrentTime(time);
				int index = getTripIndexClosestToCurrentTime(times, currentTime);
				if(index == times.size())
				{	
					index = 0;
				}
				DashScheduleAdapter scheduleAdapter = new DashScheduleAdapter(this, R.layout.dash_row, times, index);
				dashTrips.setAdapter(scheduleAdapter);
				dashTrips.setSelection(index);
				if(departure.equals(arrival))
				{
					dashTrips.setVisibility(View.GONE);
					scheduleError.setVisibility(View.VISIBLE);
					scheduleError.setText("Same departure and arrival stops." + "\n" + "\n" + "Please select another stop.");
					fareDisplay.setVisibility(View.INVISIBLE);
					
				}
				else if(getDashTripsForInputStops(departure, arrival).size() == 0)
				{
					scheduleError.setVisibility(View.VISIBLE);
					fareDisplay.setVisibility(View.INVISIBLE);
					dashTrips.setVisibility(View.GONE);
				}
				else
				{
					dashTrips.setVisibility(View.VISIBLE);
					scheduleError.setVisibility(View.GONE);
					fareDisplay.setVisibility(View.VISIBLE);
					Toast.makeText(this, NEXT_TRIP_MESSAGE, Toast.LENGTH_SHORT).show();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private int getIndex(Spinner spinner, String selection)
	{
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++)
        {
            if (spinner.getItemAtPosition(i).equals(selection))
            {
                index = i;
            }
        }
        return index;
	}
	
	/**
	 *  Method must be overridden since it is implemented, but no action will be taken in this case
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	public void onNothingSelected(AdapterView<?> parent)
	{
		
	}
	
	private ArrayList<DashTripDetails> getDashTripsForInputStops(String departingStopID, String arrivingStopID) throws IOException
	{
		ArrayList<DashTripDetails> trips = new ArrayList<DashTripDetails>();
		
		ArrayList<String> departingTimes = new ArrayList<String>();
		ArrayList<String> arrivingTimes = new ArrayList<String>();
		
		String sjStationID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf("San Jose Caltrain Station"));
		String sf1stID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf("San Fernando & 1st"));
		String sf4thID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf("4th & San Fernando"));
		String ccID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf("Convention Center Lrt Station"));
		
		String duration ="";
		
		if(departingStopID == ccID && (arrivingStopID == sf1stID || arrivingStopID == sf4thID ))
		{
			scheduleError.setVisibility(View.VISIBLE);
			scheduleError.setText("Since DASH travels in a circular route, " +
					"this trip cannot be made without a time lapse at the San Jose Diridon station." + "\n" + "\n" +
					"Please select another combination of stops.");
			
			dashTrips.setVisibility(View.GONE);
			fareDisplay.setVisibility(View.INVISIBLE);
		}
		
		else if (departingStopID == sf4thID && arrivingStopID == sf1stID)
		{
			scheduleError.setText("Since DASH travels in a circular route, " +
					"this trip cannot be made without a time lapse at the San Jose Diridon station." + "\n" + "\n" +
					"Please select another combination of stops.");
			scheduleError.setVisibility(View.VISIBLE);
			dashTrips.setVisibility(View.GONE);
			fareDisplay.setVisibility(View.INVISIBLE);
		}
		else
		{
			InputStream input = getResources().openRawResource(R.raw.dstoptimes);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			
			try
			{
				line = reader.readLine();
				if(departingStopID == sjStationID && (arrivingStopID == sf1stID || arrivingStopID == sf4thID))
				{
					while((line = reader.readLine()) != null)
					{
						line = line.replace("\"", "");
						String[] parts = line.split(",");
						
						if(MainActivity.eastTripIDs.contains(parts[0]) && (parts[3].equals(departingStopID)))
						{
							departingTimes.add(parts[1].substring(0, 5));
						}
						else if(MainActivity.eastTripIDs.contains(parts[0]) && (parts[3].equals(arrivingStopID)))
						{
							arrivingTimes.add(parts[1].substring(0, 5));
						}
					}
				}
				else if(departingStopID == sf1stID && arrivingStopID == sf4thID)
				{
					while((line = reader.readLine()) != null)
					{
						line = line.replace("\"", "");
						String[] parts = line.split(",");
						
						if(MainActivity.eastTripIDs.contains(parts[0]) && (parts[3].equals(departingStopID)))
						{
							departingTimes.add(parts[1].substring(0, 5));
						}
						else if(MainActivity.eastTripIDs.contains(parts[0]) && (parts[3].equals(arrivingStopID)))
						{
							arrivingTimes.add(parts[1].substring(0, 5));
						}
						
					}
				}
				else if(departingStopID == sf4thID && (arrivingStopID == ccID || arrivingStopID == sjStationID) )
				{
					while((line = reader.readLine()) != null)
					{
						line = line.replace("\"", "");
						String[] parts = line.split(",");
						
						if(MainActivity.westTripIDs.contains(parts[0]) && (parts[3].equals(departingStopID)))
						{
							departingTimes.add(parts[1].substring(0, 5));
						}
						else if(MainActivity.westTripIDs.contains(parts[0]) && (parts[3].equals(arrivingStopID)))
						{
							arrivingTimes.add(parts[1].substring(0, 5));
						}
					}
				}
				else if(departingStopID == ccID && (arrivingStopID == sjStationID) )
				{
					while((line = reader.readLine()) != null)
					{
						line = line.replace("\"", "");
						String[] parts = line.split(",");
						
						if(MainActivity.westTripIDs.contains(parts[0]) && (parts[3].equals(departingStopID)))
						{
							departingTimes.add(parts[1].substring(0, 5));
						}
						else if(MainActivity.westTripIDs.contains(parts[0]) && (parts[3].equals(arrivingStopID)))
						{
							arrivingTimes.add(parts[1].substring(0, 5));
						}
					}
				}
				else if(departingStopID == sjStationID && arrivingStopID == ccID)
				{
					while((line = reader.readLine()) != null)
					{
						line = line.replace("\"", "");
						String[] parts = line.split(",");
						
						if(MainActivity.eastTripIDs.contains(parts[0]) && (parts[3].equals(departingStopID)))
						{
							departingTimes.add(parts[1].substring(0, 5));
						}
						else if(MainActivity.westTripIDs.contains(parts[0]) && (parts[3].equals(arrivingStopID)))
						{
							arrivingTimes.add(parts[1].substring(0, 5));
						}
					}
				}
				else if(departingStopID == sf1stID && (arrivingStopID == ccID || arrivingStopID == sjStationID))
				{
					while((line = reader.readLine()) != null)
					{
						line = line.replace("\"", "");
						String[] parts = line.split(",");
						
						if(MainActivity.eastTripIDs.contains(parts[0]) && (parts[3].equals(departingStopID)))
						{
							departingTimes.add(parts[1].substring(0, 5));
						}
						else if(MainActivity.westTripIDs.contains(parts[0]) && (parts[3].equals(arrivingStopID)))
						{
							arrivingTimes.add(parts[1].substring(0, 5));
						}
					}
				}
				
				//sort times from earliest to latest
				Collections.sort(departingTimes);
				Collections.sort(arrivingTimes);
				
				for(int i = 0; i < departingTimes.size(); i++)
				{
					String from = departingTimes.get(i);
					String to = arrivingTimes.get(i);
					String completeTime = formatRouteTimes(from, to);
					duration = calculateTripDuration(from, to);
					trips.add(new DashTripDetails("Trip " + (i + 1), from, to, completeTime, duration));
				}
				
				trips.add(new DashTripDetails(" ", " ", " ", " ", " "));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				reader.close();
			}
			
		}
		return trips;
	}
	
	/**
	 * Method to join depart and arrive times to show "from time to time"
	 * @param start the "depart" time
	 * @param finish the "arrival" time
	 * @return a string of the format XX:XX AM/PM - XX:XX AM/PM
	 */
	private String formatRouteTimes(String start, String finish)
	{
		return changeHour(start) + " - " + changeHour(finish);
	}
	
	/**
	 * Pre-condition: the input string (time) should be in the format XX:XX
	 * Method to change the hour from 24-hour format to 12-hour format
	 * @param time the given time in 24-hour format (in XX:XX) form
	 * @return the time formatted in 12-hour time, as XX:XX AM/PM
	 */
	private String changeHour(String time)
	{	
		int changedHour = 0;
		String hourType = "";
		String hours = time.substring(0, 2);
		int hour = Integer.parseInt(hours);
		String rest = time.substring(2);
		
			if(hour < 12)
			{
				hourType = " AM";
				changedHour = hour;
			}
			else if(hour == 12)
			{
				hourType = " PM";
				changedHour = hour;
			}
			else if(hour > 12)
			{
				changedHour = hour - 12;
				if(changedHour < 12)
				{
					hourType =" PM";
				}
				else if (changedHour == 12)
				{
					hourType = " AM";
				}
				else if (changedHour > 12)
				{
					hourType=" AM";
					changedHour = changedHour % 12;
				}
			}
		
		return changedHour + rest + hourType;
	}
	
	/**
	 * Method to calculate the trip duration (length of time) between depart and arrival times.
	 * @param start the depart time
	 * @param finish the arrival time
	 * @return a string representation of the elapsed time in the format Xhrs Ymins
	 */
	private String calculateTripDuration(String start, String finish)
	{
		String[] startTime = start.split(":");
		String[] finishTime = finish.split(":");
		String time = "";
		if(startTime.length > 1 && finishTime.length > 1)
		{
			int startTimeInMin = (Integer.parseInt(startTime[0]) * 60) + Integer.parseInt(startTime[1]);
			int finishTimeInMin = (Integer.parseInt(finishTime[0]) * 60) + Integer.parseInt(finishTime[1]);
			
			int duration = finishTimeInMin - startTimeInMin;
			int hours = duration / 60;
			int minutes = duration % 60;
			if(hours == 0)
			{
				time = minutes + "mins";
			}
			else
			{
				time = hours + "hr " + minutes + "mins";
			}
		}
		return time;
	}
	
	
	/*private void loadDashTrips() throws IOException
	{
		
		InputStream input = getResources().openRawResource(R.raw.dtrips);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		String line;
		
		try
		{
			line = reader.readLine();
			while((line = reader.readLine()) != null)
			{
				
				
				line = line.replace("\"", "");
				String [] parts = line.split(",");
				if(parts[0].equals(DASH_ROUTE_ID))
				{
					if(parts[3].contains("EB"))
					{
						eastTripIDs.add(parts[2]);
					}
					else if(parts[3].contains("WB"))
					{
						westTripIDs.add(parts[2]);
					}
				
				}
				
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			reader.close();
		}
		eastTripIDs.trimToSize();
		westTripIDs.trimToSize();
		
		
	}*/
	/*
	public void loadDashStopIDs() throws IOException
	{
		InputStream input = getResources().openRawResource(R.raw.dstoptimes);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				
		String line;
		
		try
		{
			line = reader.readLine();
			
			while((line=reader.readLine()) != null)
			{
				line = line.replace("\"", "");
				String[] parts = line.split(",");
				if((eastTripIDs.contains(parts[0]) || westTripIDs.contains(parts[0])))
				{
					if(!stopIDs.contains(parts[3]))
					{
						stopIDs.add(parts[3]);
					}
				}
				
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			reader.close();
		}
		
	}*/
	
	/*
	private void loadDashStopNames() throws IOException
	{
		InputStream input = getResources().openRawResource(R.raw.dstops);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		String line;
		
		int index = 0;
		try
		{
			line = reader.readLine();
			while((line=reader.readLine()) != null)
			{
				line = line.replace("\"", "");
				String[] parts = line.split(",");
				
				if(stopIDs.contains(parts[0]) && index < stopIDs.size())
				{	
					int i = stopIDs.indexOf(parts[0]);
					String temp = stopIDs.get(index);
					
					stopIDs.set(index, parts[0]);
					stopIDs.set(i, temp);
					stopNames.add(parts[1]);
					stopLatitudes.add(Float.parseFloat(parts[3]));
					stopLongitudes.add(Float.parseFloat(parts[4]));
					index++;
				}
				
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			reader.close();
		}
		
	}*/
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() 
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_dash, menu);
		int [] array = {R.id.action_info, R.id.action_reverse_stations};
        navDrawerActivityConfiguration.setActionMenuItemsToHideWhenDrawerOpen(array);
		return true;
	}

	@Override
	/**
	 * Perform actions on click of Action Bar icons
	 * @param item the icon that is clicked
	 */
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.action_info:
				showAppInfo();
				return true;
			case R.id.action_reverse_stations:
				reverseRoutes();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Method to reverse the schedule by switching the depart and arrive locations.
	 */
	private void reverseRoutes()
	{
		int departIndex = dashDepartures.getSelectedItemPosition();
		int arriveIndex = dashArrivals.getSelectedItemPosition();
		dashArrivals.setSelection(departIndex, true);
		dashDepartures.setSelection(arriveIndex, true);
	}
	
	/**
	 * Method to show a pop-up window containing information about the application.
	 */
	private void showAppInfo()
	{
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		int width = size.x;
		final PopupWindow screen = new PopupWindow(width, height);
		LinearLayout group = (LinearLayout) findViewById(R.id.about_app_layout);
		LayoutInflater layoutInflate = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View otherLayout = layoutInflate.inflate(R.layout.app_info_popup, group);
		screen.setContentView(otherLayout);
		screen.setFocusable(true);//doesn't allow other functionality until the close button is clicked
		screen.showAtLocation(otherLayout, Gravity.CENTER, 0, 0);
				
		//close the window when "Close" button is clicked
		Button close = (Button) otherLayout.findViewById(R.id.about_info_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				screen.dismiss();
			}
		});
		
		//launch the design contributor's website activity when url is clicked
		final TextView link = (TextView) otherLayout.findViewById(R.id.design_contributor);
		link.setTextColor(getResources().getColor(R.color.white));
		final Intent otherIntent = new Intent(this, DesignContributorActivity.class);
		link.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				link.setTextColor(getResources().getColor(R.color.main_blue));
		    	startActivity(otherIntent);
			}
		});
	}
	
	/**
	 * Handle click event for Dash Route Map button
	 * @param view the view that initiates the event
	 */
	public void viewRouteMap(View view)
	{
		Intent intent = new Intent(this, DashRouteMapActivity.class );
		startActivity(intent);
	}
	
	/**
	 * Handle click event for Dash Nearest Station button
	 * @param view the view that initiates the event
	 */
	public void findNearestStop(View view)
	{
		Intent intent = new Intent(this, FindNearestStopActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Class to create adapter for DASH schedule list view
	 * @author Clark Stonehocker
	 *
	 */
	private class DashScheduleAdapter extends ArrayAdapter<DashTripDetails> 
	{
        private ArrayList<DashTripDetails> items;
        private int indexTrack; //used to store which item is next available according to current time

        public DashScheduleAdapter(Activity act, int textViewResourceId, ArrayList<DashTripDetails> items, int index) 
        {
        	super(act, textViewResourceId, items);
            this.items = items;
            indexTrack = index;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            View v = convertView;
            if (v == null) 
            {
            	LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.dash_row, null);
            }
            DashTripDetails trip = items.get(position);
            if (trip != null) 
            {
             	TextView tripNumber = (TextView) v.findViewById(R.id.trip_number);
                TextView time = (TextView) v.findViewById(R.id.dash_trip);
                TextView duration = (TextView) v.findViewById(R.id.dash_trip_duration);
                if (tripNumber != null) 
                {
                  	tripNumber.setText(trip.getTripNumber());                            
                }
                if(time != null)
                {
                	time.setText(trip.getTripTime());
                }
                if(duration != null)
                {
                  	duration.setText(trip.getTripDuration());
                }
                //highlight the next available trip's background based on current device time
                if(position == indexTrack)
                {
                  	v.setBackgroundColor(getResources().getColor(R.color.main_blue));
                }
                else
                {
                	v.setBackgroundColor(getResources().getColor(R.color.none));
                }      
            }
            return v;
        }
	}
}
