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

package nl.esciencecenter.glite.lfc.internal;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.esciencecenter.glite.lfc.IOUtil;
import nl.esciencecenter.glite.lfc.LFCServer;
import nl.esciencecenter.glite.lfc.Messages;

/** 
 * New Cns Message Class.
 * Simple message container which reads header and body in separate methods. 
 */ 
public class CnsMessage
 {
    public static int DEFAULT_BODY_SIZE=4096; 
    
    private int head=0;
    private int type=0;
    private int sizeOrError=0;
    private boolean headerRead=false; 
    
    /** Use java ByteBuffers and leave optimalization to Java.IO ! */ 
    private byte[] _recievedBuffer=null;
    private ByteArrayOutputStream _sendBuffer; // null -> no body or not read. 

    private DataOutputStream _dataOutputStream;
           
    public CnsMessage()       
    {
        
    }
    
    protected CnsMessage(int magic,int type)
    {
        this.head=magic; 
        this.type=type; 
    }
    
    public CnsMessage(DataInputStream input) throws IOException
    {
        readFrom(input); 
    }
    
    public void readFrom(DataInputStream input) throws IOException
    {
        readHeader(input);
        readBody(input); 
    }
    
    public void readHeader(DataInputStream input) throws IOException
    {
        this.head = input.readInt();
        this.type = input.readInt();
        this.sizeOrError = input.readInt();
        
        this.headerRead=true; 
        
        LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_header,  
                Integer.toHexString( this.head ),
                Integer.toHexString( this.type ),
                new Integer( this.sizeOrError ),
                CnsConstants.getResponseType( this.type  ) ) );
    }
    
    public void readBody(DataInputStream input) throws IOException
    {
        _recievedBuffer=IOUtil.readBytes(input,sizeOrError);  
    }
    
    /** Returns DataInputStream to internal (body) byte array */  
    public DataInputStream getBodyDataInput() throws IOException
    {
        if (_recievedBuffer==null)
            throw new IOException("CnsMessage: Message has NULL body. Has body been read correctly?"); 
            
        return IOUtil.createDataInputStream(_recievedBuffer); 
    }
    
    /** Returns backing bytes array */   
    public byte[] getBodyBytes() throws IOException
    {
        if (_recievedBuffer==null)
            throw new IOException("CnsMessage: Message has NULL body. Has body been read correctly?"); 
            
        return _recievedBuffer; 
    }
    
    public int head() throws IOException
    {
        if (headerRead==false)
            throw new IOException("CnsMessage: Message header has not been read!"); 
        
        return head; 
    }
    
    public int type() throws IOException
    {
        if (headerRead==false)
            throw new IOException("CnsMessage: Message header has not been read!"); 

        return type;
    }

    public boolean isResetSecurityContext() throws IOException
    {
        return (type() == CnsConstants.CNS_RC ); 
    }
    
    /** Returns size of body if current message is not an CNS_RC message */ 
    public int size() throws IOException
    {
        if (headerRead==false)
            throw new IOException("CnsMessage: Message header has not been read!");
        
        // if CNS_RC message =3 integer is ERROR code ! 
        if (this.isResetSecurityContext()==true)
            return 0;
            
        return sizeOrError; 
    }
    
    /** Return error code if current message is an CNS_RC message */ 
    public int error() throws IOException
    {
        if (headerRead==false)
            throw new IOException("CnsMessage: Message header has not been read!"); 

        // if CNS_RC message==3 integer is ERROR code ! 
        if (this.isResetSecurityContext()==false)
            return 0;

        return sizeOrError;
    }
   
    /** Intialize send buffer and return backing sendBuffer */ 
    public DataOutputStream createBodyDataOutput(int initialCapacity)
    {
        this._recievedBuffer=null; 
        // Create Byte Buffer 
        this._sendBuffer=new ByteArrayOutputStream(initialCapacity);
        // wrap around DataOutputStream 
        this._dataOutputStream=new DataOutputStream(_sendBuffer); 
        return _dataOutputStream; 
    }
    
    /** Sends Header+Message Bode to output stream */ 
    public int sendTo(DataOutputStream output) throws IOException
    {
        if (this._sendBuffer==null)
            throw new IOException("CnsMessage: Send Buffer not initialized");
        
        // update body size:
        this.sizeOrError=12+this._sendBuffer.size(); 
        Integer sizeInt=new Integer(sizeOrError); 
        
        LFCServer.staticLogIOMessage( String.format( 
                Messages.lfc_log_send_header, 
                Integer.toHexString( head ),
                Integer.toHexString( type ),
                sizeInt ) );
        
        // Send Header: 
        output.writeInt( head ); //+4
        output.writeInt( type );  //+4
        output.writeInt( sizeOrError );  //+4

        LFCServer.staticLogIOMessage( String.format( 
                Messages.lfc_log_send_body, 
                new Integer(output.size())));

        // Send Body: 
        this._sendBuffer.writeTo(output); 
        
        return 12+sizeOrError;
    }

    public void dispose()
    {
        // ByteBuffers do not need to be flushe nor closed  
        // help garbage collector: clean up an nullify 
        this._dataOutputStream=null;
        this._recievedBuffer=null;
        this._sendBuffer=null;
        this.headerRead=false; 
    }
    
    // ========================================================================
    // Static Factory Message 
    // ========================================================================
    
    
    public static CnsMessage createSendMessage(int magic,int type)
    {
        CnsMessage msg=new CnsMessage(magic,type); 
        // Byte buffer is auto expanding. 
        msg._recievedBuffer=null; 
        msg._sendBuffer=null; 
        
        return msg; 
    }
}
