package nl.vbrowser.ui.proxy;

import nl.nlesc.ptk.data.LongHolder;
import nl.nlesc.ptk.net.VRI;
import nl.nlesc.ptk.presentation.Presentation;
import nl.vbrowser.ui.data.Attribute;
import nl.vbrowser.ui.model.AttributeDataSource;
import nl.vbrowser.ui.model.DataSource;
import nl.vbrowser.ui.model.ExtendedDataSource;
import nl.vbrowser.ui.model.UIViewModel;
import nl.vbrowser.ui.model.ViewNode;

/** 
 * DataSource which produces ViewItems from ProxyItems. 
 * Bridging class connects ProxyItem (Resource) with ViewItem (UI Object). 
 */
public class ProxyNodeDataSource implements ExtendedDataSource
{
	private ProxyNode rootNode;

	private ProxyFactory proxyFactory;

    private ProxyNodeEventNotifier eventNotifier; 
	
	
	public ProxyNodeDataSource(ProxyFactory proxyFactory,ProxyNode root)
	{
		this.rootNode=root; 
		this.proxyFactory=proxyFactory; 
		this.eventNotifier=ProxyNodeEventNotifier.getInstance();
	}

	@Override
	public ViewNode getRoot(UIViewModel uiModel) throws ProxyException 
	{
		return rootNode.createViewItem(uiModel); 
	}

	public ProxyNode[] getChildProxyItems(VRI locator, 
			int offset, 
			int range, 
			LongHolder numChildsLeft) throws ProxyException 
	{
		ProxyNode[] childs; 
		
		// check toplevel: 
		if (rootNode.hasLocator(locator)) 
		{
			childs = rootNode.getChilds(offset,range,numChildsLeft); 
		}
		else
		{
		    
			ProxyNode parent = proxyFactory.openLocation(locator); 
			childs=parent.getChilds(); 
		}
		
		return childs;  
	}
	
	public ViewNode[] createViewItems(UIViewModel uIModel,ProxyNode[] childs) throws ProxyException 
	{
		if (childs==null)
			return null; 
		
		int len=childs.length; 
		
		ViewNode items[]=new ViewNode[len]; 
		
		for (int i=0;i<len;i++)
		{
			items[i]=childs[i].createViewItem(uIModel); 
		}
		
		return items; 
	}

	@Override
	public void addDataSourceListener(ProxyNodeEventListener listener) 
	{
	    eventNotifier.addListener(listener); 
	}

	@Override
	public void removeDataSourceListener(ProxyNodeEventListener listener) 
	{
	    eventNotifier.removeListener(listener); 
	}

	public ViewNode[] getChilds(UIViewModel uIModel,VRI locator) throws ProxyException 
	{
		return createViewItems(uIModel,getChildProxyItems(locator,0,-1,null)); 
	}
	
	@Override
	public ViewNode[] getChilds(UIViewModel uiModel,VRI locator,
			int offset, 
			int range, 
			LongHolder numChildsLeft)
			throws ProxyException 
	{
		return createViewItems(uiModel,getChildProxyItems(locator,offset,range,numChildsLeft)); 
	}

    @Override
    public ViewNode[] getNodes(UIViewModel uiModel, VRI[] locations) throws ProxyException
    {
        int len=locations.length; 
        
        ViewNode nodes[]=new ViewNode[len]; 
        
        for (int i=0;i<len;i++)
        {
            ProxyNode node = proxyFactory.openLocation(locations[i]);
            nodes[i]=node.createViewItem(uiModel); 
        }

        return nodes;
    }

    @Override
    public String[] getAttributeNames(VRI locator) throws ProxyException
    {
        ProxyNode node = proxyFactory.openLocation(locator);
        return node.getAttributeNames(); 
    }

    @Override
    public Attribute[] getAttributes(VRI locator, String[] attrNames) throws ProxyException
    {
        ProxyNode node = proxyFactory.openLocation(locator);
        return node.getAttributes(attrNames);  
    }
    
    @Override
    public Presentation getChildPresentation(VRI locator) throws ProxyException
    {
        ProxyNode node = proxyFactory.openLocation(locator);
        return node.getPresentation();
    }
    
    @Override
    public Presentation getPresentation() throws ProxyException
    {
    	return rootNode.getPresentation();
    }
    
	public ProxyNode getRootNode() 
	{
		return this.rootNode; 
	}


	
}
