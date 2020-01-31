package org.wrkr.clb.testseeder.seeders;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.testseeder.testrepo.OldRepo;
import org.wrkr.clb.testseeder.utils.Main;

/**
 * @author Denis Bilenko
 */
public abstract class BaseThreadedSeeder<T> {

    public int threads = Main.THREADS;
    public int amount = 0;
    public int commitByPortion = Main.MAX_COMMIT_SIZE;

    public void insertAll() {

        List<Thread> threadList = new ArrayList<Thread>();
        List<Integer> amountsPerThread = new ArrayList<Integer>();

        for (int i = 0; i < threads; i++) {
            amountsPerThread.add(calcAmountForThread(i + 1));
        }
        for (Integer amountForThisThread : amountsPerThread) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    insertMethod(amountForThisThread, OldRepo.getNewInstance());
                }
            });
            t1.start();
            threadList.add(t1);
        }
        try {
            for (Thread t1 : threadList) {
                t1.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insertMethod(int thatAmount, OldRepo repo) {
        repo.begin();
        for (int i = 0; i < thatAmount; i++) {
            T item = generateOneItem();
            repo.persist(item);
            if ((i + 1) % commitByPortion == 0) {
                repo.commit();
                repo.begin();
            }
        }
        repo.commit();
        repo.close();
    }

    public abstract T generateOneItem();

    public int calcAmountForThread(int threadID) {
        int amountForThread = amount / threads;
        if (threadID == threads) {
            int abc = amount / threads * (threads - 1);
            amountForThread = amount - abc;
        }
        return amountForThread;
    }
}
