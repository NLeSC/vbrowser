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

package nl.esciencecenter.ptk.data;

import nl.esciencecenter.ptk.util.StringUtil;

import org.junit.Assert;
import org.junit.Test;

public class Test_BytesAndBigIntegers 
{
    
    @Test
    public void test16bytesBigIntegerMax()
    {
        // create 16 bytes little endian unsinged integer. 
        byte bytes[]=new byte[16]; 
        int n=16;
        
        for (int i=0;i<n;i++)
        {
            bytes[i]=(byte)0x00ff;  
        }

        // for a all 0xff byte sequence, endianity doesn't matter. 
        testBigIntegerString("340282366920938463463374607431768211455",bytes,false,false); 
        testBigIntegerString("340282366920938463463374607431768211455",bytes,false,true); 
    }
    
    @Test
    public void test24bytesBigInteger()
    {
        // create 16 bytes unsinged integer. 
        byte bytes[]=new byte[24]; 
        int n=24;
        
        for (int i=0;i<n;i++)
        {
            bytes[i]=(byte)0x00ff;  
        }
        
        testBigIntegerString("6277101735386680763835789423207666416102355444464034512895",bytes,false,false); 
        testBigIntegerString("6277101735386680763835789423207666416102355444464034512895",bytes,false,true); 
    }
    
    private void testBigIntegerString(String expected, byte[] bytes,boolean signed, boolean isLE)
    {
        // actual endianity 
        String bigStr=StringUtil.toBigIntegerString(bytes,signed, isLE); 
        Assert.assertEquals("Big Endian BigInteger string doesn't match expected",expected, bigStr); 
        
        byte reverseEndian[];
        
        // little endian:
        if (bytes==null)
        {
            reverseEndian=null;
        }
        else
        {
            int len=bytes.length; 
            reverseEndian=new byte[len];
            for (int i=0;i<len;i++)
                reverseEndian[len-i-1]=bytes[i];
        }
        String revStr=StringUtil.toBigIntegerString(reverseEndian, false,(isLE==false)); 
        Assert.assertEquals("Little Endian BigInteger string doesn't match expected",expected, revStr); 
    } 
    
    
}