package com.pandu.allocap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pandu.allocap.data.model.AllocationSettings
import com.pandu.allocap.data.model.Transaction

@Database(entities = [Transaction::class, AllocationSettings::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun allocationDao(): AllocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "allocap_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
