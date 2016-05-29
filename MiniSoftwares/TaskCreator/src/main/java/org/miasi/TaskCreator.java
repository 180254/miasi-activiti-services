package org.miasi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.exception.TrelloHttpException;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskCreator {

    private Config config;
    private TrelloImpl trelloApi;

    private JPanel jPanel;
    private JTextField emailField;
    private JTextField nameField;
    private JTextArea descArea;
    private JButton submitButton;

    public TaskCreator() {
        submitButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitButtonClicked();
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
            submitButton.setEnabled(false);
            emailField.setEditable(false);
            descArea.setEditable(false);
            submitButton.setText("CONFIG ERROR");
            JOptionPane.showMessageDialog(null, "Error!\n" +
                            "Config file doesn't exist or has bad structure.\n" +
                            "Please fix it and restart app.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void submitButtonClicked() {
        try {
            if (!EmailValidator.getInstance().isValid(emailField.getText())) {
                throw new IllegalArgumentException("Given email is not proper");
            }
            if (StringUtils.isBlank(nameField.getText())) {
                throw new IllegalArgumentException("Issue name cannot be blank.");
            }
            if (StringUtils.isBlank(descArea.getText())) {
                throw new IllegalArgumentException("Issue description cannot be blank.");
            }


            Card card = trello$createCard();
            TList tList = trello$getProperList();
            Card cardCreated = tList.createCard(card);

            List<String> metadata = new ArrayList<>();
            metadata.add(cardCreated.getId());

            trello$addCommentToCard(cardCreated.getId(), StringUtils.join(metadata, "\n"));

            JOptionPane.showMessageDialog(null,
                    "Task successfully added.",
                    "OK", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error! " + ex.getMessage(),
                    "Warning", JOptionPane.WARNING_MESSAGE);

        } catch (TrelloHttpException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error! Something go wrong. Trello returned:\n" + ex.getMessage(),
                    "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public Card trello$createCard() {
        Card card = new Card();
        card.setName(nameField.getText());
        card.setDesc(descArea.getText());
        return card;
    }

    public TList trello$getProperList() {
        Board board = trelloApi.getBoard(config.getTrelloBoardId());
        List<TList> tLists = board.fetchLists();

        if (tLists.size() == 0 || !tLists.get(0).getName().equals("New")) {
            throw new TrelloHttpException("'New' list not found on expected pos.");
        }

        return tLists.get(0);
    }

    public void trello$addCommentToCard(String cardId, String comment) {
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


}

