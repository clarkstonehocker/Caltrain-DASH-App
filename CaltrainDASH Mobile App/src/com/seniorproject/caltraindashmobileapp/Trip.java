package com.seniorproject.caltraindashmobileapp;

/**
 * A class to manage DASH trip details
 * @author Clark Stonehocker
 */
public class Trip 
{
	private String tripID;
    private String serviceID;
    private String routeID;
    private int directionID;
    
    /**
     * Construct a default trip by setting all instance variables to default values
     */
    public Trip()
    {
    	tripID = "";
    	serviceID ="";
    	routeID ="";
    	directionID = 0;
    }
    
    /**
     * Create a new trip object
     * @param trip the id of the trip
     * @param route the route of the trip
     * @param service the service type of the trip
     * @param direction the direction of the trip (For DASH, trips are east-bound and west-bound
     */
    public Trip(String trip, String route, String service, int direction)
    {
    	tripID = trip;
    	serviceID = service;
    	routeID = route;
    	directionID = direction;
    }
    
    /**
     * Get the id of the trip
     * @return the trip id as a String value
     */
    public String getTripID()
    {
    	return tripID;
    }
    
    /**
     * Get the service id for the trip
     * @return the service id as a String value
     */
    public String getServiceID()
    {
    	return serviceID;
    }
    
    /**
     * Get the route id
     * @return the route id as a String value
     */
    public String getRouteID()
    {
    	return routeID;
    }
    
    /**
     * Get the direction id for the trip
     * @return an integer value of the direction
     */
    public int getDirectionID()
    {
    	return directionID;
    }

}