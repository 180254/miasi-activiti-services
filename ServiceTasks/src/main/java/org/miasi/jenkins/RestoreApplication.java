package org.miasi.jenkins;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.io.FileUtils;

import java.io.File;

// copy backup dir back to deploy dir
public class RestoreApplication implements JavaDelegate {

    public void execute(DelegateExecution delegateExecution) throws Exception {
        File backupDir = new File(JenkinsConfig.BACKUP_FOLDER);
        File deployDir = new File(JenkinsConfig.DEPLOY_FOLDER);

        FileUtils.cleanDirectory(deployDir);
        FileUtils.copyDirectory(backupDir, deployDir);
    }
}
