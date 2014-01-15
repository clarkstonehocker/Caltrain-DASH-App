package com.seniorproject.caltraindashmobileapp;

import com.seniorproject.caltraindashmobileapp.R;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public abstract class AbstractNavigationDrawerActivity extends FragmentActivity
{
	private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private NavigationDrawerActivityConfiguration navConf ;
    protected abstract NavigationDrawerActivityConfiguration getNavDrawerConfiguration();
    protected abstract void onNavItemSelected( int id );
    
    @Override
    /**Precondition: Nothing is configured for this activity
     * Create the activity and its components.
     * @param savedInstanceState the Android bundle to use for the activity
     * Postcondition: The activity is created.
     */
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //get the navigation drawer configuration
        navConf = getNavDrawerConfiguration();
        //set the main layout
        setContentView(navConf.getMainLayout()); 
        //set the title for the drawer
        mTitle = mDrawerTitle = getTitle();
        
        //configure the drawer's components
        mDrawerLayout = (DrawerLayout) findViewById(navConf.getDrawerLayoutId());
        mDrawerList = (ListView) findViewById(navConf.getLeftDrawerId());
        mDrawerList.setAdapter(navConf.getBaseAdapter());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        this.initDrawerShadow();
        
        //set the action bar left-most icon to open the drawer when tapped
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getDrawerIcon(),
                navConf.getDrawerOpenDesc(), navConf.getDrawerCloseDesc() ) 
        	{
        	/**
        	 * Method to handle action when drawer is closed.
        	 */
            public void onDrawerClosed(View view) 
            {
            	//change the action bar title back to previous title
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }
            /**
             * Method to handle action when drawer is opened.
             */
            public void onDrawerOpened(View drawerView) 
            {
            	//change action bar title to that of activity chosen
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        //add listener to the drawer
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
    /**
     * Method to initialize the drawer shadow.
     */
    protected void initDrawerShadow() 
    {
        mDrawerLayout.setDrawerShadow(navConf.getDrawerShadow(), GravityCompat.START);
    }
    
    /**
     * Get the action bar icon for the drawer.
     * @return the integer value representing the icon drawable
     */
    protected int getDrawerIcon() 
    {
        return R.drawable.ic_drawer;
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) 
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    /**Precondition: the action bar menu should have some icons.
     * Prepare the action bar options menu.
     * @param menu
     * @return true if the drawer is open, false otherwise
     * Postcondition: action bar icons are hidden when drawer is opened
     */
    public boolean onPrepareOptionsMenu(Menu menu) 
    {
        if (navConf.getActionMenuItemsToHideWhenDrawerOpen() != null) 
        {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
            for( int iItem : navConf.getActionMenuItemsToHideWhenDrawerOpen()) 
            {
                menu.findItem(iItem).setVisible(!drawerOpen);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        if (mDrawerToggle.onOptionsItemSelected(item)) 
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    
    @Override
    /**
     * Method to enable the device's menu button to also open and close the navigation drawer.
     * @param keyCode the code of the key that was pressed
     * @param event event handler for key presses
     * @return true if the menu button was pressed, boolean value of parent activity otherwise
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) 
        {
            if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) 
            {
                this.mDrawerLayout.closeDrawer(this.mDrawerList);
            }
            else 
            {
                this.mDrawerLayout.openDrawer(this.mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * Get the layout for the drawer.
     * @return the drawer layout instance
     */
    protected DrawerLayout getDrawerLayout() 
    {
        return mDrawerLayout;
    }
    
    /**
     * Get the toggle for the drawer.
     * @return the drawer toggle instance
     */
    protected ActionBarDrawerToggle getDrawerToggle() 
    {
        return mDrawerToggle;
    }
   
    /**
     * Private listener class for the drawer.  The class implements the listener of ListView
     * @author Clark Stonehocker
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener 
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
        {
            selectItem(position);
        }
    }
    
    /**
     * Method to take action when an item of the drawer is selected
     * @param position the position in the Navigation drawer list that is selected
     */
    public void selectItem(int position) 
    {
        NavigationDrawerItem selectedItem = navConf.getNavItems()[position];
        this.onNavItemSelected(selectedItem.getId());
        mDrawerList.setItemChecked(position, true);
        
        if(selectedItem.updateActionBarTitle()) 
        {
            setTitle(selectedItem.getLabel());
        }
        //close the drawer when an item is selected
        if(this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) 
        {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
    
    @Override
    /**
     * Set the title when an item of the Action Bar is pressed.
     * @param title the title to set the Action Bar to
     */
    public void setTitle(CharSequence title) 
    {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

}
