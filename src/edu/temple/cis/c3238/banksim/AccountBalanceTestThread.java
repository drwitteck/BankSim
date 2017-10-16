package edu.temple.cis.c3238.banksim;

/**
 * @author Derek Witteck
 */
public class AccountBalanceTestThread extends Thread {

    private Bank myBank;
    private Account[] accounts;
    private int numAccounts;
    private int initialBalance;

    public AccountBalanceTestThread(Bank b, Account[] accounts, int numAccounts, int initialBalance){
        this.myBank = b;
        this.accounts = accounts;
        this.numAccounts = numAccounts;
        this.initialBalance = initialBalance;
    }

    @Override
    public void run() {
        //myBank.setTestThreadCurrentlyTesting(true);
        int sum = 0;

        try {
            myBank.semaphore.acquire(10);
            for (Account account : accounts) {
                System.out.printf("%s %s%n",
                        Thread.currentThread().toString(), account.toString());
                sum += account.getBalance();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            myBank.semaphore.release();
        }

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
        //myBank.setTestThreadCurrentlyTesting(false);
    }
}
