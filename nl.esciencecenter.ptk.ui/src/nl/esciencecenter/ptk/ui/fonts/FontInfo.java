/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui.fonts;


import java.awt.Color;
import java.awt.Font;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JComponent;

import nl.esciencecenter.ptk.util.logging.ClassLogger;


/** 
 * Simple Font Information holder class. 
 *  
 * FontInfo is used by the FontToolbar.   
 * Use createFont() to instantiate a new Font object using the specified Font information.
 * 
 * @author P.T. de Boer
 */
public class FontInfo
{
	private static ClassLogger logger; 
	
	static
	{
		logger=ClassLogger.getLogger(FontInfo.class); 
	}

    //  ========================================================================
    //  Class Constants: 
    //  ========================================================================
    
	/** Custom name */
    public static final String FONT_ALIAS="fontAlias";
    
    /** Most specific font type. might be equal to "font family" or more specific. */ 
    public static final String FONT_TYPE="fontType";
    
    /** Less specific font "Family" */ 
    public static final String FONT_FAMILY="fontFamily";
    
    /** Italic,Bold,etc. */ 
    public static final String FONT_STYLE="fontStyle";
    
    public static final String FONT_SIZE="fontSize";
    
    public static final String FONT_RENDERING_HINTS="fontRenderingHints";
    
    public static final String fontPropertyNames[]=
        {
            FONT_ALIAS,
            FONT_FAMILY,
            FONT_STYLE,
            FONT_SIZE,
            FONT_FAMILY
        };
    
    // some default font types: 
    public static final String FONT_ICON_LABEL   = "iconlabel"; 
    public static final String FONT_MONO_SPACED  = "monospaced"; 
    public static final String FONT_DIALOG       = "dialog";
    public static final String FONT_TERMINAL     = "terminal";
    
    // enable to auto-store create fonts in ~/.vletrc/fonts/<font-alias>.prop
    
    private static boolean autosave = false;
    
    /** Font Style database */ 
    static Hashtable<String,FontInfo> fontStyles= null;  

    //  ========================================================================
    //  Info
    //  ========================================================================
    
    /** Whether to store FontInfo in persistant Font DataBase */ 
    public static void setGlobalAutoSave(boolean value)
    {
        autosave=value; 
    }

    //  ========================================================================
    //  Info
    //  ========================================================================

    /** Font Type or Font Family Name */ 
    String fontFamily="Monospaced";
    
    /** Optional Alias for the GUI (dialog,terminal,label) */ 
    String fontAlias=null; 
    
    /** Font Size */ 
    Integer fontSize=13; 
    
    /**
     * Font Style, 0=non, 0x01=bold,0x02=italic, etc. 
     * @see Font*/
    Integer fontStyle=0;
    
    /** Optional Foreground color, can be NULL (= use default) */ 
    Color foreground=Color.BLACK;
       
    /** Optional Background color, can be NULL (= use default) */ 
    Color background=Color.WHITE;
    
    /** Optional Highlighted foreground color, can be NULL (= use default) */ 
    Color highlightedForeground=new Color(64,64,240);

    /** For hierarchical Fonts: currently not used */ 
    FontInfo parent=null; 
 
    public FontInfo(Properties props)
    {
        this.setFontProperties(props);
        // backward compatibility: add alias name 
        if (fontAlias==null)
           fontAlias=fontFamily; 
    }

    public FontInfo()
    {
    }

    public FontInfo(Font font) 
    {
        init(font);  
    }
    
    void init(Font font)
    {
        fontSize=font.getSize(); 
        fontStyle=font.getStyle(); 
        fontFamily=font.getFamily();
        // alias default to fontName
        fontAlias=fontFamily;
    }

    /**
     * @return Returns the fontSize.
     */
    public int getFontSize()
    {
        return fontSize;
    }

    /**
     * @param fontSize The fontSize to set.
     */
    public void setFontSize(int size)
    {
        //System.err.println("FontInfo.setFontSize="+size);
        this.fontSize = size;
    }

    /**
     * @return Returns the fontStyle.
     */
    public int getFontStyle()
    {
        return fontStyle;
    }

    /**
     * @param fontStyle The fontStyle to set.
     */
    public void setFontStyle(int fontStyle)
    {
        this.fontStyle = fontStyle;
    }

    /**
     * @return Returns the font family, for example "Monospaced"  or "Arial"
     *  
     */
    public String getFontFamily()
    {
        return fontFamily;
    }
  
    
    /**
     * @param Set Font Family name. For example "Monospaced" or "Arial". 
     */
    public void setFontFamily(String family)
    {
        this.fontFamily = family;
    }

    /** @deprecated use setFontFamily */
    public void setType(String family)
    {
        this.fontFamily = family;
    }
    
    public Font createFont()
    {
        return new Font(fontFamily,fontStyle,fontSize);
    }
    
    
    public boolean isBold()
    {
        return (fontStyle&Font.BOLD)==Font.BOLD; 
    }

    public boolean isItalic()
    {
        return (fontStyle&Font.ITALIC)==Font.ITALIC; 
    }

    public void setBold(boolean val)
    {
        fontStyle=setFlag(fontStyle,Font.BOLD,val); 
    }

    public void setItalic(boolean val)
    {
        fontStyle=setFlag(fontStyle,Font.ITALIC,val); 
        //System.err.println("fontStyle="+fontStyle);
    }
    
    private int setFlag(int orgvalue, int flag, boolean val)
    { 
        if (val==true) 
            orgvalue=orgvalue|flag;  
        else if ((orgvalue & flag) == flag)
            orgvalue-=flag;
        // else val=false and flag not set in the first place
        return orgvalue; 
       // System.err.println("fontstyle="+fontStyle);
    }
    
    // return font properties as property set
    public Properties getFontProperties()
    {
        Properties props=new Properties();
        
        if (fontAlias==null) 
        	fontAlias=fontFamily; 
        
        props.put(FONT_ALIAS,fontAlias); 
        props.put(FONT_FAMILY,fontFamily); 
        props.put(FONT_SIZE,new Integer(fontSize).toString()); 
        props.put(FONT_STYLE,new Integer(fontStyle).toString());
        
        return props; 
    }
    
    /** Uses FONT properties and updates info */ 
    public void setFontProperties(Properties props)
    {
         String valstr=null; 
         
         valstr=(String)props.get(FONT_ALIAS);
         
         if (valstr!=null) 
             this.fontAlias=valstr;
         
         // Old Type name => renamed to FAMILY 
         valstr=(String)props.get(FONT_TYPE);
         
         if (valstr!=null) 
             setFontFamily(valstr); 
         
         // new Correct 'family' i.s.o. generic 'type' 
         valstr=(String)props.get(FONT_FAMILY);
         
         if (valstr!=null) 
             setFontFamily(valstr); 
         
         valstr=(String)props.get(FONT_SIZE);
         
         if (valstr!=null)
              setFontSize(Integer.valueOf(valstr));  
         
         valstr=(String)props.get(FONT_STYLE);
         
         if (valstr!=null) 
             setFontStyle(Integer.valueOf(valstr)); 
         
         valstr=(String)props.get(FONT_RENDERING_HINTS);
    }

    private void store()
    {
        fontStyles.put(this.fontAlias,this);
        
        if (autosave==true) 
        {
//            try
//            {
//                saveFontStyles();
//            }
//            catch (IOException e)
//            {
//            	logger.warnPrintf("Could save default font info.\n"); 
//            	//e.printStackTrace();
//            }    
        }
    }
    
    
    /** For selected text/icon label text */ 
    
    public Color getHighlightedForeground()
    {
        return this.highlightedForeground; 
    }

    public Color getBackground()
    {
        return this.background; 
    }
    
    public Color getForeground()
    {
        return this.foreground; 
    }
    
    // tbd
    public Object getRenderingHints()
    {
    	return null; 
    }
    
    // ==============================================
    // Static FontInfo Factory 
    // ==============================================
    
    /** Return Aliased Font Style */ 
    
    public static FontInfo getFontInfo(String name)
    {
        // autoinit 
        
        if (fontStyles==null) 
        {
//            try
//            {
//                loadFontStyles();
//            }
//            catch (IOException e)
//            {
//                logger.warnPrintf("Could load default fonts\n"); 
//            }
            
            if (fontStyles==null) 
                fontStyles=new Hashtable<String,FontInfo>();
        }
        
        FontInfo info=fontStyles.get(name);
        
        if (info!=null) 
            return info; 
        
        
        // current hardcoded ones: 
        if (name.compareToIgnoreCase(FONT_ICON_LABEL)==0)
        {
                Font font=new Font("dialog",0,11);
                return store(font,FONT_ICON_LABEL);
                 
        }
        else if (name.compareToIgnoreCase(FONT_DIALOG)==0)
        {
                Font font=new Font("dialog",0,11);
                return store(font,FONT_DIALOG);
                 
        }
        else if (name.compareToIgnoreCase(FONT_MONO_SPACED)==0)
        {
                Font font=new Font("monospaced",0,12);
                return store(font,FONT_MONO_SPACED); 
        }
        else if (name.compareToIgnoreCase(FONT_TERMINAL)==0)
        {
                Font font=new Font("monospaced",0,12);
                return store(font,FONT_MONO_SPACED); 
        }
        return null; 
    }
    
    // Store FontInfo under (new) alias 
    private static FontInfo store(Font font, String alias)
    {
        FontInfo info=new FontInfo(font); 
        info.fontAlias=alias; 
        info.store();
        
        return info;
    }

    /** Store FontInfo */ 
    public static void store(FontInfo info) 
    {
        info.store();  
    }

    /** Update font settings of specified Component with this font */ 
    public void updateComponentFont(JComponent jcomp)
    {
        jcomp.setFont(createFont());
    }

//    private void saveFontStyles() throws IOException
//    {
//        VRL loc=getUserFontSettingsLocation();
//        
//        //String filepath=loc.getPath();
//        
//        for (Enumeration<String> keys =fontStyles.keys();keys.hasMoreElements();) 
//        {
//            String name=(String)keys.nextElement();
//            
//            FontInfo info=fontStyles.get(name); 
//            
//            //Properties fontProps=fontStyles.get(name)filepath
//            Properties fontProps=info.getFontProperties(); 
//            
//            VRL fontLoc=loc.duplicateWithAddedPath(name+".prop");
//            
//            Global.errorPrintf(this,"Saving font:'%s' to file:%s\n",name,fontLoc);
//            
//            UIGlobal.saveProperties(fontLoc,fontProps);
//        }
//    }
    
//    private static void loadFontStyles() throws IOException
//    {
//        VRL systemLoc=getSystemFontSettingsLocation();
//        loadFontStylesFrom(systemLoc); 
//        
//        VRL userLoc=getUserFontSettingsLocation();
//        loadFontStylesFrom(userLoc);
//        
//    }
//    
//    private static void loadFontStylesFrom(VRL fontLoc) throws IOException
//    {
//        if (fontStyles==null) 
//            fontStyles=new Hashtable<String,FontInfo>();
//
//
//        Global.debugPrintf(FontInfo.class,"Loading fonts from:%s\n",fontLoc); 
//
//            
//        VFSClient vfs=new VFSClient();
//        
//        if (vfs.existsDir(fontLoc)==false)
//        {
//            // auto create location 
//            // vfs.mkdir(fontLoc);
//            return; // no fonts to load; 
//        }
//        
//        VDir dir=vfs.getDir(fontLoc); 
//        VFSNode nodes[] = dir.list(); 
//        
//        // scan font directory
//        for (VFSNode node:nodes)
//        {
//            //String name=node.getLocation().getBasename(false); // strip extension;
//            
//            // load font properties. 
//            Properties fontProps=UIGlobal.loadProperties(node.getLocation()); 
//            
//            FontInfo info=new FontInfo(fontProps);
//            fontStyles.put(info.getAlias(),info); 
//        }
//    }
//
//    private static VRL getUserFontSettingsLocation()
//    {
//        VRL loc=Global.getUserConfigDir(); 
//        
//        loc=loc.duplicateWithAddedPath("fonts");
//        return loc;
//    }
    
//    private static VRL getSystemFontSettingsLocation()
//    {
//        VRL loc=Global.getInstallationConfigDir(); 
//        
//        loc=loc.duplicateWithAddedPath("fonts");
//        return loc;
//    }
   
}