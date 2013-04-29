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

package nl.esciencecenter.vbrowser.vb2.ui.iconspanel;

import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import nl.esciencecenter.vbrowser.vb2.ui.UIGlobal;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;

public class IconListModel // implements ListModel 
{
	protected Vector<IconItem> icons=new Vector<IconItem>();  
	
	protected Vector<ListDataListener> listeners=new Vector<ListDataListener>(); 
	
	public IconListModel()
	{
		
	}
	
	//@Override
	public int getSize()
	{
		return icons.size();
	}

	//@Override
	public IconItem getElementAt(int index) 
	{
		return icons.get(index); 
	}

	//@Override
	public void addListDataListener(ListDataListener l) 
	{
		listeners.add(l); 
	}

	//@Override
	public void removeListDataListener(ListDataListener l) 
	{
		listeners.remove(l); 
	}

	public ListDataListener[] getListeners()
	{
		synchronized(this.listeners)
		{
			ListDataListener _arr[]=new ListDataListener[this.listeners.size()]; 
			_arr=this.listeners.toArray(_arr); 
			return _arr;
		}
	}
	
	public void setChilds(IconItem[] childs)
	{
		if (this.icons==null)
		{
			icons=new Vector<IconItem>();
		}
		else
		{
			synchronized(this.icons)
			{
				icons.clear(); 
			}
		}
		
		synchronized(this.icons)
		{
			// add child and fire event per child if this is an incremental update;
			if (childs!=null)
				for (IconItem child:childs)
				{
					addChild(child,false); 
				}
		}
		
		this.uiFireContentsChanged(); 
	}
	
	public void addChild(IconItem child,boolean fireEvent) 
	{
		int pos;
		
		synchronized(icons)
		{
			pos=this.icons.size(); 
			this.icons.add(child); 
		}
		
		if (fireEvent)
			uiFireChildAdded(child,pos);
	}
	
	public void uiFireChildAdded(final IconItem icon, final int pos)
	{
		if (UIGlobal.isGuiThread()==false)
		{
			Runnable updater=new Runnable()
			{
				@Override
				public void run()
				{
					uiFireChildAdded(icon,pos);
				}
			};
			
			UIGlobal.swingInvokeLater(updater); 
			return; 
		}
		
		// range is inclusive: [pos,pos]
		ListDataEvent event=new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,pos, pos); 
		
		for (ListDataListener l:getListeners())
			l.intervalAdded(event);
	}
	
	public void uiFireContentsChanged()
	{
		if (UIGlobal.isGuiThread()==false)
		{
			Runnable updater=new Runnable()
			{
				@Override
				public void run()
				{
					uiFireContentsChanged();
				}
			};
			
			UIGlobal.swingInvokeLater(updater); 
			return; 
		}
		
		// range is inclusive: [pos,pos]
		ListDataEvent event=new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,icons.size()-1); 
		
		for (ListDataListener l:getListeners())
			l.contentsChanged(event);
	}


}
