package queue;

public class MyArrayQueueTest  {
    public static void main(String[] args) {
        ArrayQueue<String> queue1 = new ArrayQueue<>();
        ArrayQueue<String> queue2 = new ArrayQueue<>();
        for (int i = 0; i < 5; ++i) {
            queue1.push("e"+i);
        }
        for (int i = 0; i < 10; ++i) {
            queue2.push("d"+i);
        }
        while (!queue2.isEmpty()) {
            System.out.println(queue2.size() + " " + queue2.remove());
            if (!queue1.isEmpty()) {
                System.out.println(queue1.size() + " " + queue1.remove());
            }
        }
    }
}
