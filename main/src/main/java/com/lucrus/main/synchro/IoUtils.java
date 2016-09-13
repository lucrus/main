/**
 *
 */
package com.lucrus.main.synchro;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author luca.russo
 */
public class IoUtils {
    private static String KEY = "ACapocchiaRossa9";

    /**
     *
     */
    private IoUtils() {
        super();
    }


    public static byte[] readLocalFile(String path, String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(path + fileName);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        decrypt(fis, bos);
        //byte[] res = new byte[fis.available()];
        //fis.read(res);
        //fis.close();
        //return res;
        return bos.toByteArray();
    }


    public static void saveFile(String fileName, byte[] buffer) throws Exception {
        File f = new File(fileName);
        if (!f.exists()) {
            Log.d("MAIN-CREATE_FILE", fileName);
            f.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(f);
        encrypt(new ByteArrayInputStream(buffer), fos);
        /*
        fos.write(buffer);
        fos.flush();
        fos.close();
        */
    }


    public static void encrypt(InputStream fis, OutputStream fos) throws Exception {
        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec(KEY.getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8192];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }

    public static void decrypt(InputStream fis, OutputStream fos) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8192];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }

    public static byte[] readRemoteFile(String httpUrl, String cookie) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(httpUrl).openConnection();
        con.setRequestProperty("Cookie", cookie);
        InputStream is;
        if (httpUrl.toLowerCase().endsWith("png") ||
                httpUrl.toLowerCase().endsWith("jpg") ||
                httpUrl.toLowerCase().endsWith("pdf") ||
                httpUrl.toLowerCase().contains("/public/")) {
            con.setRequestMethod("GET");
        } else {
            con.setRequestMethod("POST");
        }
        is = con.getInputStream();
        int responseCode = con.getResponseCode();
        if (responseCode >= 400) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int bb = 0;
        byte[] buf = new byte[4096];
        while ((bb = is.read(buf)) != -1) {
            out.write(buf, 0, bb);
        }
        is.close();

        return out.toByteArray();
    }

    public static void saveFileNoCrypt(String url, String cookie, FileOutputStream out) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Cookie", cookie);
        InputStream is = con.getInputStream();
        int bb = 0;
        byte[] buf = new byte[8192];
        while ((bb = is.read(buf)) != -1) {
            out.write(buf, 0, bb);
        }
        is.close();
        out.flush();
        out.close();
    }


    public static void saveFile(String url, String cookie, FileOutputStream out) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Cookie", cookie);
        InputStream is = con.getInputStream();
        encrypt(is, out);
    }


    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }

        if (info.isRoaming()) {
            // here is the roaming option you can change it if you want to disable internet while roaming, just return false
            return true;
        }
        return true;
    }

    public static void unzipAndSave(String relativePath, String baseDir, String zipUrl, String cookie) throws Exception {
        if (!zipUrl.startsWith("http")) {
            throw new Exception("zip Ulr not http");
        }
        String basePath = relativePath + baseDir + "/";
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        HttpURLConnection con = (HttpURLConnection) new URL(zipUrl).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Cookie", cookie);
        InputStream is = con.getInputStream();
        ZipInputStream zs = new ZipInputStream(is);
        ZipEntry ze = zs.getNextEntry();
        while (ze != null) {
            if (ze.isDirectory()) {
                File f = new File(basePath + ze.getName());
                if (!f.exists()) {
                    f.mkdirs();
                }
            } else {
                File f = new File(basePath + ze.getName());
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(f);
                byte[] buf = new byte[8192];
                int count = zs.read(buf);
                while (count > 0) {
                    fos.write(buf, 0, count);
                    count = zs.read(buf);
                }
                fos.flush();
                fos.close();
            }
            ze = zs.getNextEntry();
        }

    }

}
