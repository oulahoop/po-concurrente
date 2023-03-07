package fr.iutna.solution1;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedSet {
    private final Set<String> list;
    private final Lock lock;

    public SharedSet() {
        list = new HashSet<>();
        lock = new ReentrantLock();
    }

    public void add(String element) {
        lock.lock();
        try {
            list.add(element);
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(String element) {
        return list.contains(element);
    }

    public void addAll(List<String> liste) {
        lock.lock();
        try {
            list.addAll(liste);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return list.size();
        } finally {
            lock.unlock();
        }
    }

    public void printAll() {
        lock.lock();
        try {
            for (String s : list) {
                System.out.println(s);
            }
        } finally {
            lock.unlock();
        }
    }
}