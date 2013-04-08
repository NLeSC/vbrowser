package nl.vbrowser.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.net.URI;

import nl.vbrowser.ui.data.ConfigProperties;

public class GuiSettings
{
    // ========================================================================
    // Constans 
	// ========================================================================
    
	public static final String MOUSE_SELECTION_BUTTON = "ui.mouse_selection_button";

	public static final String SINGLE_CLICK_ACTION = "ui.single_click_action"; 

	public static String MOUSE_ALT_BUTTON="ui.mouse_alt_button"; 

	public static String MOUSE_POPUP_BUTTON="ui.popup_button"; 

    // ========================================================================
    // Public fields 
    // ========================================================================
    	
	
	private ConfigProperties properties=new ConfigProperties(); 
	
	private Color label_selected_bg_color=Color.darkGray;

	private Color label_selected_fg_color=Color.black; 

	private int  mouse_selection_button=MouseEvent.BUTTON1;

	private int  mouse_action_button=MouseEvent.BUTTON1;

	private int  mouse_alt_button=MouseEvent.BUTTON3;

	private boolean single_click_action=false;

	private int  mouse_popup_button=mouse_alt_button;


    public URI getUserIconsDir()
    {
        return null;
    }

    public URI getInstallationIconsDir()
    {
        return null;
    } 
    
   
    /** This method exists because the e.isPopupTrigger() doesn't work under windows */ 
    public boolean isPopupTrigger(MouseEvent e)
    {
        if (e.isPopupTrigger())
            return true;
       
        if  (e.getButton()==getMousePopupButton()) 
             
            return true;
        
        return false; 
    }
    
    public int getMousePopupButton()
    {
        return properties.getIntProperty(MOUSE_POPUP_BUTTON,mouse_popup_button);
    }
    
    public int getMouseAltButton()
    {
        return properties.getIntProperty(MOUSE_ALT_BUTTON,mouse_alt_button);
    }
    
    public int getMouseSelectionButton()
    {
        return properties.getIntProperty(MOUSE_SELECTION_BUTTON,mouse_selection_button);
    }
    
    public int getMouseActionButton()
    {
		return properties.getIntProperty(MOUSE_SELECTION_BUTTON,mouse_action_button);
    }

	public boolean getSingleClickAction()
    {
        return properties.getBoolProperty(SINGLE_CLICK_ACTION,single_click_action);
    }
    
    /**
     * Wrapper to detection 'Action Events' since the PLAF
     * way to detect event doesn't always work. 
     * Typically this is a single mouse click or a double mouse click. 
     * 
     * @param e
     * @return
     */
    public boolean isAction(MouseEvent e)
    {   
        int mask = e.getModifiersEx();
        
        if  ((mask & MouseEvent.CTRL_DOWN_MASK)>0)
        {
            //CONTROL DOWN, not an action, but a selection !
            return false; 
        }
        
        if  ((mask & MouseEvent.SHIFT_DOWN_MASK)>0)
        {
            //SHIFT DOWN, not an action, but a selection !
            return false; 
        }
        
        if (e.getButton()!=getMouseActionButton())
            return false; 
        
        if (getSingleClickAction() && (e.getClickCount()==1)) 
               return true;
        
        if ((getSingleClickAction()==false) && (e.getClickCount()==2)) 
            return true;
        
        return false; 
    }

    public boolean isSelection(MouseEvent e)
    {
        if (e.getButton()==getMouseSelectionButton())
        {
            if (getSingleClickAction()==false && (e.getClickCount()==1)) 
                return true; 
         
            if ((getSingleClickAction()) && (e.getClickCount()==2)) 
                return true;
        }
        
        return false; 
    }

    public Color getSelectedBGColor()
    {
    	return this.label_selected_bg_color; 
    }
}
