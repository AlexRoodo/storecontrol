package ru.tsconsulting.storecontrol.objects;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {

    public Store(int numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
        barrier = new CyclicBarrier(numberOfCustomers);
    }

    private volatile boolean isStorageEmpty = false;
    private static AtomicInteger quantityOfGoods = new AtomicInteger(1000);
    private int numberOfCustomers;
    private final CyclicBarrier barrier;

    public void startSales() {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfCustomers);

        for (int i = 0; i < numberOfCustomers; i++) {
            executor.submit(() -> {
                int bought = 0;
                int operations = 0;

                try {
                    barrier.await();
                    while (!isStorageEmpty) {
                        int buyResult = tryToBuy(ThreadLocalRandom.current()
                                                                  .nextInt(1, 11));
                        if (buyResult > 0) {
                            operations++;
                            bought += buyResult;
                        } else {
                            break;
                        }
                        barrier.await(100, TimeUnit.MILLISECONDS);
                    }
                } catch (TimeoutException e) {
                    System.out.println(Thread.currentThread().getName() + " закончил ожидание");
                } catch (InterruptedException e) {
                    System.out.println("Customer interrupted exception");
                } catch (BrokenBarrierException e) {
                    System.out.println("Customer broken barrier exception");
                }

                System.out.printf("Покупатель - %-10s\nТоваров куплено - %-5s\tсделок - %-5s\n",
                        Thread.currentThread().getName(), bought, operations);
            });
        }
        executor.shutdown();
    }

    private int tryToBuy(int amount) {
        if (quantityOfGoods.addAndGet(-amount) >= 0) {
            return amount;
        } else if (!isStorageEmpty) {
            isStorageEmpty = true;
            int newAmount = quantityOfGoods.addAndGet(amount);
            quantityOfGoods.set(0);
            return newAmount;
        } else {
            quantityOfGoods.getAndAdd(amount);
            isStorageEmpty = true;
            return 0;
        }
    }
}
