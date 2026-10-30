class MyStack<T> {

    private T[] stack;
    private int top;

    @SuppressWarnings("unchecked")
    public MyStack(int size) {
        stack = (T[]) new Object[size];
        top = -1;
    }

    public void push(T value) {

        if (top == stack.length - 1) {
            System.out.println("Stack is full!");
            return;
        }

        stack[++top] = value;
    }

    public T pop() {

        if (top == -1) {
            System.out.println("Stack is empty!");
            return null;
        }

        return stack[top--];
    }

    public T peek() {

        if (top == -1) {
            System.out.println("Stack is empty!");
            return null;
        }

        return stack[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }
}

public class GenericStackDemo {

    public static void main(String[] args) {

        // Integer stack
        MyStack<Integer> intStack = new MyStack<>(5);

        intStack.push(10);
        intStack.push(20);
        intStack.push(30);

        System.out.println("Integer stack pop: "
                + intStack.pop());

        System.out.println("Integer stack peek: "
                + intStack.peek());


        // String stack
        MyStack<String> stringStack = new MyStack<>(5);

        stringStack.push("Java");
        stringStack.push("Python");
        stringStack.push("C++");

        System.out.println("String stack pop: "
                + stringStack.pop());

        System.out.println("String stack peek: "
                + stringStack.peek());


        // Empty case
        while (!stringStack.isEmpty()) {
            stringStack.pop();
        }

        System.out.println("Trying to pop from empty stack:");
        stringStack.pop();
    }
}