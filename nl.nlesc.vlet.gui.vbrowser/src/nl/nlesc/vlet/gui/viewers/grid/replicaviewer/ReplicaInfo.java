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

package nl.nlesc.vlet.gui.viewers.grid.replicaviewer;

import nl.nlesc.vlet.data.VAttributeConstants;
import nl.nlesc.vlet.data.VAttributeSet;
import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.gui.viewers.grid.replicaviewer.ReplicaDataModel.ReplicaStatus;
import nl.nlesc.vlet.vrl.VRL;

public class ReplicaInfo
{
    private final VRL replicaVRL;
    
    private boolean error=false;
    
    private Exception exception=null;  
    
    private long length=-1; 
    
    // Might be null 
    private VRL transportURI=null;  
    
    private boolean exists=false; 
    
    public ReplicaInfo(VRL vrl)
    {
        this.replicaVRL=vrl; 
    }

    public VRL getVRL()
    {
        return replicaVRL; 
    }
    
    public String toString()
    {
        String str="";
        if (replicaVRL==null)
            if (exception==null)
                str="[NULL Replica VRL]";
            else
                str="[Exception:"+exception+"]"; 
        else
            str="<Replica>{"+replicaVRL.toString()+","+length+"}"; 
        return str;  
    }

    public String getHostname()
    {
       if (replicaVRL==null)
           return null; 
       
       return replicaVRL.getHostname(); 
    }
    
    public long getLength()
    {
        return this.length;
    }

    public boolean hasError()
    {
        return this.error; 
    }
    
    public boolean exists()
    {
        return this.exists; 
    }
    
    public void setExists(boolean val)
    {
        this.exists=val; 
    }
    
    public Exception getException()
    {
        return this.exception; 
    }
    
    public void setException(Exception e)
    {
        this.exception=e;  
    }

    public void setError(boolean val)
    {
        this.error=val; 
        
    }

    public void setLength(long val)
    {
        this.length=val;
        
    }

    public void setTransportURI(VRL vrl)
    {
        this.transportURI=vrl;  
    }

    public void setAttributes(VAttributeSet attrs)
    {
        // updat rep infos: 
        if (attrs.containsKey(VAttributeConstants.ATTR_LENGTH))
            setLength(attrs.getLongValue(VAttributeConstants.ATTR_LENGTH));
        
        if (attrs.containsKey(VAttributeConstants.ATTR_EXISTS))
            setExists(attrs.getBooleanValue(VAttributeConstants.ATTR_EXISTS,false));
     
        if (attrs.containsKey(VAttributeConstants.ATTR_STATUS))
        {
            String val=attrs.getStringValue(VAttributeConstants.ATTR_STATUS);
            if (val!=null)
                if (val.equals(ReplicaStatus.ERROR.toString()))
                    setError(true);
                else
                    setError(false); 
        }
     
        try
        {
            if (attrs.containsKey(VAttributeConstants.ATTR_TRANSPORT_URI))
                setTransportURI(attrs.getVRLValue(VAttributeConstants.ATTR_TRANSPORT_URI));
        }
        catch (VRLSyntaxException e)
        {
            e.printStackTrace();
        }
    }

}
