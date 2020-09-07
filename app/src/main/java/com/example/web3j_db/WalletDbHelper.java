package com.example.web3j_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WalletDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Wallets.db";
    private static WalletDbHelper walletDbHelper;

    private static final String SQL_CREATE_ENTRIES = "create table "+WalletContract.WalletEntry.TABLE_NAME+"("+
            WalletContract.WalletEntry.COLUMN_NAME_NAME +" text not null, "+
            WalletContract.WalletEntry.COLUMN_NAME_BALANCE + " text not null , "+
            WalletContract.WalletEntry.COLUMN_NAME_FILE+" text not null)";
    private static final String SQL_DELETE_ENTRIES =
            "drop table if exists "+WalletContract.WalletEntry.TABLE_NAME;

    public WalletDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteOpenHelper getInstance(@Nullable Context context){
        if(walletDbHelper == null){
            walletDbHelper = new WalletDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        return walletDbHelper;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

}
