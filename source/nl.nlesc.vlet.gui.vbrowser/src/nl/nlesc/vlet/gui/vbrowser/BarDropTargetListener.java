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

package nl.nlesc.vlet.gui.vbrowser;
 
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.gui.UILogger;

/**
 * NodeDropTargetListener, handles 'drop' on Node Components. Install this
 * DropTargetListener to support drops on the component.
 * 
 * ==> under construction: must check cooperation with MyTranferHandler
 * MyTranferHandler is used when a drag is started, but currently all the
 * 'drops' are handled by this DropTargetListener.
 */

public class BarDropTargetListener implements DropTargetListener// ,
                                                                // DragSourceListener,
{
    /** Main Controller */
    private BrowserController viewerController;

    public BarDropTargetListener(BrowserController controller)
    {
        this.viewerController = controller;
    }

    public void dragEnter(DropTargetDragEvent dtde)
    {
        // Global.debugPrintln(this, "dragEnter:" + dtde);
        Component source = dtde.getDropTargetContext().getComponent();
    }

    public void dragOver(DropTargetDragEvent dtde)
    {
        Component source = dtde.getDropTargetContext().getComponent();
    }

    public void dropActionChanged(DropTargetDragEvent dtde)
    {
        // Global.debugPrintln("MyDragHandler", "dropActionChanged:" + dtde);
    }

    public void dragExit(DropTargetEvent dte)
    {
        Component source = dte.getDropTargetContext().getComponent();
        // Global.debugPrintln("MyDragHandler", "dragExit:" + dte);
    }

    public void drop(DropTargetDropEvent dtde)
    {
        // currently this method does all the work when a drop is performed

        try
        {

            Transferable t = dtde.getTransferable();
            DropTargetContext dtc = dtde.getDropTargetContext();
            Component comp = dtc.getComponent();

            int action = dtde.getDropAction();

            // check dataFlavor

            /*
             * if (t.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) { //
             * Handle Java File List
             * 
             * dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
             * 
             * java.util.List fileList = (java.util.List)
             * t.getTransferData(DataFlavor.javaFileListFlavor); Iterator
             * iterator = fileList.iterator();
             * 
             * int len=fileList.size();
             * 
             * VRL sourceLocs[]=new VRL[len]; int index=0;
             * 
             * dtde.getDropTargetContext().dropComplete(true);
             * 
             * if (len<=0) return;
             * 
             * while (iterator.hasNext()) { java.io.File file =
             * (File)iterator.next();
             * 
             * Debug("name="+file.getName());
             * Debug("url="+file.toURL().toString());
             * Debug("path="+file.getAbsolutePath());
             * 
             * sourceLocs[index++]=new VRL("file",null,file.getAbsolutePath());
             * }
             * 
             * viewerController.performOpenLocation(sourceLocs[0]);
             * 
             * 
             * boolean isMove=(action==DnDConstants.ACTION_MOVE);
             * 
             * } else
             */if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                String txt = (String) t.getTransferData(DataFlavor.stringFlavor);
                // Debug("Tranferable string="+txt);

                dtde.getDropTargetContext().dropComplete(true);

                viewerController.performOpenLocation(txt);
            }

        }
        catch (Exception e)
        {
            UILogger.logException(this,ClassLogger.ERROR,e,"Failed to determine data flavor\n"); 
        }
    }

}