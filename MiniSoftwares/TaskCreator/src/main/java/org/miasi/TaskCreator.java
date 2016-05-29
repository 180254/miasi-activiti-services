package org.miasi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.exception.TrelloHttpException;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.unbescape.uri.UriEscape;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.*;

public class TaskCreator {

    private Config config;
    private TrelloImpl trelloApi;

    private JPanel jPanel;
    private JTextField emailField;
    private JTextField nameField;
    private JTextArea descArea;
    private JButton submitButton;
    private JTextArea statusArea;

    public TaskCreator() {
        DefaultCaret historyCaret = (DefaultCaret) statusArea.getCaret();
        historyCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        statusArea.setEditable(false);
        submitButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        submitButtonClicked();
                    }
                }).start();
            }
        });
    }

    public static void main(String[] args) {
        TaskCreator tc = new TaskCreator();

        JFrame frame = new JFrame("TaskCreator");
        frame.setContentPane(tc.jPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        tc.init();
    }

    public void init() {
        try {
            config = Config.readFromConfigFile();
            trelloApi = new TrelloImpl(config.getTrelloKey(), config.getTrelloToken(), new ApacheHttpClient());

        } catch (IOException ex) {
            setEnabled(false);
            submitButton.setText("CONFIG ERROR");

            String msg = "Error!\n" +
                    "Config file doesn't exist or has bad structure.\n" +
                    "Please fix it and restart app.";

            statusArea.append(msg);
            JOptionPane.showMessageDialog(null, "Error!\n" +
                            "Config file doesn't exist or has bad structure.\n" +
                            "Please fix it and restart app.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setEnabled(boolean enabled) {
        submitButton.setEnabled(enabled);
        emailField.setEditable(enabled);
        nameField.setEditable(enabled);
        descArea.setEditable(enabled);
    }

    public void submitButtonClicked() {
        try {
            setEnabled(false);

            statusArea.append("Validating: email.\n");
            if (!EmailValidator.getInstance().isValid(emailField.getText())) {
                throw new IllegalArgumentException("Given email is not proper.");
            }

            statusArea.append("Validating: issue name.\n");
            if (StringUtils.isBlank(nameField.getText())) {
                throw new IllegalArgumentException("Issue name cannot be blank.");
            }

            statusArea.append("Validating: issue description.\n");
            if (StringUtils.isBlank(descArea.getText())) {
                throw new IllegalArgumentException("Issue description cannot be blank.");
            }

            statusArea.append("Trello: creating card domain.\n");
            Card card = trello$createCard(nameField.getText(), descArea.getText());

            statusArea.append("Trello: obtaining proper list.\n");
            TList tList = trello$getListByName(trelloApi, config.getTrelloBoardId(), "New");

            statusArea.append("Activity: obtaining process id.\n");
            String processId = activity$getProcessId(config, config.getActivityProcessName());

            statusArea.append("Trello: creating card on trello.\n");
            Card cardCreated = tList.createCard(card);

            Map<String, String> metadata = new HashMap<>();
            metadata.put("trello_card_url", cardCreated.getUrl());
            metadata.put("trello_card_id", cardCreated.getId());
            metadata.put("creator_email", emailField.getText());

            statusArea.append("Activity: running new process instance.\n");
            List<String> runProcess = activity$runProcess(config, processId, metadata);

            metadata.put("activity_process_id", runProcess.get(0));
            metadata.put("activity_process_url", runProcess.get(1));

            statusArea.append("Trello: creating card comment with metadata.\n");
            trello$addCommentToCard(config, cardCreated.getId(), StringUtils.join(metadata, "\n"));

            statusArea.append("Done: task added.\n");
            statusArea.append(metadata.toString());

            JOptionPane.showMessageDialog(null,
                    "Task successfully added.",
                    "OK", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            String msg = "Warning! " + ex.getMessage();
            statusArea.append(msg + "\n");
            JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);

        } catch (TrelloHttpException ex) {
            String msg = "Error! Something go wrong. Trello returned:\n" + ex.getMessage();
            statusArea.append(msg + "\n");
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.WARNING_MESSAGE);

        } finally {
            setEnabled(true);
            statusArea.append("\n");
        }
    }

    public static Card trello$createCard(String name, String desc) {
        Card card = new Card();
        card.setName(name);
        card.setDesc(desc);
        return card;
    }

    public static TList trello$getListByName(Trello trelloApi, String boardId, String name) {
        Board board = trelloApi.getBoard(boardId);
        List<TList> tLists = board.fetchLists();

        for (TList tList : tLists) {
            if (tList.getName().equals(name)) {
                return tList;
            }
        }

        throw new TrelloHttpException("List " + name + "not found");
    }

    public static void trello$addCommentToCard(Config config, String cardId, String comment) {
        String addCommentUrl = String.format(
                "https://api.trello.com/1/cards/%s/actions/comments?key=%s&token=%s",
                cardId,
                config.getTrelloKey(),
                config.getTrelloToken());

        try {
            String commentJsonValue = new ObjectMapper().writerFor(String.class).writeValueAsString(comment);
            String commentJson = "{\"text\":  " + commentJsonValue + "}";

            Response response =
                    Request.Post(addCommentUrl)
                            .addHeader("Content-Type", "application/json")
                            .bodyString(commentJson, ContentType.APPLICATION_JSON)
                            .execute();

            if (response.returnResponse().getStatusLine().getStatusCode() != 200) {
                throw new TrelloHttpException("add comment response code != 200");
            }
        } catch (IOException e) {
            throw new TrelloHttpException(e);
        }
    }

    public static Header activity$getAuthHeader(Config config) {
        String activityBasic = config.getActivityUsername() + ":" + config.getActivityPassword();
        String activityBasic64 = BaseEncoding.base64().encode(activityBasic.getBytes());
        String headerVal = "Basic " + activityBasic64;

        return new BasicHeader("Authorization", headerVal);
    }

    public static String activity$getProcessId(Config config, String name) {
        String processDefinitionUrl = String.format(
                "%s/service/repository/process-definitions?name=%s",
                config.getActivityRestUrl(),
                UriEscape.escapeUriQueryParam(name));

        try {
            String content = Request.Get(processDefinitionUrl)
                    .addHeader(activity$getAuthHeader(config))
                    .execute().returnContent().asString();


            Map<String, Object> contentMap = new ObjectMapper().readerFor(HashMap.class).readValue(content);
            List<Object> data = (List<Object>) contentMap.get("data");
            Map<String, Object> data0 = (Map<String, Object>) data.get(0);

            return (String) data0.get("id");

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }

    public static List<String> activity$runProcess(Config config, String processId, Map<String, String> variables) {
        String processInstanceUrl = String.format(
                "%s/service/runtime/process-instances",
                config.getActivityRestUrl());

        try {

            List<Map<String, String>> variablesList = new ArrayList<>();
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                Map<String, String> varMap = new HashMap<>();
                varMap.put("name", entry.getKey());
                varMap.put("value", entry.getValue());
                variablesList.add(varMap);
            }

            HashMap<String, Object> params = new HashMap<>();
            params.put("processDefinitionId", processId);
            params.put("variables", variablesList);

            String paramsJson = new ObjectMapper().writerFor(HashMap.class).writeValueAsString(params);

            String content = Request.Post(processInstanceUrl)
                    .addHeader(activity$getAuthHeader(config))
                    .addHeader("Content-Type", "application/json")
                    .bodyString(paramsJson, ContentType.APPLICATION_JSON)
                    .execute().returnContent().asString();

            Map<String, String> contentMap = new ObjectMapper().readerFor(HashMap.class).readValue(content);
            return Arrays.asList(contentMap.get("id"), contentMap.get("url"));

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }
}

