package com.example.web3j_db;

public class WalletData {
    private String name;
    private String balance;
    private String file;

    public WalletData(String name, String file, String balance) {
        this.name = name;
        this.balance = balance;
        this.file = file;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +"\n"+
                ", balance='" + balance + '\'' +"\n"+
                ", file='" + file + '\''+"\n\n";
    }
}
