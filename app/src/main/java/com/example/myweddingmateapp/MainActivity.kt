package com.example.myweddingmateapp
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myweddingmateapp.WishlistActivity
import com.example.myweddingmateapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example: Add a button click or other trigger to open the wishlist
        // For now, let's open the wishlist directly
        startActivity(Intent(this, WishlistActivity::class.java))
        finish()
    }
}