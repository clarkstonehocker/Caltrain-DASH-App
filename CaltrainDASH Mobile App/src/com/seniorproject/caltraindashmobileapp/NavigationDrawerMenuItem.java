package com.seniorproject.caltraindashmobileapp;

import android.content.Context;

/**
 * A class to configure navigation drawer menu items
 * @author Clark Stonehocker
 */
public class NavigationDrawerMenuItem implements NavigationDrawerItem 
{
	//value of type of the standard (menu) item
	public static final int ITEM_TYPE = 1 ;

	//standard (menu) items consist of an icon and a label (text)
    private int id ;
    private String label ;  
    private int icon ;
    private boolean updateActionBarTitle ;

    /**
     * Default constructor does nothing
     */
    private NavigationDrawerMenuItem() 
    {
    	
    }

    /**
     * Create a navigation drawer item
     * @param id the integer representation of the "row" view
     * @param label the text of the item
     * @param icon the icon to place to the left of the label text
     * @param updateActionBarTitle boolean value to tell if the Action Bar title should be changed or not
     * @param context the context of the drawer
     * @return a NavigationDrawerMenuItem object
     */
    public static NavigationDrawerMenuItem create( int id, String label, int icon, boolean updateActionBarTitle, Context context ) 
    {
    	//create a NavigationDrawerMenuItem object and set instance variables
        NavigationDrawerMenuItem item = new NavigationDrawerMenuItem();
        item.setId(id);
        item.setLabel(label);
        item.setIcon(icon);
        item.setUpdateActionBarTitle(updateActionBarTitle);
        return item;
    }
    
    @Override
    public int getType() 
    {
        return ITEM_TYPE;
    }

    /**
     * Get the id of the item
     * @return an integer value of the menu item
     */
    public int getId() 
    {
        return id;
    }

    /**
     * Set the id of the menu item
     * @param id the new id of the menu item
     */
    public void setId(int id) 
    {
        this.id = id;
    }

    /**
     * Get the text of the menu item
     * @return the string value of the menu item text
     */
    public String getLabel() 
    {
        return label;
    }

    /**
     * Set the text of the drawer menu item
     * @param label the new text for the item
     */
    public void setLabel(String label) 
    {
        this.label = label;
    }

    /**
     * Get the drawer item's icon
     * @return the integer value representing the icon
     */
    public int getIcon() 
    {
        return icon;
    }

    /**
     * Set the icon of the drawer item
     * @param icon the new icon to use for the drawer item
     */
    public void setIcon(int icon) 
    {
        this.icon = icon;
    }

    @Override
    public boolean isEnabled() 
    {
        return true;
    }

    @Override
    public boolean updateActionBarTitle() 
    {
        return this.updateActionBarTitle;
    }

    /**
     * Set if the Action Bar title should be updated
     * @param updateActionBarTitle boolean value indication if the Action Bar should be updated
     */
    public void setUpdateActionBarTitle(boolean updateActionBarTitle) 
    {
        this.updateActionBarTitle = updateActionBarTitle;
    }
}