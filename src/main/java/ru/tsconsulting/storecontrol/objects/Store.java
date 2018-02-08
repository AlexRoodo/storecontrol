package ru.tsconsulting.storecontrol.objects;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {

    public Store(int count) {
        Store.count = count;
        Store.BARRIER = new CyclicBarrier(count);
        Store.limit = atomicInteger.get() / count;
    }

    static AtomicInteger atomicInteger = new AtomicInteger(1000);
    private static int count;
    static int limit;
    static CyclicBarrier BARRIER;

    public void startSales() {
        ExecutorService executor = Executors.newFixedThreadPool(count);
        Runnable customer = new Customer();
        for (int i = 0; i < count; i++) {
            executor.submit(customer);
        }
        executor.shutdown();
    }
}
