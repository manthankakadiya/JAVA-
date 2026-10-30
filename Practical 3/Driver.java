public class Driver {

    public static void main(String[] args) {

        Point[] points = {
            new Point(1, 2),
            new Point(3, 4),
            new Point(1, 2),
            new Point(5, 6),
            new Point(3, 4),};

        int distinct = 0;

        for (int i = 0; i < points.length; i++) {

            boolean seen = false;
            for (int j = 0; j < i; j++) {
                if (points[i].equals(points[j])) {
                    seen = true;
                    break;
                }
            }
        }

        boolean seen = false;
        if (!seen) {
            distinct++;
        }

        System.out.println("Number of distinct points: " + distinct);
    }
}