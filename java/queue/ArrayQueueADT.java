package queue;

@SuppressWarnings("unchecked")
public class ArrayQueueADT<T> {
    // Model: a[0]...a[n - 1]
    // Inv: n >= 0 && forall i = 0...n - 1: a[i] != null && a[n] = null
    // ImmutablePref(k): forall i = 0...k - 1: a'[i] = a[i]
    // ImmutableSuf(k): forall i = a.length-1...a.length-k: a'[i] = a[i]
    // Immutable: a' = a
    private int head, size;
    private T[] queue = (T[]) new Object[1];

    // Pre: elem != null
    // Post: a'[n] = elem && n' = n + 1 && ImmutablePref(n)
    public static <T> void enqueue(ArrayQueueADT<T> arrayQueue, T elem) {
        assert elem != null;

        if (arrayQueue.size == arrayQueue.queue.length) {
            increaseCapacity(arrayQueue);
        }
        int index = modIndex(arrayQueue, arrayQueue.head + arrayQueue.size);
        arrayQueue.queue[index] = elem;
        arrayQueue.size++;
    }

    // Pre: elem != null
    // Post: a'[0] = elem && n' = n + 1 && ImmutableSuf(n)
    public static <T> void push(ArrayQueueADT<T> arrayQueue, T elem) {
        assert elem != null;

        if (arrayQueue.size == arrayQueue.queue.length) {
            increaseCapacity(arrayQueue);
        }
        arrayQueue.head--;
        arrayQueue.head = modIndex(arrayQueue, arrayQueue.head);
        arrayQueue.queue[arrayQueue.head] = elem;
        arrayQueue.size++;
    }

    // Pre: true
    // Post: n' = n && Immutable
    private static <T> void increaseCapacity(ArrayQueueADT<T> arrayQueue) {
        T[] newQueue = (T[]) new Object[arrayQueue.queue.length * 2];
        for (int i = 0; i < arrayQueue.size; i++) {
            newQueue[i] = arrayQueue.queue[arrayQueue.head];
            arrayQueue.queue[arrayQueue.head] = null;
            arrayQueue.head = modIndex(arrayQueue, arrayQueue.head + 1);
        }
        arrayQueue.head = 0;
        arrayQueue.queue = newQueue;
    }

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[0]
    public static <T> T element(ArrayQueueADT<T> arrayQueue) {
        assert arrayQueue.size > 0;

        return arrayQueue.queue[arrayQueue.head];
    }

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[n - 1]
    public static <T> T peek(ArrayQueueADT<T> arrayQueue) {
        assert arrayQueue.size > 0;

        int index = modIndex(arrayQueue, arrayQueue.head + arrayQueue.size - 1);
        return arrayQueue.queue[index];
    }

    // Pre: n > 0
    // Post: n' = n - 1 && ans = a[n - 1] && a'[n - 1] = null && ImmutableSuf(n - 1)
    public static <T> T dequeue(ArrayQueueADT<T> arrayQueue) {
        assert arrayQueue.size > 0;

        arrayQueue.size--;
        final T answer  = arrayQueue.queue[arrayQueue.head];
        arrayQueue.queue[arrayQueue.head] = null;
        arrayQueue.head = modIndex(arrayQueue, arrayQueue.head + 1);
        return answer;
    }

    // Pre: n > 0
    // Post: n' = n - 1 && ans = a[0] && a'[0] = null && ImmutablePref(n - 1)
    public static <T> T remove(ArrayQueueADT<T> arrayQueue) {
        assert arrayQueue.size > 0;

        arrayQueue.size--;
        int index = modIndex(arrayQueue, arrayQueue.head + arrayQueue.size);
        final T answer  = arrayQueue.queue[index];
        arrayQueue.queue[index] = null;
        return answer;
    }

    // Pre: true
    // Post: ans = n && n' = n && Immutable
    public static <T> int size(ArrayQueueADT<T> arrayQueue) {
        return arrayQueue.size;
    }

    // Pre: true
    // Post: ans = (n == 0) && n' = n && Immutable
    public static <T> boolean isEmpty(ArrayQueueADT<T> arrayQueue) {
        return arrayQueue.size == 0;
    }

    // Pre: true
    // Post: n' = 0 && forall i = 0...n-1: a'[i] = null
    public static <T> void clear(ArrayQueueADT<T> arrayQueue) {
        for (int i = 0; i < arrayQueue.size; i++) {
            arrayQueue.queue[arrayQueue.head] = null;
            arrayQueue.head = modIndex(arrayQueue, arrayQueue.head + 1);
        }
        arrayQueue.head = 0;
        arrayQueue.size = 0;
        arrayQueue.queue = (T[]) new Object[1];
    }

    // Pre: index < n
    // Post: ans = a[n - 1 - index] && n' = n && Immutable
    public static  <T> T get(ArrayQueueADT<T> arrayQueue, int index) {
        assert index < arrayQueue.size;

        int ind = modIndex(arrayQueue, arrayQueue.head + arrayQueue.size - index - 1);
        return arrayQueue.queue[ind];
    }

    // Pre: index < n
    // Post: n' = n && a'[n - 1 - index] = elem && forall (i != (n - 1 - index) && i = 0...n - 1): a'[i] = a[i]
    public static <T> void set(ArrayQueueADT<T> arrayQueue, int index, T elem) {
        assert index < arrayQueue.size;

        int ind = modIndex(arrayQueue, arrayQueue.head + arrayQueue.size - index - 1);
        arrayQueue.queue[ind] = elem;
    }

    // Pre: true
    // Post: n' = n && Immutable && ans = toString(a)
    public static <T> String toStr(ArrayQueueADT<T> arrayQueue) {
        int index = arrayQueue.head;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < arrayQueue.size; i++) {
            sb.append(arrayQueue.queue[index]);
            if (i + 1 != arrayQueue.size) {
                sb.append(", ");
            }
            index = modIndex(arrayQueue, index + 1);
        }
        sb.append(']');
        return sb.toString();
    }

    // Pre: true
    // Post: ans = index%n && n' = n && Immutable
    private static <T> int modIndex(ArrayQueueADT<T> arrayQueue, int index) {
        if (index >= arrayQueue.queue.length) {
            index-=arrayQueue.queue.length;
        }
        if (index < 0) {
            index+=arrayQueue.queue.length;
        }
        return index;
    }
}
