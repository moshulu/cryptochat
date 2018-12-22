package www.cryptochat.com.cryptochat_new

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.TextView
import com.google.common.base.Utf8
import java.io.Serializable
import java.security.PrivateKey
import java.util.*

class DisplaySMSActivity : AppCompatActivity(), Serializable {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_sms)

        val cryptography = Cryptography(this)
        var receivingPhoneNumber = ""
        var receivingEncryptedMessage = ""
        var receivingDecryptedMessage = ""
        val intent = this.intent
        val bundle = intent.extras


        val key = bundle!!.getSerializable("key")
        val privateKey = (key as GenerateKeys).privateKey
        println("this is the privateKey in displaysms: $privateKey")

        val broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                //---get the SMS message passed in---
                val bundle = intent?.getExtras()

                var msgs: Array<SmsMessage?>
                if (bundle != null) {
                    //---retrieve the SMS message received---
                    val pdus = bundle.get("pdus") as Array<Any>

                    msgs = arrayOfNulls(pdus.size)
                    for (i in msgs.indices) {
                        msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        receivingPhoneNumber = msgs[i]?.originatingAddress!!
                        receivingEncryptedMessage =  msgs[i]?.messageBody.toString()
                        //start decryption!

                        println(receivingEncryptedMessage)
                        val decryptedMessage = cryptography.decryptText(receivingEncryptedMessage,privateKey)
                        println("decrypted message : $decryptedMessage")
                        receivingDecryptedMessage = decryptedMessage as String
                    }
                    val senderNum = findViewById<TextView>(R.id.senderNum)
                    senderNum.text = receivingPhoneNumber
                    val encryptedMsg = findViewById<TextView>(R.id.encryptedMsg)
                    encryptedMsg.text = receivingEncryptedMessage
                    val decryptedMsg = findViewById<TextView>(R.id.decryptedMsg)
                    decryptedMsg.text = receivingDecryptedMessage

                }
            }
        }
        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onBackPressed(){
        finish()
        startActivity(Intent(this, ChooseAction::class.java))
    }

}
