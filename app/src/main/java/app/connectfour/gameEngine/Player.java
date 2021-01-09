package app.connectfour.gameEngine;

public class Player {

    private String playerOneName = "Player 1";
    private String playerTwoName = "Player 2";
    private PieceColour playerOneColour = PieceColour.RED;
    private PieceColour playerTwoColour = PieceColour.YELLOW;
    private int myPlayerNumber = 1;


    public void setPlayerOne(boolean sender){
        if(sender)
            myPlayerNumber = 1;
        else
            myPlayerNumber = 2;
    }

    public int getMyPlayerNumber(){
        return myPlayerNumber;
    }

    public int getOtherPlayerNumber(){
        if(myPlayerNumber == 1)
            return 2;
        else
            return 1;
    }

    public void setPlayersName(String playerName, int playerNumber){
        if(playerNumber == 1)
            playerOneName = playerName;
        else
            playerTwoName = playerName;
    }

    public String getPlayersName(int playerNumber){
        if(playerNumber == 1)
            return playerOneName;
        else
            return playerTwoName;
    }

    public void pickColour(int playerNumber, PieceColour Colour){
        if(playerNumber == 1) {
            playerOneColour = Colour;
            if(Colour == PieceColour.RED)
                playerTwoColour = PieceColour.YELLOW;
            else
                playerTwoColour = PieceColour.RED;
        }
        else if(playerNumber == 2) {
            playerTwoColour = Colour;
            if(Colour == PieceColour.RED)
                playerOneColour = PieceColour.YELLOW;
            else
                playerOneColour = PieceColour.RED;
        }
    }

    public PieceColour getColourByPlayerNumber(int playerNumber){
        if(playerNumber == 1)
            return playerOneColour;
        else
            return playerTwoColour;
    }
}
