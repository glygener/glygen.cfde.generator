package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumUtil
{

    public String createMD5(String a_fileNamePath) throws IOException, NoSuchAlgorithmException
    {
        MessageDigest t_digest = MessageDigest.getInstance("MD5");
        String t_checksum = this.getChecksum(t_digest, new File(a_fileNamePath));
        return t_checksum;
    }

    public String createSha256(String a_fileNamePath) throws IOException, NoSuchAlgorithmException
    {
        MessageDigest t_digest = MessageDigest.getInstance("SHA-256");
        String t_checksum = this.getChecksum(t_digest, new File(a_fileNamePath));
        return t_checksum;
    }

    public String getChecksum(MessageDigest a_digest, File a_file) throws IOException
    {
        // Get file input stream for reading the file content
        FileInputStream t_fileInputStream = new FileInputStream(a_file);

        // // Create byte array to read data in chunks
        byte[] t_byteArray = new byte[1000000];
        int t_bytesCounter = 0;

        // Read file data and update in message digest
        while ((t_bytesCounter = t_fileInputStream.read(t_byteArray)) != -1)
        {
            a_digest.update(t_byteArray, 0, t_bytesCounter);
        }

        // close the stream; We don't need it now.
        t_fileInputStream.close();

        // Get the hash's bytes
        byte[] t_bytesDigest = a_digest.digest();

        // Convert it to hexadecimal format
        String t_hex = new BigInteger(1, t_bytesDigest).toString(16);
        // return complete hash
        return t_hex;
    }
}
