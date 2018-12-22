package www.cryptochat.com.cryptochat_new

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.widget.Toast
import java.security.PublicKey
import android.support.v4.app.CoreComponentFactory
import com.google.android.gms.common.util.ArrayUtils
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import okio.ByteString.decodeBase64
import java.io.Serializable
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec


class ChooseAction : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_action)


        val writeMessageBtn = findViewById<Button>(R.id.writeMessageBtn)
        val readMessageBtn = findViewById<Button>(R.id.readMessageBtn)

        val key = GenerateKeys(256)
        key.createKeys()
        val publicKey = key.publicKey
        val privateKey = key.privateKey
        println("$privateKey this is the privatekey")
        val cryptography = Cryptography(this)

        val privateKeyEncoded = privateKey.encoded

        println()
        println("public key: " + publicKey)
        println("private key: " + privateKey)
        println()

        var mPhoneNumber = ""

        // Permission is not granted
        //Toast.makeText(this,"permission not granted", Toast.LENGTH_LONG).show()
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                1)
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_PHONE_NUMBERS),
                1)
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                1)
        val tMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mPhoneNumber = tMgr.line1Number

        /* THIS IS HOW TO CORRECTLY WRITE AND READ TO THE DATABASE. DO NOT DELETE */
        //print out the original object
        //println("publicKey object: ${publicKey.toString()}")

        //encode the PublicKey object
        //var publicKeyEncoded = publicKey.encoded

        //convert the encoded PublicKeyObject to an encoded string
        //var publicKeyString = Base64.getEncoder().encodeToString(publicKeyEncoded)
        //println("publicKeyString $publicKeyString")

        //val pubKey = cryptography.pubKeyFromString(publicKeyString)

        //print the decoded string.
        //println("pubKey: $pubKey")
        /* END OF CRITICAL DO NOT DELETE SECTION */

        //encode the PublicKey object
        val publicKeyEncoded = publicKey.encoded

        //convert the encoded PublicKeyObject to an encoded string
        var publicKeyString = Base64.getEncoder().encodeToString(publicKeyEncoded)

//        var message = "hello"
//
//        val encryptedMessage = cryptography.encryptText(message, publicKey)
//        println("encrypted message: $encryptedMessage")
//
//        val decryptedMessage = cryptography.decryptText(encryptedMessage, privateKey)
//        println("decryptedMessage: $decryptedMessage")


        db.collection("users").whereEqualTo("phone_number", mPhoneNumber).get()
                .addOnSuccessListener{
                    var docIdForPhoneNumber = ""
                    for(document in it.documents){
                        docIdForPhoneNumber = document.id
                    }
                    println(docIdForPhoneNumber)
                    try{
                        val writableMap = mutableMapOf<String, Any?>("public_key" to publicKeyString, "phone_number" to mPhoneNumber)
                        db.collection("users").document(docIdForPhoneNumber).set(writableMap)
                    } catch(e: IllegalArgumentException){
                        val writableMap = mutableMapOf<String, Any?>("public_key" to publicKeyString, "phone_number" to mPhoneNumber)
                        db.collection("users").add(writableMap)
                    }
                }

        writeMessageBtn.setOnClickListener {
            val intent = Intent(this, WriteSMSActivity::class.java)
            startActivity(intent)
            finish()
        }

        readMessageBtn.setOnClickListener {
            val intent = Intent(this, DisplaySMSActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("key", key)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }


    }
}
