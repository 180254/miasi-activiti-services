package org.miasi.jenkins;

import java.io.File;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.fluent.Request;

public class DeployApplication implements JavaDelegate {

    // get jar from jenkins and copy it to deploy folder
    public void execute( DelegateExecution delegateExecution ) throws Exception {
        String jarUrl = JenkinsConfig.JENKINS_TASK_URL
                        + "/ws/" + JenkinsConfig.JAR_BUILD_FILE;

        String savePath = JenkinsConfig.DEPLOY_FOLDER + "/" + JenkinsConfig.JAR_NAME;
        File saveFile = new File( savePath );

        int tries = 0;
        do {
            try {
                tries++;
                Request.Get( jarUrl )
                        .addHeader( JenkinsConfig.authHeader() )
                        .execute()
                        .saveContent( saveFile );
                break;
            } catch ( Exception ex ) {
                if ( tries > 5 ) {
                    throw ex;
                }
            }
        } while ( true );
        
        delegateExecution.setVariable( "deployFailed", false);
    }

}
