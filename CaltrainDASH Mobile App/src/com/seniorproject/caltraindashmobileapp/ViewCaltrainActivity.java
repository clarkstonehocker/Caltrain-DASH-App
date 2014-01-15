package com.seniorproject.caltraindashmobileapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.seniorproject.caltraindashmobileapp.R;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.text.format.Time;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * An activity for viewing the Caltrain schedule. This class inherits from Activity and implements OnItemSelectedListener(for 
 * spinner events)
 * @author Clark Stonehocker
 */
public class ViewCaltrainActivity extends AbstractNavigationDrawerActivity implements OnItemSelectedListener
{
	//declare UI elements 
	private Spinner depart;
	private Spinner arrive;
	private Spinner scheduleDays;
	private TextView fareView;
	private ListView tripschedule;
	private TextView schedule;
	private static String lastCaltrainDepartSelection;
	private static String lastCaltrainArriveSelection;
	
	private NavigationDrawerActivityConfiguration navDrawerActivityConfiguration = new NavigationDrawerActivityConfiguration();
	
	private Time time;
	private String currentTime;
	
	private static final String NEXT_TRIP_MESSAGE = "Next available trip highlighted.";
	
	@SuppressLint("NewAPI")
	@Override
	/**
	 * Create the activity and "root" events that happen 
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//create Spinner for schedule type (weekday vs weekend)
		scheduleDays = (Spinner) findViewById(R.id.schedType);
		ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(this, R.array.schedule_type, android.R.layout.simple_spinner_item);
		adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		scheduleDays.setAdapter(adapt);
		scheduleDays.setOnItemSelectedListener(this);
			
		//create Spinners for departure and arrival stations and attach listeners
		depart = (Spinner) findViewById(R.id.departure);
		depart.setOnItemSelectedListener(this);
		arrive = (Spinner) findViewById(R.id.arrival);
		arrive.setOnItemSelectedListener(this);
		
		ArrayAdapter<String> adapter;
		
		//create TextView for display of fare information
		fareView = (TextView) findViewById(R.id.tripfare);
		
		//create TextView for display of "error" messages
		schedule = (TextView) findViewById(R.id.caltrain_schedule_error);
		
		//create ListView for display of scheduled trains
		tripschedule = (ListView) findViewById(R.id.tripschedules);
				
		//custom adapter for scheduled trips
		CaltrainScheduleAdapter scheduleAdapter;
		
        //try . . . catch needed because getCaltrainRoutes method throws an IOException
		try{
			//use array adapter for depart and arrive spinners that uses the stationNames ArrayList as input
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item , MainActivity.stationNames);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			depart.setAdapter(adapter);
			arrive.setAdapter(adapter);
			
			//set "current" items of spinners to be last selected item
			lastCaltrainDepartSelection = MainActivity.savedValues.getString("lastCaltrainDepart", "");
			lastCaltrainArriveSelection = MainActivity.savedValues.getString("lastCaltrainArrive", "");
			String scheduleType = MainActivity.savedValues.getString("lastScheduleType", "");
			if(lastCaltrainDepartSelection != null)
			{
				depart.setSelection(getIndex(depart, lastCaltrainDepartSelection ));
			}
			if(lastCaltrainArriveSelection != null)
			{
				arrive.setSelection(getIndex(arrive, lastCaltrainArriveSelection));
			}
			if(scheduleType != null)
			{
				scheduleDays.setSelection(getIndex(scheduleDays, scheduleType));
			}
			//get the currently selected departure and arrival locations along with schedule type for initial schedule display
			String departure = (String) depart.getSelectedItem();
			String arrival = (String) arrive.getSelectedItem();
			String serviceType = (String) scheduleDays.getSelectedItem();
			
			ArrayList<CaltrainTripDetails> trips = getCaltrainRoutes(departure, arrival, serviceType);
			time = new Time(Time.getCurrentTimezone());
			time.setToNow();
			currentTime = formatCurrentTime(time);
			int index = getTripIndexClosestToCurrentTime(trips, currentTime);
			if(index == trips.size())
			{	
				index = 0;
			}
			scheduleAdapter = new CaltrainScheduleAdapter(this, R.layout.caltrain_row, trips, index);
			tripschedule.setAdapter(scheduleAdapter);
			tripschedule.setSelection(index);
			
			//show error message if no trips are returned
			if(getCaltrainRoutes(departure, arrival, serviceType).size() == 0)
			{
				schedule.setText(getResources().getText(R.string.same_depart_and_arrive_selections));
				schedule.append("\n");
				schedule.append(getResources().getText(R.string.change_location));
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
	
	/**
	 * Method to find next available trip according to current time.
	 * @param array an array of trips
	 * @param time the current device time
	 * @return the index of the trips array the holds the next available trip (the one that leaves the soonest after the current time)
	 */
	private int getTripIndexClosestToCurrentTime(ArrayList<CaltrainTripDetails> array, String time)
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
	/**
	 * Get the configuration details for the navigation drawer.  This is inherited method from AbstractNavigationDrawer Activity super class.
	 * @return NavigationDrawerActiviyConfiguration an instance of the navigation drawer configuration class
	 */
    protected NavigationDrawerActivityConfiguration getNavDrawerConfiguration() 
	{
        NavigationDrawerItem[] menu = new NavigationDrawerItem[] {
        	NavigationDrawerSection.create( 100, "Public Transportation"),
            NavigationDrawerMenuItem.create(101,"Home", R.drawable.home_icon, false, this),
            NavigationDrawerMenuItem.create(102, "Caltrain Schedule", R.drawable.caltrain_icon, true, this),
            NavigationDrawerMenuItem.create(103, "Dash Schedule", R.drawable.shuttle_icon, true, this),
            NavigationDrawerSection.create(200, "Twitter"),
            NavigationDrawerMenuItem.create(202, "@Caltrain_News", R.drawable.bird_black, false, this),
            NavigationDrawerMenuItem.create(203, "@GoCaltrain", R.drawable.bird_black, false, this), 
            NavigationDrawerMenuItem.create(204, "@VTA", R.drawable.bird_black, false, this)};
        
        navDrawerActivityConfiguration.setMainLayout(R.layout.activity_view_caltrain);
        navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
        navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);
        navDrawerActivityConfiguration.setNavItems(menu);
        navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);       
        navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
        navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.drawer_close);
        navDrawerActivityConfiguration.setBaseAdapter(new NavigationDrawerAdapter(this, R.layout.navigation_drawer_menu_item, menu ));
        
        return navDrawerActivityConfiguration;
    }
    
    @Override
    /**
     * Take action on selection of navigation drawer items.
     * @param id the id number of the selected drawer item.
     */
    protected void onNavItemSelected(int id) 
    {
    	//start different activities, corresponding to the activity id number
        switch (id) 
        {
        	case 101:
        	{    
        		Intent intent = new Intent(this, MainActivity.class);
        		startActivity(intent);
        		break;
        	}
        	case 103:
        	{
        		Intent intent = new Intent(this, ViewDashActivity.class);
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
		if(parent.equals(depart) || parent.equals(arrive) || parent.equals(scheduleDays))
		{
			//get currently selected values from spinners
			String departure = (String) depart.getSelectedItem();
			MainActivity.edit.putString("lastCaltrainDepart", departure);
			String arrival = (String) arrive.getSelectedItem();
			MainActivity.edit.putString("lastCaltrainArrive", arrival);
			String serviceType = (String) scheduleDays.getSelectedItem();
			MainActivity.edit.putString("lastCaltrainScheduleType", serviceType);
			MainActivity.edit.commit();
			try{
				//set the fare between the departure and arrival stops
				fareView.setText(departure + " to " + arrival + " = $" + getRouteCost(departure, arrival));
				
				//get the routes between departure and arrival  locations based on schedule type and display
				ArrayList<CaltrainTripDetails> trips = getCaltrainRoutes(departure, arrival, serviceType);
				
				time.setToNow();
				
				currentTime = formatCurrentTime(time);
				int index = getTripIndexClosestToCurrentTime(trips, currentTime);
				if(index == trips.size())
				{	
					index = 0;
				}
				CaltrainScheduleAdapter scheduleAdapter = new CaltrainScheduleAdapter(this, R.layout.caltrain_row, trips, index);		
				tripschedule.setAdapter(scheduleAdapter);
				tripschedule.setSelection(index);
				
				//set visibility of views depending on the number of trips found.
				if(departure.equals(arrival))
				{
					schedule.setText(getResources().getText(R.string.same_depart_and_arrive_selections));
					schedule.append("\n");
					schedule.append(getResources().getText(R.string.change_location));
					schedule.setVisibility(View.VISIBLE);
					tripschedule.setVisibility(View.GONE);
					fareView.setVisibility(View.INVISIBLE);
				}
				else if(getCaltrainRoutes(departure, arrival, serviceType).size() == 0)
				{	
					schedule.setText("No trains available." + "\n" + "\n" + "Please select another location.");
					schedule.setVisibility(View.VISIBLE);
					tripschedule.setVisibility(View.GONE);
					fareView.setVisibility(View.INVISIBLE);
				}
				else
				{
					schedule.setVisibility(View.GONE);
					tripschedule.setVisibility(View.VISIBLE);
					fareView.setVisibility(View.VISIBLE);
					Toast.makeText(this, NEXT_TRIP_MESSAGE, Toast.LENGTH_SHORT).show();
				}
				
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  Method must be overridden since it is implemented, but no action will be taken in this case
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	public void onNothingSelected(AdapterView<?> parent)
	{
		
	}
	
	/**
	 * Find the index of the spinner where a certain value resides.
	 * @param spinner the spinner object to search
	 * @param selection the value to search for in the spinner
	 * @return the index where the selection is found
	 */
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
	 * Get the route fare between departure and arrival stations.
	 * @param depart the departure station
	 * @param arrive the arrival station
	 * @return the route cost as a string value
	 */
	private String getRouteCost(String depart, String arrive) throws IOException
	{
		//Establish input stream and reader for reading from the file
		InputStream input = getResources().openRawResource(R.raw.ctfarerules);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		//declare needed variables
		boolean found = false;
		String line;
		String cost = "";
		
		//find zones of departure and arrival stations
		int departZone = MainActivity.stationZoneIDs.get(MainActivity.stationNames.indexOf(depart));
		int arriveZone = MainActivity.stationZoneIDs.get(MainActivity.stationNames.indexOf(arrive));
		
		//set cost of fare to 0 if depart and arrive stations are the same
		if(depart == arrive)
		{
			cost = "0.00";
		}
		else
		{
			try
			{
				//discard the first line of the file
				line = reader.readLine();
			
				//read until either end of file or desired combination of departure and arrival zones is found
				while(((line = reader.readLine()) != null) && found == false)
				{
					//remove quotation marks and split line on commas
					line = line.replace("\"", "");
					String [] parts = line.split(",");
					
					//parts[2] contains departing zone from file line and parts[3] contains arriving zone from file line.
					//If these equal the desired departure and arrival zones, set found to true (terminates loop) and
					//set cost to corresponding value from fareValue ArrayList
					if((Integer.parseInt(parts[2])) == departZone && (Integer.parseInt(parts[3]) == arriveZone))
					{
						found = true;
						cost = MainActivity.fareValue.get(MainActivity.fareID.indexOf(parts[0]));
					}
				}//end while
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
		return cost;
	}
	
	/**
	 * Get all scheduled trips between departure and arrival zones according to schedule type.
	 * @param depart the departure station
	 * @param arrive the arrival station
	 * @param type the schedule type (weekday or weekend)
	 * @return an ArrayList of type String containing all trips according to input parameters (one trip per index)
	 */
	private ArrayList<CaltrainTripDetails> getCaltrainRoutes(String depart, String arrive, String type) throws IOException
	{
		//create array for storing trips
		ArrayList<CaltrainTripDetails> trips = new ArrayList<CaltrainTripDetails>();
		ArrayList<Boolean> saturdayTrains = new ArrayList<Boolean>(); 
		String time = "";
		String train = "";
		String duration = "";
		String routeType = "";
		
		//if departure and arrival zones are the same, display "error" message, else find appropriate trips 
		if(depart.equals(arrive))
		{
			schedule.setText("No trains available." + "\n" + "Please select another location.");
		}
		else
		{
			//Configure InputStream and reader for accessing file
			InputStream input = getResources().openRawResource(R.raw.ctstoptimes);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
			//compare index values of departure and arrival stations in stationNames ArraryList
			//stations with a greater index are south of stations with a lower index.
			//direction = 1 indicates north-bound, direction = 0 indicates south-bound
			int direction;
			if(MainActivity.stationNames.indexOf(depart) > MainActivity.stationNames.indexOf(arrive))
			{
				direction = 0;
			}
			else
			{
				direction = 1;
			}
						
			//collect list of trips we care about, based on schedule type and train direction
			ArrayList<String> tripIDs = getTripsPerScheduleType(type, direction);
			
			//declare variables for reading from file and storing schedule information
			String line, from, to;
			from = to = "";
			
			//keep track of an index
			int index = 0;
			
			//used for determining whether or not both stops in a trip have been found
			boolean departureFound = false;
			boolean arrivalFound = false;
			
			try
			{
				//disregard the first line
				line = reader.readLine();
								
				//read until the end of the file
				while((line = reader.readLine()) != null && index < tripIDs.size())
				{
					line = line.replace("\"", "");
					String[] parts = line.split(",");
					
					//only perform action on lines we care about; i.e. those whose tripID is found in 
					//the list of tripIDs we care about (specified above)
					if(tripIDs.contains(parts[0]))
					{	
						//check one particular tripID at a time, based on value in tracked index of tripID ArrayList
						if(parts[0].equals(tripIDs.get(index)))
						{
							if(parts[3].substring(0, parts[3].length() - 8).equals(depart))
							{
								//set departure found to true and only grab hours and minutes of time
								departureFound = true;
								from = parts[1].substring(0, 5);
							}
							else if(parts[3].substring(0, parts[3].length() - 8).equals(arrive))
							{
								//set arrival found to true and only grab hours and minutes of time
								arrivalFound = true;
								to = parts[1].substring(0, 5);
							}
					
							//If both departure and arrival stations are found, add times(formatted) 
							//and train numbers to respective ArrayLists
							if(departureFound && arrivalFound)
							{
								time = from + " - " + to;
								duration = calculateTripDuration(from, to);
								train = "#" + MainActivity.trainNumbers.get(MainActivity.tripID.indexOf(parts[0]));
								
								if(MainActivity.routeID.get(MainActivity.tripID.indexOf(parts[0])).contains("limited"))
								{
									routeType = "Limited";
								}
								else if(MainActivity.routeID.get(MainActivity.tripID.indexOf(parts[0])).contains("bullet"))
								{
									routeType = "Bullet";
								}
								else
								{
									routeType = "";
								}
								//store saturday trains in a separate location
								if(MainActivity.serviceID.get(MainActivity.tripID.indexOf(parts[0])).contains("ST"))
								{
									saturdayTrains.add(true);
								}
								else
								{
									saturdayTrains.add(false);
								}
								departureFound = false;
								arrivalFound = false;
								CaltrainTripDetails trip = new CaltrainTripDetails(train, from, to, time, duration, routeType);
								trips.add(trip);
							}
						}
						//if the trip id in the current index of the tripsID array is not included in the line, 
						//increment the index, then check if that line's tripID is in the desired list, and if so, 
						//check if departure and arrival stops are part of that line.
						else
						{
							index++;
							departureFound = false;
							arrivalFound = false;
							if(parts[0].equals(tripIDs.get(index)))
							{
								if(parts[3].substring(0, parts[3].length() - 8).equals(depart))
								{
									//increment found (number of stations found) and only grab hours and minutes of time
									departureFound = true;
									from = parts[1].substring(0, 5);
								}
								else if(parts[3].substring(0, parts[3].length() - 8).equals(arrive)) //check if portion of line contains arrival station
								{
									//increment found (number of stations found) and only grab hours and minutes of time
									arrivalFound = true;
									to = parts[1].substring(0, 5);
								}
							}
						}//end else
					}
				}//end outer while loop
				
				//if no routes are found, display "error" message 
				if(trips.size() == 0)
				{
					schedule.setText("No trains available." + "\n" + "Please select another location.");
				}
				else
				{
					//sort the trips array by departure time and trains array to match
					int i, j;
					String sindex;
					boolean sindex2;
					CaltrainTripDetails t;
					
					for(i = 1; i < trips.size(); i++)
					{
						t = trips.get(i);
						sindex = trips.get(i).getFormattedTime();
						sindex2 = saturdayTrains.get(i);
						j = i;
						while((j > 0) && ((trips.get(j - 1).getFormattedTime().compareTo(sindex) > 0)))
						{
							trips.set(j, trips.get(j - 1));
							saturdayTrains.set(j, saturdayTrains.get(j - 1));
							j = j - 1;
						}
						saturdayTrains.set(j, sindex2);
						trips.set(j, t);
					}
					for(int k = 0; k < trips.size(); k++)
					{
						String formatTime = formatRouteTimes(trips.get(k).getDepartTime(), trips.get(k).getArriveTime());
						trips.get(k).setFormattedTime(formatTime);
					}
					/*
					//format final time value(s) and train number(s)
					for(int k = 0; k < times.size(); k++)
					{
						String formattedTime = formatRouteTimes(trips.get(k).getTimes());
						String trainNum = "#" + trainNumbers.get(k);
						String note = "";
						if(saturdayTrains.get(k))
						{
							note = "Saturday only";
						}
						else
						{
							note = "";
						}
						String formattedLine = "";
						if(note.equals(""))
						{
							formattedLine = String.format("%18s %8s", formattedTime, trainNum);
						}	
						else
						{
							formattedLine = String.format("%25s %n %18s %8s", note, formattedTime, trainNum);
						}
						
						times.set(k, formattedLine);
					}
					*/
					
					CaltrainTripDetails trip = new CaltrainTripDetails(" ", " ", " ", " ", " ", " ");
					trips.add(trip);
					saturdayTrains.add(false);
				}
			}//end try
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				reader.close();
			}
		}//end else
				
		return trips;
	}
	
	/**
	 * Precondition: an ArrayList of all possible tripIDs is maintained in class MainActivity
	 * Get a subset of all possible trips, filtered by type(weekend or weekday) and direction(north-bound or south-bound)
	 * @param days the type of schedule, whether weekday or weekend
	 * @param direction the direction, 0 for south-bound and 1 for north-bound
	 * @return a list of the desired(filtered) tripIDs 
	 */
	private ArrayList<String> getTripsPerScheduleType(String days, int direction)
	{	
		ArrayList<String> temp = new ArrayList<String>();
		
		for(int i = 0; i < MainActivity.tripID.size(); i++)
		{	
			if(days.equals("Weekday"))
			{
				if(MainActivity.serviceID.get(i).contains("WD") && MainActivity.directionID.get(i) == direction)
				{
					temp.add(MainActivity.tripID.get(i));
				}
			}
			else if(days.equals("Weekend"))
			{
				if((MainActivity.serviceID.get(i).contains("WE") || MainActivity.serviceID.get(i).contains("ST")) && MainActivity.directionID.get(i) == direction)
				{
					temp.add(MainActivity.tripID.get(i));
				}
			}
		}
		return temp;
	}
	
	/**
	 * Method to format the route times
	 * @param start the beginning (depart) time of the trip
	 * @param finish the end (arrive) time of the trip
	 * @return a String of the trip time in the form XX:XX AM/PM - XX:XX AM/PM
	 */
	private String formatRouteTimes(String start, String finish)
	{
		return changeHour(start) + " - " + changeHour(finish);
	}
	
	/**
	 * Passed time value (really a String) must be in the form 00:00
	 * Change time from purely numeric 24-hour time values to 12-hour values with AM/PM indicator.
	 * @param time the time to be changed (formatted)
	 * @return the time value (really a String) in the format (0)0:00 AM/PM
	 */
	private String changeHour(String time)
	{
		int changedHour = 0;
		String hourType = "";
		String rest = "";
		if(time.length() >= 5)
		{
			//only change hours, so extract first two characters of string and convert to int
			String hours = time.substring(0, 2);
			int hour = Integer.parseInt(hours);
			rest = time.substring(2);
		
			//change to 12-hour from 24-hour time and add AM or PM to differentiate
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
		}
		return changedHour + rest + hourType;
	}
	
	/**
	 * Calculate the duration of the trip in minutes and/or hours
	 * @param start the start time of the trip
	 * @param finish the end time of the trip
	 * @return the total travel time as a String value
	 */
	private String calculateTripDuration(String start, String finish)
	{
		String[] startTime = start.split(":");
		String[] finishTime = finish.split(":");
		String time = "";
		
		//only perform function if arrays contain valid elements (at least two elements)
		if(startTime.length > 1 && finishTime.length > 1)
		{
			//convert start and end time to minutes
			int startTimeInMin = (Integer.parseInt(startTime[0]) * 60) + Integer.parseInt(startTime[1]);
			int finishTimeInMin = (Integer.parseInt(finishTime[0]) * 60) + Integer.parseInt(finishTime[1]);
			
			//calculate duration and convert from purely minutes to hour(s) and minute(s)
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
		getMenuInflater().inflate(R.menu.view_caltrain, menu);
		int [] array = {R.id.action_info, R.id.action_reverse_stations};
        navDrawerActivityConfiguration.setActionMenuItemsToHideWhenDrawerOpen(array);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
	 * Method to reverse the schedule display by swapping the depart and arrive locations.
	 */
	private void reverseRoutes()
	{
		int departIndex = depart.getSelectedItemPosition();
		int arriveIndex = arrive.getSelectedItemPosition();
		arrive.setSelection(departIndex, true);
		depart.setSelection(arriveIndex, true);
	}
	
	/**
	 * Method to create and show a pop-up window containing info about the app.
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
		screen.setFocusable(false);
		screen.showAtLocation(otherLayout, Gravity.CENTER, 0, 0);
		
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
		
		Button close = (Button) otherLayout.findViewById(R.id.about_info_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				screen.dismiss();
			}
		});
	}
	/**
	 * Handles click event of Route Map button
	 * @param view the view that initiates the event
	 */
	public void viewRouteMap(View view)
	{
		//start a new activity called CaltrainRouteMap
		Intent intent = new Intent(this, CaltrainRouteMap.class);
		startActivity(intent);
	}
	
	/**
	 * Handle click event of the Find Nearest Station button
	 * @param view the view that initiates the event
	 */
	public void findNearestStation(View view)
	{
		//start a new activity called FindNearestStation
		Intent intent = new Intent(this, FindNearestStationActivity.class);
		startActivity(intent);
	}
	
	/**
	 * A class for a custom ListView adapter containing Caltrain trip information.
	 * @author Clark Stonehocker
	 */
	private class CaltrainScheduleAdapter extends ArrayAdapter<CaltrainTripDetails> 
	{
		//maintain a list of trips
        private ArrayList<CaltrainTripDetails> items;
        private int indexTrack;
        
        public CaltrainScheduleAdapter(Activity act, int textViewResourceId, ArrayList<CaltrainTripDetails> items, int index) 
        {
                super(act, textViewResourceId, items);
                this.items = items;
                indexTrack = index;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
        	View v = convertView;
        	//if the view doesn't exist (is null) create a new row using caltrain_row layout XML file
            if (v == null) 
            {
            	LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.caltrain_row, null);
            }
            //get the appropriate trip and populate elements of view(ListView row) with trip values
            CaltrainTripDetails trip = items.get(position);
            if (trip != null) 
            {
            	//Each row of ListView contains train number, trip time, and trip duration
            	TextView trainNumber = (TextView) v.findViewById(R.id.train_number);
                TextView time = (TextView) v.findViewById(R.id.caltrain_trip);
                TextView duration = (TextView) v.findViewById(R.id.caltrain_trip_duration);
                
                //set color of train number text based on train type, white for Local, yellow for Limited, and red for Bullet
                //colors are according to Caltrain convention
                if (trainNumber != null) 
                {
                 	if(trip.getService().equalsIgnoreCase("Limited"))
                   	{
                 		trainNumber.setTextColor(getResources().getColor(R.color.caltrain_yellow));
                    }
                    else if(trip.getService().equalsIgnoreCase("Bullet"))
                    {
                    	trainNumber.setTextColor(getResources().getColor(R.color.caltrain_red));
                    }
                    else
                    {
                    	trainNumber.setTextColor(getResources().getColor(R.color.white));
                   	}
                 	trainNumber.setText(trip.getTrainNumber());                            
                }
                if(time != null)
                {
                	time.setText(trip.getFormattedTime());
                }
                if(duration != null)
                {
                	duration.setText(trip.getDuration());
                }
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
	}//end class CaltrainScheduleAdapter
	
}//end class View CaltrainActivity