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

package nl.nlesc.vlet.error;

/**
 * Causality error when calling methods in the wrong order. For example start()
 * and stop() methods.
 */
public class CausalityError extends VlError
{
    private static final long serialVersionUID = -6434835821934089158L;

    public CausalityError(String msg)
    {
        super(msg);
    }

    public CausalityError(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
