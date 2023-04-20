package Utils;

import java.util.ArrayList;

public class LeeAlgorithm {

    public static void printMap(boolean[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j] ? "#" : ".");
            }
            System.out.println();
        }
    }

    private static void printMap(int[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.printf("%3s", map[i][j] == -1 ? "##" : String.valueOf(map[i][j]));
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        final String[] strMap = {
                "##############",
                "#1#####00000##",
                "#00000#000#0##",
                "##00#000##000#",
                "######0####0##",
                "##20000#######",
                "##############",
        };

        Vector2D from = Vector2D.infVector, to = Vector2D.infVector;
        boolean[][] map = new boolean[strMap.length][strMap[0].length()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (strMap[i].charAt(j) == '#') {
                    map[i][j] = true;
                } else {
                    map[i][j] = false;
                    if (strMap[i].charAt(j) == '1')
                        from = new Vector2D(i, j);
                    if (strMap[i].charAt(j) == '2')
                        to = new Vector2D(i, j);
                }
            }
        }
        ArrayList<Vector2D> path = LeeAlgorithm.findPath(map, to, from);

        printMap(map);
        for (Vector2D ij : path) {
            System.out.print(ij.x + " " + ij.y + " -> ");
        }
        System.out.println("END");
    }

    private static ArrayList<Vector2D> neighbours(boolean[][] map, Vector2D vecCell) {
        int[] cell = new int[2];
        cell[0] = (int) vecCell.x;
        cell[1] = (int) vecCell.y;
        ArrayList<Vector2D> res = new ArrayList<>();
        if (cell[0] > 0 && !(map[cell[0] - 1][cell[1]]))
            res.add(new Vector2D(cell[0] - 1, cell[1]));
        if (cell[0] < map.length - 1 && !(map[cell[0] + 1][cell[1]]))
            res.add(new Vector2D(cell[0] + 1, cell[1]));
        if (cell[1] > 0 && !(map[cell[0]][cell[1] - 1]))
            res.add(new Vector2D(cell[0], cell[1] - 1));
        if (cell[1] < map[0].length - 1 && !(map[cell[0]][cell[1] + 1]))
            res.add(new Vector2D(cell[0], cell[1] + 1));
        return res;
    }

    public static ArrayList<Vector2D> findPath(boolean[][] map, Vector2D fromVec, Vector2D toVec) {
        int[][] fill = new int[map.length][map[0].length];
        int wave = 1;
        fill[(int) fromVec.x][(int) fromVec.y] = wave;
        ArrayList<Vector2D> prevWaveCells = new ArrayList<>();
        prevWaveCells.add(fromVec);
        boolean canFind = true;
        while (fill[(int) toVec.x][(int) toVec.y] == 0 && canFind) {
            ArrayList<Vector2D> curFilledCells = new ArrayList<>();
            for (Vector2D prevCell : prevWaveCells) {
                for (Vector2D n : neighbours(map, prevCell)) {
                    if (fill[(int) n.x][(int) n.y] == 0) {
                        fill[(int) n.x][(int) n.y] = wave + 1;
                        curFilledCells.add(n);
                    }
                }
            }
            prevWaveCells = curFilledCells;
            canFind = prevWaveCells.size() > 0;
            wave++;
            //printMap(fill);
            //System.out.println();
        }
        if (canFind) {
            ArrayList<Vector2D> res = new ArrayList<>();
            int cur = wave;
            Vector2D curCell = toVec;
            res.add(curCell);
            while (cur > 1) {
                Vector2D found = null;
                for (Vector2D n : neighbours(map, curCell)) {
                    if (fill[(int) n.x][(int) n.y] == cur - 1) found = n;
                }
                cur--;
                curCell = found;
                res.add(0, curCell);
            }
            return res;
        }
        return null;
    }
}