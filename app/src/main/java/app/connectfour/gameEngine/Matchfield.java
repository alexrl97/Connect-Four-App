package app.connectfour.gameEngine;

public class Matchfield{
    private int x = 7;
    private int y = 6;
    private int field[][] = new int[x][y];

    public int getFieldValue(int x, int y) {
        return field[x][y];
    }

    public void setFieldValue(int x, int y, int playernumber) {
        field[x][y] = playernumber;
    }

    public void printMatchfield(){
        System.out.println("  |_1_|_2_|_3_|_4_|_5_|_6_|_7_|");
        for(int y = 5; y >= 0; y--) {
            System.out.print("  |");
            for(int x = 0; x <= 6; x++) {
                if (field[x][y] == 0)
                    System.out.print("___|");
                else if (field[x][y] == 1)
                    System.out.print("_0_|");
                else
                    System.out.print("_X_|");
            }
            System.out.println();
        }
    }
}