package com.seniorproject.caltraindashmobileapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import com.seniorproject.caltraindashmobileapp.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

/**
 * Activity for finding route combos of Caltrain and Dash
 * @author Clark Stonehocker
 *
 */
public class FindRouteCombosActivity extends Activity 
{
	//variables for holding and displaying route combos details
	private TextView caltrainHeader;
	private TextView dashHeader;
	private TextView caltrainHeader1;
	private TextView dashHeader1;
	private TextView caltrainHeader2;
	private TextView dashHeader2;
	
	private TextView ctrain1;
	private TextView ctime1;
	private TextView cdur1;
	private TextView dtrip1;
	private TextView dtime1;
	private TextView ddur1;
	private TextView totalTime1;
	
	private TextView ctrain2;
	private TextView ctime2;
	private TextView cdur2;
	private TextView dtrip2;
	private TextView dtime2;
	private TextView ddur2;
	private TextView totalTime2;
	
	private TextView ctrain3;
	private TextView ctime3;
	private TextView cdur3;
	private TextView dtrip3;
	private TextView dtime3;
	private TextView ddur3;
	private TextView totalTime3;
	
	private TextView txt;
	
	private TextView to;
	private TextView from;

	@Override
	/**
	 * Create the activity and necessary components
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_route_combos);
		
		//initialize view components
		ctrain1 = (TextView) findViewById(R.id.train_number1);
		ctime1 = (TextView) findViewById(R.id.caltrain_trip1);
		cdur1 = (TextView) findViewById(R.id.caltrain_trip_duration1);
		dtrip1 = (TextView) findViewById(R.id.trip_number1);
		dtime1 = (TextView) findViewById(R.id.dash_trip1);
		ddur1 = (TextView) findViewById(R.id.dash_trip_duration1);
		totalTime1 = (TextView) findViewById(R.id.total_time1);
		
		ctrain2 = (TextView) findViewById(R.id.train_number2);
		ctime2 = (TextView) findViewById(R.id.caltrain_trip2);
		cdur2 = (TextView) findViewById(R.id.caltrain_trip_duration2);
		dtrip2 = (TextView) findViewById(R.id.trip_number2);
		dtime2 = (TextView) findViewById(R.id.dash_trip2);
		ddur2 = (TextView) findViewById(R.id.dash_trip_duration2);
		totalTime2 = (TextView) findViewById(R.id.total_time2);
		
		ctrain3 = (TextView) findViewById(R.id.train_number3);
		ctime3 = (TextView) findViewById(R.id.caltrain_trip3);
		cdur3 = (TextView) findViewById(R.id.caltrain_trip_duration3);
		dtrip3 = (TextView) findViewById(R.id.trip_number3);
		dtime3 = (TextView) findViewById(R.id.dash_trip3);
		ddur3 = (TextView) findViewById(R.id.dash_trip_duration3);
		totalTime3 = (TextView) findViewById(R.id.total_time3);
		
		to = (TextView) findViewById(R.id.rc_header_to);
		from = (TextView) findViewById(R.id.rc_header_from);
		//get route combo details from Main Activity
		String departLocation = MainActivity.lastCombosDepartSelection;
		String arriveLocation = MainActivity.lastCombosArriveSelection;
		int arriveHours = MainActivity.routeCombosCurrentHour;
		int arriveMinutes = MainActivity.routeCombosCurrentMinute;
		
		String arriveTime = formatArriveTime(arriveHours, arriveMinutes);
		
		//determine how to get route combos depending on the direction of travel.  
		if(MainActivity.stationNames.contains(departLocation))
		{
			/*formulate route combos with a Caltrain departure and dash arrival.  San Jose is the connection point and is 
			* therefore hard coded for the Caltrain arrival and Dash departure.
			*/
			getTopThreeRouteCombosCaltrain(departLocation, "San Jose ", "San Jose Caltrain Station", arriveLocation, arriveTime );
		}
		else if(MainActivity.stopNames.contains(departLocation))
		{
			/*
			 * formulate route combos with a Dash departure and Caltrain arrival.  San Jose is the connection point and
			 * is therefore hard-coded for the Dash arrival and Caltrain departure.
			 */
			getTopThreeRouteCombosDash("San Jose ", arriveLocation, departLocation, "San Jose Caltrain Station", arriveTime);
		}
		
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	/**
	 * Format the input arrival time from Main Activity.
	 * @param arriveHours the arrive time hours
	 * @param arriveMinutes the arrive time minutes
	 * @return a String of the arrive time in the form XX:XX
	 */
	private String formatArriveTime(int arriveHours, int arriveMinutes)
	{
		String arriveTime = "";
		String minutes = "";
		
		//add a leading 0 for arrive hours less than 10 so it can be used for comparison against times from files,
		//which are stored in the form XX:XX
		if(arriveMinutes < 10)
		{
			minutes = "0" + arriveMinutes;
		}
		else
		{
			minutes += arriveMinutes;
		}
		if(arriveHours < 10)
		{
			arriveTime = "0" + arriveHours + ":" + minutes;
		}
		else if(arriveHours == 0)
		{
			//midnight is 0 in time picker, so convert to 24 for usable time
			arriveTime = 24 + ":" + minutes;
		}
		else 
		{
			//make time be in form XX:XX
			arriveTime = arriveHours + ":" + minutes;
		}
		return arriveTime;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() 
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_route_combos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Get the top three (could be less) Caltrain/Dash route combinations with a Caltrain departure location 
	 * (implying Dash stop and arrival time)
	 * @param cdepart the Caltrain departure station input by the user
	 * @param carrive Caltrain arrival = 'hard coded' to be San Jose
	 * @param ddepart Dash departure = 'hard coded' to be San Jose
	 * @param darrive Dash arrival stop as input by user
	 * @param time Arrival time for Dash arrival stop as input by user
	 */
	private void getTopThreeRouteCombosCaltrain(String cdepart, String carrive, String ddepart, String darrive, String time)
	{
		try
		{
			//get all Dash trips that arrive at dash arrival stop previous to requested arrival time.
			ArrayList<DashTripDetails> dashTrips = getTopThreeDash(ddepart, darrive, time);
			//since Caltrain runs much less often than Dash, only get 3 Caltrain trips previous to latest Dash trip
			ArrayList<CaltrainTripDetails> caltrainTrips = new ArrayList<CaltrainTripDetails>();
			txt = (TextView) findViewById(R.id.no_combos_available);
			if(dashTrips.size() == 0)
			{
				txt.setVisibility(View.VISIBLE);
				LinearLayout combo1 = (LinearLayout) findViewById(R.id.combo1);
				LinearLayout combo2 = (LinearLayout) findViewById(R.id.combo2);
				LinearLayout combo3 = (LinearLayout) findViewById(R.id.combo3);
				LinearLayout header = (LinearLayout) findViewById(R.id.rc_header);
				header.setVisibility(View.GONE);
				combo1.setVisibility(View.GONE);
				combo2.setVisibility(View.GONE);
				combo3.setVisibility(View.GONE);
			}
			//only formulate route combos if there were dash trips found
			if(dashTrips.size() > 0)
			{
				txt.setVisibility(View.GONE);
				String t1 = dashTrips.get(0).getDepartTime();
				caltrainTrips = getTopThreeCaltrain(cdepart, carrive, "Weekday", t1);
				
				from.setText(cdepart);
				from.setTextColor(getResources().getColor(R.color.caltrain_red));
				to.setText(darrive);
				to.setTextColor(getResources().getColor(R.color.main_blue));
			}
			
			//formulate 1 route combo if 1 of each trip exists
			if(dashTrips.size() > 0 && caltrainTrips.size() > 0)
			{
				//find Dash trip immediately following Caltrain trip
				int index = findSoonestDashAfterCaltrain(dashTrips, caltrainTrips.get(0).getArriveTime());
				
				//populate text fields with dash trip info
				dtrip1.setText(dashTrips.get(index).getTripNumber());
				dtime1.setText(dashTrips.get(index).getTripTime());
				ddur1.setText(dashTrips.get(index).getTripDuration());
				
				//populate text fields with Caltrain trip info
				ctrain1.setText(caltrainTrips.get(0).getTrainNumber());
				ctime1.setText(caltrainTrips.get(0).getFormattedTime());
				cdur1.setText(caltrainTrips.get(0).getDuration());
				
				//calculate total trip time
				String time1 = caltrainTrips.get(0).getDepartTime();
				String time2 = dashTrips.get(index).getArriveTime();
				totalTime1.setText("Total transit time: " + calculateTripDuration(time1, time2));
			}
			
			if(dashTrips.size() > 1 && caltrainTrips.size() > 1)
			{
				int index = findSoonestDashAfterCaltrain(dashTrips, caltrainTrips.get(1).getArriveTime());
				//populate text fields with dash trip info
				dtrip2.setText(dashTrips.get(index).getTripNumber());
				dtime2.setText(dashTrips.get(index).getTripTime());
				ddur2.setText(dashTrips.get(index).getTripDuration());
				
				//populate text fields with Caltrain trip info
				ctrain2.setText(caltrainTrips.get(1).getTrainNumber());
				ctime2.setText(caltrainTrips.get(1).getFormattedTime());
				cdur2.setText(caltrainTrips.get(1).getDuration());
				
				//calculate total trip time
				String time1 = caltrainTrips.get(1).getDepartTime();
				String time2 = dashTrips.get(index).getArriveTime();
				totalTime2.setText("Total transit time: " + calculateTripDuration(time1, time2));
			}
			if(dashTrips.size() > 2 && caltrainTrips.size() > 2)
			{
				int index = findSoonestDashAfterCaltrain(dashTrips, caltrainTrips.get(2).getArriveTime());
				//populate text fields with dash trip info
				dtrip3.setText(dashTrips.get(index).getTripNumber());
				dtime3.setText(dashTrips.get(index).getTripTime());
				ddur3.setText(dashTrips.get(index).getTripDuration());
				
				//populate text fields with Caltrain trip info
				ctrain3.setText(caltrainTrips.get(2).getTrainNumber());
				ctime3.setText(caltrainTrips.get(2).getFormattedTime());
				cdur3.setText(caltrainTrips.get(2).getDuration());
				
				//calculate total trip time
				String time1 = caltrainTrips.get(2).getDepartTime();
				String time2 = dashTrips.get(index).getArriveTime();
				totalTime3.setText("Total transit time: " + calculateTripDuration(time1, time2));
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	/**
	 * Find Dash trip that departs soonest after a given time.
	 * @param trips an ArrayList of Dash trips
	 * @param time a given time
	 * @return the index of the ArrayList that contains the trip that departs soonest after input time
	 */
	private int findSoonestDashAfterCaltrain(ArrayList<DashTripDetails> trips, String time)
	{
		//return 0 if no trip is found
		int index = 0;
		//walk through ArrayList until Dash trip depart time is later than input time
		for(int i = 0; i < trips.size(); i++)
		{
			if(trips.get(i).getDepartTime().compareTo(time) > 0)
			{
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Find DASH trip that arrives before a given time
	 * @param trips the list of trips to search
	 * @param time the time in question
	 * @return the index of the list that contains the desired time
	 */
	private int findSoonestDashBeforeCaltrain(ArrayList<DashTripDetails> trips, String time)
	{
		//return 0 if no trip is found
		int index = 0;
		
		//walk through ArrayList until Dash trip arrive time is later than input time
		for(int i = 0; i < trips.size(); i++)
		{
			if(trips.get(i).getArriveTime().compareTo(time) > 0)
			{
				index = i;
			}
		}
		return index;
	}

	/**
	 * Get top 3 (or less) Caltrain/Dash route combos using a Dash stop as the departure location.
	 * @param cdepart the departure location for Caltrain (San Jose always in this scenario)
	 * @param carrive the arrival location for Caltrain (input by user in Main Activity)
	 * @param ddepart the departure location for Dash (input by user in Main Activity)
	 * @param darrive the arrival location for Dash (San Jose always in this scenario)
	 * @param time the arrival time for Caltrain trip 
	 */
	private void getTopThreeRouteCombosDash(String cdepart, String carrive, String ddepart, String darrive, String time)
	{
		try
		{
			//since Caltrain runs much less often than Dash, only get 3 Caltrain trips previous to latest Dash trip
			ArrayList<CaltrainTripDetails> caltrainTrips = getTopThreeCaltrain(cdepart, carrive, "Weekday", time);
			
			//get all Dash trips that arrive at dash arrival stop previous to requested arrival time.
			ArrayList<DashTripDetails> dashTrips = new ArrayList<DashTripDetails>();
			
			caltrainHeader = (TextView) findViewById(R.id.caltrain_header1);
			dashHeader = (TextView) findViewById(R.id.dash_header1);
			caltrainHeader1 = (TextView) findViewById(R.id.caltrain_header2);
			dashHeader1 = (TextView) findViewById(R.id.dash_header2);
			caltrainHeader2 = (TextView) findViewById(R.id.caltrain_header3);
			dashHeader2 = (TextView) findViewById(R.id.dash_header3);
			txt = (TextView) findViewById(R.id.no_combos_available);
			if(caltrainTrips.size() == 0)
			{
				txt.setVisibility(View.VISIBLE);
				LinearLayout combo1 = (LinearLayout) findViewById(R.id.combo1);
				LinearLayout combo2 = (LinearLayout) findViewById(R.id.combo2);
				LinearLayout combo3 = (LinearLayout) findViewById(R.id.combo3);
				LinearLayout header = (LinearLayout) findViewById(R.id.rc_header);
				header.setVisibility(View.GONE);
				combo1.setVisibility(View.GONE);
				combo2.setVisibility(View.GONE);
				combo3.setVisibility(View.GONE);
			}
			
			//only formulate route combos if there were Caltrain trips found
			if(caltrainTrips.size() > 0)
			{
				txt.setVisibility(View.GONE);
				String t1 = caltrainTrips.get(0).getDepartTime();
				dashTrips = getTopThreeDash(ddepart, darrive, t1);
				
				from.setText(ddepart);
				from.setTextColor(getResources().getColor(R.color.main_blue));
				to.setText(carrive);
				to.setTextColor(getResources().getColor(R.color.caltrain_red));
			}
			
			//formulate 1 route combo if 1 of each trip exists
			if(dashTrips.size() > 0 && caltrainTrips.size() > 0)
			{
				//find Dash trip immediately following Caltrain trip
				int index = findSoonestDashBeforeCaltrain(dashTrips, caltrainTrips.get(0).getDepartTime());
				
				caltrainHeader.setText(R.string.dash_trip_header);
				dashHeader.setText(R.string.caltrain_trip_header);
				caltrainHeader.setTextColor(getResources().getColor(R.color.main_blue));
				dashHeader.setTextColor(getResources().getColor(R.color.caltrain_red));
				//populate text fields with dash trip info
				ctrain1.setText(dashTrips.get(index).getTripNumber());
				ctime1.setText(dashTrips.get(index).getTripTime());
				cdur1.setText(dashTrips.get(index).getTripDuration());
				
				//populate text fields with Caltrain trip info
				dtrip1.setText(caltrainTrips.get(0).getTrainNumber());
				dtime1.setText(caltrainTrips.get(0).getFormattedTime());
				ddur1.setText(caltrainTrips.get(0).getDuration());
				
				//calculate total trip time
				String time2 = caltrainTrips.get(0).getArriveTime();
				String time1 = dashTrips.get(index).getDepartTime();
				totalTime1.setText("Total transit time: " + calculateTripDuration(time1, time2));
			}
			
			if(dashTrips.size() > 1 && caltrainTrips.size() > 1)
			{
				int index = findSoonestDashBeforeCaltrain(dashTrips, caltrainTrips.get(1).getDepartTime());
				if(index > 0)
					index++;
				caltrainHeader1.setText(R.string.dash_trip_header);
				dashHeader1.setText(R.string.caltrain_trip_header);
				caltrainHeader1.setTextColor(getResources().getColor(R.color.main_blue));
				dashHeader1.setTextColor(getResources().getColor(R.color.caltrain_red));
				//populate text fields with dash trip info
				ctrain2.setText(dashTrips.get(index).getTripNumber());
				ctime2.setText(dashTrips.get(index).getTripTime());
				cdur2.setText(dashTrips.get(index).getTripDuration());
				
				//populate text fields with Caltrain trip info
				dtrip2.setText(caltrainTrips.get(1).getTrainNumber());
				dtime2.setText(caltrainTrips.get(1).getFormattedTime());
				ddur2.setText(caltrainTrips.get(1).getDuration());
				
				//calculate total trip time
				String time2 = caltrainTrips.get(1).getArriveTime();
				String time1 = dashTrips.get(index).getDepartTime();
				totalTime2.setText("Total transit time: " + calculateTripDuration(time1, time2));
			}
			
			if(dashTrips.size() > 2 && caltrainTrips.size() > 2)
			{
				int index = findSoonestDashBeforeCaltrain(dashTrips, caltrainTrips.get(2).getDepartTime());
				if(index > 0)
					index++;
				caltrainHeader2.setText(R.string.dash_trip_header);
				dashHeader2.setText(R.string.caltrain_trip_header);
				caltrainHeader2.setTextColor(getResources().getColor(R.color.main_blue));
				dashHeader2.setTextColor(getResources().getColor(R.color.caltrain_red));
				//populate text fields with dash trip info
				ctrain3.setText(dashTrips.get(index).getTripNumber());
				ctime3.setText(dashTrips.get(index).getTripTime());
				cdur3.setText(dashTrips.get(index).getTripDuration());
				
				//populate text fields with Caltrain trip info
				dtrip3.setText(caltrainTrips.get(2).getTrainNumber());
				dtime3.setText(caltrainTrips.get(2).getFormattedTime());
				ddur3.setText(caltrainTrips.get(2).getDuration());
				
				//calculate total trip time
				String time2 = caltrainTrips.get(2).getArriveTime();
				String time1 = dashTrips.get(index).getDepartTime();
				totalTime3.setText("Total transit time: " + calculateTripDuration(time1, time2));
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	/**
	 * Find the top three (or less depending on how many exist) Dash trips per given inputs
	 * @param depart the departing location (always San Jose station) since interacting with Caltrain
	 * @param arrive Dash arrival stop
	 * @param arrivalTime the latest desired arrival time for the arrival stop
	 * @return an ArrayList of the number of trips found (max 3)
	 * @throws IOException throws an exception since method reads from file
	 */
	private ArrayList<DashTripDetails> getTopThreeDash(String depart, String arrive, String arrivalTime) throws IOException
	{
		//store times and trips
		ArrayList<DashTripDetails> trips = new ArrayList<DashTripDetails>();
		ArrayList<String> departingTimes = new ArrayList<String>();
		ArrayList<String> arrivingTimes = new ArrayList<String>();
		
		//get the stop IDs of Dash stops to use for comparison when reading from the file.
		String departingStopID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf(depart));
		String arrivingStopID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf(arrive));
		String sjStationID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf("San Jose Caltrain Station"));
		String sf1stID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf("San Fernando & 1st"));
		String sf4thID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf("4th & San Fernando"));
		String ccID = MainActivity.stopIDs.get(MainActivity.stopNames.indexOf("Convention Center Lrt Station"));
		
		String duration ="";
		{
			InputStream input = getResources().openRawResource(R.raw.dstoptimes);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			try
			{
				line = reader.readLine();
				
				//check all possible stop combinations and get trip information 
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
				
				//sort all departing and arriving times for the trips between given departure and arrival stops 
				Collections.sort(departingTimes);
				Collections.sort(arrivingTimes);
				int index = 0;
				//find the latest arrival time that is before the desired arrival time
				while(index < arrivingTimes.size() && arrivalTime.compareTo(arrivingTimes.get(index)) > 0)
				{
					index++;
				}
				index--; //include the latest time
				
				//Get times from departingTimes and arrivingTimes and formulate Dash trips for each
				for(int i = index; i >= 0; i--)
				{
					String from = departingTimes.get(i);
					String to = arrivingTimes.get(i);
					String completeTime = formatRouteTimes(from, to);
					duration = calculateTripDuration(from, to);
					trips.add(new DashTripDetails("Trip " + (i + 1), from, to, completeTime, duration));
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
		}
		return trips;
	}
	
	/**
	 * Get top three Caltrain trips to correspond with DASH trips per given input.
	 * @param depart the departure Caltrain station
	 * @param arrive San Jose station always because route combos is Caltrain and Dash
	 * @param type the type of service, and is "Weekday" for purposes of finding route combos since Dash doesn't operate on Weekends
	 * @param sjArriveTime The earliest San Jose arrival time, according to Dash time 
	 * @return the top three (or less if 3 don't exist) Caltrain trips meeting input criteria
	 * @throws IOException throws IOException since the method reads from a file
	 */
	private ArrayList<CaltrainTripDetails> getTopThreeCaltrain(String depart, String arrive, String type, String sjArriveTime) throws IOException
	{			
		//create array for storing trips
		ArrayList<CaltrainTripDetails> tempTrips = new ArrayList<CaltrainTripDetails>();
		ArrayList<CaltrainTripDetails> trips = new ArrayList<CaltrainTripDetails>();
		
		//variables for temporarily storing Caltrian trip details 
		String time = "";
		String train = "";
		String duration = "";
		String routeType = "";
		
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
			while(((line = reader.readLine()) != null) && (index < tripIDs.size()))
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
						//System.out.println("Trip id found");
						
						if(parts[3].substring(0, parts[3].length() - 8).equals(depart))
						{
							//set departure found to true and only grab hours and minutes of time
							departureFound = true;
							from = parts[1].substring(0, 5);
						}
						else if(parts[3].substring(0, parts[3].length() - 8).equals(arrive) && (parts[1].substring(0, 5).compareTo(sjArriveTime) < 0) )
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
							
							//find route type of Caltrian trip
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
							
							departureFound = false;
							arrivalFound = false;
							
							//create trip and add to ArrayList
							CaltrainTripDetails trip = new CaltrainTripDetails(train, from, to, time, duration, routeType);
							tempTrips.add(trip);
						}//end both trips found check
					}//end same index check
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
								//set departure found to true and only grab hours and minutes of time
								departureFound = true;
								from = parts[1].substring(0, 5);
							}
							else if(parts[3].substring(0, parts[3].length() - 8).equals(arrive)) //check if portion of line contains arrival station
							{
								//set arrival found to true and only grab hours and minutes of time
								arrivalFound = true;
								to = parts[1].substring(0, 5);
							}
						}
					}//end else
				}//end check if trips is one we care about
			}//end outer while loop
								
			//sort the trips array by departure time
			int i, j;
			String sindex;
			CaltrainTripDetails t;
			for(i = 1; i < tempTrips.size(); i++)
			{
				t = tempTrips.get(i);
				sindex = tempTrips.get(i).getDepartTime();
				j = i;
				while((j > 0) && ((tempTrips.get(j - 1).getDepartTime().compareTo(sindex) > 0)))
				{
					tempTrips.set(j, tempTrips.get(j - 1));
					j = j - 1;
				}
				tempTrips.set(j, t);
			}
			
			int counter = 0;
			while(counter < tempTrips.size() && tempTrips.get(counter).getFormattedTime().substring(tempTrips.get(counter).getFormattedTime().length() - 5, tempTrips.get(counter).getFormattedTime().length()).compareTo(sjArriveTime) < 0)
			{
				counter++;
			}
			counter--;
			//format times of all trips
			for(int c = counter; c >= 0; c--)
			{
				String leave = tempTrips.get(c).getDepartTime();
				String getThere = tempTrips.get(c).getArriveTime();
				String formatTime = formatRouteTimes(leave, getThere);
				tempTrips.get(c).setFormattedTime(formatTime);
				trips.add(tempTrips.get(c));
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
		
		return trips;
	}
	
	/**
	 * Class helper method to filter all tripIDs to just the desired ones, based on type (Weekend or Weekday) and direction(north or south) 
	 * @param days the schedule type
	 * @param direction the direction value (0 or 1)
	 * @return a list of desired tripIDs
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
	 * Class helper method to change hour values of trip times to 12-hour format
	 * @param start the departing time
	 * @param finish the arriving time
	 * @return the trip time displayed in the format 00:00 AM/PM - 00:00 AM/PM
	 */
	private String formatRouteTimes(String start, String finish)
	{
		return changeHour(start) + " - " + changeHour(finish);
	}
	
	/**
	 * Class helper method to change the hour to 12-hour format and add AM or PM
	 * @param time the time to convert
	 * @return the newly converted time value as a String
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
	 * Class helper method to calculate elapsed time between two input times.
	 * @param start the beginning time
	 * @param finish the end time
	 * @return the total trip duration as a String
	 */
	private String calculateTripDuration(String start, String finish)
	{
		start = start.trim();
		finish = finish.trim();
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

}//end class FindRouteCombosActivity