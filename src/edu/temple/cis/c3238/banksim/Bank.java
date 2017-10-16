package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified for CIS 3238 Lab 4 by Derek Witteck
 */
public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long numberOfTransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean bankOpen;
    public Semaphore semaphore;
    //private boolean testThreadCurrentlyTesting;

    public Bank(int numAccounts, int initialBalance) {
        bankOpen = true;
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        semaphore = new Semaphore(10);

        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }

        numberOfTransacts = 0;
        //testThreadCurrentlyTesting = false;
    }

    public void transfer(int from, int to, int amount) {
        //while (testThreadCurrentlyTesting);
        accounts[from].waitForAvailableFunds(amount);
        if (!bankOpen) return;
        try {
            semaphore.acquire();
            if (accounts[from].withdraw(amount)) {
                accounts[to].deposit(amount);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            semaphore.release();
        }

        if (shouldTest()) {
            //while(numberOfTransacts == 0);
            test();
        }
    }

    public void test() {
        Thread testThread = new AccountBalanceTestThread(this, accounts, initialBalance, numAccounts);
        testThread.start();
    }

    public synchronized boolean shouldTest() {
        return ++numberOfTransacts % NTEST == 0;
    }

    public int size() {
        return accounts.length;
    }
    
    public synchronized boolean isBankOpen() {return bankOpen;}
    
    public void closeBank() {
        synchronized (this) {
            bankOpen = false;
        }

        for (Account account : accounts) {
            synchronized(account) {
                account.notifyAll();
            }
        }
    }

//    public void setTestThreadCurrentlyTesting(boolean testThreadCurrentlyTesting) {
//        this.testThreadCurrentlyTesting = testThreadCurrentlyTesting;
//    }

//    public long getNumberOfTransacts() {
//        return numberOfTransacts;
//    }
//
}