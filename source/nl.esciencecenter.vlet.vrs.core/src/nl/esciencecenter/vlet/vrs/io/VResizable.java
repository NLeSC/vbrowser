/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vlet.vrs.io;

import java.io.IOException;

/**
 * Interface for (file) resources which length can be manipulated. 
 *  
 * @author Piter T. de Boer 
 */
public interface VResizable extends VSize
{
    // Explicit inheritance from  VSize 
    public long getLength() throws IOException; 

    // Explicit inheritance from  VZeroSizable 
    public void setLengthToZero()  throws IOException;
    
    /**
     * Reset length of file.
     * This method can be used to 'reserve' file space before actually writing to it. 
     * If the file size is increased, the data should be kept. 
     */ 
	public void setLength(long newLength)  throws IOException;
	
}

