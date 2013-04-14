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

package nl.esciencecenter.vbrowser.vrs.data;

import java.net.URI;
import java.net.URL;
import java.util.Date;

/** 
 * Basic Attribute Types.  
 */
public enum AttributeType
{	
	ANY("Any",Object.class), //   
    BOOLEAN("Boolean",Boolean.class), 
    INT("Integer",Integer.class), 
    LONG("Long",Long.class), 
    FLOAT("Float",Float.class), 
    DOUBLE("Double",Double.class),
    STRING("String",String.class),
    /** Enum type stores it's values as String */ 
    ENUM("Enum",String.class),
    /** Store date time as unified string */ 
    DATETIME("Time",String.class), 
    VRI("VRI",nl.esciencecenter.ptk.net.VRI.class)
    ;
	
	// === // 
	
    private final String enumName;
    
    private final Class<? extends Object> storageClass;
	
	private AttributeType(String name,Class<?> storageClass)
	{
		this.enumName=name;
		this.storageClass=storageClass; 
	}

    public String getName()
    {
        return this.enumName; 
    }
    
    public Class<? extends Object> getStorageClass()
    {
        return this.storageClass;  
    }
	
	public static AttributeType getObjectType(Object object,AttributeType defaultType) 
    {
        if (object==null)
            return defaultType; 
        
        if (object instanceof Integer)
            return AttributeType.INT;
        
        if (object instanceof Boolean)
            return AttributeType.BOOLEAN;
        
        if (object instanceof Long)
            return AttributeType.LONG;
        
        if (object instanceof Float)
            return AttributeType.FLOAT;
        
        if (object instanceof Double)
            return AttributeType.DOUBLE;
        
        if (object instanceof String)
            return AttributeType.STRING;
        
        if (object instanceof Date)
            return AttributeType.DATETIME;
        
        if (object instanceof nl.esciencecenter.ptk.net.VRI)
            return AttributeType.VRI;
        
        if (object instanceof URL)
            return AttributeType.VRI;
        
        if (object instanceof URI)
            return AttributeType.VRI; 
        
        return defaultType; 
    }

}