package com.synchronize;

public class Lock {

    private int clients;
    private int sum;

    public Lock() {
        clients = 0;
        sum = 0;
    }

    public synchronized void addClient() {
        clients++;
    }

    public synchronized void removeClient() {
        clients--;
    }

    public int getClients() {
        return clients;
    }

    public synchronized void addSum(int amount) {
        sum += amount;
    }

    public int getSum() {
        return sum;
    }
}
