package ru.tsconsulting.storecontrol.objects;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {

    public Store() {
        this(5);
    }

    public Store(int count) {
        Store.count = count;
        this.BARRIER = new CyclicBarrier(count);
        this.limit = goodsAmount.get() / count;
    }

    public static AtomicInteger goodsAmount = new AtomicInteger(1000);
    private static int count;
    private int limit;
    private CyclicBarrier BARRIER;

    public void startSales() {
        ExecutorService executor = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            executor.submit(new Customer(goodsAmount, BARRIER, limit));
        }
        executor.shutdown();
    }
}
