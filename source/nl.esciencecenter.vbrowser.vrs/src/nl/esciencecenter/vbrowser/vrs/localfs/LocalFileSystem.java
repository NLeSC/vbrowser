package nl.esciencecenter.vbrowser.vrs.localfs;

import java.io.IOException;
import java.util.List;

import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.node.VFSPathNode;
import nl.esciencecenter.vbrowser.vrs.node.VFileSystemNode;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

public class LocalFileSystem extends VFileSystemNode
{
    private FSUtil fsUtil; 
    
    public LocalFileSystem() throws VrsException
    {
        super(new VRL("file:/"));
        fsUtil=new FSUtil();
    }

    @Override
    protected VFSPathNode createVFSNode(VRL vrl) throws VrsException
    {
        
        try
        {
            return new LocalFSPathNode(this,fsUtil.newLocalFSNode(vrl.getPath()));
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e);
        }
    }
    

}
