package nl.esciencecenter.vbrowser.vrs.localfs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import nl.esciencecenter.vbrowser.vrs.node.FileAttributes;

public class LocalFileAttributes extends FileAttributes 
{
    protected BasicFileAttributes attrs; 
             
    public LocalFileAttributes(BasicFileAttributes attrs)
    {
        this.attrs=attrs;  
    }
    
    public boolean isSymbolicLink()
    {
        return attrs.isSymbolicLink();
    }
    
    public String getSymbolicLinkTarget()
    {
        return null; 
    }

    public boolean isHidden()
    {
        return false; 
    }

//    public String getPermissionsString()
//    {
//        String str;
//        
//        if (isSymbolicLink())
//        {
//            str="l"; 
//        }
//        else
//        {
//            str = (isDirectory()?"d":"-");
//        }
//        
//        str+=isReadable()?"r":"-"; 
//        str+=isWritable()?"w":"-"; 
//        str+="[";
//        str+=isHidden()?"H":""; 
//        str+="]";
//        return str;  
//    }

    @Override
    public FileTime lastModifiedTime()
    {
        return attrs.lastModifiedTime();
    }

    @Override
    public FileTime lastAccessTime()
    {
        return attrs.lastAccessTime();
    }

    @Override
    public FileTime creationTime()
    {
        return attrs.creationTime();
    }

    @Override
    public boolean isRegularFile()
    {
        return attrs.isRegularFile();
    }

    @Override
    public boolean isDirectory()
    {
       return attrs.isDirectory();
    }

    @Override
    public boolean isOther()
    {
        return attrs.isOther();
    }

    @Override
    public long size()
    {
        return attrs.size(); 
    }

    @Override
    public Object fileKey()
    {
        return attrs.fileKey();
    }

}
