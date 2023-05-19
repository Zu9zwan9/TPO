package com.lab2.task1.case2;

class Bank {

    public static final int NTEST = 10000;
    private final int[] accounts;
    private long ntransacts = 0;

    public Bank(int n, int initialBalance) {
        accounts = new int[n];
        int i;
        for (i = 0; i < accounts.length; i++)
            accounts[i] = initialBalance;
        ntransacts = 0;
    }

    public void transfer(int from, int to, int amount)
            throws InterruptedException {
        synchronized (this) {
            // синхронізована транзакція
            accounts[from] -= amount;
            accounts[to] += amount;
            ntransacts++;

            // тест також треба синхронізувати, в іншому випадку маємо некоректну суму через те,
            // що вона вже починає змінюватись іншими потоками
            if (ntransacts % NTEST == 0)
                test();
        }
    }

    public void test() {
        int sum = 0;
        for (int i = 0; i < accounts.length; i++)
            sum += accounts[i];
        System.out.println("Transactions:" + ntransacts
                + " Sum: " + sum);
    }

    public int size() {
        return accounts.length;
    }
}

