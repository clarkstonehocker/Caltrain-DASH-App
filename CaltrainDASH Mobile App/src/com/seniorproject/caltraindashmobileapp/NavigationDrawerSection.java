package com.seniorproject.caltraindashmobileapp;

/**
 * Class to initialize a section header of the navigation drawer
 * @author Clark Stonehocker
 */
public class NavigationDrawerSection implements NavigationDrawerItem 
{
	//set what type of drawer item this is
	public static final int SECTION_TYPE = 0;
	
	//Section headings only consist of ids and text
    private int id;
    private String label;

    /**
     * Default constructor does nothing
     */
    private NavigationDrawerSection() 
    {
    	
    }
    
    /**
     * Create a new section heading
     * @param id an integer value of the item's id
     * @param label the text to use for the section heading
     * @return the created NavigationDrawerSection object
     */
    public static NavigationDrawerSection create( int id, String label ) 
    {
        NavigationDrawerSection section = new NavigationDrawerSection();
        section.setLabel(label);
        return section;
    }
    
    @Override
    public int getType() 
    {
        return SECTION_TYPE;
    }

    /**
     * Get the text of the section heading
     * @return the section heading text
     */
    public String getLabel() 
    {
        return label;
    }

    /**
     * Set the text of the section heading
     * @param label the new text for the section heading
     */
    public void setLabel(String label) 
    {
        this.label = label;
    }

    @Override
    public boolean isEnabled() 
    {
        return false;
    }

    /**
     * Get the id of the section heading
     * @return the integer value of the section heading id
     */
    public int getId() 
    {
        return id;
    }

    /**
     * Set the id of the section heading
     * @param id the new section heading id
     */
    public void setId(int id) 
    {
        this.id = id;
    }

    @Override
    public boolean updateActionBarTitle() 
    {
        return false;
    }

}