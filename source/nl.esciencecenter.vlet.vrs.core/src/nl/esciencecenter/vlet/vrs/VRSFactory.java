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

package nl.esciencecenter.vlet.vrs;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.vfs.VFSFactory;

/**
 * The VRSFactory class produces instances of VResourceSystem handlers
 * which can handle a resource type and produce instances of VNodes. 
 * It can handle one or more URI schemes (for example 'gftp','gsiftp') and 
 * can create instances of VResourceSystem or VNode.  
 * <p>
 * It is a singleton class and only one instance per Registry will be 
 * used. 
 * Call openResourceSystem() for a ResourceSystem factory or openLocation() 
 * to directly get a remote resource.<br>
 */ 
public abstract class VRSFactory
{
    //=======================================================================
    // Instance: Note that a VRS Class is a singleton class, 
    // it procudes one instance per Registry!  
    //=======================================================================

    protected static VRSFactory instance;

    /** Get Class instance */ 
    protected static VRSFactory getInstance()
    {
        return instance; 
    }

    //=======================================================================
    // Instance  
    //=======================================================================

    /** Enforce public constructor for subclasses ! */ 
    public VRSFactory()
    {
        instance=this; 
    }

    public void init()
    {
       // System.out.println("--- Init VRSFactory! ---"); 
    }
    
    /**
     * Returns handler object (VNode) specified by the location string.   
     */
    public VNode openLocation(VRSContext context,String location) throws VrsException
    {
        return openLocation(context,new VRL(location));
    }

    /** 
     * Update the ServerInfo object with server/resource attributes. 
     * Must always return the updated ServerInfo or create new one 
     * if needed. 
     * <p>
     * Warning:<br>
     * This method is also used when creating a new ResourceNode in MyVle.
     * When a NEW Resource is created. the ServerInfo might be null ! 
     * @param context : VRSContext of SeverInfo object. Get defaults from this context. 
     * @param ServerInfo info : new or previous stored ServerInfo object. 
     *                          if NULL then a new Object must be created with defaults <br> 
     * @param VRL loc : optional location to get extra (resource) attribute from.  
     *                  This parameter might be NULL. 
     * 
     */ 
    protected ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VrsException
    {
        // create defaults: 
        if (info == null)
        {
            info = ServerInfo.createFor(context, loc);
        }
        
        return info; 
    }

    // public abstract void dispose(); --> clear(); 
    public String toString()
    {
        return "VRS:"+this.getName();
    }

    public VNode openLocation(VRSContext context,VRL location) throws VrsException 
    {
        return openResourceSystem(context,location).openLocation(location); 
    }
    
    public String getVersion()
    {
        String vrsInfo="VRS"; 
        
        if (this instanceof VFSFactory)
            vrsInfo="VFS"; 
        
        return vrsInfo+" Plugin:"+getName()+" ("+VletConfig.getVletVersion()+")";  
    }
    
    public String getAbout()
    {
        String vrsInfo="VRS"; 
        
        if (this instanceof VFSFactory)
            vrsInfo="VFS"; 
        
        return "<html><body><center>"
                 +"<table border=0 cellspacing=4 width=600>"
                   + "<tr bgcolor=#c0c0c0><td> <h3> "+vrsInfo+" Plugin:</td><td> "+getName()+"</h3></td></tr>"
                   + "<tr bgcolor=#f0f0f0><td>Version Information:</td><td>"+getVersion()+"</td></tr><br>"
                   + "</table></center></body></html>";
    }

    public String createID(ServerInfo info,String scheme, String hostname, int port)
	{
        //check ServerInfo for ID details!
            
    	if (hostname==null)
    		hostname="";
    	// remember: port -1 => don't care, port==0 => default 
 		return "serverid:" + scheme + "@" + hostname + ":" + port;
	} 

	/** 
	 * Creates ResourceSystem ID. 
	 * Default = scheme+host+port ID from VRL 
	 */
	public String createID(ServerInfo info,VRL loc)
	{
		return createID(info,loc.getScheme(),loc.getHostname(),loc.getPort()); 
	}
	
    public VResourceSystem openResourceSystem(VRSContext context,VRL location) throws VrsException
	{
		// Enter critical region to avoid multithreaded duplication
		// of server objects 
		ServerInfo info=context.getServerInfoFor(location,true);
		// default serverid is created from location
		// Create new Default Server.
		String serverid=createID(info,location); 
		VResourceSystem server = (VResourceSystem) context.getServerInstance(serverid,VResourceSystem.class); 
		
		if (server==null)
		{
			ClassLogger.getLogger(VRSFactory.class).infoPrintf("Creating new ResourceSystem:%s\n",serverid); 
			server=createNewResourceSystem(context,info,location); 
			
			// server.setID(serverid);
			//
			// Store server in Context depened ServerRegistry 
			// for later use 
			context.putServerInstance(serverid,server); 
		}
		
		return server; 
	}
    // ========================================================================
    // Abtract Interface 
    // ========================================================================


    /** 
     * Returns logical name of service, for example GridFTP
     */   
    abstract public String getName();

    /** 
     * Returns list of scheme types it support. 
     * This is the protocol part in an URI.  
     * For example 'file' in 'file:/home/user' or 'srb' for 'srb:///'. 
     * <p>
     * The FIRST scheme name returned will be considered the default scheme. 
     */ 
    public abstract String[] getSchemeNames();

    /**
     * Returns list of Resources or Child types. 
     * For example "File" or "Dir for a VFS Implementation. 
     * No Abstract type may be returned here like VFile or VDir. 
     */ 
    public abstract String[] getResourceTypes();

    /**
     * Instead of a dispose() method, a VRS 
     * has a clear() method, where it cleans up
     * all cached objects/servers. 
     * This method can be used before a dispose, but also
     * can be used to clean up memory and/or reset server
     * connections completely. 
     * Although the object might be disposed after this call, a
     * reconnect or reinitialize might occur as well.  
     */
    public abstract  void clear();
	
	/**
	 * Factory method creating a new ResourceSystem instance. 
	 * Will only be called when a new resource system is needed. 
	 * Instance will be used for similar locations.  
	 * @throws VrsException 
	 */
	abstract public VResourceSystem createNewResourceSystem(VRSContext context,ServerInfo info, VRL location) throws VrsException; 

}
