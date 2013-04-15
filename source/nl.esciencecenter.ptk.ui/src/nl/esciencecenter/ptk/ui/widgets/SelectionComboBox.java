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

package nl.esciencecenter.ptk.ui.widgets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class SelectionComboBox extends JComboBox
{
    private static final long serialVersionUID = -8233146785771708020L;

    //private StringList values;
    
    boolean enumEditable = false; // whether enum types are editable

    public SelectionComboBox()
    {
        super();
        init();
    }

    public SelectionComboBox(String name, String[] vals)
    {
        super();
        setName(name);
        setValues(vals);
    }

    private void init()
    {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        setModel(model);
    }

    public void setValues(String[] values)
    {
        //this.values = new StringList(values);

        if (values == null)
            values = new String[0];

        this.setModel(new DefaultComboBoxModel(values));
    }

    public void addValue(String enumVal)
    {
        ((DefaultComboBoxModel) this.getModel()).addElement(enumVal);
    }

    public void removeValue(String enumVal)
    {
        ((DefaultComboBoxModel) this.getModel()).removeElement(enumVal);
    }

    public void setValue(String txt)
    {
        this.getModel().setSelectedItem(txt);
    }

    public String getName()
    {
        return super.getName();
    }

    public String getValue()
    {
        Object obj = this.getSelectedItem();
        if (obj != null)
            return obj.toString();
        return null;
    }

    public void updateFrom(String value)
    {
        this.setValue(value);
    }

    public void setEditable(boolean flag)
    {
        this.setEditable(flag);
    }


    /**
     * Selectable => drop down option is 'selectable'. optionsEditable = drop
     * down selection entries are editable as well !
     */
    public void setEditable(boolean selectable, boolean optionsEditable)
    {
        this.setEnabled(selectable);
        this.setEditable(optionsEditable);
    }
    
    public String getSelectedItemString()
    {
        Object obj=super.getSelectedItem();
        if (obj==null)
            return null; 
        
        return obj.toString(); 
    }
}
