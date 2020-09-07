package com.example.web3j_db;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Web3j web3j;
    Admin admin;
    private static String TAG = "MainActivity";
    Button createWalletActivity_btn;
    TextView walletDbContents_textView;
    List walletData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        walletDbContents_textView = findViewById(R.id.walletDbContents_textView);
        createWalletActivity_btn = findViewById(R.id.createWalletActivity_btn);
        createWalletActivity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateWallet.class));
            }
        });
//        walletData = getDataFromWalletDb();
//        setWalletDataToTextView();

        //http conn...
        web3j = Web3jConnection.getInstance();


        try {
            //client version check
            Log.d(TAG, web3j.web3ClientVersion().sendAsync().get().getWeb3ClientVersion() + "");
        } catch (InterruptedException e) {
            Web3jConnection.shutDown();
            e.printStackTrace();
        } catch (ExecutionException e) {
            Web3jConnection.shutDown();
            e.printStackTrace();
        }

        getDataFromWalletDb();
    }


    private List getDataFromWalletDb() {
        SQLiteDatabase db = WalletDbHelper.getInstance(this).getReadableDatabase();

        String[] projection = {
                WalletContract.WalletEntry.COLUMN_NAME_NAME,
                WalletContract.WalletEntry.COLUMN_NAME_FILE,
                WalletContract.WalletEntry.COLUMN_NAME_BALANCE
        };
        Cursor cursor = db.query(
                WalletContract.WalletEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List items = new ArrayList();
        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
            );
            String file = cursor.getString(
                    cursor.getColumnIndexOrThrow("file")
            );
            String balance = cursor.getString(
                    cursor.getColumnIndexOrThrow("balance")
            );


            items.add(new WalletData(name, file, balance));
        }
        cursor.close();
        Log.d(TAG, items.toString());
        db.close();

        return items;
    }

    private void setWalletDataToTextView() {
        walletDbContents_textView.clearComposingText();

        walletDbContents_textView.setText(walletData.toString());
        /*for(int i=0;i<walletData.size();i++){
            walletDbContents_textView.append("이름 : "+walletData.get(i).);
        }*/
    }

    @Override
    protected void onDestroy() {
        Web3jConnection.shutDown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        walletData = getDataFromWalletDb();
        setWalletDataToTextView();

        super.onResume();
    }
}