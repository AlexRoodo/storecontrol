package ru.tsconsulting.storecontrol.objects;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ThreadLocalRandom;

public class Customer implements Runnable {
    public void run() {
        int goodsAmount = 0;
        int purchasesAmount = 0;
        int bound = 11;

        try {
            Store.BARRIER.await();
        } catch (InterruptedException e) {
            System.out.println("Customer interrupted exception");
        } catch (BrokenBarrierException e) {
            System.out.println("Customer broken barrier exception");
        }

        while (goodsAmount <= Store.limit && Store.atomicInteger.get() > 0) {
            int currentGoodsPurchaseAmount =
                    ThreadLocalRandom.current().nextInt(1, bound);
            if (Store.limit < goodsAmount + currentGoodsPurchaseAmount) {
                bound--;
                if (bound == 1) {
                    break;
                }
                continue;
            }
            purchasesAmount++;
            goodsAmount += currentGoodsPurchaseAmount;
            Store.atomicInteger.getAndAdd(-currentGoodsPurchaseAmount);
        }

        try {
            Store.BARRIER.await();
            if (Store.atomicInteger.decrementAndGet() < 0) {
                Store.atomicInteger.incrementAndGet();
            } else {
                goodsAmount++;
                purchasesAmount++;
            }
        } catch (InterruptedException e) {
            System.out.println("Customer interrupted exception");
        } catch (BrokenBarrierException e) {
            System.out.println("Customer broken barrier exception");
        }

        System.out.printf("Покупатель - %-10s\nТоваров куплено - %-5s\tсделок - %-5s\n\n",
                Thread.currentThread().getName(), goodsAmount, purchasesAmount);
    }
}
