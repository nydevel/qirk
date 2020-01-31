package org.wrkr.clb.testseeder.seeders.dependent;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.testseeder.testrepo.OldRepo;
import org.wrkr.clb.testseeder.utils.Main;

/**
 * @author Denis Bilenko
 */
public abstract class BaseDependentThreadedSeeder<T> {
    public int threads = Main.THREADS;
    public int selectBy = Main.DEPENDENT_SEEDER_PAGE_SIZE;
    public int maxCommitSize = Main.MAX_COMMIT_SIZE;
    public int totalPages = 0;

    public void insertAll() {
        
        System.out.println("BaseDependentThreadedSeeder: insertAll totalPages = " + totalPages);

        for (int currentPage = 1; currentPage <= totalPages;) {
            List<Thread> threadList = new ArrayList<Thread>();

            List<Integer> pages = new ArrayList<Integer>();

            for (int i = 0; i < threads; i++) {
                pages.add(currentPage++);
            }

            for (Integer page : pages) {
                Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OldRepo repoForThisThread = OldRepo.getNewInstance();
                        List<T> abc = selectPageFromRepo(repoForThisThread, page);
                        executeOnEachThread(abc, repoForThisThread);
                        repoForThisThread.close();
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
            } finally {

            }
        }
    }

    public abstract void executeOnEachThread(List<T> eachThreadList, OldRepo repo);

    public abstract List<T> selectPageFromRepo(OldRepo repo, int page);

    public abstract int totalPages();

    public int calcMinFromPage(int page) {
        int min = (page - 1) * selectBy + 1;
        return min;
    }

    public int calcMaxFromPage(int page) {
        int max = calcMinFromPage(page) + selectBy - 1;
        return max;
    }
}
