package com.seniorproject.caltraindashmobileapp;

/**
 * A class to manage Caltrain station details
 * @author Clark Stonehocker
 */
public class Station 
{
	private String name;
	private String address;
	private float latitude;
	private float longitude;
	private int zone;
	
	/**
	 * Construct a new Station
	 * @param sname the station name
	 * @param saddress the station address
	 * @param lat the station latitude value
	 * @param lon the station longitude value
	 * @param zoneID the station zone id
	 */
	public Station(String sname, String saddress, float lat, float lon, int zoneID)
	{ 
		name = sname;
		address = saddress;
		latitude = lat;
		longitude = lon;
		zone = zoneID;
	}
	
	/**
	 * Get the station name
	 * @return the station's name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Get the station's address
	 * @return the Caltrain station's address
	 */
	public String getAddress()
	{
		return address;
	}
	
	/**
	 * Get the station's latitude value
	 * @return the latitude value as a float 
	 */
	public float getLatitude()
	{
		return latitude;
	}
	
	/**
	 * Get the station's longitude value
	 * @return the longitude value as a float
	 */
	public float getLongitude()
	{
		return longitude;
	}
	
	/**
	 * Get the station's zone
	 * @return the station zone as an integer value
	 */
	public int getZone()
	{
		return zone;
	}

}