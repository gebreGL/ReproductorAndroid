package com.example.reproductor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteAssetHelper extends com.readystatesoftware.sqliteasset.SQLiteAssetHelper {

    // Nombre de la base de datos externa
    private static final String DATABASE_NAME = "bdReproductor.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteAssetHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
}
