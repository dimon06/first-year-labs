package queue;

import java.util.function.Predicate;

public abstract class AbstractQueue<T> implements Queue<T> {
    protected int size;

    // Pre: elem != null
    // Post: a'[n] = elem && n' = n + 1 && ImmutablePref(n)
    @Override
    public void enqueue(T elem) {
        assert elem != null;
        enqueueImpl(elem);
        size++;
    }

    protected abstract void enqueueImpl(T elem);

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[0]
    public abstract T element();

    // Pre: n > 0
    // Post: n' = n - 1 && ans = a[n - 1] && a'[n - 1] = null && ImmutableSuf(n - 1)
    @Override
    public T dequeue() {
        assert size > 0;

        size--;
        return dequeIml();
    }

    protected abstract T dequeIml();

    // Pre: true
    // Post: ans = n && n' = n && Immutable
    @Override
    public int size() {
        return size;
    }

    // Pre: true
    // Post: ans = (n == 0) && n' = n && Immutable
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Pre: true
    // Post: n' = 0 && forall i = 0...n-1: a'[i] = null
    @Override
    public void clear() {
        while (size > 0) {
            dequeue();
        }
        clearIml();
    }

    protected abstract void clearIml();

    // Pre: true
    // Post: forall i = 0...n-1: if predicate(a[i]) -> delete(a[i])
    @Override
    public void removeIf(Predicate<T> predicate) {
        deleteIf(predicate.negate());
    }

    // Pre: true
    // Post: forall i = 0...n-1: if not predicate(a[i]) -> delete(a[i])
    @Override
    public void retainIf(Predicate<T> predicate) {
        deleteIf(predicate);
    }

    private void deleteIf(Predicate<T> pred) {
        int sz = size;
        for (int i = 0; i < sz; i++) {
            T elem = dequeue();
            if (pred.test(elem)) {
                enqueue(elem);
            }
        }
    }

    // Pre: true
    // Post: ImmutablePref(k) -> forall i = 0...k-1: predicate(a[i]) == true
    // && n' = k && forall i = 0...k-1: a'[i] = a[i]
    @Override
    public void takeWhile(Predicate<T> predicate) {
        deleteWhile(predicate, true);
    }

    // Pre: true
    // Post: ImmutableSuf(k) -> forall i = 0...n-k-1: predicate(a[i]) == true
    // && n' = n - k && forall i = 0...n - k - 1: a'[i] = a[i + k]
    @Override
    public void dropWhile(Predicate<T> predicate) {
        deleteWhile(predicate, false);
    }

    private void deleteWhile(Predicate<T> predicate, boolean type) {
        int sz = size;
        boolean result = type;
        for (int i = 0; i < sz; i++) {
            T elem = dequeue();
            result = predicate.test(elem) ? result : !type;
            //type ? (result && predicate.test(elem)) : (result || !predicate.test(elem));
            if (result) {
                enqueue(elem);
            }
        }
    }
}
