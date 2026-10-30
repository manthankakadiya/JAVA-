abstract class Shape {

    public abstract double area();
}

class Circle extends Shape {

    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {

    private double width, height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double area() {
        return width * height;
    }
}

class Triangle extends Shape {

    private double base, height;

    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    public double area() {
        return 0.5 * base * height;
    }
}

public class ShapeTest {

    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle(4),
            new Rectangle(3, 6),
            new Triangle(4, 6),};
        double totalArea = 0;
        double largestArea = Double.MIN_VALUE;
        Shape largestShape = null;

        for (Shape s : shapes) {
            double area = s.area();
            System.out.println("Area: " + area);
            totalArea += area;

            if (area > largestArea) {
                largestArea = area;
                largestShape = s;
            }
        }

        System.out.println("Total area = " + totalArea);
        System.out.println("Largest area = " + largestArea + " (from " + largestShape.getClass().getSimpleName() + ")");
    }
}