package es.usj.drodriguez.movieapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.usj.drodriguez.movieapp.databinding.ActivityContactBinding

class Contact : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarContact.setNavigationOnClickListener {
            finish()
        }
        binding.btnCall.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${getString(R.string.phone)}")
            })
        }
        binding.btnMail.setOnClickListener {
           startActivity(Intent(Intent.ACTION_SENDTO).apply {
               data = Uri.parse("mailto:${getString(R.string.mail)}")
           })
        }
        binding.btnTelegram.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/${getString(R.string.telegramUser)}")))
        }
    }
}