package org.miasi;

import com.google.common.io.BaseEncoding;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.eclipse.jgit.api.Git;
import org.json.JSONArray;
import org.json.JSONObject;
import org.miasi.config.Config;
import org.miasi.exception.ActivityException;
import org.miasi.exception.GitException;
import org.miasi.model.Task;
import org.unbescape.uri.UriEscape;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PushApp {

    private JPanel jPanel;
    private JComboBox<Task> tasksCombo;
    private JButton pushButton;
    private JTextArea statusArea;
    private JButton refreshButton;
    private JScrollPane scrollArea;

    public static void main(String[] args) {
        PushApp pushApp = new PushApp();

        JFrame frame = new JFrame("PushApp");
        frame.setContentPane(pushApp.jPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
        } catch (ClassNotFoundException | InstantiationException |
                UnsupportedLookAndFeelException | IllegalAccessException ignored) {
        }

        pushApp.init();
    }

    private Config config;

    public PushApp() {
        log("App started. Please wait. Initializing ...");

        refreshButton.setEnabled(false);
        pushButton.setEnabled(false);
        statusArea.setEditable(false);
    }

    // --------------------------------------------------------------------------------------------

    public void init() {
        init$actionListeners();

        try {
            init$config();
        } catch (Exception ex) {
            init$error("CONFIG ERROR", "Error!\n" +
                    "Config file doesn't exist or has bad structure.\n" +
                    "Please fix it and rebuild app.");
            return;
        }

        try {
            init$verifyRepo();
        } catch (Exception e) {
            File repo = init$gitRepo();
            init$error("CONFIG ERROR", "Error!\n" +
                    "Unable to verify git repo.\n" +
                    "Configured repo path: " + repo.getPath() + "\n" +
                    "Configured repo path (absolute): " + repo.getAbsolutePath() + "\n" +
                    "Double check if: \n" +
                    "- given path is git repo,\n" +
                    "- remote url is specified.\n" +
                    "Please fix it and rebuild/restart app.");
            return;
        }

        action$refreshTasks();
    }

    // --------------------------------------------------------------------------------------------

    public void init$actionListeners() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        action$onRefreshButtonClicked();
                    }
                }).start();
            }
        });

        pushButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        action$onPushButtonClicked();
                    }
                }).start();
            }
        });
        tasksCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        action$onTaskSelected();
                    }
                }).start();
            }
        });
    }

    public void init$config() throws Exception {
        config = Config.readFromConfigFile();
    }

    public File init$gitRepo() {
        return new File(config.getGitPath());
    }

    public void init$verifyRepo() throws Exception {
        try (Git git = Git.open(init$gitRepo())) {
            if (git.remoteList().call().isEmpty()) {
                throw new GitException("no remote found");
            }
        }
    }

    public void init$error(String type, String msg) {
        pushButton.setEnabled(true);
        pushButton.setText(type);
        statusArea.append(msg);
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // --------------------------------------------------------------------------------------------
    
    private void action$onRefreshButtonClicked() {
        action$refreshTasks();
    }

    private void action$onPushButtonClicked() {

    }

    private void action$onTaskSelected() {
        log("Task selected, item=", tasksCombo.getSelectedItem());
    }

    public void action$refreshTasks() {
        log("Refresh: start.");

        try {
            refreshButton.setEnabled(false);
            pushButton.setEnabled(false);

            List<Task> tasks = activity$getPushTasks();
            Task[] tasksArray = tasks.toArray(new Task[tasks.size()]);
            ComboBoxModel<Task> taskModel = new DefaultComboBoxModel<>(tasksArray);
            tasksCombo.setModel(taskModel);

            if (!tasks.isEmpty()) {
                log("Task selected, item=", tasksCombo.getSelectedItem());
            }

            pushButton.setEnabled(true);
            log("Refresh: done.");

        } catch (Exception ex) {
            log("Exception. ", ExceptionUtils.getStackTrace(ex));
            log("Something go wrong. Try refresh tasks.");

        } finally {
            refreshButton.setEnabled(true);
        }
    }

    // --------------------------------------------------------------------------------------------

    public static Header activity$getAuthHeader(Config config) {
        String activityBasic = config.getActivityUsername() + ":" + config.getActivityPassword();
        String activityBasic64 = BaseEncoding.base64().encode(activityBasic.getBytes());
        String headerVal = "Basic " + activityBasic64;
        return new BasicHeader("Authorization", headerVal);
    }

    public List<Task> activity$getPushTasks() throws ActivityException {
        log("Activity: retrieving tasks ...");

        String taskInfoUrl = String.format(
                "%s/service/runtime/tasks?taskDefinitionKey=%s",
                config.getActivityRestUrl(),
                UriEscape.escapeUriQueryParam(config.getActivityPushTaskId()));

        try {
            String content = Request.Get(taskInfoUrl)
                    .addHeader(activity$getAuthHeader(config))
                    .execute().returnContent().asString();

            JSONObject taskInfo = new JSONObject(content);
            JSONArray data = taskInfo.getJSONArray("data");

            List<Task> tasks = new ArrayList<>();

            for (int i = 0; i < data.length(); i++) {
                log("Activity: retrieving tasks ... ", i + 1, "/", data.length());

                Task task = new Task();
                JSONObject taskObject = data.getJSONObject(i);

                task.setId(taskObject.getString("id"));
                task.setDeveloper(taskObject.getString("assignee"));
                task.setName(activity$getTaskName(task.getId()));
                tasks.add(task);
            }

            return tasks;

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }

    public String activity$getTaskName(String taskId) throws ActivityException {
        log("Activity: retrieving task name, id=", taskId);

        String taskVars = String.format(
                "%s/service/runtime/tasks/%s/variables",
                config.getActivityRestUrl(),
                UriEscape.escapeUriPathSegment(taskId));

        try {
            String content = Request.Get(taskVars)
                    .addHeader(activity$getAuthHeader(config))
                    .execute().returnContent().asString();

            JSONArray data = new JSONArray(content);

            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                if (object.getString("name").equals("task_name")) {
                    return object.getString("value");
                }
            }

            throw new IllegalStateException("no such task");

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }

    // --------------------------------------------------------------------------------------------

    public void git$push() throws GitException {
        File gitRepo = new File(config.getGitPath());

        try (Git git = Git.open(gitRepo)) {
            git.push().call();
        } catch (Exception e) {
            throw new GitException(e);
        }
    }

    // --------------------------------------------------------------------------------------------

    private void log(Object... str) {
        for (Object o : str) {
            statusArea.append(o.toString());

        }
        statusArea.append("\n");

        try {
            statusArea.setCaretPosition(statusArea.getLineStartOffset(statusArea.getLineCount() - 1));
        } catch (BadLocationException ignored) {
        }
    }

}
