package es.usj.drodriguez.movieapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_contact.*

class Contact : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        toolbar_contact.setNavigationOnClickListener {
            finish()
        }
        btn_call.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${getString(R.string.phone)}")
            })
        }
        btn_mail.setOnClickListener {
           startActivity(Intent(Intent.ACTION_SENDTO).apply {
               data = Uri.parse("mailto:${getString(R.string.mail)}")
           })
        }
        btn_telegram.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/${getString(R.string.telegramUser)}")))
        }
    }
}