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

package nl.nlesc.glite.lbl.srm.status;

import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;

/**
 * Interface of RequestStatusOfRequest. An abstract representation of an SRM
 * Request Status of a Request. Created as an interface for all srm Requests
 * Status of Requests. For example in asynchronous requests (put, get,copy), the
 * client can request the status of the request (hence the confusing names) i.e.
 * processing aborted, etc.
 * 
 * @author S. Koulouzis
 * 
 */
public interface ISRMRequestStatusOfRequest
{
    /**
     * Gets the request token for this request. It's a string uniquely
     * identifying each request.
     * 
     * @return the request token.
     */
    public String getRequestToken();

    /**
     * Gets an array of SRM URIs. It's the set of files that the request
     * (put,get,copy) is made for.
     * 
     * @return the SRM URIs
     */
    public URI[] getSURIs();

    /**
     * Gets the status of the request. i.e. in queue, success, etc.
     * 
     * @return the request status.
     */
    public TReturnStatus getReturnStatus();

}
