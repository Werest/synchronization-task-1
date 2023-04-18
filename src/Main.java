import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final int LENGTH_ROUTE = 100;
    public static final int THREADS = 1000;
    public static final String LETTERS = "RLRFR";

    public static final char charFind = 'R';

    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++) {
            new Thread(() -> {
                String textRoute = generateRoute(LETTERS, LENGTH_ROUTE);
                int countLetter = (int) textRoute.chars().filter(c -> c == charFind).count();

                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(countLetter)) {
                        sizeToFreq.put(countLetter, sizeToFreq.get(countLetter) + 1);
                    } else {
                        sizeToFreq.put(countLetter, 1);
                    }
                }
            }).start();
        }

        Map.Entry<Integer, Integer> max = sizeToFreq.entrySet()
                .stream().max(Map.Entry.comparingByValue()).orElseThrow();

        System.out.println("Самое частое количество повторений " + max.getKey()
                + " (встретилось " + max.getValue() + " раз)");

        System.out.println("Другие размеры:");

        sizeToFreq.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach(x -> System.out.println("- " + x.getKey() + " (" + x.getValue() + " раз)"));
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