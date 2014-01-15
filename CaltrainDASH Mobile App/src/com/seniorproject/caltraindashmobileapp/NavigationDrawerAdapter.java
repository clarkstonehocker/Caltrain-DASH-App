package com.seniorproject.caltraindashmobileapp;

import com.seniorproject.caltraindashmobileapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A custom adapter for the app's navigation drawer.
 * @author Clark Stonehocker
 */
public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem>
{
	private LayoutInflater inflater; //used to generate layouts
    
	/**
	 * Use the superclass to build adapter.
	 * @param context the context of the adapter
	 * @param textViewResourceId the view of concern
	 * @param objects the list of objects to add to the ListView
	 */
    public NavigationDrawerAdapter(Context context, int textViewResourceId, NavigationDrawerItem[] objects ) 
    {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    /**
     * Get the right type of view for the item in question.
     * @param position the position of the item in the ListView
     * @param convertView the view to convert
     * @param parent the ListView to which the item belongs
     * @return the View (whether standard menu item or section (header))
     */
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        View view = null ;
        NavigationDrawerItem menuItem = this.getItem(position);
        
        //if item is a menu type item, get the item view, otherwise get the section (heading) view
        if ( menuItem.getType() == NavigationDrawerMenuItem.ITEM_TYPE ) 
        {
            view = getItemView(convertView, parent, menuItem );
        }
        else 
        {
            view = getSectionView(convertView, parent, menuItem);
        }
        return view ;
    }
    
    /**
     * Build and return the view of a navigation drawer menu item.
     * @param convertView the view to convert
     * @param parentView the ListView to which the element belongs
     * @param navDrawerItem the navigation drawer item
     * @return the constructed view
     */
    public View getItemView( View convertView, ViewGroup parentView, NavigationDrawerItem navDrawerItem ) 
    {
        NavigationDrawerMenuItem menuItem = (NavigationDrawerMenuItem) navDrawerItem;
        NavMenuItemHolder navMenuItemHolder = null;
        
        //build the view if it's null (not already constructed)
        if (convertView == null) 
        {
            convertView = inflater.inflate( R.layout.navigation_drawer_menu_item, parentView, false);
            TextView labelView = (TextView) convertView.findViewById( R.id.navmenuitem_label );
            ImageView iconView = (ImageView) convertView.findViewById( R.id.navmenuitem_icon );

            navMenuItemHolder = new NavMenuItemHolder();
            navMenuItemHolder.labelView = labelView;
            navMenuItemHolder.iconView = iconView;

            convertView.setTag(navMenuItemHolder);
        }

        if ( navMenuItemHolder == null ) 
        {
            navMenuItemHolder = (NavMenuItemHolder) convertView.getTag();
        }
                    
        navMenuItemHolder.labelView.setText(menuItem.getLabel());
        navMenuItemHolder.iconView.setImageResource(menuItem.getIcon()); //setImageResource(menuItem.getIcon());
        
        return convertView ;
    }
    
    /**
     * Build and return a section view of the navigation drawer.
     * @param convertView the View to build and return
     * @param parentView the ListView to which the View belongs
     * @param navDrawerItem the item upon which to pattern the view
     * @return the constructed View
     */
    public View getSectionView(View convertView, ViewGroup parentView, NavigationDrawerItem navDrawerItem) 
    {
        NavigationDrawerSection menuSection = (NavigationDrawerSection) navDrawerItem;
        NavMenuSectionHolder navMenuItemHolder = null;
        
        //build section view if not already constructed
        if (convertView == null) 
        {
            convertView = inflater.inflate( R.layout.navigation_drawer_section, parentView, false);
            TextView labelView = (TextView) convertView.findViewById( R.id.navmenusection_label );
            navMenuItemHolder = new NavMenuSectionHolder();
            navMenuItemHolder.labelView = labelView ;
            convertView.setTag(navMenuItemHolder);
        }
        if ( navMenuItemHolder == null ) 
        {
            navMenuItemHolder = (NavMenuSectionHolder) convertView.getTag();
        }           
        navMenuItemHolder.labelView.setText(menuSection.getLabel());
               
        return convertView ;
    }
    
    @Override
    /**
     * Get the number of different types of navigation drawer items.
     * @return the number of different types of items.
     */
    public int getViewTypeCount() 
    {
        return 2;
    }
    
    @Override
    /**
     * Get the type of an item at a given index
     * @param position the index 
     * @return the type of the item at the passed position
     */
    public int getItemViewType(int position) 
    {
        return this.getItem(position).getType();
    }
    
    @Override
    /**
     * Check if a navigation drawer item is enabled (if it can be clicked).
     * @param position the position (from top to bottom) of the item in the drawer
     * @return true if the item can be clicked, false otherwise
     */
    public boolean isEnabled(int position) 
    {
        return getItem(position).isEnabled();
    }
    
    /**
     * A helper class to hold navigation drawer menu items.
     * @author Clark Stonehocker
     */
    private static class NavMenuItemHolder 
    {
    	//each menu item is composed of a text field and an icon
        private TextView labelView;
        private ImageView iconView;
    }
    
    /**
     * A helper class to hold navigation drawer section (header) items.
     * @author Clark Stonehocker
     *
     */
    private class NavMenuSectionHolder 
    {
    	//each section item contains only a text field, no icon
        private TextView labelView;
    }

}