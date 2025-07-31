package queue;

public class MyArrayQueueADTTest {
    public static void main(String[] args) {
        ArrayQueueADT<String> queue1 = new ArrayQueueADT<>();
        ArrayQueueADT<String> queue2 = new ArrayQueueADT<>();
        for (int i = 0; i < 5; ++i) {
            ArrayQueueADT.push(queue1, "e"+i);
        }
        for (int i = 0; i < 10; ++i) {
            ArrayQueueADT.push(queue2, "d"+i);
        }
        while (!ArrayQueueADT.isEmpty(queue2)) {
            System.out.println(ArrayQueueADT.size(queue2) + " " + ArrayQueueADT.remove(queue2));
            if (!ArrayQueueADT.isEmpty(queue1)) {
                System.out.println(ArrayQueueADT.size(queue1) + " " + ArrayQueueADT.remove(queue1));
            }
        }
    }
}
