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
 * $Id: EnumCellEditor.java,v 1.1 2012/11/18 13:20:35 piter Exp $  
 * $Date: 2012/11/18 13:20:35 $
 */ 
// source: 

package nl.vbrowser.ui.resourcetable;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class EnumCellEditor extends DefaultCellEditor 
{
    private static final long serialVersionUID = -2099371556722425549L;

    public EnumCellEditor(String[] items) 
    {
            super(new JComboBox(items));
    }

    
}
