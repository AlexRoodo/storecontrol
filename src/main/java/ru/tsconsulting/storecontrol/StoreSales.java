package ru.tsconsulting.storecontrol;

import ru.tsconsulting.storecontrol.objects.Store;

public class StoreSales {
    public static void main(String[] args) {
        try {
            if (args.length != 1 || Integer.parseInt(args[0]) <= 0) {
                System.out.println("Необходимо передать программе 1 аргумент - количество покупателей");
                System.exit(1);
            }
            Store store = new Store(Integer.parseInt(args[0]));
            store.start();
        } catch (NumberFormatException e) {
            System.out.println("Введен некорректный аргумент");
        }
    }
}
