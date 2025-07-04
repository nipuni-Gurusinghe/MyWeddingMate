package com.example.myweddingmateapp

import android.os.Bundle

class VendorSelectionActivity : BaseActivity() {

    override fun getCurrentNavId(): Int = R.id.navWishlist

    override fun getLayoutResourceId(): Int = R.layout.activity_vendor_selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize your vendor selection-specific UI components here
        initializeVendorComponents()
    }

    private fun initializeVendorComponents() {
        // vendor selection activity initialization
    }
}