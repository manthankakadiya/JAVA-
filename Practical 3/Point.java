import java.util.Objects;

public class Point {

    private int x;
    private int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString(int x, int y) {
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(!(obj instanceof Point)) {
            return false;
        }

        Point p1 = (Point) obj;
        return this.x == p1.x && this.y == p1.y;
    }

    public int hashCode() {
        return Objects.hash(x, y);
    }
}