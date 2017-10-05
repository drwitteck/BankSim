package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 */
public class BankSimMain {

    public static final int TOTAL_NUM_ACCOUNTS = 10;
    public static final int INITIAL_BALANCE = 10000;

    public static void main(String[] args) {
        Bank bankOfDerek = new Bank(TOTAL_NUM_ACCOUNTS, INITIAL_BALANCE);
        Thread[] threads = new Thread[TOTAL_NUM_ACCOUNTS];
        // Start a thread for each account
        for (int i = 0; i < TOTAL_NUM_ACCOUNTS; i++) {
            threads[i] = new TransferThread(bankOfDerek, i, INITIAL_BALANCE);
            threads[i].start();
        }
        // Wait for all threads to finish
        for (int i = 0; i < TOTAL_NUM_ACCOUNTS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                // Ignore this
            }
        }
        bankOfDerek.test();
    }
}


