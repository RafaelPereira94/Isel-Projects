package pdm.isel.pt.yawa.models.content

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbOpenHelper(context: Context?) : SQLiteOpenHelper(context, DbSchema.DB_NAME, null, DbSchema.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        createDb(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        deleteDb(db)
        createDb(db)
    }

    private fun createDb(db: SQLiteDatabase?) {
        db!!.execSQL(DbSchema.Weather.DDL_CREATE_TABLE)
    }

    private fun deleteDb(db: SQLiteDatabase?) {
        db!!.execSQL(DbSchema.Weather.DDL_DROP_TABLE)
    }
}