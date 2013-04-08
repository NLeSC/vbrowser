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

package nl.uva.vlet.vrs.io;

import java.io.IOException;

/** 
 * Single method interface to detect whether the length of a resource 
 * can be set to zero. 
 * Some resources or file systems cannot set the length, but can truncate 
 * or reset the file size to zero. 
 * This a legacy interface used for optimization in the case where deleting and 
 * recreating the file takes longer then just resetting the file size to zero.
 * <p>  
 * This method can be used when the meta data attributes of the resource
 * needs to be kept, but a new (empty) resource needs to be created before writing to it. 
 */

public interface VZeroSizable 
{
    /**
     * Reset file length to zero, but keep permissions and other (meta)data 
     * attributes. 
     * @throws VlException
     */
	public void setLengthToZero()  throws IOException;
}

