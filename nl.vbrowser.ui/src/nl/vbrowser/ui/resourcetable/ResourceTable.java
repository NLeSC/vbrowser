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
 * $Id: ResourceTable.java,v 1.4 2013/01/25 11:11:35 piter Exp $  
 * $Date: 2013/01/25 11:11:35 $
 */ 
// source: 

package nl.vbrowser.ui.resourcetable;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.DefaultCellEditor;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import nl.nlesc.ptk.data.StringList;
import nl.nlesc.ptk.presentation.Presentation;
import nl.nlesc.ptk.ui.object.Disposable;
import nl.nlesc.ptk.util.StringUtil;
import nl.vbrowser.ui.browser.BrowserInterface;
import nl.vbrowser.ui.data.Attribute;
import nl.vbrowser.ui.model.UIViewModel;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.model.ViewNodeContainer;
import nl.vbrowser.ui.presentation.UIPresentation;
import nl.vbrowser.ui.proxy.ProxyException;
import nl.vbrowser.ui.proxy.ProxyNode;

/** 
 * Generic Resource Table. 
 * Rewrite of VBrowser Table. 
 * @author Piter T. de Boer 
 *
 */
public class ResourceTable extends JTable implements Disposable, ViewNodeContainer 
{
	private static final long serialVersionUID = -8190587704685619938L;
  
    // default presentation
	
    private UIPresentation presentation=null; 
    
    private TablePopupMenu popupMenu=new TablePopupMenu();
    
    private boolean isEditable=true;
    
    protected int defaultColumnWidth=80;
    
    protected TableDataProducer dataProducer; 

    private MouseListener mouseListener; 
    
    protected ResourceTableControler controller;

	private UIViewModel uiModel;

    private String sortColumnName;

    private boolean columnSortOrderIsReversed; 
    
	public ResourceTable()
	{
	    // defaults 
	    super(new ResourceTableModel()); 
	    init(); 
	}
	
    public ResourceTable(BrowserInterface browserController,ResourceTableModel dataModel)
    {
    	// defaults 
        super(dataModel);
        this.controller=new ResourceTableControler(this,browserController); 
        init(); 
    }
    
    public ResourceTableModel getModel()
	{
	   return (ResourceTableModel)super.getModel(); 
	}
	
	public void setDataModel(ResourceTableModel dataModel)
	{
	    this.setModel(dataModel); 
	    initColumns(); 
	}
	
	public void refreshAll()
	{
	    //init(); 
	}

	private void init()
	{
		if (this.uiModel==null)
			this.uiModel=UIViewModel.createTableModel(); 
		
	    this.setAutoCreateColumnsFromModel(false);
	    //this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    
	    this.setColumnSelectionAllowed(true);
 	    this.setRowSelectionAllowed(true);

 	    initColumns();
 	    
 	    // Listeners ! 
        JTableHeader header = this.getTableHeader();
        mouseListener = new TableMouseListener(this);
        header.addMouseListener(mouseListener);
        this.addMouseListener(mouseListener);
	}
	
	/** (re)Created columns from headers taken from DataModel */ 
    public void initColumns()
    {
        String headers[]=getModel().getHeaders(); 
        this.getPresentation().setChildAttributeNames(headers);
        // Use order from presentation 
        initColumns(getPresentation().getChildAttributeNames());
    }
    
    public boolean isEditable()
    {
        return isEditable;
    }
     
    public void setEditable(boolean val)
    {
        this.isEditable=val; 
    }
    
    private void updateCellEditors()
    {
        // set cell editors:
        TableColumnModel cmodel = getColumnModel(); 
        
        int nrcs=cmodel.getColumnCount(); 

        for (int i=0;i<nrcs;i++)
        {
           TableColumn column = cmodel.getColumn(i);
           Object obj=getModel().getValueAt(0,i); 
           
           if (obj instanceof Attribute)
           {
               Attribute attr=(Attribute)obj;
               
               if (attr.isEditable()==true)
               {
                   switch(attr.getType())
                   {
                       // both boolean and enum use same select box 
                       case ENUM: 
                       case BOOLEAN:
                       {
                          // debug("setting celleditor to EnumCellEditor of columnr:"+i);
                          column.setCellEditor(new EnumCellEditor(attr.getEnumValues()));
                          break;
                       }
                       case STRING: 
                       {
                           column.setCellEditor(new DefaultCellEditor(new JTextField())); 
                       }
                       default: 
                           break; 
                   }                
               }
           }
        }
    }
    
	/** Return DEFAULT Table Mouse Listener */ 
	public MouseListener getMouseListener()
	{
	    return this.mouseListener; 
	}
	public ResourceTableModel getResourceTableModel()
	{
	    TableModel model = super.getModel(); 
	        
	    if (model instanceof ResourceTableModel)
	    {
	        return (ResourceTableModel)model; 
        }
	        
        throw new Error("Resource Table NOT initialized with compatible Table Model!:"+model.getClass()); 
	}

	   
    private void initColumns(String headers[])
    {
        setAutoResizeMode(getPresentation().getColumnsAutoResizeMode());
                
        TableColumnModel columnModel=new DefaultTableColumnModel();

        for (int i=0;i<headers.length;i++)
        {
            String headerName=headers[i]; 
            // debug("Creating new column:"+headers[i]);
            TableColumn column = createColumn(i,headerName);
            // update column width from presentation
            Integer prefWidth=getPresentation().getAttributePreferredWidth(headerName);
            if (prefWidth==null)
                prefWidth=headers[i].length()*10;// 10 points font ? 
            
            column.setPreferredWidth(prefWidth);
            
            //if (prefWidth<50) 
            //   column.setResizable(false); 
            column.setResizable(getPresentation().getAttributeFieldResizable(headerName)); 
            
            columnModel.addColumn(column); 
        }
        
        this.setColumnModel(columnModel);
        if (this.isEditable)
            this.updateCellEditors(); 
    }
    
    private TableColumn createColumn(int modelIndex,String headerName)
    {
        // one renderer per column 
        ResourceTableCellRenderer renderer= new ResourceTableCellRenderer();
        
        TableColumn column = new TableColumn(modelIndex,10,renderer, null);
        column.setIdentifier(headerName); 
        column.setHeaderValue(headerName);
        column.setCellRenderer(renderer); 
        // update presentation
        Integer size=getPresentation().getAttributePreferredWidth(headerName);
        if (size!=null)
            column.setWidth(size); 
        else
            column.setWidth(defaultColumnWidth); 
        
        return column; 
    }
    
    
    /** Returns headers as defined in the DATA model */ 
    public String[] getDataModelHeaders()
    {
        return getModel().getHeaders(); 
    }

    public int getDataModelHeaderIndex(String name)
    {
        return getModel().getHeaderIndex(name); 
    }

    /**
     * Get the header names as shown, thus in the order 
     * as used in the VIEW model  (Not dataModel).   
     */
    public StringList getColumnHeaders()
    {
        // get columns headers as currently shown in the VIEW model 
        TableColumnModel colModel = this.getColumnModel(); 
        int len=colModel.getColumnCount(); 
        StringList names=new StringList(len); 
        
        for (int i=0;i<len;i++)
            names.add(colModel.getColumn(i).getHeaderValue().toString()); 
        
        return names; 
    }
    
    /** 
     * Insert new column after specified 'headerName'. 
     * This will insert a new headername but use the current header as viewed as new
     * order so the new headers and column order is the same as currently viewed. 
     * This because the user might have switched columns in the VIEW order of the table. 
     */   
    public void insertColumn(String headerName, String newName, boolean insertBefore)
    {
        if (this.getHeaderModel().isEditable()==false)
            return; 
        
        // remove column but use order of columns as currently viewed ! 
        StringList viewHeaders=this.getColumnHeaders(); 
        if (insertBefore)   
            viewHeaders.insertBefore(headerName,newName); 
        else
            viewHeaders.insertAfter(headerName,newName);
        
        // insert empty column and fire change event. This will update the table. 
        this.getModel().setHeaders(viewHeaders); 
        
        try 
        {
			this.dataProducer.updateColumn(newName);
		}
        catch (ProxyException e)
        {
			handle(e);
		} 
        
    }


	public void removeColumn(String headerName)
    {
        if (this.getHeaderModel().isEditable()==false)
            return; 
        
        // remove column but use order of columns as currently viewed ! 
        StringList viewHeaders=this.getColumnHeaders(); 
        viewHeaders.remove(headerName); 
        
        // triggers restructure, and KEEP the current view order of Columns. 
        this.getModel().setHeaders(viewHeaders);
        this.presentation.setChildAttributeNames(viewHeaders.toArray());
        this.getModel().fireTableStructureChanged(); 
    }
    
    public HeaderModel getHeaderModel()
    {
        return this.getModel().getHeaderModel(); 
    }
    
    public void tableChanged(TableModelEvent e)
    {   
        if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW)
        {
            initColumns(); 
        }
        super.tableChanged(e); 
    }

    /** Has visible column (Must exist in columnmodel!) */  
    public boolean hasColumn(String headerName)
    {
        Enumeration enumeration = getColumnModel().getColumns();
        TableColumn aColumn;
        int index = 0;

        while (enumeration.hasMoreElements()) 
        {
            aColumn = (TableColumn)enumeration.nextElement();
            // Compare them this way in case the column's identifier is null.
            if (StringUtil.equals(headerName,aColumn.getHeaderValue().toString()))
                return true;
            index++;
        }
        
        return false; 
    }
    
    /** Has visible column (Must exist in columnmodel!) */  
    public TableColumn getColumnByHeader(String headerName)
    {
        Enumeration enumeration = getColumnModel().getColumns();
        
        while (enumeration.hasMoreElements()) 
        {
            TableColumn col = (TableColumn)enumeration.nextElement();
            // Compare them this way in case the column's identifier is null.
            if (StringUtil.equals(headerName,col.getHeaderValue().toString()))
                return col; 
        }
        
        return null;  
    }

    public TablePopupMenu getPopupMenu(MouseEvent e, boolean canvasMenu) 
    {
        if (popupMenu!=null)
            popupMenu.updateFor(this,e,canvasMenu); 
        return popupMenu; 
    }

    public void setPopupMenu(TablePopupMenu menu)
    {
       this.popupMenu=menu; 
    }

    /** Return row Key under Point point. Might return NULL */ 
    public String getKeyUnder(Point point)
    {
        if (point==null)
            return null; 
        int row=rowAtPoint(point); 
        if (row<0)
            return null; 
        return getModel().getRowKey(row); 
    }

    public UIPresentation getPresentation()
    {
        if (this.presentation==null)
            presentation=new UIPresentation(); 
        
       return this.presentation; 
    }

    public void dispose()
    {
        
    }

    public void setPresentation(UIPresentation newPresentation)
    {
        this.presentation=newPresentation;
        this.refreshAll();
    }
    
    /** 
     * Update Data Source. 
     */
    public void setDataSource(ProxyNode node, boolean update)
    {
    	ResourceTableModel model = new ResourceTableModel(); 
    	this.setModel(model); 
        setDataProducer(new ProxyNodeTableDataProducer(node,model),true); 
        UIPresentation pres = node.getPresentation(); 
        if (pres!=null)
            this.setPresentation(pres); 
    }

	public void setDataProducer(TableDataProducer producer,boolean update)
	{
		this.dataProducer=producer; 
		if (update==false)
			return;
		
		if (dataProducer!=null)
		{
			//recreate table 
			try 
			{
				this.dataProducer.createTable(true,true);
			}
			catch (ProxyException e) 
			{
				handle(e);
			}
		}
		else
		{
			this.removeAll(); 
		}
	}
 
	/** Returns root ViewNode if Model supports this ! */ 
	public ViewNode getRootViewNode()
	{
		return null;
		// return this.getModel().getRootViewNode(); 
	}
	
	/** Returns root ViewNode if Model supports this ! */ 
	public ViewNode getViewNodeByKey(String key)
	{
		return this.getModel().getViewNode(key);  
	}

	@Override
	public UIViewModel getUIViewModel() 
	{
		return this.uiModel; 
	}

	@Override
	public ViewNode getViewNode()
	{
		return this.getModel().getRootViewNode(); 
	}

	@Override
	public boolean requestFocus(boolean value)
	{
	    if (value==true)
	        return this.requestFocusInWindow(); 
	    
	    return false; // unfocus not applicable ?
	}

	@Override
	public ViewNodeContainer getViewContainer() 
	{
		return this;
	}

	@Override
	public ViewNode getNodeUnderPoint(Point p)
	{
		return null;
	}

	@Override
	public JPopupMenu createNodeActionMenuFor(ViewNode node, boolean canvasMenu)
	{
		return null;
	}

	@Override
	public void clearNodeSelection()
	{
		
	}

	@Override
	public ViewNode[] getNodeSelection()
	{
		return null;
	}

	@Override
	public void setNodeSelection(ViewNode node, boolean isSelected) 
	{
		
	}

	@Override
	public void setNodeSelectionRange(ViewNode firstNode, ViewNode lastNode,
			boolean isSelected) 
	{
		
	}

	@Override
	public boolean requestNodeFocus(ViewNode node, boolean value)
	{
		// set focus to table cell!
	    return false; 
	}

	public TableDataProducer getDataProducer() 
	{
		return this.dataProducer; 
	}
	
	private void handle(ProxyException e)
    {
		controller.handle(e); 
	}

    public void doSortColumn(String name,boolean reverse)
    {
        this.getResourceTableModel().doSortColumn(name, reverse); 
        this.sortColumnName=name;
        this.columnSortOrderIsReversed=reverse; 
    }
    
    public boolean getColumnSortOrderIsReversed()
    {
        return this.columnSortOrderIsReversed; 
    }

    public String getSortColumnName()
    {
        return sortColumnName;
    }
   
}
