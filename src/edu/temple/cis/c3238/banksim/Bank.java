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
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean open;
    private Thread testThread;
    protected ReadWriteLock balanceTestLock;

    public Bank(int numAccounts, int initialBalance) {
        open = true;
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        //balanceTestSemaphore = new Semaphore(10);
        balanceTestLock = new ReentrantReadWriteLock();
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
    }

    public void transfer(int from, int to, int amount) {
        accounts[from].waitForAvailableFunds(amount);
        balanceTestLock.writeLock().lock();
        if (!open) return;
        if (accounts[from].withdraw(amount)) {
            accounts[to].deposit(amount);
        }
        balanceTestLock.writeLock().unlock();
        if (shouldTest()) {
            test();
        }
    }

    public void test() {
        testThread = new AccountBalanceTestThread(this, accounts, initialBalance, numAccounts);
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
        return ++ntransacts % NTEST == 0;
    }

}