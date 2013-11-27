package nl.esciencecenter.ptk.web.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.web.PutMonitor;

import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

/** 
 * File Body which uses FSNodes. 
 * Based on Apache FileBody. 
 * Example how to upload custom Object types. 
 * Since FSNode can be sub-classed, any file type can be uploaded using this Content Body Type.  
 */
public class FSNodeBody  extends AbstractContentBody 
{
    private final FSNode fsNode; 
    
    private long totalWritten=0;

    private PutMonitor putMonitor=null;
    
    private int defaultChunkSize=4096; 
    
    public FSNodeBody(final FSNode node, final String mimeType, PutMonitor putMonitor) 
    {
        super(mimeType);
        this.putMonitor=putMonitor; 
        
        if (node == null) 
        {
            throw new IllegalArgumentException("File may not be null");
        }
        
        this.fsNode = node;
    }
    
    public FSNodeBody(final FSNode node) 
    {
        this(node, "application/octet-stream",null);
    }
    
    public FSNodeBody(final FSNode node, PutMonitor putMonitor) 
    {
        this(node, "application/octet-stream",putMonitor);
    }
    
    public InputStream getInputStream() throws IOException 
    {
        return fsNode.createInputStream(); 
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException 
    {
        if (out == null) 
        {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        
        InputStream in = fsNode.createInputStream(); 
        
        try
        {
            byte[] tmp = new byte[defaultChunkSize];
            
            int numRead=0;
            
            while ((numRead = in.read(tmp)) >=0 )
            {
                out.write(tmp, 0, numRead);
                
                if (numRead==0)
                {
                    // micro sleep: allow IO to happen when thread sleeps. 
                    try
                    {
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                totalWritten+=numRead; 
                logPrintf("Total written=%d\n",totalWritten);

                if (putMonitor!=null)
                {
                    putMonitor.bytesWritten(totalWritten);
                }
            }
            
            out.flush();
            
            if (putMonitor!=null)
            {
                putMonitor.bytesWritten(totalWritten);
                putMonitor.putDone(); 
            }
            
            logPrintf("Done: Total written=%d\n",totalWritten); 
        } 
        finally
        {
            in.close();
        }
    }

    public String getTransferEncoding()
    {
        return MIME.ENC_BINARY;
    }

    public String getCharset() 
    {
        return null;
    }

    public long getContentLength() 
    {
        try
        {
            return this.fsNode.getFileSize();
        }
        catch (IOException e)
        {
            return 0; 
        }
    }
    
    public String getFilename()
    {
        return this.fsNode.getBasename();
    }
    
    public long getProgress()
    {
        return totalWritten; 
    }
    
    protected void logPrintf(String format,Object... args)
    {
        // Delegate to monitor: 
    }
    
}

