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

package nl.nlesc.vlet.bootstrap;


/** 
 * Wrapper class to bootstrap the VBrowser. 
 * The main method calls the BootStrap class 
 * with the configuration needed to start the VBrowser. 
 * Use this class as the main class in the manifest to create a 
 * self executing jar to start the VBrowser. 
 * Only the Bootstrapper class has to be present in the jar file. 
 * The rest is added automagically. 
 * 
 * @author Piter T. de Boer/Piter.NL
 */
public class startVBrowser 
{
	public static void main(String[] args) 
    {
		Bootstrapper boot=new Bootstrapper();
		
		try 
		{
            boot.launch("nl.nlesc.vlet.gui.startVBrowser",args);
		}
		catch (Exception e) 
		{
			  System.out.println("***Error: Exception:" + e);
	          e.printStackTrace();
		} 
		
    }
	
}