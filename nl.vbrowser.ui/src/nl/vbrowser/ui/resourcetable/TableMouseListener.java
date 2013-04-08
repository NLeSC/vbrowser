/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: TableMouseListener.java,v 1.4 2013/01/25 11:11:35 piter Exp $  
 * $Date: 2013/01/25 11:11:35 $
 */ 
// source: 

package nl.vbrowser.ui.resourcetable;

import java.awt.Component;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nl.nlesc.ptk.util.StringUtil;
import nl.vbrowser.ui.GuiSettings;
import nl.vbrowser.ui.UIGlobal;

public class TableMouseListener implements MouseListener
{
    private ResourceTable table;

    public TableMouseListener(ResourceTable source)
    {
        table=source;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        System.err.printf("Event:%s\n",e);
        if (isHeader(e) && (UIGlobal.getGuiSettings().isSelection(e)))
        {
            String  name=this.getColumnNameOf(e); 
            if(name!=null)
            {
                String prevCol=this.table.getSortColumnName();
                boolean reverse=false; 
                        
                // click on already sorted column name -> reverse sorting. 
                if (StringUtil.compare(prevCol,name)==0)
                {
                    reverse=(table.getColumnSortOrderIsReversed()==false);
                }
                this.table.doSortColumn(name,reverse); 
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        Component comp=(Component)e.getSource();
        Point clickPoint=e.getPoint(); 
       
        
        //boolean ctrl=((e.getModifiersEx() & e.CTRL_DOWN_MASK) !=0);
        
        // Show Header Popup! 
        if (isHeader(e) && (UIGlobal.getGuiSettings().isPopupTrigger(e)))
        {
            String  name=this.getColumnNameOf(e); 
            if(name!=null)
            {
                HeaderPopupMenu popupMenu=new HeaderPopupMenu(table,name); 
                popupMenu.show(comp,e.getX(),e.getY());
            }
            else
            {
                // debug("No Column Header name!:"+e);
            }
        }
        else if (comp.equals(table))
        {
            if (UIGlobal.getGuiSettings().isPopupTrigger(e))
            {
                TablePopupMenu popupMenu=table.getPopupMenu(e,false); 
                if (popupMenu!=null)
                    popupMenu.show(comp,e.getX(),e.getY());
            }
        }
        else if (comp.equals(table.getParent()))
        {
            if (UIGlobal.getGuiSettings().isPopupTrigger(e))
            {
                TablePopupMenu popupMenu=table.getPopupMenu(e,true); 
                if (popupMenu!=null)
                    popupMenu.show(comp,e.getX(),e.getY());
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }
    
    private String getColumnNameOf(MouseEvent e)
    {
        // Warning: Apply coordinates to VIEW model ! 
        TableColumnModel columnModel = table.getColumnModel();
        
        int colnr=columnModel.getColumnIndexAtX(e.getX());
        
        if (colnr<0) 
            return null; 
        
        TableColumn column = columnModel.getColumn(colnr); 
        String name=(String)column.getHeaderValue();
        return name; 
    }
    
    protected boolean isHeader(MouseEvent e)
    {   
        if (e.getSource() instanceof JTableHeader) 
            return true; 
        else
            return false; 
    }

}
