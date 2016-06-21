package org.miasi.jenkins;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class BackupApplication implements JavaDelegate {

    // just copy from deploy dir to backup dir
    // throws exception if fail
    public void execute(DelegateExecution de) throws Exception {
        File deployDir = new File(JenkinsConfig.DEPLOY_FOLDER);
        File backupDir = new File(JenkinsConfig.BACKUP_FOLDER);

        FileUtils.cleanDirectory(backupDir);
        FileUtils.copyDirectory(deployDir, backupDir);
    }
}
