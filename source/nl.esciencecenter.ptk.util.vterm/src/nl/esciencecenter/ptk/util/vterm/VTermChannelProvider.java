package nl.esciencecenter.ptk.util.vterm;

import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import nl.esciencecenter.ptk.crypt.Secret;

public class VTermChannelProvider
{
    public static class ChannelOptions
    {
        public boolean useChannelCompression;

        public String channelCompressionType;

        public boolean useChannelXForwarding;

        public String channelXForwardingHost;

        public int channelXForwardingPort;

        @Override
        public String toString()
        {
            return "ChannelOptions [useChannelCompression=" + useChannelCompression + ", channelCompressionType="
                    + channelCompressionType + ", useChannelXForwarding=" + useChannelXForwarding
                    + ", channelXForwardingHost=" + channelXForwardingHost + ", channelXForwardingPort="
                    + channelXForwardingPort + "]";
        }
        
    }

    // ===
    //
    // ===

    protected Map<String,ShellChannelFactory> factories=new Hashtable<String,ShellChannelFactory>(); 

    protected Map<String,ChannelOptions> defaultOptions = new Hashtable<String,ChannelOptions>(); 
    
    public VTermChannelProvider()
    {
    }
    
    public void registerChannelFactory(String type,ShellChannelFactory factory)
    {
        factories.put(type,factory);
    }
    
    public ShellChannel createChannel(String type, URI uri, String username, Secret password, ChannelOptions options)
            throws IOException
    {
        if ("BASH".equals(type))
        {
            return new BASHChannel(uri, options);
        }

        ShellChannelFactory factory=factories.get(type); 
        
        if (factory!=null)
        {
            return factory.createChannel(uri,username,password,options);
        }
        
        throw new IOException("Channel type not supported:" + type + " (when connecting to:" + uri + ")");
    }

    public ChannelOptions getChannelOptions(String type)
    {
        return defaultOptions.get(type); 
    }

    public void setChannelOptions(String type,ChannelOptions newOptions)
    {
        defaultOptions.put(type,newOptions); 
        System.out.println("Channel options:"+newOptions);
    }

}
