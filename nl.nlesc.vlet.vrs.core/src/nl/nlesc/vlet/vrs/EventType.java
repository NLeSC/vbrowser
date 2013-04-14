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

package nl.nlesc.vlet.vrs;

import java.io.Serializable;

/**
 * Event types supported by the VRS Event Handler.
 */
public enum EventType implements Serializable
{
    /** For testing purposes only */
    NO_EVENT,
    /**
     * New Resource Added. Note that the CHILDS_ADDED event also notifies the
     * creation of new resources but here the getSource() method will return the
     * actual new source.
     */
    NEW_RESOURCE,
    /** Resource has been deleted */
    DELETE,
    /** Resource has a new child list */
    SET_CHILDS,
    /**
     * New child resources added. Note that NEW_RESOURCE event also notifies the
     * creation of new resource(s) but the source (getSource() in this case is
     * the parent resource.
     */
    CHILDS_ADDED,
    /** Child resources deleted */
    CHILDS_DELETED,
    /** If attributes of the specified resource are cached, refresh them all */
    REFRESH,
    /** When a rename occured, the event contains both old and new VRL ! */
    RENAME,
    /** Certain attributes have been changed. */
    SET_ATTRIBUTES,
    /** A SET_BUSY event is used to set busy to true and to false */
    SET_BUSY,
    /** Resource Message Event */
    MESSAGE;
}
