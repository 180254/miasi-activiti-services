package org.miasi;

import com.google.common.io.BaseEncoding;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.miasi.config.Config;
import org.miasi.exception.ActivityException;
import org.miasi.model.Task;
import org.unbescape.uri.UriEscape;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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

    private void init() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        onRefreshButtonClicked();
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
                        onPushButtonClicked();
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
                        onTaskSelected();
                    }
                }).start();
            }
        });

        try {
            config = Config.readFromConfigFile();

        } catch (IOException | IllegalArgumentException ex) {
            pushButton.setEnabled(true);
            pushButton.setText("CONFIG ERROR");

            String msg = "Error!\n" +
                    "Config file doesn't exist or has bad structure.\n" +
                    "Please fix it and rebuild app.";

            statusArea.append(msg);
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refreshTasks();
    }


    private void onRefreshButtonClicked() {
        refreshTasks();
    }

    private void onPushButtonClicked() {

    }

    private void onTaskSelected() {
        log("Task selected, item=", tasksCombo.getSelectedItem());
    }

    public void refreshTasks() {
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
