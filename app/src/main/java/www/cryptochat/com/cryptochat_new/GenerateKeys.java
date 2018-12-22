package www.cryptochat.com.cryptochat_new;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;


public class GenerateKeys implements Serializable {

    transient KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private int keySize;

    public GenerateKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
        keySize=keylength;
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keylength);

    }

    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
        //System.out.println(publicKey.toString());
        //System.out.println(privateKey.toString());
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key, Context context) throws IOException {

        File f = new File(context.getFilesDir(),path);
        FileOutputStream outputStream;
        outputStream = context.openFileOutput(path,context.MODE_PRIVATE);
        outputStream.write(key);
        outputStream.close();

    }

    public void main(Context context) {
        GenerateKeys gk;
        try {
            gk = new GenerateKeys(keySize);
            gk.createKeys();
            gk.writeToFile("publicKey", gk.getPublicKey().getEncoded(),context);
            gk.writeToFile("privateKey", gk.getPrivateKey().getEncoded(),context);
            System.out.println("privateKey: " + privateKey);
            System.out.println("publicKey: " + publicKey);
        } catch (NoSuchAlgorithmException e) {
            Log.i("nope1","nope");
        } catch( NoSuchProviderException e){
            Log.i("nope3","nope");
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.i("nope2",e.toString());
        }

    }
}
