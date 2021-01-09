package app.connectfour.gameEngine;

import java.io.IOException;

public interface ConnectFour {

    void setPlayerOneOnline(boolean sender) throws IOException;

    void setPlayersName(String playerName, int playerNumber);

    void pickColour(int playerNumber, PieceColour colour);

    boolean set(int x);

    boolean reset();
}
