package com.seniorproject.caltraindashmobileapp;

/**
 * A class to handle DASH trip details
 * @author Clark Stonehocker
 */
public class DashTripDetails 
{	
	private String tripNumber; //the trip number
	private String departTime; //the depart time of the trip
	private String arriveTime; //the arrive time of the trip
	private String formattedTime; //the formatted time of the trip
	private String duration; //the trip duration
	
	/**
	 * Construct a new trip.
	 * @param num the trip number as a string
	 * @param depart the depart time as a a string
	 * @param arrive the arrive time as a string
	 * @param t the formatted time as a string
	 * @param dur the trip duration as a string
	 */
	public DashTripDetails(String num, String depart, String arrive, String t, String dur)
	{
		tripNumber = num;
		departTime = depart;
		arriveTime = arrive;
		formattedTime= t;
		duration = dur;
	}
	
	/**
	 * Get the trip number
	 * @return the trip number as a string value
	 */
	public String getTripNumber()
	{
		return tripNumber;
	}
	
	/**
	 * Get the depart time of the trip
	 * @return the depart time as a string in the format XX:XX (24 hr. format)
	 */
	public String getDepartTime()
	{
		return departTime;
	}
	
	/**
	 * Get the arrive time of the trip
	 * @return the arrive time as a string in the format XX:XX (24 hr. format)
	 */
	public String getArriveTime()
	{
		return arriveTime;
	}
	
	/**
	 * Get the formatted trip time
	 * @return the formatted time as a string, ideally in the format XX:XX AM/PM - XX:XX AM/PM
	 */
	public String getTripTime()
	{
		return formattedTime;
	}
	
	/**
	 * Get the trip duration
	 * @return the duration time of the trip, in the format Xhrs Xmins
	 */
	public String getTripDuration()
	{
		return duration;
	}
	
	/**
	 * Set the trip number
	 * @param num the new trip number
	 */
	public void setTripNumber(String num)
	{
		tripNumber = num;
	}
	
	/**
	 * Set the depart time for the trip
	 * @param depart the new depart time
	 */
	public void setDepartTime(String depart)
	{
		departTime = depart;
	}
	
	/**
	 * Set the arrive time for the trip
	 * @param arrive the new arrival time
	 */
	public void setArriveTime(String arrive)
	{
		arriveTime = arrive;
	}
	
	/**
	 * Set the formatted time of the trip
	 * @param t the new formatted time
	 */
	public void setTripTime(String t)
	{
		formattedTime = t;
	}
	
	/**
	 * Set the duration of the trip
	 * @param dur the new trip duration
	 */
	public void setTripDuration(String dur)
	{
		duration = dur;
	}
}