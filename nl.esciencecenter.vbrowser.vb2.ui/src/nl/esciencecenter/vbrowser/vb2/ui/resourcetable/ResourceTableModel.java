package nl.esciencecenter.vbrowser.vb2.ui.resourcetable;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.util.QSort;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.data.Attribute;
import nl.esciencecenter.vbrowser.vb2.ui.data.AttributeSet;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.resourcetable.ResourceTableModel.RowData;

/**
 * Generic Resource Table Model. 
 * 
 * ViewNode can both be the "row" object or an Attribute Object. 
 * 
 */

public class ResourceTableModel extends AbstractTableModel implements Iterable<ResourceTableModel.RowData>  
{
    private static final long serialVersionUID = 4392816921140385298L;

    private static ClassLogger logger;
    
    static 
    {
        logger=ClassLogger.getLogger(ResourceTableModel.class); 
        logger.setLevelToDebug();
    }
    
    /** Resource Row Data */ 
    
	public class RowData
	{
	    // OPTIONAL vrl (if resource row has VRL) 
		private String rowKey; 
		
        private AttributeSet rowAttributes=new AttributeSet();

		private ViewNode viewNode; 

        //private ViewNode viewItem; 
        
        public RowData(String rowKey, AttributeSet attrs)
		{
			this.rowKey=rowKey;  

			if (attrs!=null)
			    this.rowAttributes=attrs.duplicate(); // empty set!
		}
        
        public String getKey()
        {
            return rowKey; 
        }
        
		public int getNrAttributes()
		{
		    return rowAttributes.size(); 
		}

        public Attribute getAttribute(String name)
        {
            return rowAttributes.get(name); 
        }
        
        public Attribute getAttribute(int nr)
        {
            String key=rowAttributes.getKey(nr);
            if (key==null)
                return null;
            return rowAttributes.get(key); 
        }

        public void updateData(AttributeSet data, boolean merge)
        {
            if (merge==false)
            {
                this.rowAttributes=data.duplicate(); 
                return; 
            }
            
            // dump:
            this.rowAttributes.putAll(data); 
            uiFireRowChanged(this); 
        }

        public void setValue(String attrName, String value)
        {
           this.rowAttributes.set(attrName,value); 
           uiFireCellChanged(this,attrName); 
        }

		public void setValue(String attrName, Object obj)
		{
			 this.rowAttributes.setAny(attrName,obj);
			 uiFireCellChanged(this,attrName); 
		}
        
        public void setValues(Attribute[] attrs)
        {
            if (attrs==null)
                return; 
            
            for (Attribute attr:attrs)
                this.rowAttributes.put(attr); 
            
            uiFireRowChanged(this); 
        }

        public void removeValue(String attr)
        {
            this.rowAttributes.remove(attr); 
        }

        public int getIndex()
        {
            // check entry inside Table Rows !
            synchronized(ResourceTableModel.this.rows)
            {   
                return ResourceTableModel.this.rows.indexOf(this); 
            }
        }

        public String[] getAttributeNames()
        {
            return this.rowAttributes.getAttributeNames(); 
        }

        /** Get Optional ViewNode */ 
		public ViewNode getViewNode() 
		{
			return this.viewNode; 
		}
		
        /** Set Optional ViewNode */ 
		public void setViewNode(ViewNode node) 
		{
			this.viewNode=node; 
		}
	}
	
	// ========================================================================
	// Instance 
	// ========================================================================
	
	/** 
	 * Synchronized Vector which contains rows as shown, default empty NOT null. 
	 * Also serves as data mutex! 
	 */
	protected Vector<RowData> rows=new Vector<RowData>();
	
	/** Mapping of Key String to row Index number */  
    private Map<String,Integer> rowKeyIndex=new Hashtable<String,Integer>();
    
	// all headers: default empty, NOT null 
    private HeaderModel headers=new HeaderModel(); 
    
    /** All potential headers */ 
    private StringList allHeaders=null;

	private ViewNode rootViewNode;  
    
    /** Testing */
	protected ResourceTableModel(String[] headers)
    {
        super(); 
        initHeaders(headers); 
    }
	
	protected void initHeaders(String[] headers)
	{
        this.headers=new HeaderModel(headers); 
    }
	
	public ResourceTableModel()
	{
		super(); 
		String names[]={"Dummy1","Dummy2"}; 
		headers=new HeaderModel(names); 
		AttributeSet dummySet=new AttributeSet(); 
		dummySet.set("Dummy1","value1"); 
		dummySet.set("Dummy2","value2"); 
        
		this.rows.add(new RowData("row1",dummySet)); 
		this.rows.add(new RowData("row2",dummySet)); 
        
		// allow EMPTY table model: init with defaults!
	}
    
    public void setHeaders(String[] headers)
    {
        this.headers.setValues(headers); 
        this.fireTableStructureChanged();
    }
    
    public void setHeaders(StringList headers)
    {
        this.headers.setValues(headers);    
        this.fireTableStructureChanged(); 
    }

    
    @Override
	public int getColumnCount()
	{
        return headers.getSize();  
	}

	@Override
	public int getRowCount() 
	{
		return rows.size(); 
	}

	/** Returns private array containing current row objects */  
	public RowData[] getRows() 
	{
	    synchronized(this.rows)
	    {
	        int len=this.rows.size(); 
	        RowData rows[]=new RowData[len];    
	        rows=this.rows.toArray(rows);
	        return rows; 
	    }
	}
	
	@Override
	public Attribute getValueAt(int rowIndex, int columnIndex) 
	{
	    synchronized(rows)
        {
	        if ((rowIndex<0) || rowIndex>=this.rows.size())
                return null;
	        
	        if ((columnIndex<0) || columnIndex>=this.headers.getSize())
                return null;
            
    		RowData rowObj = rows.get(rowIndex); 
    		
    		if (rowObj==null)
    		{
    		    logger.warnPrintf("getValueAt: Index Out of bounds:[%d,%d]\n",rowIndex,columnIndex); 
    			return null;
    		}
    		
    		String header=this.headers.getElementAt(columnIndex);
    	
    		Attribute attr=rowObj.getAttribute(header);
    		
    		if (attr==null)
    		    return null;
    		
    		return attr;
        }
	}

	public Object getValueAt(String rowKey,String attrName)
	{
	    synchronized(rows)
        {
	        Attribute attr=getAttribute(rowKey,attrName); 
	        if (attr==null)
	            return null; 
	        return attr.getValue();
        }
	}
	
	/** Returns Attribute Value ! use getAttrStringValue for actual string value of attribute */ 
	public Object getValueAt(int rowIndex,String attrName)
    {
	    synchronized(rows)
	    {
	        if ((rowIndex<0) || rowIndex>=this.rows.size())
                return null;
	        
	        Attribute attr=this.rows.get(rowIndex).getAttribute(attrName);
	        
	        if (attr==null)
	            return null;
	        
	        return attr.getValue();
	    }
    }

	/** Return Attribute.getValue() of specified row,attributeName */
	public String getAttrStringValue(String rowKey,String attrName)
    {
	    Attribute attr=getAttribute(rowKey,attrName); 
	    if (attr==null)
	        return null; 
	    return attr.getStringValue(); 
    }
	
	public String getAttrStringValue(int row, String attrName)
	{
	    Attribute attr=getAttribute(row,attrName); 
        if (attr==null)
            return null; 
        return attr.getStringValue(); 
	}
	
	public boolean setValue(String key,String attrName,String value)
	{
	    synchronized(rows)
        {
    	    int rowIndex=this.getRowIndex(key); 
            if (rowIndex<0)
                return false; 
            
            RowData row = this.rows.get(rowIndex);
            if (row==null)
                return false; 
            row.setValue(attrName,value); 
            this.fireTableRowsUpdated(rowIndex,rowIndex);
            return true;
        }
	}
	
	public boolean setValue(String key,Attribute attr)
    {
	    return setValues(key,new Attribute[]{attr});
    }

	public boolean setValues(String key,Attribute attrs[])
    {
	    synchronized(rows)
        {
            int rowIndex=this.getRowIndex(key); 
            if (rowIndex<0)
                return false; 
            
            RowData row = this.rows.get(rowIndex);
            if (row==null)
                return false; 
            row.setValues(attrs);  
            this.fireTableRowsUpdated(rowIndex,rowIndex);
            return true;
        }
    }
	
    public void setValueAt(Object value,int rowNr, int colNr)
    {
        RowData row = getRow(rowNr);
        
        if (row==null)
        {
            logger.warnPrintf("setValueAt(): Index out of bound:[%d,%d]=%s\n",rowNr,colNr,value); 
            return;
        }
        
        // Let Attribute figure object out!  
        row.getAttribute(colNr).setObjectValue(value);
        
        // optimization note: table will collect multiple events
        // and do the drawing at once. 
        
        this.fireTableCellUpdated(rowNr,colNr); 
    }
	
	public Attribute getAttribute(String rowKey,String attrName)
    {
	    synchronized(rows)
        {
            int rowIndex=this.getRowIndex(rowKey); 
            if (rowIndex<0)
                return null; 
            return getAttribute(rowIndex,attrName);
        }
    }
	    
	
	public Attribute getAttribute(int rowIndex,String attrName)
	{
	    synchronized(rows)
        {
	        RowData row = this.rows.get(rowIndex);
            if (row==null)
                return null; 
            return row.getAttribute(attrName);
        }
    }
	
	public Attribute getAttribute(int row,int col)
    {
        synchronized(rows)
        {
            RowData rowdata = this.rows.get(row);
            if (rowdata==null)
                return null; 
            return rowdata.getAttribute(col);
        }
    }
	
	public String[] getHeaders()
	{
		return headers.toArray();  
	}
	
	/** Create new Rows with empty Row Data */ 
    public void setRows(List<String> rowKeys)
    {
        synchronized(rows)
        {
            this.rows.clear();
            this.rowKeyIndex.clear(); 
            for (String key:rowKeys)
            {
                // add to internal data structure only
                _addRow(key,new AttributeSet()) ; 
            }
        }
        
        this.fireTableDataChanged();
    }
    
    /** Add new Row and return index to row */ 
    public int addRow(String key,AttributeSet attrs)
    {
        int index=this._addRow(key, attrs);
        this.fireTableRowsInserted(index,index);
        return this.rows.size()-1; 
    }
    
    public int addEmptyRow(String key)
    {
        return this.addRow(key,new AttributeSet()); 
    }
    
    public int delRow(String key)
    {
        int index=this._delRow(key);
        if (index<0)
            return -1; 
        
        this.fireTableRowsDeleted(index,index);
        return index; 
    }
    
    /**
     * Deletes Row. 
     * Performance note: 
     * Since a delete triggers an update for the used Key->Index mapping. 
     * This method takes O(N) time.  
     * @param index
     * @return
     */
    public boolean delRow(int index)
    {
        boolean result=this._delRow(index); 
        this.fireTableRowsDeleted(index,index);
        return result; 

    }
    
    /**
     * Deletes Row. 
     * Performance note: 
     * Since a delete triggers an update for the used Key->Index mapping. 
     * This method takes O(N) time.  (Where N= nr of rows in table) 
     * @param index
     * @return
     */
    public boolean delRows(int indices[])
    {
        // multi delete to avoid O(N*N) rekeying of key mapping ! 
        boolean result=this._delRows(indices);
        for (int i=0;i<indices.length;i++)
        {
            this.fireTableRowsDeleted(indices[i],indices[i]); 
        }
            
        return result; 
    }
    
    // add row to internal data structure 
    private int _addRow(String key,AttributeSet attrs)
    {
        synchronized(rows)
        {
            int index=rows.size(); 
            this.rows.add(new RowData(key,attrs)); 
            this.rowKeyIndex.put(key,new Integer(index));
            return index;
        }
    }
    
    // delete row from internal data structure 
    private int _delRow(String key)
    {
        // synchronized for  ROWS and rowKeyIndex as well ! 
        synchronized(rows)
        {
            Integer index=this.rowKeyIndex.get(key); 
            if (index==null)
            {
                //Global.warnPrintln(this,"_delRow: Invalid Row Key:"+key); 
                return -1;
            }
            
            this._delRow(index);            
            return index;
        }
    }
    
    /**
     * Delete row from internal data structure. 
     * Performance note: here the internal key mapping is regenerated. 
     * This take O(N) time.   
     * @param index
     * @return
     */
    private boolean _delRow(int index)
    {
        synchronized(rows)// sync for both rows and rowKeyIndex!
        {
            if ((index<0) || (index>=rows.size()))
            {
                return false; 
            }
            
            RowData rowObj=rows.get(index); 
            String key=rowObj.getKey(); 
            rows.remove(index); 
            this.rowKeyIndex.remove(key); 

            // update indices: start from 'index'  
            //index=0; 
            //rowKeyIndex.clear(); 
            for (int i=index;i<rows.size();i++)
            {
                this.rowKeyIndex.put(rows.get(i).getKey(),new Integer(i)); 
            }
            
            return true; 
        }
    }
    
    /**
     * Multi delete rows from internal data structure. 
     * Performance note: here the internal key mapping is regenerated. 
     * This take O(N) time.   
     * @param index
     * @return
     */
    private boolean _delRows(int indices[])
    {
        synchronized(rows)// sync for both rows and rowKeyIndex!
        {
            for (int index:indices)
            {
                if ((index<0) || (index>=rows.size()))
                    continue;
                        
                RowData rowObj=rows.get(index); 
                String key=rowObj.getKey(); 
                rows.remove(index); 
                this.rowKeyIndex.remove(key); 
            }
            
            // regenerate key mapping   
            rowKeyIndex.clear(); 
            for (int i=0;i<rows.size();i++)
            {
                this.rowKeyIndex.put(rows.get(i).getKey(),new Integer(i)); 
            }
            
            return true; 
        }
    }
    

    /** Search key and return row index */ 
    public int getRowIndex(String key)
    {
        if (key==null)
            return -1; 
        
        Integer index=this.rowKeyIndex.get(key); 
        
        if (index==null)
            return -1;
        
        return index; 
    }
    
    /** Return Key String of (Model) Row index. */  
    public String getRowKey(int index)
    {
        synchronized(this.rows)
        {
            if ((index<0) || (index>=this.rows.size()))
                return null;
        
            return this.rows.get(index).getKey();
        }
    }
    
    /** Return copy of current keys as array */  
    public String[] getRowKeys()
    {
        synchronized(this.rows)
        {
            String keys[]=new String[this.rows.size()];
            for (int i=0;i<this.rows.size();i++)
                keys[i]=rows.elementAt(i).getKey();
            return keys;
        }
    }

    public int getHeaderIndex(String name)
    {
        return this.headers.indexOf(name); 
    }
  
    /** Clear Data information. Keeps header information */ 
    public void clearData()
    {
        synchronized(rows) // for rows and keys 

        {
            this.rows.clear();
            this.rowKeyIndex.clear(); 
        }
        
        this.fireTableDataChanged(); 
    }
    
    public RowData getRow(int index)
    {
        synchronized(rows)
        {
            if ((index<0) || (index>=rows.size()))
                return null;
            return this.rows.get(index);
        }
    }
    
    public RowData getRow(String key)
    {
        synchronized(rows)
        {
            int index=this.getRowIndex(key); 
            if ((index<0) || (index>=rows.size()))
                return null;
            return this.rows.get(index);
        }
    }

   
   public boolean isCellEditable(int row, int col)
   {
       Object obj=this.getValueAt(row,col); 
       if (obj instanceof Attribute)
       {
           return ((Attribute)obj).isEditable(); 
       }
       return false; 
   }
   
   /**
    * Return COPY of Data as Attribute matrix. 
    * Returns: Attribute[row][col].  
    */ 
   public Attribute[][] getAttributeData() 
   {
       synchronized(rows)
       {
           int nrRows=this.rows.size();
           if (nrRows<=0)
               return null; 
           
           // assume symmetrical: 
           int nrCols=rows.get(0).getNrAttributes(); 
           if  (nrCols<=0)
               return null; 
           
           Attribute attrs[][]=new Attribute[nrRows][]; 
           for (int row=0;row<nrRows;row++)
           {
               attrs[row]=new Attribute[nrCols]; 
               for (int col=0;col<nrCols;col++)
               {
                   attrs[row][col]=this.getAttribute(row,col).duplicate(); 
               }
           }
           
           return attrs;  
       }
   }

  public String[] getAllHeaders()
  {
     if (allHeaders==null)
         return this.headers.toArray(); 
     
     return this.allHeaders.toArray(); 
  }
  
  /** Allow editable columns by specifying all possible headers */ 
  public void setAllHeaders(StringList list)
  {
      this.allHeaders=list.duplicate();  
  }

  public HeaderModel getHeaderModel()
  {
     return this.headers; 
  }

  /** 
   * Removes header and fires TableStructureChanged event.
   * Actual column data is kept in the model to avoid null pointer bugs. 
   *  
   * Method fires TableStructureChanged event which update the actual table. 
   * Only after the TableStructureChanged event has been handled.*/ 
  public void removeHeader(String headerName)
  {
      this.headers.remove(headerName); 
      this.fireTableStructureChanged();
  }

  /** 
   * Inserts new header into the headermodel after or before 'headerName'.
   * Method fires TableStructureChanged event which updates the Table. 
   * Only after the TableStructureChanged event has been handled, the table
   * column model has added the new Column ! 
   * This Table Data Model can already be updated asynchronously after the new header 
   * has been added. 
   */ 
  public int insertHeader(String headerName, String newName, boolean insertBefore)
  {
      // update Table Structure
      int index= this.headers.insertHeader(headerName, newName, insertBefore);
      this.fireTableStructureChanged();
      return index; 
  }
   
  /** 
   * Add listener to header list model, which controls the column headers. 
   * Not that due to the asynchronous nature of Swing Events, the Header Model 
   * might already have changed, but the Viewed column model use by Swing might not have. 
   */ 
  public void addHeaderModelListener(ListDataListener listener)
  {
      this.headers.addListDataListener(listener); 
  }
  
  public void removeHeaderModelListener(ListDataListener listener)
  {
      this.headers.removeListDataListener(listener); 
  }

  @Override
  public RowIterator iterator()
  {
      return new RowIterator(this); 
  }

  public void removeRow(int index)
  {
     this.rows.remove(index);    
     this.fireTableRowsDeleted(index,index); 
  }

  public boolean hasHeader(String name) 
  {
      return getHeaderModel().contains(name); 
  }
  
  public ViewNode getViewNode(String key) 
  {
	  RowData row=this.getRow(key);
	  if (row==null)
		  return null;
	
	  return row.getViewNode(); 
  }
  
  public ViewNode getRootViewNode()
  {
	  return rootViewNode; 
  }

  protected void setRootViewNode(ViewNode node)
  {
	  this.rootViewNode=node;  
  }

  // ==========================================================================
  // Events
  // ==========================================================================
  
  public int[] doSortColumn(String name, boolean reverse)
  {
      debugPrintf("sortBy:%s , reverse=%s\n",name,reverse); 
      
      int colnr=getHeaderIndex(name);    
      
      if (colnr<0) 
          return null; 
      
      debugPrintf("sortBy column number=%d\n",colnr);
      
      TableRowComparer comparer=new TableRowComparer(colnr,reverse); 
      QSort<RowData> sorter=new QSort<RowData>(comparer); 
      
      int mapping[];
      
      synchronized(rows)
      {
          // in memory sort ! 
          mapping=sorter.sort(rows);
          
          // reINdex key vecto:
          reindexKeyVector(); 
      }
      
      this.fireTableDataChanged();
      return mapping; 
  }

  private void debugPrintf(String format, Object... args)
  {
      System.err.printf(format,args); 
  }
  /** 
   * Reindex key to index mapping.  
   */
  protected void reindexKeyVector()
  {
      synchronized(rows)
      {
          synchronized(rowKeyIndex)
          {
              this.rowKeyIndex.clear();
          }
          
          int n=rows.size(); 
          
          for (int i=0;i<n;i++)
          {
              RowData row = this.rows.get(i); 
              this.rowKeyIndex.put(row.getKey(),new Integer(i)); 
          }
      }
    
  }

  // ==========================================================================
  // Events
  // ==========================================================================


public void uiFireRowChanged(RowData row)
  {
	  int index=this.getRowIndex(row.getKey());
	  this.fireTableRowsUpdated(index,index);
  }
  	
  public void uiFireCellChanged(RowData row,String name)
  {
	  int rownr=this.getRowIndex(row.getKey());
	  int colnr=getHeaderModel().indexOf(name); 

	  if ((rownr<0) || (colnr<0))
	  {
		  logger.warnPrintf("Error, couldn't find {row,attr}=%s,%s\n",row.getKey(),name); 
		  return; 
	  }
	  this.fireTableCellUpdated(rownr, colnr);
  }

  // ==========================================================================
  // ViewNode Interface ()
  // ==========================================================================

  
}
