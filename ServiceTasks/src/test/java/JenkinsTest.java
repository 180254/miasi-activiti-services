import org.activiti.engine.delegate.DelegateExecution;
import org.junit.Test;
import org.miasi.jenkins.*;
import org.mockito.Mockito;

public class JenkinsTest {

    public DelegateExecution deMock = Mockito.mock(DelegateExecution.class);

    public boolean doRestore = false;

    @Test
    public void test() throws Exception {
        System.out.println("BackupApplication");
        new BackupApplication().execute(deMock);

        System.out.println("BuildApplication");
        new BuildApplication().execute(deMock);

        System.out.println("TestResult");
        new TestResult().execute(deMock);

        System.out.println("DeployApplication");
        new DeployApplication().execute(deMock);

        if (doRestore) {
            System.out.println("RestoreApplication");
            new RestoreApplication().execute(null);
        }
    }
}
