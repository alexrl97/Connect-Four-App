package app.connectfour.gameEngine;

import java.util.ArrayList;

import app.connectfour.gameActivity.Game_Activity_Offline;
import app.connectfour.network.Game_Fragment_Online;

public class ConnectFourGame implements ConnectFour {

    private Matchfield matchfield = new Matchfield();
    private boolean myTurn = true;
    public Player players = new Player();
    private int scoreboard[] = new int[2];
    private boolean computerEnabled;
    private boolean onDeviceEnabled;
    private ComputerPlayer comp;
    private boolean computerWon;
    private int lastComputerStartSet = 0;
    private final int defend = 1;
    private final int attack = 2;
    private boolean offline = true;


    public ConnectFourGame(boolean online){
        if(online) {
            onDeviceEnabled = false;
            offline = false;
        }
        else
            onDeviceEnabled = true;
    }

    public ConnectFourGame(int difficulty){
        computerEnabled = true;
        this.comp = new ComputerPlayer(difficulty);
    }

    @Override
    public void setPlayerOneOnline(boolean sender){
        if(sender)
            players.setPlayerOne(true);
        else
            players.setPlayerOne(false);

        myTurn = sender;
    }

    @Override
    public void setPlayersName(String playerName, int playerNumber){
        players.setPlayersName(playerName, playerNumber);
    }

    public String getPlayersname(int playernumber){
        return players.getPlayersName(playernumber);
    }

    @Override
    public void pickColour(int playerNumber, PieceColour colour){
        players.pickColour(playerNumber, colour);
    }

    public PieceColour getColourByPlayerNumber(int playerNumber){
        return players.getColourByPlayerNumber(playerNumber);
    }

    public String getScoreFromPlayer(int playernumber){
        return Integer.valueOf(scoreboard[playernumber-1]).toString();
    }

    public boolean getComputerWon(){
        return computerWon;
    }

    public boolean getOnDeviceEnabled(){
        return onDeviceEnabled;
    }

    @Override
    public boolean set(int x){
        boolean setdone = false;
        if(computerEnabled){
            for (int y = 0; y < 6; y++)
                if (matchfield.getFieldValue(x, y) == 0) {
                    matchfield.setFieldValue(x, y, players.getMyPlayerNumber());
                    setdone = true; break;
                }
            if(setdone){
                if(win())
                    return true;
                else{
                    x = comp.computerSet();
                    for (int y = 0; y < 6; y++)
                        if (matchfield.getFieldValue(x, y) == 0) {
                            matchfield.setFieldValue(x, y, players.getOtherPlayerNumber());
                            Game_Activity_Offline.registerLastSetPosition(x,y);
                            break;
                }
                    }
            }
        }
        else {
            for (int y = 0; y < 6; y++) {
                if (matchfield.getFieldValue(x, y) == 0) {
                    if (myTurn)
                        matchfield.setFieldValue(x, y, players.getMyPlayerNumber());
                    else
                        matchfield.setFieldValue(x, y, players.getOtherPlayerNumber());
                    setdone = true;

                    if(offline) {
                        Game_Activity_Offline.registerLastSetPosition(x,y);
                    }
                    else
                        Game_Fragment_Online.registerLastSetPosition(x,y);

                    break;
                }
            }
            if(setdone)
                playerswitch();
        }

        if(win())
            return true;
        else
            return false;
    }


    public void updateScoreboard(int playerNumber){
        if(playerNumber == 1)
            scoreboard[0]++;
        else
            scoreboard[1]++;
    }

    public void playerswitch(){
        if(myTurn)
            myTurn = false;
        else
            myTurn = true;
    }

    @Override
    public boolean reset(){
        resetMatchfield();
        scoreboard[0] = 0;
        scoreboard[1] = 0;
        return true;
    }

    public void resetMatchfield(){
        for(int x = 0; x < 7; x++)
            for(int y = 0; y < 6; y++)
                matchfield.setFieldValue(x,y,0);
    }

    public boolean columnIsFull(int column){
        if(matchfield.getFieldValue(column, 5) == 0)
            return false;
        else
            return true;
    }

    public boolean win(){
        if(winHorizontal() || winVertical() || winDiagonal())
            return true;
        else
            return false;
    }

    public boolean winHorizontal(){
        for(int x = 0; x < 4; x++)
            for(int y = 0; y < 6; y++)
                if (fourInARow(x,y, 1, 0))
                    return true;
        return false;
    }

    public boolean winVertical(){
        for(int x = 0; x < 7; x++)
            for(int y = 0; y < 3; y++)
                if (fourInARow(x,y, 0, 1))
                    return true;
        return false;
    }

    public boolean winDiagonal(){
        for(int x = 0; x < 4; x++)
            for(int y = 0; y < 3; y++) {
                if (fourInARow(x,y, 1, 1))
                    return true;
            }

        for(int x = 6; x > 2; x--)
            for(int y = 2; y > -1; y--)
                if (fourInARow(x,y, -1, 1))
                    return true;
        return false;
    }

    public boolean fourInARow(int x, int y, int operatorX, int operatorY){
        if (matchfield.getFieldValue(x, y) != 0 &&
                matchfield.getFieldValue(x, y) == matchfield.getFieldValue(x + 1*operatorX, y + 1*operatorY) &&
                matchfield.getFieldValue(x, y) == matchfield.getFieldValue(x + 2*operatorX, y + 2*operatorY) &&
                matchfield.getFieldValue(x, y) == matchfield.getFieldValue(x + 3*operatorX, y + 3*operatorY)) {

            int[] values = {x,y, x + 1*operatorX, y + 1*operatorY, x + 2*operatorX, y + 2*operatorY, x + 3*operatorX, y + 3*operatorY};
            if(offline) {
                scoreboard[matchfield.getFieldValue(x, y) - 1]++;
                Game_Activity_Offline.registerLastWinPositions(values);
            }
            else
                Game_Fragment_Online.registerLastWinPositions(values);

            if(matchfield.getFieldValue(x, y) == 1)
                computerWon = false;
            else if(computerEnabled)
                computerWon = true;
            else
                computerWon = false;
            return true;
        }
        else if(matchfieldIsFull()){
            scoreboard[0]++;
            scoreboard[1]++;
            return true;
        }
        else
            return false;
    }

    public Matchfield getMatchfield(){
        return matchfield;
    }

    public boolean matchfieldIsFull(){
        int count = 0;
        for(int x = 0; x < 7; x++)
            if(matchfield.getFieldValue(x,5) != 0)
                count++;
        if(count == 7)
            return true;
        else
            return false;
    }

    public void doFirstComputerSet(){
        int randomColumn = (int)(Math.random()*5)+1;
        while(randomColumn == lastComputerStartSet)
            randomColumn = (int)(Math.random()*5)+1;
        lastComputerStartSet = randomColumn;
        matchfield.setFieldValue(randomColumn, 0, 2);
    }

    public class ComputerPlayer{

        int difficulty;

        public ComputerPlayer(int difficulty){
            this.difficulty = difficulty;
        }

        public int computerSet(){
            int column = -1;
            ArrayList<Integer> possibleSets = new ArrayList<>();

            possibleSets.add(treeInARowHorizontal());
            possibleSets.add(treeInARowVertical());
            possibleSets.add(treeInARowDiagonal());
            possibleSets.add(treeInARowWithGapHorizontal());
            possibleSets.add(treeInARowWithGapDiagonal());

            //Mittlerer Modus, 1 entspricht verteidigen, 2 entspricht angreifen
            if(difficulty > 1 && noSetFoundYet(possibleSets)){
                possibleSets.add(twoInARowHorizontal(attack));
                possibleSets.add(twoInARowDiagonal(attack));
                possibleSets.add(twoInARowVertical(attack));
                possibleSets.add(twoInARowVertical(defend));
                possibleSets.add(twoInARowHorizontal(defend));
                possibleSets.add(twoInARowDiagonal(defend));
            }
            //Schwerer Modus, 1 entspricht verteidigen, 2 entspricht angreifen
            if(difficulty > 2 && noSetFoundYet(possibleSets)){
                possibleSets.add(twoInARowWithGapHorizontal(attack));
                possibleSets.add(twoInARowWithGapDiagonal(attack));
                possibleSets.add(twoInARowWithGapHorizontal(defend));
                possibleSets.add(twoInARowWithGapDiagonal(defend));

                possibleSets.add(singleStone(attack));
                possibleSets.add(singleStone(defend));
            }

            for (int i = 0; i < possibleSets.size(); i ++)
                if(possibleSets.get(i) != -1) {
                    column = possibleSets.get(i);
                    break;
                }

            if(column != -1)
                return column;
            else{
                int randomColumn = (int) (Math.random() * 7);
                while(matchfield.getFieldValue(randomColumn, 5) != 0 && ! matchfieldIsFull())
                    randomColumn = (int) (Math.random() * 7);
                return randomColumn;
            }
        }

        private boolean noSetFoundYet(ArrayList<Integer> possibleSets){
            for(int i = 0; i < possibleSets.size(); i++)
                if(possibleSets.get(i) != -1) {
                    return false;
                }
            return true;
        }

        private int treeInARowHorizontal(){
            for(int x = 0; x < 5; x++)
                for(int y = 0; y < 6; y++) {
                    if (x < 4 && treeInARow(x, y, 1, 0) && matchfield.getFieldValue(x + 3, y) == 0) {
                        if(y == 0)
                            return x+3;
                        else
                        if(matchfield.getFieldValue(x + 3, y-1) != 0)
                            return x+3;
                    }
                    if((x-1) >= 0 && treeInARow(x, y, 1, 0) && matchfield.getFieldValue(x - 1, y) == 0)
                        if(y == 0)
                            return x-1;
                        else
                        if(matchfield.getFieldValue(x - 1, y-1) != 0)
                            return x-1;
                }
            return -1;
        }

        private int treeInARowVertical(){
            for(int x = 0; x < 7; x++)
                for(int y = 0; y < 3; y++)
                    if (treeInARow(x,y, 0, 1) && matchfield.getFieldValue(x, y+3) == 0)
                        return x;
            return -1;
        }

        private int treeInARowDiagonal(){
            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 3; y++)
                    if (treeInARow(x,y, 1, 1) && matchfield.getFieldValue(x+3, y+2) != 0 && matchfield.getFieldValue(x+3, y+3) == 0)
                        return x+3;

            for(int x = 6; x > 2; x--)
                for(int y = 2; y > -1; y--)
                    if (treeInARow(x,y, -1, 1) && matchfield.getFieldValue(x-3, y+2) != 0 && matchfield.getFieldValue(x-3, y+3) == 0)
                        return x-3;

            return -1;
        }

        private int treeInARowWithGapHorizontal(){
            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 6; y++) {
                    if (treeInARowWithGap(x, y, 1, 0)) {
                        if (matchfield.getFieldValue(x + 1, y) == 0) {
                            if (y == 0)
                                return x + 1;
                            else if (matchfield.getFieldValue(x + 1, y - 1) != 0)
                                return x + 1;
                        }
                        else if(matchfield.getFieldValue(x + 2, y) == 0)
                            if (y == 0)
                                return x + 2;
                            else if (matchfield.getFieldValue(x + 2, y - 1) != 0)
                                return x + 2;
                    }
                }

            return -1;
        }

        private int treeInARowWithGapDiagonal(){
            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 3; y++)
                    if(treeInARowWithGap(x,y,1,1)) {
                        if (matchfield.getFieldValue(x + 1, y + 1) == 0) {
                            if (y == 0)
                                return x + 1;
                            else if (matchfield.getFieldValue(x + 1, y) != 0)
                                return x + 1;
                        }
                        else if (matchfield.getFieldValue(x + 2, y + 2) == 0)
                            if (y == 0)
                                return x + 2;
                            else if (matchfield.getFieldValue(x + 2, y + 1) != 0)
                                return x + 2;
                    }

            for(int x = 6; x > 2; x--)
                for(int y = 2; y > -1; y--)
                    if(treeInARowWithGap(x,y,-1,1)) {
                        if (matchfield.getFieldValue(x - 1, y + 1) == 0) {
                            if (y == 0)
                                return x - 1;
                            else if (matchfield.getFieldValue(x - 1, y) != 0)
                                return x - 1;
                        } else if (matchfield.getFieldValue(x - 2, y + 2) == 0)
                            if (y == 0)
                                return x - 2;
                            else if (matchfield.getFieldValue(x - 2, y + 1) != 0)
                                return x - 2;
                    }
            return -1;
        }

        private int twoInARowVertical(int defendOrAttack){
            for(int x = 0; x < 7; x++)
                for(int y = 0; y < 3; y++)
                    if (twoInARow(x,y, 0, 1, defendOrAttack) && matchfield.getFieldValue(x, y+2) == 0)
                        return x;
            return -1;
        }

        private int twoInARowHorizontal(int defendOrAttack){
            for(int x = 0; x < 5; x++)
                for(int y = 0; y < 6; y++) {
                    if (x < 4 && twoInARow(x, y, 1, 0, defendOrAttack) && matchfield.getFieldValue(x + 2, y) == 0) {
                        if(y == 0)
                            return x+2;
                        else if(matchfield.getFieldValue(x + 2, y-1) != 0)
                            return x+2;
                    }
                    if((x-1) >= 0 && twoInARow(x, y, 1, 0, defendOrAttack) && matchfield.getFieldValue(x - 1, y) == 0)
                        if(y == 0)
                            return x-1;
                        else if(matchfield.getFieldValue(x - 1, y-1) != 0)
                            return x-1;
                }
            return -1;
        }

        private int twoInARowDiagonal(int defendOrAttack){
            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 3; y++)
                    if (twoInARow(x,y, 1, 1, defendOrAttack) && matchfield.getFieldValue(x+2, y+1) != 0 && matchfield.getFieldValue(x+2, y+2) == 0)
                        return x+2;

            for(int x = 6; x > 2; x--)
                for(int y = 2; y > -1; y--)
                    if (twoInARow(x,y, -1, 1, defendOrAttack) && matchfield.getFieldValue(x-2, y+1) != 0 && matchfield.getFieldValue(x-2, y+2) == 0)
                        return x-2;

            return -1;
        }

        public int twoInARowWithGapHorizontal(int defendOrAttack){
            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 6; y++) {
                    if (twoInARowWithGap(x, y, 1, 0, defendOrAttack)) {
                        if (matchfield.getFieldValue(x + 1, y) == 0) {
                            if (y == 0 || matchfield.getFieldValue(x + 1, y - 1) != 0)
                                return x + 1;
                        }
                    }
                }
            return -1;
        }

        private int twoInARowWithGapDiagonal(int defendOrAttack){
            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 3; y++)
                    if(twoInARowWithGap(x,y,1,1, defendOrAttack)) {
                        if (matchfield.getFieldValue(x + 1, y + 1) == 0 && matchfield.getFieldValue(x + 1, y) != 0) {
                            return x + 1;
                        }
                    }

            for(int x = 6; x > 2; x--)
                for(int y = 2; y > -1; y--)
                    if(twoInARowWithGap(x,y,-1,1, defendOrAttack)) {
                        if (matchfield.getFieldValue(x - 1, y + 1) == 0 && matchfield.getFieldValue(x - 1, y) != 0) {
                            return x - 1;
                        }
                    }
            return -1;
        }

        private int singleStone(int defendOrAttack){
            for(int x = 0; x < 7; x++)
                for(int y = 0; y < 6; y++)
                    if (matchfield.getFieldValue(x, y) == defendOrAttack){
                        //vertical
                        if(y < 5 && matchfield.getFieldValue(x, y + 1) == 0)
                            return x;
                        //horizontal
                        if(x < 6 && matchfield.getFieldValue(x + 1, y) == 0)
                            return x + 1;
                        if(x == 6 && matchfield.getFieldValue(x - 1, y) == 0)
                            return x - 1;
                        //diagonal right
                        if(x < 6 && y < 5 && matchfield.getFieldValue(x + 1, y + 1) == 0 && matchfield.getFieldValue(x + 1, y) != 0)
                            return x + 1;
                        // diagonal left
                        if(x > 0 && y < 5 && matchfield.getFieldValue(x - 1, y + 1) == 0 && matchfield.getFieldValue(x - 1, y) != 0)
                            return x - 1;
                    }
            return -1;
        }

        private boolean treeInARow(int x, int y, int operatorX, int operatorY){
            if (matchfield.getFieldValue(x, y) != 0 &&
                    matchfield.getFieldValue(x, y) == matchfield.getFieldValue(x + 1*operatorX, y + 1*operatorY) &&
                    matchfield.getFieldValue(x, y) == matchfield.getFieldValue(x + 2*operatorX, y + 2*operatorY))
                return true;
            else
                return false;
        }

        private boolean treeInARowWithGap(int x, int y, int operatorX, int operatorY){
            int count = 1;
            if (matchfield.getFieldValue(x, y) != 0)
                for(int i = 1; i < 4; i++)
                    if(matchfield.getFieldValue(x, y) == matchfield.getFieldValue(x + i*operatorX, y + i*operatorY))
                        count++;

            if(count == 3) {
                return true;
            }
            else
                return false;
        }

        private boolean twoInARow(int x, int y, int operatorX, int operatorY, int defendOrAttack){
            if (matchfield.getFieldValue(x, y) == defendOrAttack &&
                    matchfield.getFieldValue(x, y) == matchfield.getFieldValue(x + 1*operatorX, y + 1*operatorY))
                return true;
            else
                return false;
        }

        private boolean twoInARowWithGap(int x, int y, int operatorX, int operatorY, int defendOrAttack){
            if (matchfield.getFieldValue(x, y) == defendOrAttack &&
                    matchfield.getFieldValue(x + 1*operatorX, y + 1*operatorY) == 0 &&
                    matchfield.getFieldValue(x + 2*operatorX, y + 2*operatorY) == defendOrAttack)
                return true;
            else
                return false;
        }
    }
}
