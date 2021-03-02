import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

class TestRunner {
    public static void main(String[] args) {

        println ""
        println "Running tests..."
        println ""

        Result result = JUnitCore.runClasses(UnitTest.class);

        for (Failure failure : result.getFailures()) {
            println failure.toString()
            println ""
        }

        print "Tests passed: \t\t\t"
        println result.wasSuccessful()

        print "Total number of tests: \t\t"
        println result.getRunCount()

        print "The number of tests failed: \t"
        println result.getFailureCount()

        print "The number of tests ignored: \t"
        println result.getIgnoreCount()

        print "Tests run in: \t\t\t"
        print result.getRunTime()
        println "ms"

        println ""
    }
}  	