package org.miasi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.miasi.config.Config;
import org.miasi.exception.ActivityException;
import org.miasi.exception.TrelloException;
import org.unbescape.uri.UriEscape;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        statusArea.setEditable(false);
        DefaultCaret historyCaret = (DefaultCaret) statusArea.getCaret();
        historyCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

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

            Map<String, String> metadataForActivity = ImmutableMap.of(
                    "task_creator_email", emailField.getText(),
                    "trello_card_url", cardCreated.getUrl(),
                    "trello_card_id", cardCreated.getId());

            statusArea.append("Activity: running new process instance.\n");
            String newProcessId = activity$runProcess(config, processId, metadataForActivity);

            Map<String, String> metadataForTrello = ImmutableMap.of(
                    "task_creator_email", emailField.getText(),
                    "activity_process_id", newProcessId);

            statusArea.append("Trello: creating card comment with metadataForActivity.\n");
            trello$addCommentToCard(config, cardCreated.getId(), mapToString(metadataForTrello));

            statusArea.append("Done: task added.\n");
            statusArea.append("   - info about Trello\n");
            statusArea.append(mapToString(metadataForActivity) + "\n");
            statusArea.append("   - info about Activity\n");
            statusArea.append(mapToString(metadataForTrello) + "\n");

            nameField.setText("");
            descArea.setText("");

            JOptionPane.showMessageDialog(null,
                    "Task successfully added.",
                    "OK", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            String msg = "Validation error! " + ex.getMessage();
            statusArea.append(msg + "\n");
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.WARNING_MESSAGE);

        } catch (TrelloException ex) {
            String msg = "Error! Something go wrong with Trello. Exception:\n" + ex.getClass();
            statusArea.append(msg + "\n");
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.WARNING_MESSAGE);

        } catch (ActivityException ex) {
            String msg = "Error! Something go wrong with Activity. Exception:\n" + ex.getClass();
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

    public static TList trello$getListByName(Trello trelloApi, String boardId, String name) throws TrelloException {
        try {
            Board board = trelloApi.getBoard(boardId);
            List<TList> tLists = board.fetchLists();

            for (TList tList : tLists) {
                if (tList.getName().equals(name)) {
                    return tList;
                }
            }

            throw new TrelloException("List " + name + "not found.");
        } catch (Exception e) {
            throw new TrelloException(e);
        }
    }

    public static void trello$addCommentToCard(Config config, String cardId, String comment) throws TrelloException {
        String addCommentUrl = String.format(
                "https://api.trello.com/1/cards/%s/actions/comments?key=%s&token=%s",
                cardId,
                config.getTrelloKey(),
                config.getTrelloToken());

        try {
            Map<String, String> commentMap = ImmutableMap.of("text", comment);
            String commentJson = toJson(Map.class, commentMap);

            Request.Post(addCommentUrl)
                    .bodyString(commentJson, ContentType.APPLICATION_JSON)
                    .execute().returnContent().asString();

        } catch (Exception e) {
            throw new TrelloException(e);
        }
    }

    public static Header activity$getAuthHeader(Config config) {
        String activityBasic = config.getActivityUsername() + ":" + config.getActivityPassword();
        String activityBasic64 = BaseEncoding.base64().encode(activityBasic.getBytes());
        String headerVal = "Basic " + activityBasic64;
        return new BasicHeader("Authorization", headerVal);
    }

    public static String activity$getProcessId(Config config, String name) throws ActivityException {
        String processDefinitionUrl = String.format(
                "%s/service/repository/process-definitions?name=%s",
                config.getActivityRestUrl(),
                UriEscape.escapeUriQueryParam(name));

        try {
            String content = Request.Get(processDefinitionUrl)
                    .addHeader(activity$getAuthHeader(config))
                    .execute().returnContent().asString();

            Map<String, Object> contentMap = fromJson(Map.class, content);
            List<Object> data = (List<Object>) contentMap.get("data");
            Map<String, Object> data0 = (Map<String, Object>) data.get(0);

            return (String) data0.get("id");

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }

    public static String activity$runProcess(Config config, String processId, Map<String, String> variables)
            throws ActivityException {

        String processInstanceUrl = String.format(
                "%s/service/runtime/process-instances",
                config.getActivityRestUrl());

        try {
            List<Map<String, String>> variablesListOfMaps = new ArrayList<>();
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                Map<String, String> varMap = ImmutableMap.of(
                        "name", entry.getKey(),
                        "value", entry.getValue());
                variablesListOfMaps.add(varMap);
            }

            Map<String, Object> params = ImmutableMap.of(
                    "processDefinitionId", processId,
                    "variables", variablesListOfMaps);
            String paramsJson = toJson(Map.class, params);

            String content = Request.Post(processInstanceUrl)
                    .addHeader(activity$getAuthHeader(config))
                    .bodyString(paramsJson, ContentType.APPLICATION_JSON)
                    .execute().returnContent().asString();

            Map<String, String> contentMap = fromJson(Map.class, content);
            return contentMap.get("id");

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }

    public static <T> String toJson(Class<T> clazz, T obj) throws IOException {
        try {
            return new ObjectMapper().writerFor(clazz).writeValueAsString(obj);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static <T1, T2> T2 fromJson(Class<T1> clazz, String obj) throws IOException {
        try {
            return new ObjectMapper().readerFor(clazz).readValue(obj);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static String mapToString(Map<?, ?> map) {
        return Joiner.on("\n").withKeyValueSeparator(" -> ").join(map);
    }
}

