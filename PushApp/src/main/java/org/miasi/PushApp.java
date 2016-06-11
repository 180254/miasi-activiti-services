package org.miasi;

import com.google.common.io.BaseEncoding;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PushApp {

    private JPanel jPanel;
    private JComboBox<Task> tasksCombo;
    private JButton pushButton;
    private JTextArea statusArea;
    private JButton refreshButton;

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
        refreshButton.setEnabled(false);
        statusArea.setEditable(false);
    }

    private void init() {
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
        try {
            List<Task> tasks = activity$getPushTasks(config);
            Task[] tasks1 = tasks.toArray(new Task[tasks.size()]);
            tasksCombo.setModel(new DefaultComboBoxModel<Task>(tasks1));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static Header activity$getAuthHeader(Config config) {
        String activityBasic = config.getActivityUsername() + ":" + config.getActivityPassword();
        String activityBasic64 = BaseEncoding.base64().encode(activityBasic.getBytes());
        String headerVal = "Basic " + activityBasic64;
        return new BasicHeader("Authorization", headerVal);
    }

    public static List<Task> activity$getPushTasks(Config config) throws ActivityException {
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
                Task task = new Task();
                JSONObject taskObject = data.getJSONObject(i);

                task.setId(taskObject.getString("id"));
                task.setDeveloper(taskObject.getString("assignee"));
                task.setName(activity$getTaskName(config, task.getId()));
                tasks.add(task);
            }

            return tasks;

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }

    public static String activity$getTaskName(Config config, String taskId) throws ActivityException {
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
}
