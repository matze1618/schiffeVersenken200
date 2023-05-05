import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import static java.util.Map.entry;

public class Main {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    Scanner scan = new Scanner(System.in);
    enum Phase { START, PICKPHASE1, PICKPHASE2, ATCKPLAYER1, ATCKPLAYER2, GAMEOVER }
    static Phase phase = Phase.START;
    int size = 10;
    String p1Name; //Playername
    String p2Name; //Playername
    String [][] mapLayoutArray = {{" ", "|", "1", "2", "3", "4", "5", "6", "7", "8", "9", "z", "|"}, {"=", "+", "=", "=", "=", "=", "=", "=", "=", "=", "=", "=", "|"}, {"A", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"B", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"C", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"D", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"E", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"F", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"G", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"H", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"I", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"J", "|", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "|"}, {"=", "+", "=", "=", "=", "=", "=", "=", "=", "=", "=", "=", "|"}};
    String [][] mapShips1Array = new String[13][13];
    String [][] mapShips2Array = new String[13][13];
    String [][] mapATCK1Array = new String[13][13];
    String [][] mapATCK2Array = new String[13][13];
    String [][] mapArray = new String[13][13];
    Map<String, String> mapForDraw = Map.ofEntries(entry("=", "---"), entry("z", " 10"), entry("|", "\u007C"), entry("+", "+"), entry("0", "~~~"), entry("neighbor", "~~~"), entry("a", " X "), entry("b", " X "), entry("c", " X "), entry("d", " X "), entry("e", " X "), entry("f", " X "), entry("g", " X "), entry("h", " X "), entry("i", " X "), entry("j", " X "), entry(" ", "   "), entry("1", " 1 "), entry("2", " 2 "), entry("3", " 3 "), entry("4", " 4 "), entry("5", " 5 "), entry("6", " 6 "), entry("7", " 7 "), entry("8", " 8 "), entry("9", " 9 "), entry(ANSI_RED + " X " + ANSI_RESET, ANSI_RED + " X " + ANSI_RESET));
    int[] lengthByID = {1, 2, 2, 2, 3, 3, 3, 4, 4, 5};
    String[] id = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    int[] livesByID1 = new int[10];
    int[] livesByID2 = new int[10];
    int p1counter = 10;
    int p2counter = 10;
    boolean checker = true;

    public static void main(String[] args) throws InterruptedException {
        Main game = new Main();
        game.setArrayEqual(game.mapShips1Array, game.mapLayoutArray);
        game.setArrayEqual(game.mapShips2Array, game.mapLayoutArray);
        game.setArrayEqual(game.mapATCK1Array, game.mapLayoutArray);
        game.setArrayEqual(game.mapATCK2Array, game.mapLayoutArray);
        System.arraycopy(game.lengthByID, 0, game.livesByID1, 0, 10);
        System.arraycopy(game.lengthByID, 0, game.livesByID2, 0, 10);
        while(!(phase == Phase.GAMEOVER)) {
            if (phase == Phase.START) {
                game.getName();
//                game.test();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();

                game.draw();
                game.input();
                if(phase == Phase.ATCKPLAYER1 || phase == Phase.ATCKPLAYER2) { TimeUnit.MILLISECONDS.sleep(1500); }
            }
        }
    }
    void draw(){
        switch (phase) {
            case ATCKPLAYER1 -> setArrayEqual(mapArray, mapATCK1Array);
            case ATCKPLAYER2 -> setArrayEqual(mapArray, mapATCK2Array);
            case PICKPHASE1 -> setArrayEqual(mapArray, mapShips1Array);
            case PICKPHASE2 -> setArrayEqual(mapArray, mapShips2Array);
        }
        for(int j = 0; j < size + 3; j++){
            for(int i = 0; i < size + 3; i++){
                System.out.print(mapForDraw.getOrDefault(mapArray[j][i], " X "));
            }
            System.out.println();
        }
    }
    void getName(){
        System.out.print("Player1 Name: ");
        p1Name = ANSI_RED + scan.next() + ANSI_RESET;
        System.out.print("Player2 Name: ");
        p2Name = ANSI_GREEN + scan.next() + ANSI_RESET;
        phase = Phase.PICKPHASE1;
        System.out.println();
    }
    void input(){
        checker = true;
        String inputHV;
        int xCord;
        String yCord;
        if (phase == Phase.ATCKPLAYER1 || phase == Phase.ATCKPLAYER2) {
            System.out.println((phase == Phase.ATCKPLAYER1 ? p1Name : p2Name) + ", wähle eine Position, an die Du schießen möchtest (X Y)");
            xCord = scan.nextInt();
            xCord++;
            yCord = scan.next();
            if (!shoot(xCord, abcToInt(yCord), (phase == Phase.ATCKPLAYER1 ? mapShips2Array : mapShips1Array))) { phase = (phase == Phase.ATCKPLAYER1 ? Phase.ATCKPLAYER2 : Phase.ATCKPLAYER1); }
        } else {
            System.out.println((phase == Phase.PICKPHASE1 ? p1Name : p2Name) + ", wähle die Position für dein Schiff der Länge " + lengthByID[p1counter - 1] + "\nVertikal oder Horizontal (V/H) und X Y");
            inputHV = scan.next();
            xCord = scan.nextInt();
            xCord++;
            yCord = scan.next();
            if (placeShip(lengthByID[(phase == Phase.PICKPHASE1 ? p1counter : p2counter) - 1], xCord, yCord, inputHV, mapShips1Array, (phase == Phase.PICKPHASE1 ? p1counter : p2counter))) { p1counter--; }
            if ((phase == Phase.PICKPHASE1 ? p1counter : p2counter) == 0) {
                draw();
                phase = (phase == Phase.PICKPHASE1 ? Phase.PICKPHASE2 : Phase.ATCKPLAYER1);
            }
        }
    }
    void setArrayEqual(String[][]array1, String[][]array2){
        for(int j=0; j < array2.length; j++){
            System.arraycopy(array2[j], 0, array1[j], 0, array2.length);
        }
    }
    int abcToInt(String yString) {
        if (yString.length() == 1){
            return yString.toUpperCase().charAt(0) - 'A' + 2;
        } else {
            throw new IllegalArgumentException();
        }
    }
    boolean placeShip(int size, int x, String yString, String orientation, String[][] map, int counter){
        if(x < 2 || x > 11) x = 0;
        int y = abcToInt(yString);
        if (orientation.equals("V") || orientation.equals("v")) {
            for (int i = 0; i < size; i++) {
                if (checker && !Objects.equals(map[y + i][x], "0")) {
                    System.out.println("Position nicht möglich. Wähle eine neue.");
                    checker = false;
                    return false;
                }
            }
            if (checker) {
                for (int i = 0; i < size; i++) {
                    map[y + i][x] = id[counter-1];
                    placeBlock(y + i, x - 1, map);
                    placeBlock(y + i, x + 1, map);
                }
                placeBlock(y - 1, x, map);
                placeBlock(y + size, x, map);
                return true;
            }
        }
        if (orientation.equals("H") || orientation.equals("h")) {
            for (int i = 0; i < size; i++) {
                if (checker && !(mapArray[y][x + i]).equals("0")) {
                    System.out.println("Position nicht möglich. Wähle eine neue.");
                    checker = false;
                    return false;
                }
            }
            if (checker) {
                for (int i = 0; i < size; i++) {
                    mapShips1Array[y][x + i] = id[counter-1];
                    placeBlock(y - 1, x + i, map);
                    placeBlock(y + 1, x + i, map);
                }
                placeBlock(y, x - 1, map);
                placeBlock(y, x + size, map);
                return true;
            }
        }
        return false;
    }
    void placeBlock(int y, int x, String[][] map){
        if(Objects.equals(map[y][x], "0")) { map[y][x] = "neighbor"; }
    }
    boolean shoot(int x, int y, String[][] map){
        if(map[y][x].charAt(0) == 'a' || map[y][x].charAt(0) == 'b' || map[y][x].charAt(0) == 'c' || map[y][x].charAt(0) == 'd' || map[y][x].charAt(0) == 'e' || map[y][x].charAt(0) == 'f' || map[y][x].charAt(0) == 'g' || map[y][x].charAt(0) == 'h' || map[y][x].charAt(0) == 'i' || map[y][x].charAt(0) == 'j') {
            if(phase == Phase.ATCKPLAYER2) {
                livesByID1[abcToInt(map[y][x].substring(0, 1)) - 2]--;
                mapATCK2Array[y][x] = ANSI_RED + " X " + ANSI_RESET;
            } else if(phase == Phase.ATCKPLAYER1){
                livesByID2[abcToInt(map[y][x].substring(0, 1)) - 2]--;
                mapATCK1Array[y][x] = ANSI_RED + " X " + ANSI_RESET;
            }
            map[y][x] = "X";
            System.out.println("Hit!");
            checkForLife();
            return true;
        }
        else{
            if(phase == Phase.ATCKPLAYER2) {
                mapATCK2Array[y][x] = "X";
            } else if(phase == Phase.ATCKPLAYER1){
                mapATCK1Array[y][x] = "X";
            }
            System.out.println("Miss!");
            return false;
        }
    }
    void checkForLife() {
        for (int i = 0; i < 10; i++) {
            if ((phase == Phase.ATCKPLAYER1 ? livesByID2 : livesByID1)[i] <= 0) {
                System.out.println("Du hast ein Schiff mit der Länge " + lengthByID[i] + " zerstört!");
                if (phase == Phase.ATCKPLAYER1) livesByID2[i]--;
                if (phase == Phase.ATCKPLAYER2) livesByID1[i]--;
                checkGameOver();
            }
        }
    }
    void checkGameOver() {
        boolean gameOver = true;
        for (int i = 0; i < 10; i++) {
            if (livesByID2[i] >= 0) {
                gameOver = false;
                break;
            }
        }
        if (gameOver) { phase = Phase.GAMEOVER; }
    }
















    void test(){
        placeShip(5, 2, "c", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(4, 4, "a", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(4, 6, "a", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(3, 8, "a", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(3, 10, "a", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(3, 11, "g", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(2, 9, "g", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(2, 7, "g", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(2, 5, "g", "v", mapShips1Array, p1counter);
        p1counter--;
        placeShip(1, 3, "i", "v", mapShips1Array, p1counter);
        p1counter--;

        placeShip(5, 2, "c", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(4, 4, "a", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(4, 6, "a", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(3, 8, "a", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(3, 10, "a", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(3, 11, "g", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(2, 9, "g", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(2, 7, "g", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(2, 5, "g", "v", mapShips2Array, p2counter);
        p2counter--;
        placeShip(1, 3, "i", "v", mapShips2Array, p2counter);
        p2counter--;
        phase = Phase.ATCKPLAYER1;
    }
}
