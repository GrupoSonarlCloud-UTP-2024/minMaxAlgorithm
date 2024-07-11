import java.util.Scanner;
import java.util.Random;

public class App
{
    // Game Data
    static char[][] squares = new char[3][3];
    static int k = 3, depth = 7;
    static boolean gameCompleted = false, aiTurn = false;
    static char ai, opponent;

    // AI Calculations
    static boolean drew = false;
    static int tempWins, tempLosses, tempDraws;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int round = 1, choice;
        int[] move = new int[2];

        for (int x = 0; x < squares.length; x++) {
            for (int y = 0; y < squares[0].length; y++) {
                squares[x][y] = '-';
            }
        }

        printBoard();

        System.out.println(k + "-in-a-row, " + squares[0].length + "x" + squares.length);

        // Opening Phase
        System.out.println("Input 0 for AI to start, 1 for you");
        choice = sc.nextInt();
        if (choice == 0) {
            ai = 'x';
            opponent = 'o';
        } else {
            ai = 'o';
            opponent = 'x';
        }

        // Game Loop
        while (!gameCompleted) {
            System.out.println("Round: " + round);
            if (aiTurn) {
                move = Choose(depth);

                System.out.println("Move: " + move[1] + " " + move[0]);

                squares[move[0]][move[1]] = ai;
            } else {
                do {
                    System.out.println("Choose Move[x][-]: ");
                    move[1] = sc.nextInt();
                    System.out.println("Choose Move[-][y]: ");
                    move[0] = sc.nextInt();
                } while (!isValidMove(move[0], move[1]));

                squares[move[0]][move[1]] = opponent;
            }

            int eval = Evaluation(squares, 0);
            if (Math.abs(eval) == 5000) {
                gameCompleted = true;
            }

            aiTurn = !aiTurn;
            round += 1;

            printBoard();
            System.out.println("Evaluation: " + eval);
            System.out.println();
        }

        System.out.println("Match ended");
        sc.close();
    }

    static void printBoard() {
        for (int x = 0; x < squares.length; x++) {
            System.out.print("| ");
            for (int y = 0; y < squares[0].length; y++) {
                System.out.print(squares[x][y] + " | ");
            }
            System.out.println();
        }
    }

    static boolean isValidMove(int x, int y) {
        return x >= 0 && x < squares.length && y >= 0 && y < squares[0].length && squares[x][y] == '-';
    }

    static int[] Choose(int depth) {
        int[] candidate = new int[2];
        int bestValue = Integer.MIN_VALUE;

        for (int x = 0; x < squares.length; x++) {
            for (int y = 0; y < squares[0].length; y++) {
                if (isValidMove(x, y)) {
                    squares[x][y] = ai;
                    int value = Search(squares, depth - 1, false);
                    squares[x][y] = '-';

                    if (value > bestValue || (value == bestValue && new Random().nextInt(2) == 1)) {
                        bestValue = value;
                        candidate[0] = x;
                        candidate[1] = y;
                    }
                }
            }
        }

        return candidate;
    }

    static int Search(char[][] board, int depth, boolean maximizingPlayer) {
        int eval = Evaluation(board, depth);
        if (Math.abs(eval) > 3000 || depth == 0) {
            return eval;
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[0].length; y++) {
                    if (isValidMove(x, y)) {
                        board[x][y] = ai;
                        int evalValue = Search(board, depth - 1, false);
                        board[x][y] = '-';
                        maxEval = Math.max(maxEval, evalValue);
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[0].length; y++) {
                    if (isValidMove(x, y)) {
                        board[x][y] = opponent;
                        int evalValue = Search(board, depth - 1, true);
                        board[x][y] = '-';
                        minEval = Math.min(minEval, evalValue);
                    }
                }
            }
            return minEval;
        }
    }

    static int Evaluation(char[][] board, int depth) {
        int aiScore = 0, opponentScore = 0;

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                if (board[x][y] == ai) {
                    aiScore += checkLines(board, x, y, ai);
                } else if (board[x][y] == opponent) {
                    opponentScore += checkLines(board, x, y, opponent);
                }
            }
        }

        if (aiScore >= 5000) return 5000 - depth;
        else if (opponentScore >= 5000) return -5000 + depth;
        else return aiScore - opponentScore;
    }

    static int checkLines(char[][] board, int x, int y, char player) {
        int score = 0;

        score += checkDirection(board, x, y, 1, 0, player); // Horizontal
        score += checkDirection(board, x, y, 0, 1, player); // Vertical
        score += checkDirection(board, x, y, 1, 1, player); // Diagonal \
        score += checkDirection(board, x, y, 1, -1, player); // Diagonal /

        return score;
    }

    static int checkDirection(char[][] board, int x, int y, int dx, int dy, char player) {
        int count = 0;

        for (int i = 0; i < k; i++) {
            int newX = x + i * dx;
            int newY = y + i * dy;

            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == player) {
                count++;
            } else {
                break;
            }
        }

        if (count == k) return 5000;
        else return count;
    }
}
