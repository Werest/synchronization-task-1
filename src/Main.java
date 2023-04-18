import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final int LENGTH_ROUTE = 100;
    public static final int THREADS = 1000;
    public static final String LETTERS = "RLRFR";

    public static final char charFind = 'R';

    public static void main(String[] args) throws InterruptedException {

        Runnable runnable = () -> {
            for (int i = 0; i < THREADS; i++) {
                String textRoute = generateRoute(LETTERS, LENGTH_ROUTE);
                int countLetter = (int) textRoute.chars().filter(c -> c == charFind).count();

                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(countLetter)) {
                        sizeToFreq.put(countLetter, sizeToFreq.get(countLetter) + 1);
                    } else {
                        sizeToFreq.put(countLetter, 1);
                    }
                    sizeToFreq.notify();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        Thread thread1 = new Thread(
                () -> {
                    while (!Thread.interrupted()) {
                        synchronized (sizeToFreq) {
                            try {
                                sizeToFreq.wait();
                            } catch (InterruptedException e) {
                                return;
                            }
                            System.out.printf("Текущий лидер среди частот: %s (встретилось %s раз) \n",
                                    sizeToFreq.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey(),
                                    sizeToFreq.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getValue());
                        }
                    }
                }
        );

        thread1.start();

        thread.join();
        thread1.interrupt();

        System.out.println(System.lineSeparator());

        Map.Entry<Integer, Integer> max = sizeToFreq.entrySet()
                .stream().max(Map.Entry.comparingByValue()).orElseThrow();

        System.out.printf("Самое частое количество повторений %s (встретилось %s раз) \n",
                max.getKey(),
                max.getValue());

        System.out.println("Другие размеры:");

        sizeToFreq.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach(x -> System.out.printf("- %s (%s раз) \n", x.getKey(), x.getValue()));
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}