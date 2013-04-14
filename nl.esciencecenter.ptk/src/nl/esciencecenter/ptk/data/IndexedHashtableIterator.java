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

package nl.esciencecenter.ptk.data;

import java.util.Iterator;
import java.util.NoSuchElementException;


/** 
 * AttributeSet Iterator 
 */ 

public class IndexedHashtableIterator<TK,TV> implements Iterator<TV>
{
	private IndexedHashtable<TK,TV> hash=null;
	
	/** 
	 * currentKey=NULL means first. When the end of the list is reached this value
	 * will contain the last key returned. 
	 */
	private TK currentKey=null;


	public IndexedHashtableIterator(IndexedHashtable<TK,TV> hash)
	{
		this.hash=hash;  
		currentKey=null; 
	}

	public boolean hasNext()
	{
		// getKeyIndex return -1 when currentKey==null ! 
		return (getKeyIndexOf(currentKey)+1<hash.size());
	}

	private int getKeyIndexOf(TK key)
	{
		if (key==null)
			return -1;  

		return hash.getIndexOfKey(key); 
	}

	public TV next() throws NoSuchElementException
	{
		synchronized(hash)
		{
			if (hasNext()==false)
				throw new NoSuchElementException("No next element!");
			
			TK previousKey=currentKey; 
			currentKey=nextKey(); // returns FIRST key at init time ! 

			if (currentKey==null)
				if (previousKey==null)
					throw new NoSuchElementException("Couldn't find first element. Empty Set ? ");
				else
					throw new NoSuchElementException("Couldn't find next element after:"+previousKey);

			TV value = hash.get(currentKey);

			if (value==null) 
				throw new NoSuchElementException("Internal Error: Attribute not in set:"+currentKey); 

			return value; 
		}
	}

	/** Return FIRST key at init time ! */
	private TK nextKey()
	{
		synchronized(hash)
		{
			// init ! 
			if (currentKey==null)
			{
				if (hash.size()<=0)
				{
					// Empty list: 
					return null;
				}
				else
				{
					// first: 
					currentKey=hash.getKeyArray()[0];
					return currentKey; 
				}
			}

			// next: 
			int index=hash.getIndexOfKey(currentKey);

			if (index<0)
			{
				// currentKey removed ???
				// Global.debugPrintln(this,"Iterator error: currentKey doesn't exists anymore:"+currentKey);
				throw new NoSuchElementException("Iterator error: curentKey has been removed:"+currentKey); 
			}

			if (index+1<hash.size())
			{
				int currentKeyIndex=index+1;
				currentKey=hash.getKey(currentKeyIndex);
				return currentKey; 
			}
			else
				return null;
		}
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Cannot perform save remove() operation here."); 
		/**
		if (currentKey==null)
			return; 

		int index=getKeyIndexOf(currentKey);

		if (index<0)
			return; // currentKey already removed ?

		if (attrSet.containsKey(currentKey))
			attrSet.remove(currentKey);
		//else key already removed ... 

		return;
		 **/  
	}

}
