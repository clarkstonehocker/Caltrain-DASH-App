package com.seniorproject.caltraindashmobileapp;

/**
 * An interface used for navigation drawer items since they can be of different types 
 * (Section headers, and standard items for example)
 * @author Clark Stonehocker
 */
public interface NavigationDrawerItem 
{
	public int getId(); //method to get the item's layout id
    public String getLabel(); //method to get the item's label (text)
    public int getType(); //method to get the item type (standard vs section heading for instance)
    public boolean isEnabled(); //method to check if the item is enabled (can be clicked)
    public boolean updateActionBarTitle(); //method to check if the Action Bar title should be updated
}
