package com.example.myweddingmateapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.myweddingmateapp.models.Review
import com.example.myweddingmateapp.models.Service
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "WeddingMateDB"
        private const val DATABASE_VERSION = 2
        private const val TABLE_SERVICES = "services"
        private const val TABLE_PORTFOLIO = "portfolio"
        private const val TABLE_REVIEWS = "reviews"
        private const val KEY_ID = "id"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_SERVICE_NAME = "name"
        private const val KEY_SERVICE_DESC = "description"
        private const val KEY_SERVICE_PRICE = "price"
        private const val KEY_PORTFOLIO_IMAGE = "image"
        private const val KEY_REVIEW_TITLE = "title"
        private const val KEY_REVIEW_COMMENT = "comment"
        private const val KEY_REVIEW_RATING = "rating"
        private const val KEY_REVIEW_USER_NAME = "user_name"
    }

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_SERVICES(
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_USER_ID TEXT NOT NULL,
                $KEY_SERVICE_NAME TEXT NOT NULL,
                $KEY_SERVICE_DESC TEXT NOT NULL,
                $KEY_SERVICE_PRICE TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_PORTFOLIO(
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_USER_ID TEXT NOT NULL,
                $KEY_PORTFOLIO_IMAGE BLOB NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_REVIEWS(
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_USER_ID TEXT NOT NULL,
                $KEY_REVIEW_TITLE TEXT NOT NULL,
                $KEY_REVIEW_COMMENT TEXT NOT NULL,
                $KEY_REVIEW_RATING REAL NOT NULL,
                $KEY_REVIEW_USER_NAME TEXT NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SERVICES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PORTFOLIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REVIEWS")
        onCreate(db)
    }

    fun addService(service: Service): Long {
        val currentUserId = auth.currentUser?.uid ?: return -1
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_USER_ID, currentUserId)
            put(KEY_SERVICE_NAME, service.name)
            put(KEY_SERVICE_DESC, service.description)
            put(KEY_SERVICE_PRICE, service.price)
        }
        return db.insert(TABLE_SERVICES, null, values).also { db.close() }
    }

    fun getServicesForCurrentUser(): List<Service> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        val db = readableDatabase
        val cursor = db.query(TABLE_SERVICES, null, "$KEY_USER_ID = ?", arrayOf(currentUserId), null, null, null)
        return mutableListOf<Service>().apply {
            if (cursor.moveToFirst()) {
                do {
                    add(Service(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERVICE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERVICE_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERVICE_PRICE))
                    ))
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        }
    }

    fun addPortfolioImage(bitmap: Bitmap): Long {
        val currentUserId = auth.currentUser?.uid ?: return -1
        val db = writableDatabase
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val values = ContentValues().apply {
            put(KEY_USER_ID, currentUserId)
            put(KEY_PORTFOLIO_IMAGE, stream.toByteArray())
        }
        return db.insert(TABLE_PORTFOLIO, null, values).also { db.close() }
    }

    fun getPortfolioForCurrentUser(): List<Bitmap> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        val db = readableDatabase
        val cursor = db.query(TABLE_PORTFOLIO, null, "$KEY_USER_ID = ?", arrayOf(currentUserId), null, null, null)
        return mutableListOf<Bitmap>().apply {
            if (cursor.moveToFirst()) {
                do {
                    BitmapFactory.decodeByteArray(
                        cursor.getBlob(cursor.getColumnIndexOrThrow(KEY_PORTFOLIO_IMAGE)),
                        0,
                        cursor.getBlob(cursor.getColumnIndexOrThrow(KEY_PORTFOLIO_IMAGE)).size
                    )?.let { add(it) }
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        }
    }

    fun addReview(review: Review, userId: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_USER_ID, userId)
            put(KEY_REVIEW_TITLE, review.title)
            put(KEY_REVIEW_COMMENT, review.comment)
            put(KEY_REVIEW_RATING, review.rating)
            put(KEY_REVIEW_USER_NAME, review.userName)
        }
        return db.insert(TABLE_REVIEWS, null, values).also { db.close() }
    }

    fun getReviewsForUser(userId: String): List<Review> {
        val db = readableDatabase
        val cursor = db.query(TABLE_REVIEWS, null, "$KEY_USER_ID = ?", arrayOf(userId), null, null, null)
        return mutableListOf<Review>().apply {
            if (cursor.moveToFirst()) {
                do {
                    add(Review(
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_REVIEW_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_REVIEW_COMMENT)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_REVIEW_RATING)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_REVIEW_USER_NAME))
                    ))
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        }
    }
}