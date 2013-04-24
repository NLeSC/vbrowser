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

package test;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserPlatform;
import nl.esciencecenter.vbrowser.vb2.ui.browser.ProxyBrowser;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.vbrowser.ui.dcm.proxy.DCMProxyFactory;

public class testDCMBrowser 
{

	public static void main(String args[])
	{
		try 
		{
			BrowserPlatform platform=BrowserPlatform.getInstance(); 
		    
			DCMProxyFactory fac = DCMProxyFactory.getDefault();  
		    platform.registerProxyFactory(fac);  
            
		    ProxyBrowser frame=(ProxyBrowser)platform.createBrowser(); 
		    
    		ProxyNode root = fac.openLocation(GlobalProperties.getGlobalUserHome()+"/data"); 
    		
			frame.setRoot(root,true); 
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		// frame.setRoot(root); 
		
	}
}
