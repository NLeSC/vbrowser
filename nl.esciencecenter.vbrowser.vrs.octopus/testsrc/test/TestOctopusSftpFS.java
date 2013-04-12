package test;

import nl.esciencecenter.vbrowser.vrs.octopus.OctopusFSFactory;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.vfs.VDir;
import nl.nlesc.vlet.vfs.VFSClient;
import nl.nlesc.vlet.vfs.VFSNode;
import nl.nlesc.vlet.vrs.VRS;

public class TestOctopusSftpFS
{
    private static VFSClient vfs=null; 
    
    public static VFSClient initOctopusVFS() throws Exception
    {
        if (vfs!=null)
            return vfs; 
        
        VletConfig.init();
        VRS.getRegistry().unregisterVRSDriverClass(nl.nlesc.vlet.vdriver.vfs.localfs.LocalFSFactory.class); 
        VRS.getRegistry().registerVRSDriverClass(OctopusFSFactory.class);
        
        VFSClient vfs=VFSClient.getDefault(); 
        return vfs; 
    }
    
    public static void main(String args[]) throws Exception
    {
        VFSClient vfs=initOctopusVFS(); 
        
        VDir dir = vfs.getDir("sftp://localhost/home/"+VletConfig.getUserName()); 

        System.out.printf(">>> Dir:"+dir); 
        VFSNode[] nodes = dir.list(); 
        
        System.out.printf(">>> Dir.list():\n"); 
        
        for (int i=0;i<nodes.length;i++)
        {
            System.out.printf(" - node[#%d] =%s\n",i,nodes[i]);
            System.out.printf(" - node[#%d].getPath() =%s\n",i,nodes[i].getPath());

        }
        
    }
    
}