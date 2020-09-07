package com.example.web3j_db;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.concurrent.ExecutionException;

public class CreateWallet extends AppCompatActivity {
    final static String TAG = "CreateWallet";
    Button create_btn;
    TextView info_textView;
    EditText userName_EditText;
    WalletDbHelper mWalletDbHelper;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        handler = new Handler();

        setupBouncyCastle();
        userName_EditText = findViewById(R.id.userName_editText);
        create_btn = findViewById(R.id.create_btn);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(userName_EditText.getText())) {
                    Log.d(TAG, "이름이 입력되지 않았음");
                    return;
                }
                createWallet(userName_EditText.getText().toString());
            }
        });
        info_textView = findViewById(R.id.info_textView);
        //get Web3j instance
        Web3j web3j = Web3jConnection.getInstance();


        //version check
        try {
            String version = web3j.web3ClientVersion().sendAsync().get().getWeb3ClientVersion();
            Log.d(TAG, version + "");

        } catch (InterruptedException e) {
            Web3jConnection.shutDown();
            e.printStackTrace();
        } catch (ExecutionException e) {
            Web3jConnection.shutDown();
            e.printStackTrace();
        }


    }

    //지갑 생성 함수
    private void createWallet(String userName) {
        Log.d(TAG, "createWallet()...");

        try {
            String filePath = WalletUtils.generateNewWalletFile("1234", new File(getFilesDir().toString()), false);
            Log.d(TAG, filePath);
            info_textView.setText(filePath);
            String balance = getBalanceFromWallet(filePath);
            storeInDb(userName, filePath, balance);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getBalanceFromWallet(String filePath) {
        Log.d(TAG, "getBalanceFromWallet()...");
        Credentials credentials = loadWallet(filePath);
        if (credentials == null) {
            return null;
        }

        Web3j web3j = Web3jConnection.getInstance();
        try {
            synchronized (this) {
                BigInteger balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
                return balance.toString();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return null;
    }

    private Credentials loadWallet(String filePath) {

        try {
            Log.d(TAG, "loadWallet()...");
            Credentials credentials = WalletUtils.loadCredentials("1234", getFilesDir() + "/" + filePath);
            return credentials;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ContentValues setContentValues(String name, String file, String balance) {
        Log.d(TAG, "setContentValues()...");
        ContentValues values = new ContentValues();
        values.put(WalletContract.WalletEntry.COLUMN_NAME_NAME, name);
        values.put(WalletContract.WalletEntry.COLUMN_NAME_BALANCE, balance);
        values.put(WalletContract.WalletEntry.COLUMN_NAME_FILE, file);

        return values;
    }

    private long storeInDb(String name, String file, String balance) {
        Log.d(TAG, "storeInDb()...");
        mWalletDbHelper = (WalletDbHelper) WalletDbHelper.getInstance(this);
        SQLiteDatabase db = mWalletDbHelper.getWritableDatabase();

        ContentValues contentValues = setContentValues(name, file, balance);

        long newRowId = db.insert(WalletContract.WalletEntry.TABLE_NAME, null, contentValues);
        Log.d(TAG, "newRowId :" + newRowId);
        return newRowId;
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    @Override
    protected void onDestroy() {
        if(mWalletDbHelper != null) {
            mWalletDbHelper.close();
        }
        super.onDestroy();
    }
}