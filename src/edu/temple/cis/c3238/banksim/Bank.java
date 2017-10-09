package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 */
public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long numberOfTransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean open;
    protected ReentrantReadWriteLock balanceTestLock;
    private int transferCount;

    public Bank(int numAccounts, int initialBalance) {
        open = true;
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        balanceTestLock = new ReentrantReadWriteLock();

        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }

        numberOfTransacts = 0;
    }

    public void transfer(int from, int to, int amount) {
        accounts[from].waitForAvailableFunds(amount);

        balanceTestLock.writeLock().lock();
        if (!open) return;
        if (accounts[from].withdraw(amount)) {
            accounts[to].deposit(amount);
            ++transferCount;
            System.out.println("Number of transfers: " + transferCount);
        }
        balanceTestLock.writeLock().unlock();

        if (shouldTest()) {
            test();
        }
    }

    public void test() {
        Thread testThread = new AccountBalanceTestThread(this, accounts, initialBalance, numAccounts);
        testThread.start();
    }

    public int size() {
        return accounts.length;
    }
    
    public synchronized boolean isOpen() {return open;}
    
    public void closeBank() {
        synchronized (this) {
            open = false;
        }

        for (Account account : accounts) {
            synchronized(account) {
                account.notifyAll();
            }
        }
    }
    
    public synchronized boolean shouldTest() {
        return ++numberOfTransacts % NTEST == 0;
    }

}