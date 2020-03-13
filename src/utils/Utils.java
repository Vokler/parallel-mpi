package utils;

import java.util.Random;

public class Utils {
    public static int randomInt(int min, int max) {
        int diff = max - min;
        Random random = new Random();
        int num = random.nextInt(diff + 1);
        num += min;
        return num;
    }

    public static int getStartIndex(int i, int k) {
        int start = (i - 1) * k;
        return start;
    }

    public static int getStep(int start, int k, int N) {
        int end = start + k;
        if (N < end) {
            end = N;
        }
        return end - start;
    }

    public static void printMatrix(int matrix[][]) {
        for (int[] m : matrix) {
            for (int j = 0; j < m.length; j++) {
                System.out.print(m[j] + "\t");
            }
            System.out.println();
        }
    }
}
