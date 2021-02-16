import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

public class TestOrderRandomizer implements IMethodInterceptor {

    //This method is taken from: https://mrslavchev.com/2015/09/19/randomize-test-execution-in-testng-to-improve-test-isolation/
    //This will be used for randomizing the test execution in the Test_Todo class
    @Override
    public List intercept(List methods, ITestContext context) {
        long seed = System.nanoTime();
        System.out.println(String.format("Randomizing order of tests with seed: %s", seed));
        Collections.shuffle(methods, new Random(seed));
        return methods;
    }
}