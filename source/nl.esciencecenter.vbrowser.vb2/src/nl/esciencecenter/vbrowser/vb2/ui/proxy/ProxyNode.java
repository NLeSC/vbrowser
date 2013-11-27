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

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.ui.icons.IconProvider;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserPlatform;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeDnDHandler;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeNames;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * ProxyNode is abstract interface to Resource Nodes. It represents a "Proxy" of the actual viewed Node. 
 */ 
public abstract class ProxyNode
{
    private static int idCounter=0; 
    
    private static ClassLogger logger; 
    {
    	logger=ClassLogger.getLogger(ProxyNode.class); 
    }

    // ========================================================================
    // helpers
    // ========================================================================

    // Null pointer safe toArray() method. 
    public static ProxyNode[] toArray(List<? extends ProxyNode> nodes)
    {
        if (nodes==null)
            return null; 
        
        return nodes.toArray(new ProxyNode[0]); 
    } 
    
    public static List<? extends ProxyNode> toList(ProxyNode[] nodes)
    {
        ArrayList<ProxyNode> list=new ArrayList<ProxyNode>(nodes.length); 
        for (int i=0;i<nodes.length;i++)
            list.add(nodes[i]); 
            
        return list;  
    }
    
     // Get subrange of array nodes [offset:offset+range] . 
    public static List<? extends ProxyNode> subrange(List<? extends ProxyNode> nodes,int offset,int range)
    {
    	// no change:
    	if ((offset<=0) && (range<0)) 
    		return nodes;  
    
    	if (offset<0)
    		throw new Error("subrange(): Parameter 'offset' can't be negative. Use 0 for 'don't care'"); 
    	
    	if (nodes==null) 
    		return null; 

    	int len=nodes.size(); 

    	// no more nodes after len: 
    	if (offset>=len)
    		return null; 
    	
    	// range=-1 means all 
    	if (range<0) 
    		range=len;  
    	
    	// overflow, requested more then there are. 
    	if (offset+range>len) 
    		range=len-offset; // remainder 

    	if (range==0)
    		return null; 
    	
    	// Assert: 0 <= offset < len 
    	// Assert: 0 <= range <= (len - offset)

    	ArrayList<ProxyNode> subnodes=new ArrayList<ProxyNode>(range); 
    	for (int i=0;i<range;i++)
    	    subnodes.add(nodes.get(offset+i));
    	return subnodes; 
    }
    
    public static int newID()
    {
        return idCounter++;
    }
    
    // ========================================================================
    // Cache ! 
    // ========================================================================
    
    class Childs
    {
    	List<? extends ProxyNode> nodes=null;

    	long getChildsTime=-1; 
    }
    
    /** Cache for Object attributes ! */ 
    public class Cache
    {
    	String name=null; 
        
    	Boolean is_composite=null; 
 
		String mime_type=null; 

		String resource_type=null;

		String resource_status=null;

		Childs childs=new Childs(); 
		
		ProxyNode parent=null;

        public List<String> child_types;


		public void setName(String newName) 
		{
			this.name=newName; 
		}
		
		public void setResourceType(String value) 
		{
			this.resource_type=value; 
		} 
		
		public void setIsComposite(boolean val) 
		{
			this.is_composite=val; 
		}

		public void setMimeType(String mimeType) 
		{
			this.mime_type=mimeType; 
		}

		public String getMimeType() 
		{
			return this.mime_type; 
		}

		public boolean getIsComposite() 
		{
			return this.is_composite; 
		}
    }
    
 	// ========================================================================
    //
    // ========================================================================
    
    protected final int id;
    
    protected final VRL locator; 
    
    protected Cache cache=new Cache(); // default empty 
    
    protected ProxyFactory proxyFactory; 
    
    protected ProxyNode(ProxyFactory factory,VRL proxyLocation) 
    {
        id=newID();  
        this.locator=proxyLocation; 
        this.proxyFactory=factory;
    }
    
    protected ProxyFactory getProxyFactory()
    {
        return proxyFactory; 
    }
    
    public int getID()
    {
        return id; 
    }
    
    public VRL getVRL()
    {
        return  locator; 
    }
    
    /** 
     * Called by ProxyNode factory to prefill core attributes. 
     * Subclass can extend this method to prefetch attributes during the initialization
     * of this Node.  
     */ 
    protected void doPrefetchAttributes() throws ProxyException 
    {
        synchronized(cache)
        {
        	// update name. 
            this.cache.name=doGetName();  

            try { this.cache.is_composite=doGetIsComposite(); } catch(Exception e)
            {
                handle("Couldn't prefetch isComposite().",e);
            }  

            try { this.cache.resource_type=doGetResourceType(); } catch(Exception e)
            {
                handle("Couldn't prefetch resourceType.",e);
            }

            // might be null anyway
            try { this.cache.resource_status=doGetResourceStatus(); } catch(Exception e)
            {
                handle("Couldn't prefetch resourceStatus.",e);
            }

			// might be null anyway. 
            try { this.cache.mime_type=doGetMimeType(); }  catch(Exception e)
            {   
                handle("Couldn't prefetch mimeType.",e);
            }
    		
            try { this.cache.child_types=doGetChildTypes(); }  catch(Exception e)
            { 
                handle("Couldn't prefetch childTypes.",e);
            } 
        }
    }
    
    private void handle(String message,Exception e)
    {
        this.getProxyFactory().handleException(message,e);  
    }

    public Icon getIcon(UIViewModel model,boolean greyOut,boolean focus) throws ProxyException
    {
        return getIcon(model.getIconSize(),greyOut,focus); 
    }
    
    public Icon getIcon(int size,boolean greyOut,boolean focus) throws ProxyException
    {
        IconProvider provider=BrowserPlatform.getInstance().getIconProvider();
        
        String mimeType=this.getMimeType(); 
        
        String iconUrl=this.getIconURL(getResourceStatus(),size);  
        
        return provider.createDefaultIcon(iconUrl,
                this.isComposite(),
                false, // islink
                mimeType,
                size,
                greyOut,
                focus);  
    }

    public String getName() // no throw: name should already be fetched 
    {
    	if (this.cache.name==null)
    	{
    		logger.warnPrintf("getName(): name NOT prefetched:%s\n",this);
    		try 
    		{
				this.cache.name=doGetName();
			}
    		catch (ProxyException e)
    		{
    			handle("Method getName() Failed",e); 
    			this.cache.name=getVRL().getBasename(); 
			} 
    	}
    	
        return this.cache.name; 
    }


	public String getResourceStatus()  
    {
		// could be prefetched or not. NULL could also mean no status info.  
        return this.cache.resource_status;  
    }
    
    public String getMimeType() throws ProxyException
    {
    	if (this.cache.mime_type==null)
    	{
    		logger.warnPrintf("getMimeType(): mime_type NOT prefetched:%s\n",this);
    		this.cache.mime_type=doGetMimeType(); 
    	}
    	
        return this.cache.mime_type; 
    }
    
    /** 
     * Get Icon uses for specified satus and optional a prerender icon matching the specified
     * size 
     * @param status  Optional status attribute 
     * @param size    Desired size 
     * @return
     * @throws ProxyException
     */
    public String getIconURL(String status,int size) throws ProxyException
    {
        return null; 
    }

    public boolean hasChildren() throws ProxyException
    {
        List<? extends ProxyNode> childs = this.getChilds(); 
        
        if ((childs==null) || (childs.size()<=0))
            return false;
        
        return true; 
    }

    public ViewNode createViewItem(UIViewModel model) throws ProxyException
    {
        Icon defaultIcon=getIcon(model,false,false);
        ViewNode viewNode=new ViewNode(locator,defaultIcon,getName(),isComposite());
        viewNode.setResourceType(this.getResourceType()); 
        viewNode.setMimeType(this.getMimeType());
        viewNode.setResourceStatus(this.getResourceStatus());
        // handlers: 
        viewNode.setDnDHandler(createViewNodeDnDHandler(viewNode)); 
        
        // other 
        viewNode.setIcon(ViewNode.FOCUS_ICON,getIcon(model,false,true));
        viewNode.setIcon(ViewNode.SELECTED_ICON,getIcon(model,true,false));
        viewNode.setIcon(ViewNode.SELECTED_FOCUS_ICON,getIcon(model,true,true)); 
        
        return viewNode; 
    } 

	protected ViewNodeDnDHandler createViewNodeDnDHandler(ViewNode viewNode) 
	{
		return new ViewNodeDnDHandler(viewNode); 
	}

	public boolean hasLocator(VRL locator)
	{
		return this.locator.equals(locator); 
	}

	public List<? extends ProxyNode> getChilds() throws ProxyException
	{
		return getChilds(0,-1,null); 
	}

	// ========================================================================
    // Cached methods
    // ========================================================================
   	
	public List<? extends ProxyNode> getChilds(int offset, int range, LongHolder numChildsLeft) throws ProxyException
	{
		 synchronized(this.cache.childs)
	     {
			 if (cache.childs.nodes==null)
			 {
				 List<? extends ProxyNode> childs = doGetChilds(offset,range,numChildsLeft); 
				 
				 if ((offset>0) || (range>0)) 
				 {
				     // todo: update ranged childs into cache, but typically ranged results
				     // are used in the case the actual child list is to big or the invoker
				     // caches the data itself. 
				     return childs; // do not cache ranged results! 
				 }
				 
				 // only cache complete results! 
			     cache.childs.nodes=childs; 
			     cache.childs.getChildsTime=System.currentTimeMillis(); 

				 if (cache.childs.nodes!=null)
				     for (ProxyNode child:cache.childs.nodes)
				     {  
				         child.doPrefetchAttributes(); 
				     }
			 }
			 
			 return cache.childs.nodes;
	     }
	}
	
	public ProxyNode getParent() throws ProxyException
	{
		 synchronized(this.cache)
	     {
			 if (cache.parent==null)
			 {
				 cache.parent=doGetParent();  
			 }
			 
			 return cache.parent;
	     }
	}
	
   public VRL getParentLocation() throws ProxyException
   {
       ProxyNode parent = getParent();
       
       if (parent!=null)
           return parent.getVRL();
       
       return null;  
   }

	
	/** @deprecated to be investigated */ 
    public boolean isBusy()
    {
    	return false; 
    }
	
    /**
	 * Returns (cached) ResourceType value. 
	 * Should not throw exception since attribute must be known at creation time. 
	 * 
	 * @return
	 */
	public String getResourceType()
	{
	    if (this.cache.resource_type==null)
        {
            logger.warnPrintf("resource_type NOT prefetched!\n");
            
            try
            {
                this.cache.resource_type=doGetResourceType();
            }
            catch (ProxyException e)
            {
               handle("getResourceType()",e); 
            } 
        }
		return this.cache.resource_type; 
	}
	
	/**
	 * Returns (cached) isComposite value. 
	 * Should not throw exception since attribute must be known at creation time. 
	 * 
	 * @return
	 */
	public boolean isComposite()
	{
		if (this.cache.is_composite==null)
		{
			logger.warnPrintf("***>>>> isComposite() NOT prefetched!\n"); 
			try
			{ 
				this.cache.is_composite=doGetIsComposite(); 
			} 
			catch(Exception e)
			{ 
				handle("isComposite()",e);
				return true; 
			}  
		}
		
		return this.cache.is_composite; 
	}
	
	public List<String> getCreateTypes() 
	{
	    if (this.cache.child_types==null)
	    {
	        try
	        {
	            this.cache.child_types=doGetChildTypes();
	        }
	        catch(ProxyException e)
	        {
	            handle("getCreateTypes",e);
	        }
	    }
	    
	    return this.cache.child_types;
	}
	
    public List<String> getAttributeNames() throws ProxyException
    {   
        List<String> names=doGetAttributeNames(); 
        
        if (names!=null)
            return names;
        
        return getDefaultProxyAttributesNames(); 
    }

    public List<Attribute> getAttributes(List<String> names) throws ProxyException
    {
        List<Attribute> attrs = doGetAttributes(names); 

        if (attrs!=null)
            return attrs; 
        
        return getDefaultProxyAttributes(names);
    }
    
    // ============
    // Presentation 
    // ============
   
    public Presentation getPresentation() 
    {
        try
        {
            return doGetPresentation();
        }
        catch (ProxyException e)
        {
            // Default Presentation! 
            logger.errorPrintf("FIXME: Could not get presentation. Need default Presentation!"); 
            e.printStackTrace();
            return null; 
        } 
    }
	
    protected List<String> getDefaultProxyAttributesNames()
    {
        return new StringList(new String[]
            {
                AttributeNames.ATTR_ICON,
                AttributeNames.ATTR_NAME,
                AttributeNames.ATTR_RESOURCE_TYPE,
                AttributeNames.ATTR_URI,
                AttributeNames.ATTR_MIMETYPE 
            });
    }
    
    protected List<Attribute> getDefaultProxyAttributes(List<String> names) throws ProxyException
    {
        List<Attribute> attrs=new ArrayList<Attribute>(); 
        
        // hard coded default attributes: 
        for (int i=0;i<names.size();i++)
        {
            String name=names.get(i);
            if (name.equals(AttributeNames.ATTR_ICON))
                attrs.add(new Attribute(name,this.getIconURL(this.getResourceStatus(), 48))); 
            else if (name.equals(AttributeNames.ATTR_NAME))
                attrs.add(new Attribute(name,this.getName()));  
            else if (name.equals(AttributeNames.ATTR_URI))
                attrs.add(new Attribute(name,this.getVRL()));
            else if (name.equals(AttributeNames.ATTR_RESOURCE_TYPE))
                attrs.add(new Attribute(name,this.getResourceType())); 
            else if (name.equals(AttributeNames.ATTR_MIMETYPE))
                attrs.add(new Attribute(name,this.getMimeType()));
        }
        return attrs; 
    }
    
    public String toString()
    {
        return "<ProxyNode:"+getResourceType()+":"+getVRL(); 
    }
    

    
	// ========================================================================
    // Protected implementation interface ! 
    // ========================================================================
    
	abstract protected String doGetName() throws ProxyException; 
    
    abstract protected String doGetResourceType() throws ProxyException;  
    
    abstract protected String doGetResourceStatus() throws ProxyException;
  
    abstract protected String doGetMimeType() throws ProxyException;

    abstract protected boolean doGetIsComposite() throws ProxyException;
    
    // ====
    // Child/Composite Attributes
    // ====
    
	/**  Uncached doGetChilds, using optional range */  
	abstract protected List<? extends ProxyNode> doGetChilds(int offset, int range, LongHolder numChildsLeft) throws ProxyException;
    
    /** Uncached doGetParent() */  
	abstract protected ProxyNode doGetParent() throws ProxyException;
    
    /** Resource Type this node can create/contain. */ 
    abstract protected List<String> doGetChildTypes() throws ProxyException;

    // ====
    // Attributes 
    // ====

    abstract protected List<String> doGetAttributeNames() throws ProxyException;

    abstract protected List<Attribute> doGetAttributes(List<String> names) throws ProxyException;

    abstract protected Presentation doGetPresentation() throws ProxyException;



    
}
