package queue;

public class ArrayQueueModule {
    // Model: a[0]...a[n - 1]
    // Inv: n >= 0 && forall i = 0...n - 1: a[i] != null && a[n] = null
    // ImmutablePref(k): forall i = 0...k - 1: a'[i] = a[i]
    // ImmutableSuf(k): forall i = a.length-1...a.length-k: a'[i] = a[i]
    // Immutable: a' = a
    private static int head = 0, size = 0;
    private static Object[] queue = new Object[1];

    // Pre: elem != null
    // Post: a'[n] = elem && n' = n + 1 && ImmutablePref(n)
    public static void enqueue(Object elem) {
        assert elem != null;

        if (size == queue.length) {
            increaseCapacity();
        }
        int index = modIndex(head + size);
        queue[index] = elem;
        size++;
    }

    // Pre: elem != null
    // Post: a'[0] = elem && n' = n + 1 && ImmutableSuf(n)
    public static void push(Object elem) {
        assert elem != null;

        if (size == queue.length) {
            increaseCapacity();
        }
        head--;
        head = modIndex(head);
        queue[head] = elem;
        size++;
    }

    // Pre: true
    // Post: n' = n && Immutable
    private static void increaseCapacity() {
        Object[] newQueue = new Object[queue.length * 2];
        for (int i = 0; i < size; i++) {
            newQueue[i] = queue[head];
            queue[head] = null;
            head = modIndex(head + 1);
        }
        head = 0;
        queue = newQueue;
    }

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[0]
    public static Object element() {
        assert size > 0;

        return queue[head];
    }

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[n - 1]
    public static Object peek() {
        assert size > 0;

        int index = modIndex(head + size - 1);
        return queue[index];
    }

    // Pre: n > 0
    // Post: n' = n - 1 && ans = a[n - 1] && a'[n - 1] = null && ImmutableSuf(n - 1)
    public static Object dequeue() {
        assert size > 0;

        size--;
        int index = modIndex(head);
        head = modIndex(head + 1);
        final Object answer  = queue[index];
        queue[index] = null;
        return answer;
    }

    // Pre: n > 0
    // Post: n' = n - 1 && ans = a[0] && a'[0] = null && ImmutablePref(n - 1)
    public static Object remove() {
        assert size > 0;

        size--;
        int index = modIndex(head + size);
        final Object answer  = queue[index];
        queue[index] = null;
        return answer;
    }

    // Pre: true
    // Post: ans = n && n' = n && Immutable
    public static int size() {
        return size;
    }

    // Pre: true
    // Post: ans = (n == 0) && n' = n && Immutable
    public static boolean isEmpty() {
        return size == 0;
    }

    // Pre: true
    // Post: n' = 0 && forall i = 0...n-1: a'[i] = null
    public static void clear() {
        for (int i = 0; i < size; i++) {
            queue[head] = null;
            head = modIndex(head + 1);
        }
        head = 0;
        size = 0;
        queue = new Object[1];
    }

    // Pre: index < n
    // Post: ans = a[n - 1 - index] && n' = n && Immutable
    public static Object get(int index) {
        assert index < size;

        int ind = modIndex(head + size -1 - index);
        return queue[ind];
    }

    // Pre: index < n
    // Post: n' = n && a'[n - 1 - index] = elem && forall (i != (n - 1 - index) && i = 0...n - 1): a'[i] = a[i]
    public static void set(int index, Object elem) {
        assert index < size;

        int ind = modIndex(head + size -1 - index);
        queue[ind] = elem;
    }

    // Pre: true
    // Post: n' = n && Immutable && ans = toString(a)
    public static String toStr() {
        int index = head;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < size; i++) {
            sb.append(queue[index]);
            if (i + 1 != size) {
                sb.append(", ");
            }
            index = modIndex(index + 1);
        }
        sb.append(']');
        return sb.toString();
    }

    // Pre: true
    // Post: ans = index%n && n' = n && Immutable
    private static int modIndex(int index) {
        if (index >= queue.length) {
            index-=queue.length;
        }
        if (index < 0) {
            index+=queue.length;
        }
        return index;
    }
}
