package com.example.web3j_db;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

public class Web3jConnection {
    private static Web3j web3j;

    private Web3jConnection() {
    }

    static Web3j getInstance() {
        if (web3j == null){
            web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/1b4540028fcd48cfa8d2167e17cf3db1"));
        }
        return web3j;
    }
    public static void shutDown(){
        if(web3j !=null) {
            web3j.shutdown();
            web3j = null;
        }
    }
}
