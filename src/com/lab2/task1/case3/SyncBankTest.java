package com.lab2.task1.case3;

/**
 * Реалізувати програмний код, даний у лістингу (у додатку), та протестувати його при різних значеннях
 * параметрів. Модифікуйте програму, використовуючи методи управління потоками, так, щоб її робота була
 * завжди коректною. Запропонувати три різних варіанти управління.
 * <p>
 * Варіант управління 3. Використання ReentrantLock.
 */


public class SyncBankTest {
    public static final int NACCOUNTS = 10;
    public static final int INITIAL_BALANCE = 10000;

    public static void main(String[] args) {
        Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
        int i;
        for (i = 0; i < NACCOUNTS; i++) {
            TransferThread t = new TransferThread(b, i,
                    INITIAL_BALANCE);
            t.setPriority(Thread.NORM_PRIORITY + i % 2);
            t.start();
        }
    }
}

