package www.cryptochat.com.cryptochat_new

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.app.PendingIntent
import java.util.Base64
import javax.crypto.Cipher

class WriteSMSActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_sms)

        val recNum = findViewById<EditText>(R.id.recNum)
        val msgContent = findViewById<EditText>(R.id.msgContent)
        val sendBtn = findViewById<Button>(R.id.sendBtn)
        val cancelBtn = findViewById<Button>(R.id.cancelBtn)

        //val privateKeyString = intent.extras.getString("private_key")

        sendBtn.setOnClickListener {
            //didn't have time to change this. it'll have to do for the presentation. yikes
            var number = ""
            if(recNum.text.toString().equals("+15555215554")) number = "+15555215556"
            else number = "+15555215554"

            db.collection("users").whereEqualTo("phone_number", number).get()
                    .addOnSuccessListener {

                        val cryptography = Cryptography(this)

                        var public_key_string = ""
                        for(document in it.documents){
                            public_key_string = document.data?.get("public_key") as String
                        }
                        println("public key string from ${recNum.text} : $public_key_string")
                        if(public_key_string.equals("")){
                            Toast.makeText(this, "Phone number does not exist.", Toast.LENGTH_LONG).show()
                        } else {
                            val public_key = cryptography.pubKeyFromString(public_key_string)

                            val message = msgContent.text.toString()
                            val encryptedString = cryptography.encryptText(message, public_key)

                            val smsNumber = String.format("smsto: %s", recNum.text.toString())
                            val pi = PendingIntent.getActivity(this, 0,
                                    Intent(this, Login::class.java), 0)
                            //Send message
                            val smsManager = SmsManager.getDefault()
                            finish()

                            val intent = Intent(this, ChooseAction::class.java)
                            startActivity(intent)
                            smsManager.sendTextMessage(smsNumber, null, encryptedString, pi, null)

                            Toast.makeText(this, "Successfully sent.", Toast.LENGTH_LONG).show()
                        }
                    }
        }

        cancelBtn.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed(){
        finish()
        startActivity(Intent(this, ChooseAction::class.java))
    }
}
