package ru.tsconsulting.storecontrol.objects;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {

    public Store(int count) {
        this.count = count;
        barrier = new CyclicBarrier(count);
    }

    private boolean storeIsEmpty = false;
    private static AtomicInteger goodsAmount = new AtomicInteger(1000);
    private int count;
    private final CyclicBarrier barrier;

    public void startSales() {
        ExecutorService executor = Executors.newFixedThreadPool(count);

        for (int i = 0; i < count; i++) {
        executor.submit(() -> {
            int customerGoodsAmount = 0;
            int purchasesAmount = 0;
            int bound = 11;

            try {
                barrier.await();
                while (!storeIsEmpty) {
                    int currentGoodsPurchaseAmount =
                            ThreadLocalRandom.current().nextInt(1, bound);
                    int buyResult = tryToBuy(currentGoodsPurchaseAmount);
                    if (buyResult > 0) {
                        purchasesAmount++;
                        customerGoodsAmount += buyResult;
                    } else {
                        break;
                    }
                    barrier.await(500, TimeUnit.MILLISECONDS);
                }
            } catch (TimeoutException e) {
                System.out.println(Thread.currentThread().getName() + " закончил ожидание");
            } catch (InterruptedException e) {
                System.out.println("Customer interrupted exception");
            } catch (BrokenBarrierException e) {
                System.out.println("Customer broken barrier exception");
            }

            System.out.printf("Покупатель - %-10s\nТоваров куплено - %-5s\tсделок - %-5s\n",
                    Thread.currentThread().getName(), customerGoodsAmount, purchasesAmount);
        });
        }

        executor.shutdown();
    }

    private int tryToBuy(int amount) {
        goodsAmount.getAndAdd(-amount);
        if (goodsAmount.get() >= 0) {
            return amount;
        } else if (!storeIsEmpty) {
            storeIsEmpty = true;
            int newAmount = goodsAmount.get() + amount;
            goodsAmount.set(0);
            return newAmount;
        } else {
            goodsAmount.getAndAdd(amount);
            storeIsEmpty = true;
            return 0;
        }
    }
}
