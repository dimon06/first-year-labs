package queue;

import java.util.function.Predicate;

// Model: a[0]...a[n - 1]
// Inv: n >= 0 && forall i = 0...n - 1: a[i] != null && a[n] = null
// ImmutablePref(k): forall i = 0...k - 1: a'[i] = a[i]
// ImmutableSuf(k): forall i = a.length-1...a.length-k: a'[i] = a[i]
// Immutable: a' = a
public interface Queue<T> {

    // Pre: elem != null
    // Post: a'[n] = elem && n' = n + 1 && ImmutablePref(n)
    void enqueue(T elem);

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[0]
    T element();

    // Pre: n > 0
    // Post: n' = n - 1 && ans = a[n - 1] && a'[n - 1] = null && ImmutableSuf(n - 1)
    T dequeue();

    // Pre: true
    // Post: n' = 0 && forall i = 0...n-1: a'[i] = null
    void clear();

    // Pre: true
    // Post: ans = n && n' = n && Immutable
    int size();

    // Pre: true
    // Post: ans = (n == 0) && n' = n && Immutable
    boolean isEmpty();

    // Pre: true
    // Post: forall i = 0...n-1: if predicate(a[i]) -> delete(a[i])
    void removeIf(Predicate<T> predicate);

    // Pre: true
    // Post: forall i = 0...n-1: if not predicate(a[i]) -> delete(a[i])
    void retainIf(Predicate<T> predicate);

    // Pre: true
    // Post: ImmutablePref(k) -> forall i = 0...k-1: predicate(a[i]) == true
    // && n' = k && forall i = 0...k-1: a'[i] = a[i]
    void takeWhile(Predicate<T> predicate);

    // Pre: true
    // Post: ImmutableSuf(k) -> forall i = 0...n-k-1: predicate(a[i]) == true
    // && n' = n - k && forall i = 0...n - k - 1: a'[i] = a[i + k]
    void dropWhile(Predicate<T> predicate);
}
