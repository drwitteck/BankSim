package edu.temple.cis.c3238.banksim;

public class AccountBalanceTestThread extends Thread {

    private Bank myBank;
    private Account[] accounts;
    private int numAccounts;
    private int initialBalance;

    public AccountBalanceTestThread(Bank b, Account[] accounts, int numAccounts, int initialBalance){
        this.myBank = b;
        this.accounts = accounts;
        this.numAccounts = numAccounts;
        this. initialBalance = initialBalance;
    }

    public void run() {
        int sum = 0;
        //try {
            myBank.balanceTestLock.writeLock().lock();
//            try {
//                myBank.balanceTestSemaphore.acquire(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            for (Account account : accounts) {
                System.out.printf("%s %s%n",
                        Thread.currentThread().toString(), account.toString());
                sum += account.getBalance();
            }
//        } finally {
//            myBank.balanceTestSemaphore.release(10);
//        }
        myBank.balanceTestLock.writeLock().unlock();

        System.out.println(Thread.currentThread().toString() +
                " Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString() +
                    " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString() +
                    " The bank is in balance");
        }
    }
}
