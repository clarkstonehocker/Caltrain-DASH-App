package com.seniorproject.caltraindashmobileapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.seniorproject.caltraindashmobileapp.R;

import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * The app's main activity class which creates the home screen and contains most functionality for app functions.
 * @author Clark Stonehocker
 */
public class MainActivity extends AbstractNavigationDrawerActivity implements OnItemSelectedListener
{
	//load these in Main so they are available to other activities
	protected static ArrayList<String> stationNames = new ArrayList<String>();
	protected static ArrayList<String> stationAddresses = new ArrayList<String>();
	protected static ArrayList<Double> stationLatitude = new ArrayList<Double>();
	protected static ArrayList<Double> stationLongitude = new ArrayList<Double>();
	protected static ArrayList<Integer> stationZoneIDs = new ArrayList<Integer>();
	protected static ArrayList<String> fareID = new ArrayList<String>();
	protected static ArrayList<String> fareValue = new ArrayList<String>();
	protected static ArrayList<String> tripID = new ArrayList<String>();
	protected static ArrayList<String> trainNumbers = new ArrayList<String>();
	protected static ArrayList<String> routeID = new ArrayList<String>();
	protected static ArrayList<String> serviceID = new ArrayList<String>();
	protected static ArrayList<Integer> directionID = new ArrayList<Integer>();
	
	protected static ArrayList<String> dashStops = new ArrayList<String>();
	protected static ArrayList<String> stopIDs = new ArrayList<String>();
	protected static ArrayList<String> stopNames = new ArrayList<String>();
	protected static ArrayList<Double> stopLatitudes = new ArrayList<Double>();
	protected static ArrayList<Double> stopLongitudes = new ArrayList<Double>();
	protected static ArrayList<String> westTripIDs = new ArrayList<String>();
	protected static ArrayList<String> eastTripIDs = new ArrayList<String>();
	protected static String DASH_ROUTE_ID;
	protected static String dashDepart;
	protected static String dashArrive;
	
	private static ArrayList<String> allLocations = new ArrayList<String>();
	
	private Spinner routeCombosDepart;
	private Spinner routeCombosArrive;
	private TimePicker routeCombosArriveTime;
	private TextView rcError;
	protected static String lastCombosDepartSelection;
	protected static String lastCombosArriveSelection;
	protected static int routeCombosCurrentHour;
	protected static int routeCombosCurrentMinute;
	
	protected static SharedPreferences savedValues;
	protected static SharedPreferences.Editor edit;
		
	protected static String caltrainDepart;
	protected static String caltrainArrive;
	protected static String caltrainSchedule;
	
	private NavigationDrawerActivityConfiguration navDrawerActivityConfiguration  = new NavigationDrawerActivityConfiguration();
	
	@Override
	/**
	 * Create the activity
	 */
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        rcError = (TextView) findViewById(R.id.route_combos_error);
        rcError.setVisibility(View.GONE);
        
        // enable app icon in ActionBar to behave as action to toggle navigation drawer
        setupActionBar();
        
        //save fare, trip, and stops information in arrays
        try
        {	
        	//only load data into ArrayLists if they are empty to avoid duplicate data
        	if(fareID.size() < 1)
        	{
        		getCaltrainFares();
        	}
        	if(tripID.size() < 1)
        	{
        		loadCaltrainTrips();
        	}
        	if(stationNames.size() < 1)
        	{
        		loadCaltrainStops();
        	}
        	if(DASH_ROUTE_ID == null)
        	{
        		loadDashRouteId();
        	}
        	if(eastTripIDs.size() < 1 || westTripIDs.size() < 1)
        	{
        		loadDashTrips();
        	}
        	if(stopIDs.size() < 1)
        	{
        		loadDashStopIDs();
        	}
			if(stopNames.size() < 1)
			{
				loadDashStopNames();
			}
			if(allLocations.size() < 1)
			{
				combineCaltrainDashStops();
			}
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }
        
        //create depart spinner for finding route combos portion of main view
		routeCombosDepart = (Spinner) findViewById(R.id.departing);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allLocations);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		routeCombosDepart.setAdapter(adapter);
		routeCombosDepart.setOnItemSelectedListener(this);
		
		//create arrive spinner based on selection from depart spinner
		routeCombosArrive = (Spinner) findViewById(R.id.arriving);
		String stopName = (String) routeCombosDepart.getSelectedItem();
		ArrayAdapter<String> adapter2;
		
		//if selected departure location is a Caltrain stop, load arrival spinner with only Dash stop names
		if(stationNames.contains(stopName))
		{
			adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stopNames);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			routeCombosArrive.setAdapter(adapter2);
		}
		//if selected departure location is a Dash stop, load arrival spinner with only Caltrain station names
		else if(stopNames.contains(stopName))
		{
			adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stationNames);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			routeCombosArrive.setAdapter(adapter2);
		}
		
		//set spinners to last recorded selection so state is preserved
		savedValues = getSharedPreferences("preferences", 0);
		edit = savedValues.edit();
		lastCombosDepartSelection = savedValues.getString("lastCombosDepart", "");
		lastCombosArriveSelection = savedValues.getString("lastCombosArrive", "");
		if( lastCombosDepartSelection!= null)
		{
			routeCombosDepart.setSelection(getIndex(routeCombosDepart, lastCombosDepartSelection ));
		}
		if(lastCombosArriveSelection != null)
		{
			routeCombosArrive.setSelection(getIndex(routeCombosArrive, lastCombosArriveSelection));
		}
		
		//set arrival time elements to last preserved state
		routeCombosArriveTime = (TimePicker) findViewById(R.id.timePicker1);
		routeCombosCurrentHour = savedValues.getInt("lastCombosHour", -1);
		routeCombosCurrentMinute = savedValues.getInt("lastCombosMinute", -1);
		if( routeCombosCurrentHour >= 0)
		{
			routeCombosArriveTime.setCurrentHour(routeCombosCurrentHour);
		}
		if( routeCombosCurrentMinute >= 0)
		{
			routeCombosArriveTime.setCurrentMinute(routeCombosCurrentMinute);
		}
	}
	
	/**
	 * Method to show a tutorial screen for navigation drawer and route reversal on app installation.
	 */
	private void initializeTutorial()
	{
		//initialize pop-up window
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		int width = size.x;
		System.out.println(height);
		final PopupWindow screen = new PopupWindow(width, height);
		RelativeLayout group = (RelativeLayout) findViewById(R.id.tutorial_layout);
		LayoutInflater layoutInflate = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View otherLayout = layoutInflate.inflate(R.layout.tutorial_screen_popup, group);
		screen.setContentView(otherLayout);
		screen.setFocusable(true);
		
		//specify location of window display
		screen.showAtLocation(otherLayout, Gravity.TOP, 0, 0);
		
		//set button and click event handler for closing the tutorial screen
		Button close = (Button) otherLayout.findViewById(R.id.tutorial_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				screen.dismiss();
			}
		});
	}
	
	@Override
	/**
	 * Set up the navigation drawer for this activity.
	 * @return the configuration of the drawer
	 */
    protected NavigationDrawerActivityConfiguration getNavDrawerConfiguration() 
	{
		//create the array of drawer items
		NavigationDrawerItem[] menu = new NavigationDrawerItem[] {
                NavigationDrawerSection.create( 100, "Public Transportation"),
                NavigationDrawerMenuItem.create(101,"Home", R.drawable.home_icon, false, this),
                NavigationDrawerMenuItem.create(102, "Caltrain Schedule", R.drawable.caltrain_icon, true, this),
                NavigationDrawerMenuItem.create(103, "Dash Schedule", R.drawable.shuttle_icon, true, this),
                NavigationDrawerSection.create(200, "Twitter"),
                NavigationDrawerMenuItem.create(202, "@Caltrain_News", R.drawable.bird_black, false, this),
                NavigationDrawerMenuItem.create(203, "@GoCaltrain", R.drawable.bird_black, false, this), 
                NavigationDrawerMenuItem.create(204, "@VTA", R.drawable.bird_black, false, this)};
        
		//configure the drawer
        //navDrawerActivityConfiguration = new NavigationDrawerActivityConfiguration();
        navDrawerActivityConfiguration.setMainLayout(R.layout.activity_main);
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
     * Handle click events of drawer items.
     * @param id the id number of the clicked item
     */
    protected void onNavItemSelected(int id) 
    {
    	//don't take any action if the home (this current activity) item is selected, but do for all others
        switch (id) 
        {
        	case 102:
        	{    
        		Intent intent = new Intent(this, ViewCaltrainActivity.class);
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
	 * Listener method for spinners
	 * @param parent the spinner that triggered the event
	 * @param view the view that the spinner belongs to
	 * @param pos the position of the selected item within the spinner
	 * @param id the id number of the clicked item
	 */
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		//take action only for the depart spinner
		if(parent.equals(routeCombosDepart))
		{
			//get the currently selected item from the depart spinner
			String rcDepart = (String) routeCombosDepart.getSelectedItem();
			if(rcDepart.contains("DASH"))
			{
				rcDepart = rcDepart.substring(8);
			}
			String stopName = rcDepart;
			ArrayAdapter<String> adapter2;
			
			/*set contents of arrive spinner based on depart spinner selection.  Depart spinner contains Caltrain
			and Dash stops.  If a Caltrain stop is selected, the arrive spinner is populated with Dash stops.  Otherwise,
			if a Dash stop is selected, the arrive spinner is populated with Caltrain stops.
			*/ 
			if(stationNames.contains(stopName))
			{
				if(savedValues.getString("lastCombosArrive", "") != null)
				{
					adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stopNames);
					adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					routeCombosArrive.setAdapter(adapter2);
					routeCombosArrive.setSelection(getIndex(routeCombosArrive, savedValues.getString("lastCombosArrive", "")), true);
				}
				else
				{
					adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stopNames);
					adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					routeCombosArrive.setAdapter(adapter2);
				}
				
			}
			else if(stopNames.contains(stopName))
			{
				if(savedValues.getString("lastCombosArrive", "") != null)
				{
					adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stationNames);
					adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					routeCombosArrive.setAdapter(adapter2);
					routeCombosArrive.setSelection(getIndex(routeCombosArrive, savedValues.getString("lastCombosArrive", "")), true);
				}
				else
				{
					adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stationNames);
					adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					routeCombosArrive.setAdapter(adapter2);
				}
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
	 * Find the index of a given String value within a spinner
	 * @param spinner the spinner to be searched
	 * @param selection the string value to find
	 * @return the index of the spinner where the selection was found, or 0 by default
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
	 * Combine Caltrain and Dash stops into one ArrayList.
	 */
	private void combineCaltrainDashStops()
	{
		for(int i = 0; i < stopNames.size(); i++)
		{
			allLocations.add("DASH -- " + stopNames.get(i));
		}
		for(int j = 0; j < stationNames.size(); j++)
		{
			allLocations.add(stationNames.get(j));
		}
		
	}
	
	/**
	 * Load fare information in ArrayLists
	 * @throws IOException
	 */
    private void getCaltrainFares() throws IOException
    {
    	//open file for reading fare details
    	InputStream input = getResources().openRawResource(R.raw.ctfareattributes);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	
    	//variable for using with file reading
    	String line;
    	
    	try
    	{
    		//discard the first line of the file
    		line = reader.readLine();
    		
    		//go till end of file
    		while( (line=reader.readLine()) != null)
    		{
    			//remove all quotation marks and split on commas
    			line = line.replace("\"", "");
    			String [] parts = line.split(",");
    			
    			//assign fareID and dollar amount to appropriate arrays
    			fareID.add(parts[0]);
    			fareValue.add(parts[1].substring(0, parts[1].indexOf(".") + 3));
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
    
    /**
     * Load train trips information into corresponding ArrayLists
     * @throws IOException
     */
    private void loadCaltrainTrips() throws IOException
    {
    	//allocate InputStream and reader for reading from file
    	InputStream input = getResources().openRawResource(R.raw.cttrips);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	
    	//variable for storing single lines of file
    	String line;
    	
    	try
    	{
    		//discard the first line of the file
    		line = reader.readLine();
    		
    		//read until the end of the file
    		while((line=reader.readLine()) != null)
    		{
    			//remove quotation marks from line, split it on commas, and store values in corresponding ArrayLists
    			line = line.replace("\"", "");
    			String [] parts = line.split(",");
    			tripID.add(parts[0]); // indicates trip id for identifying unique trips
    			trainNumbers.add(parts[1]); //indicates number of train (unique)
    			routeID.add(parts[2]); //indicates route type, whether local, limited, or bullet
    			serviceID.add(parts[3]); //indicates service type, whether weekday or weekend
    			directionID.add(Integer.parseInt(parts[5])); //indicates direction of trip, whether north-bound or south-bound
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
    
    /**
     * Read and store stops information.
     * @throws IOException
     */
	private void loadCaltrainStops() throws IOException
	{
		//open input stream and reader for opening and reading from file
		InputStream input = getResources().openRawResource(R.raw.ctstops);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		//use variable for storing single lines of file
		String line;
		
		try
		{
			//discard the first line of the file
			line = reader.readLine();
			
			//read through file line by line and grab appropriate data
			while((line = reader.readLine()) != null)
			{
				//remove all quotation marks from line and split on commas
				line = line.replace("\"", "");
				String [] parts = line.split(",");
				
				//remove word Caltrain from station name
				stationNames.add(parts[0].substring(0, parts[0].length() - 8 ));
				
				//store address (combined two elements of parts array because they are split because address contains a comma)
				stationAddresses.add(parts[2]+", "+parts[3]); 
				stationLatitude.add(Double.parseDouble(parts[4])); //store latitude value of station
				stationLongitude.add(Double.parseDouble(parts[5])); //store longitude value of station
				stationZoneIDs.add(Integer.parseInt(parts[6])); //store zone of station
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
		//store the Caltrain stations in order from north to south instead of in alphabetical order
		sortStationDetailsNorthToSouth();
    }
	
	/**
	 * Sort the ArrayLists so station names and details are indexed north to south rather than alphabetically, as
	 * they are listed in the data file.
	 */
	private void sortStationDetailsNorthToSouth()
	{
		int i, j;
		double index; 
		double index1;
		int index2;
		String sindex;
		String sindex1;
		
		//use insert sort algorithm to sort the data 
		//all ArrayLists with station details are sorted together so that their indexes match
		for(i = 1; i<stationLatitude.size(); i++)
		{
			index = stationLatitude.get(i);
			index1 = stationLongitude.get(i);
			index2 = stationZoneIDs.get(i);
			sindex = stationNames.get(i);
			sindex1 = stationAddresses.get(i);
			j = i;
			while((j > 0) && (stationLatitude.get(j - 1) < index))
			{
				stationLatitude.set(j, stationLatitude.get(j - 1));
				stationNames.set(j, stationNames.get(j - 1));
				stationLongitude.set(j, stationLongitude.get(j - 1));
				stationZoneIDs.set(j,  stationZoneIDs.get(j - 1));
				stationAddresses.set(j, stationAddresses.get(j - 1));
				j = j - 1;
			}
			stationLatitude.set(j, index);
			stationLongitude.set(j, index1);
			stationZoneIDs.set(j, index2);
			stationNames.set(j, sindex);
			stationAddresses.set(j, sindex1);
		}
	}
	
	/**
	 * Load the route id for DASH
	 * @throws IOException
	 */
	public void loadDashRouteId() throws IOException
	{
		InputStream input = getResources().openRawResource(R.raw.droutes);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		try
		{
			line = reader.readLine();
			while((line= reader.readLine()) != null)
			{
				if(line.contains("DASH"))
				{
					line = line.replace("\"", "");
					String parts[] = line.split(",");
					DASH_ROUTE_ID = parts[0];
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
	}
	
	/**
	 * Load the stop IDs for all DASH stops
	 * @throws IOException
	 */
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
	}
	
	/**
	 * Load the Dash stop names.
	 * @throws IOException
	 */
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
					stopNames.add(formatDashStopName(parts[1]));
					stopLatitudes.add(Double.parseDouble(parts[3]));
					stopLongitudes.add(Double.parseDouble(parts[4]));
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
	}
	
	/**
	 * Format Dash stop names so that they're not in all caps (they are contained that way in the files)
	 * @param name the name of the DASH stop
	 * @return a String with the name properly formatted
	 */
	private String formatDashStopName(String name)
	{
		String[] array = name.split(" ");
		String newName = "";
		for(int index = 0; index < array.length; index++)
		{
			//just make the first letter of each word capitalized instead of every letter
			String word = array[index];
			String firstLetter = word.substring(0, 1);
			String rest = word.substring(1).toLowerCase();
			newName += firstLetter + rest + " ";
		}
		newName = newName.trim();
		return newName;
	}
	
	/**
	 * Load DASH trip ids, separted by west-bound trips and east-bound trips.
	 * @throws IOException
	 */
	private void loadDashTrips() throws IOException
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
        getMenuInflater().inflate(R.menu.main, menu);
        int [] array = {R.id.action_info, R.id.action_reverse_stations};
        navDrawerActivityConfiguration.setActionMenuItemsToHideWhenDrawerOpen(array);
        if(savedValues.getBoolean("showTutorial", true))
		{
			initializeTutorial();
			edit.putBoolean("showTutorial", false);
			edit.commit();
		}
        return true;
    }
    
    @Override
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
     * Create and display a pop-up window containing information about the app.
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
		screen.setFocusable(true);
		screen.showAtLocation(otherLayout, Gravity.CENTER, 0, 0);
		
		Button close = (Button) otherLayout.findViewById(R.id.about_info_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				screen.dismiss();
			}
		});
		
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
     * Reverse the route combos depart and arrival selections.
     */
    private void reverseRoutes()
	{
    	//get the depart and arrival location names
    	String depart = (String) routeCombosDepart.getSelectedItem();
    	String arrive = (String) routeCombosArrive.getSelectedItem();
    	
    	//store the values to have access to them later
    	//the Spinner listener method OnItemSelected changes the arrival spinner value once the depart is set
    	edit.putString("lastCombosArrive", depart);
    	edit.putString("lastCombosDepart", arrive);
    	edit.commit();
    	
    	//set depart selection to arrive selection
		int departIndex = allLocations.indexOf(arrive);
		routeCombosDepart.setSelection(departIndex, true);
	}
    
    /**
     * Event handler for click of View Caltrain Schedule area
     * @param view the view that initiates the event
     */
    public void viewCaltrainSchedule(View view)
    {
    	Intent intent = new Intent(this, ViewCaltrainActivity.class);
    	startActivity(intent);
    }
    
    /**
     * Event handler for click of View Dash Schedule area
     * @param view the view that initiates the event
     */
    public void viewDashSchedule(View view)
    {
    	Intent intent = new Intent(this, ViewDashActivity.class);
    	startActivity(intent);
    }
    
    /**
     * Event handler for click of Find Route Combos button
     * @param view the view (button) that initiates the event
     */
    public void findRouteCombos(View view)
    {
    	String depart = (String) routeCombosDepart.getSelectedItem();
    	if(depart.contains("DASH"))
    	{	depart = depart.substring(8);
    		System.out.println("rcdepart=" + depart);
    	}
    	lastCombosDepartSelection = depart;
    	lastCombosArriveSelection = (String) routeCombosArrive.getSelectedItem();
    	routeCombosCurrentHour = routeCombosArriveTime.getCurrentHour();
    	routeCombosCurrentMinute = routeCombosArriveTime.getCurrentMinute();
    	edit.putString("lastCombosDepart", lastCombosDepartSelection);
    	edit.putString("lastCombosArrive", lastCombosArriveSelection);
    	edit.putInt("lastCombosHour", routeCombosCurrentHour);
    	edit.putInt("lastCombosMinute", routeCombosCurrentMinute);
    	edit.commit();
    	if(lastCombosDepartSelection.equals(lastCombosArriveSelection))
    	{
    		rcError.setVisibility(View.VISIBLE);
    	}
    	else if(6 > routeCombosCurrentHour  || routeCombosCurrentHour > 21 )
    	{
    		rcError.setText("No DASH during this time.  Revise selection.");
    		rcError.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		rcError.setVisibility(View.GONE);
    		Intent intent = new Intent(this, FindRouteCombosActivity.class);
        	startActivity(intent);
    	}
    }
}//end class MainActivity