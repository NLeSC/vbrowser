package nl.esciencecenter.vbrowser.vb2.ui.browser;

import javax.swing.JPopupMenu;

import nl.esciencecenter.vbrowser.vb2.ui.actionmenu.Action;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeContainer;

/** 
 * Default Browser Interface Adaptor
 */
public class DummyBrowserInterface  implements BrowserInterface
{

    private BrowserPlatform platform;

    public DummyBrowserInterface(BrowserPlatform platform)
    {
        this.platform=platform; 
    }
    
    @Override
    public BrowserPlatform getPlatform()
    {
        return this.platform;
    }

    @Override
    public void handleException(Throwable exception)
    {
        exception.printStackTrace(); 
    }

    @Override
    public JPopupMenu createActionMenuFor(ViewNodeContainer container, ViewNode viewNode, boolean canvasMenu)
    {
        return null;
    }

    @Override
    public void handleNodeAction(ViewNode node, Action action)
    {
        
    }

}