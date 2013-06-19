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

package nl.esciencecenter.ptk.io;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.io.LocalFSNode;

public class ITTestFSUtil 
{
	protected static Object testDirMutex=new Object(); 
	
	protected static LocalFSNode testDir=null;

	protected static FSUtil getFSUtil()
	{
	    return FSUtil.getDefault(); 
	}
	
	protected static LocalFSNode getCreateTestDir() throws IOException
	{
		synchronized(testDirMutex)
		{
			if (testDir!=null)
				if (testDir.exists())
					return testDir; 
		
			FSUtil fsUtil = getFSUtil();  
	    
			// setup also tests basic methods ! 
			testDir=fsUtil.newLocalFSNode(GlobalProperties.getGlobalTempDir()+"/testfsutil/"); 

			Assert.assertNotNull("Local test directory is null",testDir);

			if (testDir.exists()==false)
				testDir.mkdir(); 

			Assert.assertTrue("Local test directory MUST exist!",testDir.exists());		

			return testDir; 
		}
	}
	
	@BeforeClass
	public static void setup() throws Exception 
	{
		Assert.assertNotNull("Test Dir must be initialized",getCreateTestDir());
	}
	
	@Test
	public void checkTestDir()
	{
		Assert.assertTrue("Local test directory MUST exist!",testDir.exists());
		Assert.assertTrue("Local test directory MUST is not directory!",testDir.isDirectory());
	}
	
	public FSNode getTestDir()
	{
		return testDir; 
	}
	
	@Test
	public void testCreateDeleteDir() throws Exception
	{
	    FSNode tDir=getTestDir();
	    String path=tDir.getPath(); 
	    String subdir="subdir"; 
	    FSNode subDir=tDir.getNode(subdir);
	    Assert.assertFalse("Subdirectory already exists:"+subDir,subDir.exists()); 
	    subDir.mkdir(); 
	    Assert.assertTrue("Subdirectory must exist after mkdir():"+subDir,subDir.exists());
	    Assert.assertTrue("Subdirectory must be directory after mkdir():"+subDir,subDir.isDirectory());
        if (subDir.isLocal())
        {
            java.io.File jfile=new java.io.File(subDir.getPath());
            Assert.assertTrue("A local created file must be compatible with an existing (local) java.io.File",jfile.exists()); 
            Assert.assertTrue("A local directory must be a real 'Directory' type",jfile.isDirectory()); 
        }

		subDir.delete();  
		Assert.assertFalse("Subdirectory may not exist after delete():"+subDir,subDir.exists());
	}
	
	@Test
    public void testCreateDeleteFile() throws Exception
    {
	    testCreateDeleteFile(getTestDir(),"testFile1");
        testCreateDeleteFile(getTestDir(),"test File1");
    }
	
	public void testCreateDeleteFile(FSNode parent,String fileName) throws Exception
	{
        FSNode tDir=getTestDir();
        String path=tDir.getPath(); 
 
        FSNode file=tDir.getNode(fileName);
        Assert.assertFalse("Test file already exists:"+file,file.exists()); 
        file.create();  
        Assert.assertTrue("Test file must exist after mkdir():"+file,file.exists());
        Assert.assertTrue("Test file be of file type after create():"+file,file.isFile());
        if (file.isLocal())
        {
            java.io.File jfile=new java.io.File(file.getPath());
            Assert.assertTrue("A local created file must be compatible with an existing (local) java.io.File",jfile.exists()); 
            Assert.assertTrue("A local file must be a real 'file' type",jfile.isFile()); 
        }
        
        file.delete();  
        Assert.assertFalse("Test file may not exist after delete():"+file,file.exists());
    }
	
	// ========================================================================
	// Finalize Test Suite: cleanup test dir! 
	// ========================================================================
	
	@Test
	public void removeTestDir() throws IOException
	{
	    try
        {
	        FSNode tDir=getTestDir();
	        getFSUtil().delete(tDir, true); 
	        Assert.assertFalse("Test directory must be deleted after testSuite",tDir.exists()); 
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw e; 
        } 
	}
}
