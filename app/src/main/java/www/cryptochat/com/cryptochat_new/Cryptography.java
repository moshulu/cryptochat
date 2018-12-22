package www.cryptochat.com.cryptochat_new;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.security.KeyChain;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Cryptography {

    int flags =  android.util.Base64.NO_WRAP | android.util.Base64.URL_SAFE;
    private Cipher cipher;

    PublicKey publicKey;
    PrivateKey privateKey;
    private Context context;

    public Cryptography(Context context) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, CertificateException, IOException, KeyStoreException {
        this.cipher = Cipher.getInstance("RSA");
        this.context = context;
    }

    public PublicKey pubKeyFromString(String encodedKeyString) throws Exception {
        byte [] publicBytes = Base64.getDecoder().decode(encodedKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public PrivateKey priKeyFromString(String encodedKeyString) throws Exception {
        //byte [] hold2 = android.util.Base64.decode(encodedKeyString,flags);
        byte [] hold2 = Base64.getDecoder().decode(encodedKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(hold2);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public void encrypt(byte[] input, PublicKey key) throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        System.out.println("encrypted: " + this.cipher.doFinal(input));
    }

    public void decrypt(byte[] input,  PrivateKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        System.out.println("decrypted: " + this.cipher.doFinal(input));
    }


    public String encryptText(String msg, PublicKey key) throws Exception{
        byte [] lastword;
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] h = (msg.getBytes("UTF-8"));
        Log.i("AC test1 ", " "+h.length);
        if(h.length>65&&true==false){
            byte [] total = new byte[0];
            for(int i=0; i<h.length/65+.9999999;i++){
                byte [] hold = Arrays.copyOfRange(h,i*65,(i*65)+64);
                hold = cipher.doFinal(hold);
                byte [] store = new byte [total.length+hold.length];
                System.arraycopy(store, 0, hold, 0, hold.length);
                total=store;
                Log.i("total length"," " + total.length);
            }
            lastword=total;
        }
        else {
            lastword = cipher.doFinal(h);
        }
        return android.util.Base64.encodeToString(lastword,flags);
    }

    public String decryptText(String msg, PrivateKey key)
            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(android.util.Base64.decode(msg,flags)), "UTF-8");
    }

    public PublicKey getPublicKey() throws Exception {
        return publicKey;
    }

    public PrivateKey getPrivateKey() throws Exception {
        return privateKey;
    }
}