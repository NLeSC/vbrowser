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

package nl.esciencecenter.vlet.vfs.gftp;

import nl.esciencecenter.vlet.exception.NestedIOException;

public class GftpException extends NestedIOException
{
    private static final long serialVersionUID = -57064076580488986L;

    protected GftpException(String message, Throwable cause)
    {
        super("GftpException", message, cause);
    }

    protected GftpException(String name, String message, Throwable cause)
    {
        super("GftpException:" + name, message, cause);
    }

}
