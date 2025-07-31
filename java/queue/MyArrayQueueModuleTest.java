package queue;

public class MyArrayQueueModuleTest {
    public static void main(String[] args) {
        for (int i = 0; i < 5; ++i) {
            ArrayQueueModule.push("e"+i);
        }
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(ArrayQueueModule.size() + " " + ArrayQueueModule.remove());
        }
    }
}
