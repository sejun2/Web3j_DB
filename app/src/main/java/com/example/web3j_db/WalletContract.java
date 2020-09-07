package com.example.web3j_db;

import android.provider.BaseColumns;

public class WalletContract {

    private WalletContract(){}

    //Inner class which defines the table contents

    public static class WalletEntry implements BaseColumns{
        public static final String TABLE_NAME="wallet";

        public static final String COLUMN_NAME_BALANCE = "balance";
        public static final String COLUMN_NAME_FILE = "file";
        public static final String COLUMN_NAME_NAME = "name";

    }

}
