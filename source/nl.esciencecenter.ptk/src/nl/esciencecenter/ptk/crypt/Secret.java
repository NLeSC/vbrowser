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

package nl.esciencecenter.ptk.crypt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Store password in to a character array instead of a String 
 * This is slightly more secure, but not much. 
 * It avoids accidental copying of the object or printing out of the password in for example 
 * log files. 
 * Also Password fields in Swing already return char arrays. 
 */
public class Secret
{
    /** 
     * Wrap Secret object around character array.
     * The actual character array is used inside this object. 
     */
    public static Secret wrap(char[] chars)
    {
        Secret secret=new Secret(); 
        secret.secret=chars;
        return secret; 
    }

    // === instance === 
    
    private char[] secret; 

    private Secret()
    {
        
    }
    
    /** 
     * Store secret characters into this object. 
     * The characters are copied into new array. 
     */ 
    public Secret(char[] source)
    {
        init(source,false);
    }
    
    /**
     * Copy secret chars and optionally clear source. 
     * This way the secret content will be move from the source into this Secret object. 
     * 
     * @param source - char array of secret characters 
     * @param clearSource - set to true if source needs to be moved into this Secrect object 
     */
    public Secret(char[] source, boolean clearSource)
    {
        init(source,clearSource); 
    }
    
    protected void init(char[] source,boolean clearSource)
    {
        int n=source.length; 
        this.secret=new char[n]; 
        for (int i=0;i<n;i++)
        {
            secret[i]=source[i]; 
            source[i]=0; 
        }
    }
    
    /**
     * Clear Char Array. 
     * It is recommend to explicitly call dispose() after usage. 
     */
    public void dispose()
    {
        if (secret==null)
            return; 
        int n=secret.length; 
        for (int i=0;i<n;i++)
            secret[i]=0; 
        secret=null; 
    }
    
    /** 
     * Returns clone of secret character array. 
     */
    public char[] getChars()
    {
        return secret.clone();
    }
    
    public void finalize()
    {
        dispose(); 
    }
    
    public String toString()
    {
        return "<Secret!>"; 
    }

    /** 
     * Encode characters to bytes using specified charSet and return as ByteBuffer. 
     * It is recommend to clear the ByteBuffer after use. 
     */ 
    public ByteBuffer toByteBuffer(String charSetName)
    {
        return toByteBuffer(Charset.forName(charSetName));
    }
    
    /** 
     * Encode characters to bytes using specified charSet and return as ByteBuffer. 
     * It is recommend to clear the ByteBuffer after use. 
     */ 
    public ByteBuffer toByteBuffer(Charset charSet)
    {
        CharBuffer cbuff=CharBuffer.wrap(secret); 
        ByteBuffer buf=charSet.encode(cbuff);
        return buf; 
    }
    
    public boolean isEmpty()
    {
        if (secret==null)
           return true; 

        if (secret.length<=0)
            return true; 
        
        return false; 
    }
}
