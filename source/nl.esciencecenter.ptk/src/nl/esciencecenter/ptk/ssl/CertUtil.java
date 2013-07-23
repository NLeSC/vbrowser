package nl.esciencecenter.ptk.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.io.FileURISyntaxException;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.util.StringUtil;

public class CertUtil
{
    /**
     * Create DER Encoded Certificate from String. 
     * Certificate String needs to be between:
     * 
     * <pre>
     * -----BEGIN CERTIFICATE-----
     * ...  
     * -----END CERTIFICATE-----
     * </pre>
     * @return X509Certificate
     * @throws UnsupportedEncodingException 
     * @throws CertificateException 
     */
    public static X509Certificate createDERCertificateFromString(String derEncodedString) throws UnsupportedEncodingException, CertificateException 
    {
        byte bytes[] = derEncodedString.getBytes("ASCII"); // plain aksii
        ByteArrayInputStream binps = new ByteArrayInputStream(bytes);
    
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate x590 = (X509Certificate) cf.generateCertificate(binps);
    
        return x590;
    }

    /** 
     * Load .pem Certificate file 
     * @return X509Certificate
     * @throws IOException 
     * @throws FileURISyntaxException 
     * @throws CertificateException 
     */
    public static X509Certificate loadPEMCertificate(String filename) throws FileURISyntaxException, IOException, CertificateException 
    {
        CertificateStore.logger.debugPrintf("Loading (PEM) Certificate :%s\n", filename);
    
        String pemStr = FSUtil.getDefault().readText(filename);
    
        int index = pemStr.indexOf("-----BEGIN CERTIFICATE");
    
        if (index < 0)
            throw new IOException("Couldn't find start of (DER) certificate!\n---\n" + pemStr);
    
        // Get DER part
        String derStr = pemStr.substring(index);
        return createDERCertificateFromString(derStr);
    }

    /**
     * Load DER encoded Certificate .cer .crt .der
     * @return X509Certificate 
     * @throws IOException 
     * @throws FileURISyntaxException 
     * @throws CertificateException 
     */
    public static X509Certificate loadDERCertificate(String filename) throws FileURISyntaxException, IOException, CertificateException 
    {
        CertificateStore.logger.debugPrintf("Loading (DER ENCODED) Certificate :%s\n", filename);
    
        FSUtil fsUtil = FSUtil.getDefault();
        InputStream finps = fsUtil.getInputStream(filename);
    
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate x590 = (X509Certificate) cf.generateCertificate(finps);
    
        // sun.security.x509.X509CertImpl x590=new
        // sun.security.x509.X509CertImpl(finps);
        // use basename as default certificate alias (more can be used). 
        String alias = URIFactory.basename(filename);
    
        CertificateStore.logger.debugPrintf("+++ Adding cert file: %s\n", filename);
        CertificateStore.logger.debugPrintf(" -  Alias    = %s\n", alias);
        CertificateStore.logger.debugPrintf(" -  Subject  = %s\n", x590.getSubjectDN().toString());
        CertificateStore.logger.debugPrintf(" -  Issuer   = %s\n", x590.getIssuerDN().toString());
        return x590;
    }

    public static String toString(X509Certificate cert, String indent, String eolStr) throws NoSuchAlgorithmException, CertificateEncodingException
    {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        sha1.update(cert.getEncoded());
        sha1.update(cert.getEncoded());
            
        String certStr;
        certStr   = indent + "Subject :" + cert.getSubjectDN() + eolStr;
        certStr += indent + "Issuer  :" + cert.getIssuerDN() +  eolStr;
        certStr += indent + "sha1    :" + StringUtil.toHexString(sha1.digest(), true) + eolStr;
        certStr += indent + "md5     :" + StringUtil.toHexString(md5.digest(), true) + eolStr; 

        return certStr; 
    }


}
