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

package nl.esciencecenter.vbrowser.vb2.ui.proxy;

import nl.esciencecenter.ptk.net.VRI;


public class ProxyNodeEvent
{
	public static enum ProxyNodeEventType
	{
		RESOURCES_ADDED,
		RESOURCES_DELETED,
		RESOURCES_RENAMED,
		ATTRIBUTES_CHANGED, 
		REFRESH_RESOURCES
	}
    
    // ========================================================================
    // 
    // ========================================================================
	
    public static ProxyNodeEvent createChildsAddedEvent(VRI optionalParent,VRI childs[])
    {
        ProxyNodeEvent event=new ProxyNodeEvent(ProxyNodeEventType.RESOURCES_ADDED);
        event.parent=optionalParent; 
        event.resources=childs; 
        return event; 
    }
    
    public static ProxyNodeEvent createChildAddedEvent(VRI parent,VRI child)
    {
        VRI childs[]=new VRI[1]; 
        childs[0]=child; 
        return createChildsAddedEvent(parent,childs);
    }
	
	public static ProxyNodeEvent createChildDeletedEvent(VRI optionalParent,VRI childs[])
    {
        ProxyNodeEvent event=new ProxyNodeEvent(ProxyNodeEventType.RESOURCES_DELETED);
        event.parent=optionalParent; 
        event.resources=childs; 
        return event; 
    }
	
	public static ProxyNodeEvent createChildDeletedEvent(VRI optionalParent,VRI child)
    {
        ProxyNodeEvent event=new ProxyNodeEvent(ProxyNodeEventType.RESOURCES_DELETED);
        event.parent=optionalParent; 
        event.resources=new VRI[1];
        event.resources[0]=child; 
        return event; 
    }
    
    public static ProxyNodeEvent createRefreshEvent(VRI optionalParent, VRI res)
    {
        ProxyNodeEvent event=new ProxyNodeEvent(ProxyNodeEventType.REFRESH_RESOURCES);
        event.parent=optionalParent; 
        event.resources=new VRI[1];
        event.resources[0]=res; 
        return event; 
    }
    

	// ========================================================================
    // 
    // ========================================================================
	
	protected ProxyNodeEventType type; 
	
	/** Optional parent resource. */  
	protected VRI parent; 
	
	/** Sources this event applies to */ 
	protected VRI[] resources; 
	
	/** Optional attribute names involved */ 
	protected String attributeNames[]; 
	
	protected ProxyNodeEvent(ProxyNodeEventType type)
	{
	    this.type=type; 
	}
	
	public ProxyNodeEventType getType()
	{
		return this.type; 
	}
	
	/** Resources this event applies to. */ 
	public VRI[] getResources()
	{
		return this.resources;
	}

    /** 
     * If the parent resource has been specified, it is the parent
     * of all the resource from getResources() 
     */  
	public VRI getParent()
	{
		return parent; 
	}
	
	/** Attributes this event applies to if this is an Attribute Event */ 
	public String[] getAttributeNames()
	{
		return this.attributeNames; 
	}

	public String toString()
	{
	    return "DataSourceEvent:"+this.type+":(parent="+parent+", resources={"+flattenStr(resources)+"})"; 
	}

    private String flattenStr(VRI[] locs)
    {   
        if (locs==null)
            return "";
        
        String str="";
        for (int i=0;i<locs.length;i++)
        {
            str+=locs[i];
            if (i+1<locs.length)
                str+=","; 
        }
        
        return str; 
    }


}
