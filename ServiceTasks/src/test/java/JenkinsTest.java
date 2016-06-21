import mock.DeMock;
import org.activiti.engine.delegate.DelegateExecution;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.miasi.jenkins.*;
import repeat.Repeat;
import repeat.RepeatRule;

public class JenkinsTest {

    public DelegateExecution deMock = new DeMock();

    public boolean doRestore = true;

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

    @Test
    @Repeat(times = 10)
    public void test() throws Exception {
        // may throw exception
        System.out.println("BackupApplication");
        new BackupApplication().execute(deMock);

        // set variable: buildFailed
        System.out.println("BuildApplication");
        new BuildApplication().execute(deMock);
        Assert.assertSame(false, deMock.getVariable("buildFailed"));

        // set variable: testResults
        System.out.println("TestResult");
        new TestResult().execute(deMock);
        Assert.assertSame(true, deMock.getVariable("testResults"));

        // set variable: deployFailed
        System.out.println("DeployApplication");
        new DeployApplication().execute(deMock);
        Assert.assertSame(false, deMock.getVariable("deployFailed"));

        if (doRestore) {
            // may throw exception
            System.out.println("RestoreApplication");
            new RestoreApplication().execute(deMock);
        }
    }
}
