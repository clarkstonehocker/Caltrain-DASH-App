package com.seniorproject.caltraindashmobileapp;

import android.widget.BaseAdapter;

/**
 * Class to configure the Navigation Drawer
 * @author Clark Stonehocker
 */
public class NavigationDrawerActivityConfiguration 
{
	private int mainLayout;
    private int drawerShadow;
    private int drawerLayoutId;
    private int leftDrawerId;
    private int[] actionMenuItemsToHideWhenDrawerOpen;
    private NavigationDrawerItem[] navItems;
    private int drawerOpenDesc;
    private int drawerCloseDesc;
    private BaseAdapter baseAdapter;
    
    /**
     * Get the main layout id
     * @return an integer value of the layout id
     */
    public int getMainLayout() 
    {
    	return mainLayout;
    }
    
    /**
     * Set the value of the main layout
     * @param mainLayout the new id of the layout
     */
    public void setMainLayout(int mainLayout) 
    {
        this.mainLayout = mainLayout;
    }

    /**
     * Get the id of the drawer shadow
     * @return an integer value of the drawer shadow's id
     */
    public int getDrawerShadow() 
    {
        return drawerShadow;
    }

    /**
     * Set the value of the drawer shadow id
     * @param drawerShadow the new drawer shadow id
     */
    public void setDrawerShadow(int drawerShadow) 
    {
        this.drawerShadow = drawerShadow;
    }

    /**
     * Get the id of the drawer layout
     * @return the drawer layout id (an integer value)
     */
    public int getDrawerLayoutId() 
    {
        return drawerLayoutId;
    }

    /**
     * Set the id of the drawer layout
     * @param drawerLayoutId the new id of the drawer layout
     */
    public void setDrawerLayoutId(int drawerLayoutId) 
    {
        this.drawerLayoutId = drawerLayoutId;
    }

    /**
     * Get the id of the left drawer
     * @return an integer value of the left drawer id
     */
    public int getLeftDrawerId() 
    {
        return leftDrawerId;
    }

    /**
     * Set the id of the left drawer
     * @param leftDrawerId the new value for the left drawer id (an integer)
     */
    public void setLeftDrawerId(int leftDrawerId) 
    {
        this.leftDrawerId = leftDrawerId;
    }

    /**
     * Get the ids of the Action Bar icons to hide when drawer is opened
     * @return an array of the ids of Action Bar icons to hide
     */
    public int[] getActionMenuItemsToHideWhenDrawerOpen() 
    {
        return actionMenuItemsToHideWhenDrawerOpen;
    }

    /**
     * Set the list of Action Bar icons to hide when drawer is open
     * @param actionMenuItemsToHideWhenDrawerOpen an array of integers containing Action Bar icon ids
     */
    public void setActionMenuItemsToHideWhenDrawerOpen(int[] actionMenuItemsToHideWhenDrawerOpen) 
    {
        this.actionMenuItemsToHideWhenDrawerOpen = actionMenuItemsToHideWhenDrawerOpen;
    }

    /**
     * Get the navigation drawer items.
     * @return an array of type NavigationDrawerItem (items composed of icons and text)
     */
    public NavigationDrawerItem[] getNavItems() 
    {
        return navItems;
    }

    /**
     * Set the list of navigation drawer items    
     * @param navItems an array of type NavigationDrawerItem containing items the drawer
     * is to be composed of
     */
    public void setNavItems(NavigationDrawerItem[] navItems) 
    {
        this.navItems = navItems;
    }

    /**
     * Get the description of the drawer when open
     * @return an integer value representing the open drawer description
     */
    public int getDrawerOpenDesc() 
    {
        return drawerOpenDesc;
    }

    /**
     * Set the description of the drawer when opened
     * @param drawerOpenDesc the new description
     */
    public void setDrawerOpenDesc(int drawerOpenDesc) 
    {
        this.drawerOpenDesc = drawerOpenDesc;
    }

    /**
     * Get the description of the drawer when closed
     * @return an integer value of the closed door description
     */
    public int getDrawerCloseDesc() 
    {
        return drawerCloseDesc;
    }

    /**
     * Set the description of the drawer when closed
     * @param drawerCloseDesc the new description of the closed drawer
     */
    public void setDrawerCloseDesc(int drawerCloseDesc) {
        this.drawerCloseDesc = drawerCloseDesc;
    }

    /**
     * Get the base type of adapter used by the drawer
     * @return the BaseAdapter instance
     */
    public BaseAdapter getBaseAdapter() 
    {
        return baseAdapter;
    }

    /**
     * Set the base adapter for the drawer
     * @param baseAdapter the new adapter to use for the drawer
     */
    public void setBaseAdapter(BaseAdapter baseAdapter) 
    {
        this.baseAdapter = baseAdapter;
    }

}