package edu.temple.cis.c3238.banksim;

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
    private boolean testThreadCurrentlyTesting;

    public Bank(int numAccounts, int initialBalance) {
        bankOpen = true;
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }

        numberOfTransacts = 0;
        testThreadCurrentlyTesting = false;
    }

    /**
     * All threads are free to make transfers because this method is not locked. When the
     * number of threads making transfers is > 0, the test does nothing; when it is 0 the
     * test will proceed and the flag will be updated to testing. The transfer threads
     * will check this flag before they begin transferring. If the flag is true the threads
     * will wait, if false they will proceed to transfer.
     *
     */
    public void transfer(int from, int to, int amount) {
        while (testThreadCurrentlyTesting);
        numberOfTransacts++;
        System.out.println("Before" + numberOfTransacts);
        accounts[from].waitForAvailableFunds(amount);

        if (!bankOpen) return;

        if (accounts[from].withdraw(amount)) {
            accounts[to].deposit(amount);
        }
        numberOfTransacts--;
        System.out.println("After " + numberOfTransacts);

        if (shouldTest()) {
            test();
        }
    }

    public void test() {
        Thread testThread = new AccountBalanceTestThread(this, accounts, initialBalance, numAccounts);
        testThread.start();
    }

    public synchronized boolean shouldTest() {
        return (numberOfTransacts + 1) % NTEST == 0;
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

    public void setTestThreadCurrentlyTesting(boolean testThreadCurrentlyTesting) {
        this.testThreadCurrentlyTesting = testThreadCurrentlyTesting;
    }

    public long getNumberOfTransacts() {
        return numberOfTransacts;
    }

}