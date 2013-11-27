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

package nl.esciencecenter.vbrowser.vb2.ui.proxy.fsnode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.io.FSUtil;

import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.dummy.DummyProxyFactory;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.mimetypes.MimeTypes;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/** 
 * Example ProxyNode based on (Generic) FSNode class. 
 */
public class FSNodeProxyNode extends ProxyNode
{
	FSNode file;
	
	private FSNodeAttributes metaFile; 
	
    protected FSNodeProxyNode createChild(String childname) throws ProxyException
    {
        return new FSNodeProxyNode(getProxyFactory(),getVRL().appendPath(childname)); 
    }
    
    public FSNodeProxyNode(ProxyFactory anyFileProxyFactory, VRL loc) throws ProxyException
    {
        super(anyFileProxyFactory,loc); 
        try
        {
            file=FSUtil.getDefault().newFSNode(loc.getPath());
        }
        catch (IOException e)
        {
            throw new ProxyException(e.getMessage(),e); 
        } 
        init(); 
    } 
    
    public FSNodeProxyNode(ProxyFactory anyFileProxyFactory,VRL loc,FSNode file) throws ProxyException
    {
        super(anyFileProxyFactory,loc); 
        this.file=file; 
        init(); 
    }
    
    protected FSNodeProxyNode(ProxyFactory anyFileProxyFactory,FSNodeProxyNode parent,VRL locator) throws ProxyException
    {
        super(anyFileProxyFactory,locator);
        init(); 
    }

    private void init() throws ProxyException
    {
        this.metaFile=new FSNodeAttributes(file); 
    	//super.prefetch(); 
    }
    
    public String toString()
    {
        return "<AnyFileProxyNode>:"+file; 
    }
    
    @Override
    public boolean isBusy()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return file.getBasename();  
    }

    @Override
    public boolean hasChildren()
    {   
       if (file.isFile())
    	   return false; 
       
       try 
       {
    	   return (file.list()!=null);
       }
       catch (IOException e) 
       {
    	   return false;
       }
    }

    @Override
    protected ProxyNode doGetParent() throws ProxyException
    {
        return DummyProxyFactory.getDefault().doOpenLocation(this.locator.getParent());
    }
    
   
    @Override
    public List<? extends ProxyNode> doGetChilds(int offset, int range,LongHolder numChildsLeft) throws ProxyException
    {
    	FSNode[] files;
		try 
		{
			files = file.listNodes();
		} 
		catch (IOException e) 
		{
			throw new ProxyException("Couldn't list contents:"+this,e);
		}
		
    	if (files==null)
    		return null; 
    	
    	ArrayList<FSNodeProxyNode> nodes=new ArrayList<FSNodeProxyNode>(files.length); 
    	
    	for (int i=0;i<files.length;i++)
    		nodes.add(new FSNodeProxyNode(getProxyFactory(),new VRL(files[i].getURI()),files[i])); 
        
    	return subrange(nodes,offset,range);  
    }

    @Override
	public ProxyFactory getProxyFactory()
	{
		return FSNodeProxyFactory.getDefault(); 
	}

	@Override
	protected String doGetMimeType() throws ProxyException 
	{
	    return MimeTypes.getDefault().getMimeType(file.getPathname());  
	}

	@Override
	protected boolean doGetIsComposite() throws ProxyException
	{
		return this.file.isDirectory();
	}

    @Override
    protected String doGetName() 
    {
        return this.getVRL().getBasename(); 
    }

    @Override
    protected String doGetResourceType() 
    {
    	if (file.isFile())
    		return FSNode.FILE_TYPE;
    	else
    		return FSNode.DIR_TYPE; 
    }

    @Override
    protected String doGetResourceStatus() 
    {
        return null;
    }

    @Override
    protected List<String> doGetChildTypes() 
    {
        return new StringList(FSNode.FILE_TYPE,FSNode.DIR_TYPE); 
    }

    @Override
    protected List<String> doGetAttributeNames() throws ProxyException
    {
        return metaFile.getAttributeNames(); 
    }

    @Override
    protected List<Attribute> doGetAttributes(List<String> names) throws ProxyException
    {
        return metaFile.getAttributes(names); 
    }

    @Override
    protected Presentation doGetPresentation()
    {
        // redirect to meta file 
        return metaFile.getPresentation();
    }

}
