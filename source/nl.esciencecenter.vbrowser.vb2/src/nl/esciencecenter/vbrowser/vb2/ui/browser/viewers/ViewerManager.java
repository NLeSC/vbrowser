package nl.esciencecenter.vbrowser.vb2.ui.browser.viewers;

import nl.esciencecenter.vbrowser.vb2.ui.browser.ProxyBrowser;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerFrame;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerPanel;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerRegistry;

public class ViewerManager
{

    private ProxyBrowser browser;

    public ViewerManager(ProxyBrowser proxyBrowser)
    {
        browser=proxyBrowser;
    }

    public ViewerPanel createViewerFor(ViewNode node,String optViewerClass) throws ProxyException
    {
        ViewerRegistry registry = browser.getPlatform().getViewerRegistry();

        String resourceType = node.getResourceType();
        // String resourceStatus = node.getResourceStatus();
        String mimeType = node.getMimeType();


        Class clazz=null; 
        
        if (optViewerClass!=null)
        {
            clazz = loadViewerClass(optViewerClass); 
        }
        
        if ((clazz==null) && (mimeType!=null))
        {
            if (clazz==null)
                clazz=registry.getMimeTypeViewerClass(mimeType);
        }
        
        if (clazz==null)
            return null; 
        
        ViewerPanel viewer = registry.createViewer(clazz);
        return viewer;
    }
    
    private Class loadViewerClass(String optViewerClass)
    {
        try
        {
            return this.getClass().getClassLoader().loadClass(optViewerClass);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return null; 
        } 
    }

    public ViewerFrame createViewerFrame(ViewerPanel viewer, boolean initViewer)
    {
        return ViewerFrame.createViewerFrame(viewer,initViewer);
    }
}
