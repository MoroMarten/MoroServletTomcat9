package com.synchronize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Communicator {

    private static Communicator instance = null;
    private static final List<Lock> locks = Collections.synchronizedList(new ArrayList<>());

    private Communicator() {
        Lock lock = new Lock();
        locks.add(lock);
    }

    public static synchronized Communicator getInstance() {
        if (instance == null) {
            instance = new Communicator();
        }
        return instance;
    }

    public int handleEnd() {
        Lock lock = new Lock();
        locks.add(lock);
        if (locks.size() <= 2) {
            locks.get(0).addClient();
            notifyFirstLock();
            return handleResponse();
        } else {
            return increaseAndReceiveSum(0, locks.size() - 2);
        }
    }

    public int increaseAndReceiveSum(int amount) {
        return increaseAndReceiveSum(amount, locks.size() - 1);
    }

    public int increaseAndReceiveSum(int amount, int lockIndex) {
        Lock lock = locks.get(lockIndex);
        synchronized (lock) {
            lock.addSum(amount);
            getSumQueue(lock);
            return handleResponse();
        }
    }

    private int handleResponse() {
        synchronized (locks.get(0)) {
            Lock lock = locks.get(0);
            lock.removeClient();
            if (lock.getClients() == 0) return resetCommunicator();
            else return lock.getSum();
        }
    }

    private int resetCommunicator() {
        int returnSum = locks.get(0).getSum();
        if (locks.size() > 1) locks.remove(0);
        notifyFirstLock();
        return returnSum;
    }

    private void notifyFirstLock() {
        synchronized (locks.get(0)) {
            locks.get(0).notifyAll();
        }
    }

    private void getSumQueue(Lock lock) {
        synchronized (lock) {
            try {
                lock.addClient();
                lock.wait();
            } catch (InterruptedException ie) {
                System.out.println(ie.getMessage());
            }
        }
    }

}
