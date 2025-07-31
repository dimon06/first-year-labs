package queue;

public class MyQueueTest {
    public static void main(String[] args) {
        Queue<String> queue1 = new ArrayQueue<>();
        Queue<String> queue2 = new LinkedQueue<>();
        for (int i = 0; i < 5; ++i) {
            queue1.enqueue("e"+i);
        }
        for (int i = 0; i < 10; ++i) {
            queue2.enqueue("d"+i);
        }
        while (!queue2.isEmpty()) {
            System.out.println(queue2.size() + " " + queue2.dequeue());
            if (!queue1.isEmpty()) {
                System.out.println(queue1.size() + " " + queue1.dequeue());
            }
        }
    }
}
