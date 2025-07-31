package queue;

public class LinkedQueue<T> extends AbstractQueue<T> implements Queue<T> {
    private Node<T> head, tail;

    // Pre: true
    // Post: n' = 0
    public LinkedQueue() {
        size = 0;
        head = tail = null;
    }

    protected void enqueueImpl(T elem) {
        if (head == null) {
            head = tail = new Node<>(elem);
        } else {
            Node<T> temp = new Node<>(elem);
            tail.next = temp;
            tail = temp;
        }
    }

    // Pre: n > 0
    // Post: n' = n && Immutable && ans = a[0]
    @Override
    public T element() {
        assert size > 0;

        return head.value;
    }

    protected T dequeIml() {
        Node<T> ans = head;
        if (head.next != null) {
            head = head.next;
        } else {
            head = tail = null;
        }
        return ans.value;
    }

    protected void clearIml() {}

    private static class Node<T> {
        private Node<T> next;
        private final T value;
        public Node(T value) {
            this.next = null;
            this.value = value;
        }
    }
}
