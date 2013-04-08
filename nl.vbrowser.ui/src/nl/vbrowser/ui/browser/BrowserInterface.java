package nl.vbrowser.ui.browser;

import javax.swing.JPopupMenu;

import nl.vbrowser.ui.actionmenu.Action;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.model.ViewNodeContainer;

public interface BrowserInterface
{
    /** Returns master platform this browser is associated with */ 
    public BrowserPlatform getPlatform(); 
    
    public void handleException(Throwable exception);

	public JPopupMenu createActionMenuFor(ViewNodeContainer container, ViewNode viewNode,boolean canvasMenu);

    public void handleNodeAction(ViewNode node, Action action);
    
   
}
