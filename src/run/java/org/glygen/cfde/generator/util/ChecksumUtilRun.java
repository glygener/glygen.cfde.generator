package org.glygen.cfde.generator.util;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ChecksumUtilRun
{

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException
    {
        String t_file = "e:\\Linux\\kali-linux-2021.2-installer-amd64.iso";
        ChecksumUtil t_util = new ChecksumUtil();

        long time = System.currentTimeMillis();

        String t_md5 = t_util.createMD5(t_file);
        System.out.println(t_md5);
        System.out.println(t_md5.equalsIgnoreCase("7A9ECEDE3F2665179F4D9C6B5217C846"));

        String t_sha256 = t_util.createSha256(t_file);
        System.out.println(t_sha256);
        System.out.println(t_sha256.equalsIgnoreCase(
                "EF83BAFE1F19088666A8080D7EA07BD4F8DA2FDA0FCB3EF5F2CE2658F349C119"));

        System.out.println(System.currentTimeMillis() - time);
    }

}
// 1K = 49603
// 1M = 29029