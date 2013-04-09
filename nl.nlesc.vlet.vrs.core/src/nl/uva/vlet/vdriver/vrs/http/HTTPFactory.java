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

package nl.uva.vlet.vdriver.vrs.http;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.net.ssl.SslUtil;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VResourceSystem;

public class HTTPFactory extends VRSFactory
{
	// static code ! 
	
	static
	{
	    // HTTPS/SSL verifier
	    SslUtil.init(); // initSslHostnameVerifier(); 
	    //SslUtil.setMySslValidation();
	    //SslUtil.setNoSslValidation();
	}
	
    String schemes[]={VRS.HTTP_SCHEME,VRS.HTTPS_SCHEME,"httpg"}; 

    @Override
    public String getName()
    {
        return "HTTP";
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemes; 
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public void clear()
    {
    }

	@Override
	public VResourceSystem createNewResourceSystem(VRSContext context, ServerInfo info,VRL location)
			throws VlException 
	{
		return HTTPRS.getClientFor(context,info,location); 
	}

	public String getVersion()
    {
	    return super.getVersion();   
    }
	
	public String getAbout()
	{
	    return super.getAbout();   
    }

}