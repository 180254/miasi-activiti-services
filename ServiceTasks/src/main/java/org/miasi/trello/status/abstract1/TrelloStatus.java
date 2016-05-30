package org.miasi.trello.status.abstract1;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import org.miasi.Logger;
import org.miasi.config.Config;

import java.io.IOException;
import java.util.List;

public class TrelloStatus {

    public static final String VAR_NAME = "trello_card_id";

    private final Logger logger;
    private final Config config;

    public TrelloStatus() throws IOException {
        logger = Logger.forClass(TrelloStatus.class);
        logger.log("New instance started.");

        logger.log("Reading config. Expected in: " + Config.configPath());
        try {
            config = Config.readFromConfigFile();
        } catch (IOException ex) {
            logger.log("Unable to read config.");
            logger.log(ex);
            throw ex;
        }
    }

    public void change(String trelloCardId, String newListName) {
        try {
            logger.log("STARTED for TRELLO_CARD_ID=" + trelloCardId + ", LIST=" + newListName);

            logger.log("Creating new instance of TrelloApi.");
            Trello trelloApi = new TrelloImpl(config.getTrelloKey(), config.getTrelloToken(), new ApacheHttpClient());

            logger.log("Getting card.");
            Card card = trelloApi.getCard(trelloCardId);

            logger.log("Getting new list.");
            TList newList = trello$getListByName(trelloApi, card.getIdBoard(), newListName);

            logger.log("Moving list.");
            card.setIdList(newList.getId());
            card.update();

            logger.log("Done");

        } catch (Exception ex) {
            logger.log("Failed!");
            logger.log(ex);
        }
    }

    public static TList trello$getListByName(Trello trelloApi, String boardId, String name) {
        Board board = trelloApi.getBoard(boardId);
        List<TList> tLists = board.fetchLists();

        for (TList tList : tLists) {
            if (tList.getName().equals(name)) {
                return tList;
            }
        }

        throw new RuntimeException("List " + name + "not found.");
    }
}
