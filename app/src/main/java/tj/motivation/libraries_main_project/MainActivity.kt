package tj.motivation.libraries_main_project

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import tj.otp.pin.master.OTPinMaster

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val otp = findViewById<OTPinMaster>(R.id.otp_test)

        otp.setOnDoneListener { result ->
            /*Check if result is correct
            otp.setIsCorrect(true)
            or
            otp.setIsCorrect(false)
             */
            Toast.makeText(this, "$result", Toast.LENGTH_LONG).show()
        }

        /*Attributes for changing
            otp.setActiveRectangleColor(Color.GREEN)
            otp.setErrorRectangleColor(Color.RED)
            otp.setInActiveRectangleColor(Color.GRAY)
            otp.setRectangleWidth(40)
            otp.setRectangleHeight(60)
            otp.setIsCorrect(true)
            otp.setRectangleCount(5)
            otp.setRectangleSpace(10)
            otp.setTextColor(Color.BLACK)
         */

    }
}