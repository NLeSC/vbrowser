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
 * $Id: ResResourceSystem.java,v 1.5 2011-04-27 12:32:21 ptdeboer Exp $  
 * $Date: 2011-04-27 12:32:21 $
 */ 
// source: 

package nl.nlesc.vlet.gui.lobo.resfs;

import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.VResourceSystem;

public class ResResourceSystem implements VResourceSystem
{
    private VRSContext vrsContext;
    private VRL vrl; 
    
    public ResResourceSystem(VRSContext context, VRL location)
    {
        this.vrsContext=context;
        this.vrl=location.replacePath("/"); 
    }

    @Override
    public VRL resolve(String path) throws VRISyntaxException 
    {
        return vrl.resolve(path);
    }
    
    @Override
    public VRL getVRL()
    {
        return this.vrl; 
    }
    
    //@Override
    public String getID()
    {
        return "res-resource"; 
    }

    //@Override
    public VNode openLocation(VRL vrl) throws VlException
    {
        return new ResFile(this,vrl);
    }

    //@Override
    public VRSContext getVRSContext()
    {
        return vrsContext;
    }

    @Override
    public void connect()
    {
    }
    
    @Override
    public void disconnect()
    {
    }

    @Override
    public void dispose()
    {
    } 
     
}
