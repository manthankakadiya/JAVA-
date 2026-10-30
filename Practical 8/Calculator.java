
import java.util.Scanner;

public class Calculator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Enter first number: ");
            double num1 = sc.nextDouble();

            System.out.print("Enter second number: ");
            double num2 = sc.nextDouble();

            if (num1 < 0 || num2 < 0) {
                throw new InvalidNumberException("Negative numbers are not allowed!");
            }

            if(num1==0 || num2==0){
                throw new InvalidNumberException("Multiplication By Zero is not allowed!");
            }

            System.out.print("Enter operator (+, -, *, /): ");
            char op = sc.next().charAt(0);

            System.out.println("You entered: " + num1 + " " + op + " " + num2);

            double result = 0;

            if (op == '+') {
                result = num1 + num2;
            } else if (op == '-') {
                result = num1 - num2;
            } else if (op == '*') {
                result = num1 * num2;
            } else if (op == '/') {
                if (num2 == 0) {
                    throw new DivideByZeroException("Cannot divide by zero!");
                }
                result = num1 / num2;
            } else {
                throw new InvalidNumberException("Invalid operator entered!");
            }

            System.out.println(num1 + " " + op + " " + num2 + " = " + result);

        } catch (DivideByZeroException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidNumberException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            System.out.println("Exiting the program.");
            sc.close();
        }
    }
}

class DivideByZeroException extends Exception {

    public DivideByZeroException(String message) {
        super(message);
    }
}

class InvalidNumberException extends Exception {

    public InvalidNumberException(String message) {
        super(message);
    }
}