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

package nl.nlesc.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.nlesc.glite.lfc.IOUtil;
import nl.nlesc.glite.lfc.LFCServer;



/**
 * Request creation of a Symbolic Link. 
 * Uses new CnsMessage class.
 *  
 * @author Piter T. de Boer 
 */
public class CnsSymLinkRequest  // extends AbstractCnsRequest 
{
  private int uid;
  private int gid;
  private long cwd;
  private String sourcePath;
  private String newLinkPath;

  /**
   * Creates request for renaming the LFC entry.
   * @param oldPath 
   * @param newPath 
   * 
   */
  public CnsSymLinkRequest( final String source, final String newlink)
  {
    this.sourcePath = source;
    this.newLinkPath = newlink;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
    
  }

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param out output stream to which request will be written
   * @param in input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsRenameResponse
   */
  public CnsVoidResponse sendTo( final DataOutputStream output, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( "sending SymLink: " + this.sourcePath +"<="+this.newLinkPath ); 
    
    CnsMessage sendMsg=CnsMessage.createSendMessage(
                    CnsConstants.CNS_MAGIC,    
                    CnsConstants.CNS_SYMLINK); 

    DataOutputStream bodyOut = sendMsg.createBodyDataOutput(4096); // auto expands 
    
    bodyOut.writeInt( this.uid );  // user id [4b]
    bodyOut.writeInt( this.gid );  // group id [4b]
    bodyOut.writeLong( this.cwd ); // I have no idea what is this [8b] 
    IOUtil.writeString(bodyOut,this.sourcePath); // 1+length in bytes 
    IOUtil.writeString(bodyOut,this.newLinkPath); // 1+length in bytes 
   
    // Send
    sendMsg.sendTo(output); 
    output.flush();
    sendMsg.dispose(); // help garbage collection ! 
    
    // Receive: 
    CnsVoidResponse result = new CnsVoidResponse();
    result.readFrom( in );
    return result;
    
  }
  
//  /**
//   * <p>Sends prepared request to the output stream and then fetch the response</p>
//   * 
//   * @param out output stream to which request will be written
//   * @param in input stream from which response will be read
//   * @return object that encapsulates response
//   * @throws IOException in case of any I/O problem
//   * @see CnsRenameResponse
//   */
//  public CnsVoidResponse sendTo( final DataOutputStream out, final DataInputStream in )
//    throws IOException  {
//    
//    LFCServer.staticLogIOMessage( "sending SymLink: " + this.sourcePath +"<="+this.newLinkPath ); 
//    
//    int messageSize= 30 + IOUtil.byteSize(sourcePath,newLinkPath); 
//    
//    this.sendHeader( out,                       // header [12b]
//                     CnsConstants.CNS_MAGIC,    
//                     CnsConstants.CNS_SYMLINK,    
//                     messageSize); 
//    
//    out.writeInt( this.uid );  // user id [4b]
//    out.writeInt( this.gid );  // group id [4b]
//    out.writeLong( this.cwd ); // I have no idea what is this [8b] 
//    IOUtil.writeString(out,this.sourcePath); // 1+length in bytes 
//    IOUtil.writeString(out,this.newLinkPath); // 1+length in bytes 
//    
//    out.flush();
//    
//    // PdB: New message, added check: 
//    this.assertMessageLength(out, messageSize);
//    // reuse void response: 
//    CnsVoidResponse result = new CnsVoidResponse();
//    result.readFrom( in );
//    return result;
//    
//  }
}
