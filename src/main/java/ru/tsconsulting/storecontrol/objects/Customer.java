package ru.tsconsulting.storecontrol.objects;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Customer implements Runnable {

    public Customer(AtomicInteger storeGoodsAmount, CyclicBarrier BARRIER, int limit) {
        this.storeGoodsAmount = storeGoodsAmount;
        this.BARRIER = BARRIER;
        this.limit = limit;
    }

    private AtomicInteger storeGoodsAmount;
    private final CyclicBarrier BARRIER;
    private final int limit;

    public void run() {
        int customerGoodsAmount = 0;
        int purchasesAmount = 0;
        int bound = 11;

        try {

        BARRIER.await();

        while (customerGoodsAmount <= limit ) {
            int currentGoodsPurchaseAmount =
                    ThreadLocalRandom.current().nextInt(1, bound);
            if (limit < customerGoodsAmount + currentGoodsPurchaseAmount) {
                bound--;
                if (bound == 1) {
                    break;
                }
                continue;
            }
            purchasesAmount++;
            customerGoodsAmount += currentGoodsPurchaseAmount;
            storeGoodsAmount.getAndAdd(-currentGoodsPurchaseAmount);
        }

        BARRIER.await();

        if (storeGoodsAmount.decrementAndGet() < 0) {
            storeGoodsAmount.incrementAndGet();
        } else {
            customerGoodsAmount++;
            purchasesAmount++;
        }

        } catch (InterruptedException e) {
            System.out.println("Customer interrupted exception");
        } catch (BrokenBarrierException e) {
            System.out.println("Customer broken barrier exception");
        }

        System.out.printf("Покупатель - %-10s\nТоваров куплено - %-5s\tсделок - %-5s\n\n",
                Thread.currentThread().getName(), customerGoodsAmount, purchasesAmount);
    }
}
