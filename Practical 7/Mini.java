import java.lang.annotation.*;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Run {
}

class MyTests {

    @Run
    public void test1() {
        System.out.println("Test 1 executed");
    }

    @Run
    public void test2() {
        System.out.println("Test 2 executed");
    }

    public void normalMethod() {
        System.out.println("Normal method");
    }
}

public class Mini {

    public static void main(String[] args) throws Exception {

        MyTests obj = new MyTests();

        int count = 0;

        Method[] methods = obj.getClass().getDeclaredMethods();

        for (Method method : methods) {

            if (method.isAnnotationPresent(Run.class)
                    && method.getParameterCount() == 0) {

                method.invoke(obj);
                count++;
            }
        }

        System.out.println("Total methods executed: " + count);
    }
}