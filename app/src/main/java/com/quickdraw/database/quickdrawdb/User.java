package com.quickdraw.database.quickdrawdb;

/**
 * Created by Matthew on 11/8/16.
 */

public class User {

    private String name;
    private float balance;
    private float[] history = new float[10];
    private String pin;

    public User() {

    }

    public User(String name, float balance, String pin) {
        this.name = name;
        this.balance = balance;
        this.pin = pin;
    }


    public String getName() {
        return name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float newBalance) {
        balance = newBalance;
    }

    public float[] getHistory() {
        return history;
    }

    public String getPin() { return pin; }

    public void addTransaction(float amount) {
        for (int i = history.length - 1; i > 0; i--) {
            history[i] = history[i-1];
        }
        history[0] = amount;
    }
}
