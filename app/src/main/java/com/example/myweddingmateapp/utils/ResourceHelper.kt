package com.example.myweddingmateapp.utils

import android.util.Log
import com.example.myweddingmateapp.R

object ResourceHelper {

    private val venueImageMap = mapOf(
        "kingsbury" to R.drawable.kingsbury,
        "cinnamon" to R.drawable.cinnamon,
        "waters_edge" to R.drawable.waters_edge,
        "araliya" to R.drawable.araliya
    )


    private val photographyImageMap = mapOf(
        "prabath_photo" to R.drawable.prabath_photo,
        "harsha_photo" to R.drawable.harsha_photo,
        "adeesha_photo" to R.drawable.adeesha_photo,
        "geeshan_photo" to R.drawable.geeshan_photo
    )


    private val bridalWearImageMap = mapOf(
        "amilani" to R.drawable.amilani_bwear,
        "bridezone" to R.drawable.bridezone_bware
    )


    private val beauticianImageMap = mapOf(
        "dhananjaya_bandara" to R.drawable.dhananjaya_bandara,
        "neeliya_mendis" to R.drawable.neeliya_mendis,
        "meylisha" to R.drawable.meylisha,
        "default_beautician" to R.drawable.placeholder_venue

    )

    private val groomWearImageMap = mapOf(
        "ramani" to R.drawable.ramani_brothers,
        "dinesh" to R.drawable.dinesh_clothing,
        "houseOfFashion" to R.drawable.house_of_fashion,
        "britishDress" to R.drawable.british_dress
    )


    private val jewelleryImageMap = mapOf(
        "vogue" to R.drawable.vogue,
        "raja" to R.drawable.raja,
        "mallika" to R.drawable.mallika
    )

    private val entertainmentImageMap = mapOf(
        "dj_ash" to R.drawable.dj_ash,
        "uma_dancing" to R.drawable.uma_dancing,
        "budhawaththa" to R.drawable.budhawaththa
    )

    private val floralImageMap = mapOf(
        "lassana_flora" to R.drawable.lassana_flora,
        "araliya_flora" to R.drawable.araliya_flora,
        "ninety_f_flora" to R.drawable.ninety_f_flora
    )

    private val invitationImageMap = mapOf(
        "card_craft" to R.drawable.card_craft,
        "elegant_invites" to R.drawable.elegant_invites,
        "wedding_card_co" to R.drawable.wedding_card_co,
        "premium_cards" to R.drawable.premium_cards
    )

    private val weddingCarImageMap = mapOf(
        "malkey_car" to R.drawable.malkey_car,
        "cason_car" to R.drawable.cason_car,
        "master_car" to R.drawable.master_car,
        "premium_wedding_cars" to R.drawable.placeholder_venue
    )


    fun getDrawable(resName: String?, type: String? = null): Int {
        if (resName == null) {
            Log.w("ResourceHelper", "Null resource name provided")
            return getDefaultPlaceholder(type)
        }

        return when (type?.lowercase()) {
            "venue" -> venueImageMap[resName]
            "photography" -> photographyImageMap[resName]
            "bridalwear" -> bridalWearImageMap[resName]
            "beauticianbride", "beautician" -> beauticianImageMap[resName]
            "groomwear" -> groomWearImageMap[resName]
            // --- NEW CASES START HERE ---
            "jewellery" -> jewelleryImageMap[resName]
            "entertainment" -> entertainmentImageMap[resName]
            "floral" -> floralImageMap[resName]
            "invitation" -> invitationImageMap[resName]
            "weddingcar" -> weddingCarImageMap[resName]
            // --- NEW CASES END HERE ---
            else -> {
                // If the type is not recognized, try all maps as a last resort
                // or return a generic placeholder with a warning.
                Log.w("ResourceHelper", "Unknown type '$type'. Trying all image maps for '$resName'.")
                venueImageMap[resName]
                    ?: photographyImageMap[resName]
                    ?: bridalWearImageMap[resName]
                    ?: beauticianImageMap[resName]
                    ?: groomWearImageMap[resName]
                    ?: jewelleryImageMap[resName]
                    ?: entertainmentImageMap[resName]
                    ?: floralImageMap[resName]
                    ?: invitationImageMap[resName]
                    ?: weddingCarImageMap[resName]
                    ?: run {
                        Log.w("ResourceHelper", "Image not found for '$resName' in any category map.")
                        getDefaultPlaceholder(type)
                    }
            }
        } ?: run {

            Log.w("ResourceHelper", "Image '$resName' not found for specific type '$type'. Falling back to generic search.")
            getDefaultPlaceholder(type)
        }
    }

    private fun getDefaultPlaceholder(type: String?): Int {
        return R.drawable.placeholder_venue

    }
}