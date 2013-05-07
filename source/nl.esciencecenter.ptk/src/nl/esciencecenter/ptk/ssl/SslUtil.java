package nl.esciencecenter.ptk.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import nl.esciencecenter.ptk.util.logging.ClassLogger;

public class SslUtil
{
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(SslUtil.class); 
    }
    
    public static final int DEFAULT_TIMEOUT = 30000; 
    
    /**
     *  Open SSL ('SSLv3') socket using CertificateStore for trusted certificates. 
     *  
     *  @param cacerts - the trusted certificate store which may contain a user 'private' key.
     *  @param host - the hostname to connect to 
     *  @param port - the port. 
     *  @param open - create and open socket.  
     */
    public static SSLSocket createSSLv3Socket(CertificateStore cacerts,String host, int port, int timeOut,boolean open) throws Exception
    {
        SSLContext sslContext = cacerts.createSSLContext("SSLv3");
        return createSSLSocket(sslContext, host, port, timeOut,open);
    }
    
    /** 
     * Open SSL socket using specified SSLContext. 
     */
    public static SSLSocket createSSLSocket(SSLContext context, String host, int port, int timeOut,boolean open) throws Exception
    {
        SSLSocketFactory factory = context.getSocketFactory();

        logger.debugPrintf("Opening connection to %s:%d...\n", host, port);
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

        if (timeOut <= 0)
            timeOut = DEFAULT_TIMEOUT;
        
        socket.setSoTimeout(timeOut);

        logger.debugPrintf("Starting SSL handshake...\n");
        if (open)
        {
            socket.startHandshake();
            logger.debugPrintf("No errors, certificate is trusted for: %s:%d\n", host, port);
        }
        
        return socket;
    }
}
