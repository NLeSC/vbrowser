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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.esciencecenter.ptk.data.Duplicatable;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

/**
 * String Hasher Util class.
 * 
 * Salting:<br>
 * If a salt is specified the byte value of the salt string is added as extra digest after processing the source text. 
 */
public class StringHasher implements Cloneable, Duplicatable<StringHasher>
{
    public final static String SHA_256 = "SHA-256"; 
    public final static String SHA_1 = "SHA-1"; 
    public final static String MD5 = "MD5"; 
    
    // ========================================================================
    //
    // ========================================================================
    
    // public static enum HashType 
    
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(StringHasher.class);
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    private MessageDigest messageDigest; 
    
    private Charset charSet=null;

    // private byte[] salt=null;

    private boolean debug;

    protected StringHasher()
    {
    }
    
    public StringHasher(String hashType) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        init(hashType,"UTF8");
    }
    
    public StringHasher(String hashType,String charSet) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        init(hashType,charSet);
    }

    public StringHasher clone()
    {
        StringHasher hasher=new StringHasher(); 
        hasher.copyFrom(this); 
        return hasher; 
    }
    
    protected void init(String hashType,String encoding) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        setEncoding(encoding);
        setHashType(hashType);
        
        this.debug=logger.hasEffectiveLevel(ClassLogger.DEBUG);// extra field to limit debugging overhead.
    }
    
    protected void copyFrom(StringHasher other)
    {
        this.charSet=other.charSet; 
        this.debug=other.debug;
        
        if (other.messageDigest!=null)
        {
            // exception free clone:
            try
            {
                this.messageDigest=MessageDigest.getInstance(other.messageDigest.getAlgorithm());
            }
            catch (NoSuchAlgorithmException e)
            {
               logger.logException(ClassLogger.FATAL,e,
                "Impossible Exception:Already existing MessageDigest Instance returns NoSuchAlgorithmException"
                +"of its own algorithm:"+other.messageDigest.getAlgorithm()); 
            } 
        }
    }
    
    public void setHashType(String hashType) throws NoSuchAlgorithmException
    {
        messageDigest = MessageDigest.getInstance(hashType);
        
        if ( (StringUtil.equalsIgnoreCase(hashType,MD5)) 
              || (StringUtil.equalsIgnoreCase(hashType,SHA_1)) )
              logger.warnPrintf("Don't use outdated MD5 od SHA-1\n");         
    }
    
    public void setEncoding(String charSetStr) throws UnsupportedEncodingException
    {
        charSet=Charset.forName(charSetStr);
        
        if (charSet==null)
            throw new UnsupportedEncodingException("No such Character Encoding:"+charSetStr);
    }
    
    public Charset getEncoding()
    {
        return this.charSet; 
    }
    
    public void reset()
    {
        messageDigest.reset(); 
    }
    
    public String createFileChecksum(String checksumType,String filename) throws IOException 
    {
        reset(); 
        
        FileInputStream fis = new FileInputStream(filename);

        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1)
        {
            messageDigest.update(dataBytes, 0, nread);
        }
        
        byte[] mdbytes = messageDigest.digest();

        return StringUtil.toHexString(mdbytes);
    }
   
    /**
     * Create hash from provided text. 
     * The hash length (in bytes) will be limited to maxhashLen. 
     * The remainder of the hash will be exored with the actual truncated hash.  
     * @param text - String to hash 
     * @param maxHashLen - maximum hash length in bytes or -1. 
     * @return hash - in hexidecimal String format, without "0x" prefixed.  
     */
    public String createHashString(String text, String salt,int maxHashLen) 
    {
        byte digest[]=hash(text,true,salt,false); 
                
        if (maxHashLen>0)
        {
            digest=truncate(digest,maxHashLen); 
        }
        
        return StringUtil.toHexString(digest);
    }

    /** 
     * Truncate the hash bytes (digest) and exor the remaining bytes with the beginning.  
     */
    public byte[] truncate(byte digest[],int maxLen)
    {
        byte subDigest[]=new byte[maxLen];
        for (int i=0;i<maxLen;i++)
            subDigest[i]=0; // clear
        // truncate hash and exor the remainder: 
        for (int i=0;i<digest.length;i++)
            subDigest[i%maxLen]^=digest[i];
        
        if (debug)
            logger.debugPrintf("Truncating digest: %s -> %s\n",StringUtil.toHexString(digest),StringUtil.toHexString(subDigest));
    
        return subDigest;
    }

    /**
     * Create hash from provided text. 
     * The hash length (in bytes) will be limited to maxhashLen. 
     * The remainder of the hash will be exored with the actual truncated hash.  
     * @param text - String to hash 
     * @param maxHashLen - maximum hash length in bytes or -1. 
     * @return hash - base64 encoded hash value. 
     */
    public String createHashBase64String(String text, String salt, int maxHashLen) 
    {
        byte digest[]=hash(text,true,salt,false); 
        
        if (maxHashLen>0)
        {
            digest=truncate(digest,maxHashLen); 
        }
        
        return StringUtil.base64Encode(digest);
    }

    public byte[] hash(String text,boolean reset,String saltText,boolean prefixHash) 
    {
        return hash(text.getBytes(charSet),reset,saltText.getBytes(charSet),prefixHash); 
    }
    
    /** 
     * Actual hash method. All other methods call this one.
     * @param sourceBytes[] - actual bytes to create hash from. 
     * @param reset - reset state of hasher before applied hash. 
     * @param bashBytes[] - optional hash bytes to add. 
     * @param prefixHash - if true hash is applied before hashing the actual bytes 
     *                      if false, the source bytes are hashed first, then the hashBytes are digested.  
     */ 
    public byte[] hash(byte sourceBytes[],boolean reset,byte saltBytes[],boolean prefixHash)
    {
        if (reset)
            reset();
        
        if (sourceBytes==null)
            throw new NullPointerException("Source bytes can't be NULL!"); 
        
        if ((saltBytes!=null) && (prefixHash==true))
            messageDigest.update(saltBytes);
        
        messageDigest.update(sourceBytes);
        
        if ((saltBytes!=null) && (prefixHash==false))
            messageDigest.update(saltBytes);
        
        byte byteData[] = messageDigest.digest();
 
        return byteData; 
    }
    
    public String createHashString(String text)
    {
        byte digest[]=hash(text,true,null,false); 
        return StringUtil.toHexString(digest, true); 
    }

    /** 
     * Create Hash from String and return hash value as Base64 encoded String.
     * When using SHA-256 these 32 bytes will be encode to 48 characters. */  
    public String createHashBase64String(String text,String salt)
    {
        byte digest[]=hash(text,true,salt,false); 
        return StringUtil.base64Encode(digest); 
    }
    
    /** 
     * Create Hash from byte array and return hash value as Base64 encoded String.
     * When using SHA-256 these 32 bytes will be encode to 48  */  
    public String createHashBase64String(byte bytes[],byte saltBytes[])
    {
        byte digest[]=hash(bytes,true,saltBytes,false); 
        return StringUtil.base64Encode(digest); 
    }
    
    @Override
    public boolean shallowSupported()
    {
        return false;
    }

    @Override
    public StringHasher duplicate()
    {
        return clone();
    }

    @Override
    public StringHasher duplicate(boolean shallow)
    {
        return clone(); 
    }


  
}

