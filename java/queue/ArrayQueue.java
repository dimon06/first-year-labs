package queue;

@SuppressWarnings("unchecked")
public class ArrayQueue<T> extends AbstractQueue<T> implements Queue<T> {
    private int head;
    private T[] queue;

    // Pre: true
    // Post: n' = 0
    public ArrayQueue() {
        head = size = 0;
        queue = (T[]) new Object[1];
    }

    protected void enqueueImpl(T elem) {
        if (size == queue.length) {
            increaseCapacity();
        }
        int index = modIndex(head + size);
        queue[index] = elem;
    }

    // Pre: elem != null
    // Post: a'[0] = elem && n' = n + 1 && ImmutableSuf(n)
    public void push(T elem) {
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
    private void increaseCapacity() {
        Object[] newQueue = new Object[queue.length * 2];
        for (int i = 0; i < size; i++) {
            newQueue[i] = queue[head];
            head = modIndex(head + 1);
        }
        head = 0;
        queue = (T[]) newQueue;
    }

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[0]
    @Override
    public T element() {
        assert size > 0;

        return queue[head];
    }

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[n - 1]
    public T peek() {
        assert size > 0;

        int index = modIndex(head + size - 1);
        return queue[index];
    }

    protected T dequeIml() {
        int index = modIndex(head);
        head = modIndex(head + 1);
        final T answer  = queue[index];
        queue[index] = null;
        return answer;
    }

    // Pre: n > 0
    // Post: n' = n - 1 && ans = a[0] && a'[0] = null && ImmutablePref(n - 1)
    public T remove() {
        assert size > 0;

        size--;
        int index = modIndex(head + size);
        final T answer  = queue[index];
        queue[index] = null;
        return answer;
    }

    protected void clearIml() {
        head = 0;
        size = 0;
        queue = (T[]) new Object[1];
    }

    // Pre: index < n
    // Post: ans = a[n - 1 - index] && n' = n && Immutable
    public T get(int index) {
        assert index < size;

        int ind = modIndex(head + size -1 - index);
        return queue[ind];
    }

    // Pre: index < n
    // Post: n' = n && a'[n - 1 - index] = elem && forall (i != (n - 1 - index) && i = 0...n - 1): a'[i] = a[i]
    public void set(int index, T elem) {
        assert index < size;

        int ind = modIndex(head + size -1 - index);
        queue[ind] = elem;
    }

    // Pre: true
    // Post: n' = n && Immutable && ans = toString(a)
    public String toStr() {
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
    private int modIndex(int index) {
        if (index >= queue.length) {
            index-=queue.length;
        }
        if (index < 0) {
            index+=queue.length;
        }
        return index;
    }
}
