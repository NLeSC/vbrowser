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

package nl.esciencecenter.vbrowser.vb2.ui.tree;

import javax.swing.JPopupMenu;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.actionmenu.Action;
import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserInterface;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeActionListener;

public class ResourceTreeController implements TreeExpansionListener, ViewNodeActionListener
{
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(ResourceTreeController.class); 
    }
    
	private ResourceTree tree;
    private BrowserInterface browser;

	public ResourceTreeController(BrowserInterface browser,ResourceTree resourceTree,
			ResourceTreeModel model) 
	{
		this.tree=resourceTree; 
		this.browser=browser; 
	}
	
	public void handleNodeActionEvent(ViewNode node,Action action)
	{
	    this.browser.handleNodeAction(node,action); 
	}

	// From TreeExpansionListener
    public void treeExpanded(TreeExpansionEvent evt)
    {
        logger.debugPrintf("TreeExpansionHandler.treeExpanded()\n");
        
        TreePath path = evt.getPath();
        if (evt.getSource().equals(tree)==false)
        {
            logger.errorPrintf("***Received event from different tree!\n"); 
            return ; 
        }
        // Get the last component of the path and
        // arrange to have it fully populated.
        ResourceTreeNode node = (ResourceTreeNode) path.getLastPathComponent();

        if (node.isPopulated() == false)
            tree.populate(node); 
        // else update ? 
    }
    
    // From TreeExpansionListener
    public void treeCollapsed(TreeExpansionEvent evt)
    {
        logger.debugPrintf("TreeExpansionHandler.treeCollapsed()\n"); 
    }

    /** Package protected debug handler */
	void debugPrintf(Object source,String format, Object... args) 
	{
		//logger.debugPrintf(ClassLogger.object2classname(source)+":"+format,args); 
	}

	public BrowserInterface getMasterBrowser()
	{
		return this.browser; 
	}

}
