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

package nl.esciencecenter.vbrowser.vb2.ui.presentation;

import java.awt.Color;
import java.util.Map;

public class AttributePresentation
{
    // === class === //s
    public static class PreferredSizes
    {
        int minimum=-1; 
        int preferred=-1; 
        int maximum=-1; 
    
        public PreferredSizes(int minWidth, int prefWidth, int maxWidth)
        {
            this.minimum=minWidth;
            this.preferred=prefWidth;
            this.maximum=maxWidth;
        }
        
        public int getMinimum()
        {
            return minimum; 
        }
        
        public int getMaximum()
        {
            return maximum; 
        }
        
        public int getPreferred()
        {
            return preferred; 
        }
        
        public int[] getValues()
        {
            return new int[]{minimum,preferred,maximum}; 
        }
    
        /** Set [minimum,preferred,maximum] values */ 
        public void setValues(int[] values)
        {
            this.minimum   = values[0]; 
            this.preferred = values[1]; 
            this.maximum   = values[2]; 
        }
    }

    AttributePresentation.PreferredSizes widths = null;

    Color foreground = null;
    
    Color background = null;

    Map<String, Color> colorMap = null;

    boolean attributeFieldResizable=true; 

    public AttributePresentation.PreferredSizes getWidths()
    {   
        return widths; 
    }
    
    public int[] getWidthValues()
    {   
        if (widths==null)
            return null;
        
        return widths.getValues();  
    }
    
    public void setWidthValues(int[] values)
    {
        if (this.widths==null)
            this.widths=new AttributePresentation.PreferredSizes(values[0],values[1],values[2]); 
        else
            this.widths.setValues(values); 
    }
}