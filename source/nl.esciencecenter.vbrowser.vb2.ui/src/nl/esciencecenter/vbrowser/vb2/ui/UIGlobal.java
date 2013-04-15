/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.esciencecenter.vbrowser.vb2.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nl.esciencecenter.ptk.ui.icons.IconProvider;
import nl.esciencecenter.ptk.ui.util.UIResourceLoader;
import nl.esciencecenter.ptk.util.logging.ClassLogger;


public class UIGlobal
{
    private static GuiSettings guiSettings; 
    private static ClassLogger uiLogger;
    private static UIResourceLoader resourceLoader; 
    private static JFrame rootFrame;
    private static IconProvider iconProvider;
    
    static
    {
    	uiLogger=ClassLogger.getLogger(UIGlobal.class); 
        uiLogger.debugPrintf(">>> UIGlobal.init() <<<\n"); 
        
        try
        {
            guiSettings=new GuiSettings();
            uiLogger=ClassLogger.getLogger("UIGlobal"); 
            resourceLoader=UIResourceLoader.getDefault();
            rootFrame=new JFrame(); 
            iconProvider=new IconProvider(rootFrame,resourceLoader); 
        }
        catch (Exception e)
        {
            uiLogger.logException(ClassLogger.FATAL,e,"Exception during initialization!"); 
        }
    }
    
    public static void init()
    {
    }
   

    public static GuiSettings getGuiSettings()
    {
        return guiSettings; 
    }

    public static IconProvider getIconProvider()
    {
        return iconProvider; 
    }  

    public static void assertNotGuiThread(String msg) throws Error
	{
		assertGuiThread(false,msg);
	}
	
	public static void assertGuiThread(String msg) throws Error
	{
		assertGuiThread(true,msg); 
	}

	public static void assertGuiThread(boolean mustBeGuiThread,String msg) throws Error
	{
        // still happens when trying to read/acces link targets of linknodes 
        if (mustBeGuiThread!=UIGlobal.isGuiThread())
        {
            uiLogger.infoPrintf("\n>>>\n    *** Swing GUI Event Assertion Error *** !!!\n>>>\n");
            throw new Error("Internal Error. Cannot perform this "
            						+(mustBeGuiThread?"during":"outside")+"during the Swing GUI Event thread.\n"+msg);
        }
	}
    
	public static void swingInvokeLater(Runnable task)
	{
		SwingUtilities.invokeLater(task); 
	}
	
	public static boolean isGuiThread()
	{
		 return (SwingUtilities.isEventDispatchThread()==true);
	}

	public static boolean isApplet() 
	{
		return false;
	}
}

