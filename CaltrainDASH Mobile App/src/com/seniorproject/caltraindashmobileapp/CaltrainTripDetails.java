package com.seniorproject.caltraindashmobileapp;

/**
 * A class to work with Caltrain trip details
 * @author Clark Stonehocker
 */
public class CaltrainTripDetails 
{
	//instance variables of the class 
	private String trainNumber; //the train number
	private String service; //the service type (weekend or weekday)
	private String departTime; //the depart time for the trip
	private String arriveTime; //the arrive time for the trip
	private String formattedTime; //the trip time, composed of departTime and ArriveTime
	private String duration; //the duration of the trip
	
	/**
	 * Construct a trip.
	 * @param num the train number
	 * @param depart the departure time for the trip
	 * @param arrive the arrival time for the trip
	 * @param formatTime the duration time for the trip
	 * @param dur the trip duration
	 * @param ser the service type (weekend or weekday)
	 */
	public CaltrainTripDetails(String num, String depart, String arrive, String formatTime, String dur, String ser)
	{
		trainNumber = num;
		departTime = depart;
		arriveTime = arrive;
		formattedTime = formatTime;
		duration = dur;
		service = ser;
	}
	
	/**
	 * Returns the train number of the trip
	 * @return the train number as a string value
	 */
	public String getTrainNumber()
	{
		return trainNumber;
	}
	
	/**
	 * Get the service type for the trip
	 * @return the servie type (weekend or weekday)
	 */
	public String getService()
	{
		return service;
	}
	
	/**
	 * Get the departure time of the trip
	 * @return the departure time in the format XX:XX (24 hr. format)
	 */
	public String getDepartTime()
	{
		return departTime;
	}
	
	/**
	 * Get the arrival time of the trip
	 * @return the arrival time in the format XX:XX (24 hr. format)
	 */
	public String getArriveTime()
	{
		return arriveTime;
	}
	
	/**
	 * Get the formatted time for the trip
	 * @return the formatted time in the format XX:XX AM/PM - XX:XX AM/PM 
	 */
	public String getFormattedTime()
	{
		return formattedTime;
	}
	
	/**
	 * Get the trip duration
	 * @return the duration of the trip in the format Xhrs Xmins
	 */
	public String getDuration()
	{
		return duration;
	}
	
	/**
	 * Set the train number for the trip.
	 * @param num the new train number as a String value
	 */
	public void setTrainNumber(String num)
	{
		trainNumber = num;
	}
	
	/**
	 * Set the service type for the trip
	 * @param serve the new service type 
	 */
	public void setService(String serve)
	{
		service = serve;
	}
	
	/**
	 * Set the depart time of the trip
	 * @param depart the new depart time
	 */
	public void setDepartTime(String depart)
	{
		departTime = depart;
	}
	
	/**
	 * Set the arrive time of the trip
	 * @param arrive the new arrive time
	 */
	public void setArriveTime(String arrive)
	{
		arriveTime = arrive;
	}
	
	/**
	 * Set the formatted time of the trip
	 * @param time the new formatted time
	 */
	public void setFormattedTime(String time)
	{
		formattedTime = time;
	}
	
	/**
	 * Set the trip duration
	 * @param dur the new duration
	 */
	public void setDuration(String dur)
	{
		duration = dur;
	}
}