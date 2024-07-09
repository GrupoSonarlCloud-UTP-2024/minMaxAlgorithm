import java.util.Scanner;
import java.util.Random;

public class App
{
    //Game Data
    static char[][] squares = new char[4][4];
    static int k = 4;
    static boolean gameCompleted = false, aiTurn = false;
    static char ai, opponent;

    //AI Calculations
    static boolean drew = false;
    static int tempWins, tempLosses, tempDraws;

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        int round = 1, choice;
        int[] move = new int[2];
        for(int x = 0; x < squares.length; x++)
        {
            for(int y = 0; y < squares[0].length; y++)
            {
                squares[x][y] = '-';
            }
        }
        

        printBoard();

        System.out.println(k + "-in-a-row, " + squares[0].length + "x" + squares.length);

        //Opening Phase
        System.out.println("Input 0 for ai to start, 1 for you");
        choice = sc.nextInt();
        if(choice == 0)
        {
            ai = 'x';
            opponent = 'o';
            move = Choose(16);
            System.out.println("Move: " + move[1] + " " + move[0]);
            squares[move[0]][move[1]] = ai;
            round += 1;

            printBoard();
        }
        else
        {
            ai = 'o';
            opponent = 'x';
        }

        //Game Loop
        while(!gameCompleted)
        {
            System.out.println("Round: " + round);
            if(aiTurn)
            {
                System.out.println();
                move = Choose(16);

                System.out.println("Move: " + move[1] + " " + move[0]);

                squares[move[0]][move[1]] = ai;
            }
            else
            {
                System.out.println("Choose Move[x][-]: ");
                move[1] = sc.nextInt();
                System.out.println("Choose Move[-][y]: ");
                move[0] = sc.nextInt();

                squares[move[0]][move[1]] = opponent;
            }

            int eval = Evaluation(squares, 0);
            if(eval == 5000 || eval == -5000)
            {
                gameCompleted = true;
            }

            if(aiTurn)
            {
                aiTurn = false;
            }
            else
            {
                aiTurn = true;
            }

            round += 1;

            printBoard();
            System.out.println("Evaluation: " + eval);
            System.out.println();
        }

        System.out.println("Match ended");
        sc.close();
    }

    static void printBoard()
    {
        for(int x = 0; x < squares.length; x++)
        {
            System.out.print("| ");
            for(int y = 0; y < squares[0].length; y++)
            {
                System.out.print(squares[x][y] + " | ");
            }
            System.out.println();
        }
    }
    
    //Returns move
    static int[] Choose(int depth)
    {
        int[] candidate = {0, 0};
        int value = 0, tempValue, result;

        for(int x = 0; x < squares.length; x++)
        {
            for(int y = 0; y < squares[0].length; y++)
            {
                result = 2;
                if(x == 0 && y == 0)
                {
                    candidate[0] = x;
                    candidate[1] = y;
                }

                if(Available(squares, x, y))
                {
                    char[][] board = new char[squares.length][squares[0].length];

                    for(int z = 0; z < squares.length; z++)
                    {
                        for(int w = 0; w < squares.length; w++)
                        {
                            board[z][w] = squares[z][w];
                        }
                    }

                    board[x][y] = ai;
                    tempValue = Search(board, depth, opponent);
                    
                    result = Compare(value, tempValue);
                    
                    if(result == 2)
                    {
                        //Random between the two nodes if they're of equal value
                        Random rand = new Random();

                        result = rand.nextInt(2);
                    }

                    if(result == 1)
                    {
                        candidate[0] = x;
                        candidate[1] = y;
                    }
                }
            }
        }

        return candidate;
    }

    //Compares two values
    static int Compare(int value1, int value2)
    {
        int toReturn = 2;
        if(value1 > value2)
        {
            toReturn = 0;
        }
        else if(value2 > value1)
        {
            toReturn = 1;
        }

        return toReturn;
    }

    //Returns value of an option
    static int Search(char[][] board, int depth, char turn)
    {
        int value, tempValue;

        value = Evaluation(board, depth);
        if(!drew && depth > 0)
        {
            value = 0;
            
            for(int x = 0; x < board.length; x++)
            {
                for(int y = 0; y < board[0].length; y++)
                {
                    if(Available(board, x, y))
                    {
                        char[][] testBoard = new char[board.length][board[0].length];

                        for(int z = 0; z < board.length; z++)
                        {
                            for(int w = 0; w < board.length; w++)
                            {
                                testBoard[z][w] = board[z][w];
                            }
                        }

                        testBoard[x][y] = turn;
                        if(turn == ai)
                        {
                            tempValue = Search(testBoard, depth - 1, opponent);
                            //Minimaxing - Choosing the value for the father node as the ai
                            if((x == 0 && y == 0) || tempValue < value)
                            {
                                value = tempValue;
                            }
                        }
                        else
                        {
                            tempValue = Search(testBoard, depth - 1, ai);
                            //Minimaxing - Choosing the value for the father node as the opponent
                            if((x == 0 && y == 0) || tempValue > value)
                            {
                                value = tempValue;
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    //Checks if the move is available
    static boolean Available(char[][] board, int x, int y)
    {
        boolean availability = false;
        if(board[x][y] == '-')
        {
            availability = true;
        }

        return availability;
    }

    //Returns the value of a board state; win, loss, draw or heuristics
    static int Evaluation(char[][] board, int depth)
    {

        boolean win = false, lose = false, draw = false;
        int rowWin = 0, rowLose = 0, maxRowWin = 0, maxRowLose = 0, value = 0;

        for(int x = 0; x < board.length;x++)
        {
            for(int y = 0; y < board[0].length; y++)
            {
                if(board[x][y] == ai)
                {
                    rowWin++;
                    for(int z = 1; x + z < board.length; z++)
                    {
                        if(board[x + z][y] == ai)
                        {
                            rowWin += 1;
                        }
                        else
                        {
                            if(z >= k - 1)
                            {
                                win = true;
                            }
                            break;
                        }
                    }

                    for(int z = 1; y + z < board[0].length; z++)
                    {
                        if(board[x][y + z] == ai)
                        {
                            rowWin += 1;
                        }
                        else
                        {
                            if(z >= k - 1)
                            {
                                win = true;
                            }
                            break;
                        }
                    }

                    for(int z = 1; x + z < board.length & y + z < board[0].length; z++)
                    {
                        if(board[x + z][y] == ai && board[x][y + z] == ai)
                        {
                            rowWin += 1;
                        }
                        else
                        {
                            if(z >= k - 1)
                            {
                                win = true;
                            }
                            break;
                        }
                    }

                    for(int z = 1; x + z < board.length & y - z >= 0; z++)
                    {
                        if(board[x + z][y] == ai && board[x][y - z] == ai)
                        {
                            rowWin += 1;
                        }
                        else
                        {
                            if(z >= k - 1)
                            {
                                win = true;
                            }
                            break;
                        }
                    }

                    if(rowWin > maxRowWin)
                    {
                        maxRowWin = rowWin;
                    }

                    rowWin = 0;
                }
                else if(board[x][y] == opponent)
                {
                    rowLose++;
                    for(int z = 1; x + z < board.length; z++)
                    {
                        if(board[x + z][y] == opponent)
                        {
                            rowLose += 1;
                        }
                        else
                        {
                            if(z >= k - 1)
                            {
                                lose = true;
                            }
                            break;
                        }
                    }

                    for(int z = 1; y + z < board[0].length; z++)
                    {
                        if(board[x][y + z] == opponent)
                        {
                            rowLose += 1;
                        }
                        else
                        {
                            if(z >= k - 1)
                            {
                                lose = true;
                            }
                            break;
                        }
                    }

                    for(int z = 1; x + z < board.length & y + z < board[0].length; z++)
                    {
                        if(board[x + z][y] == ai && board[x][y + z] == opponent)
                        {
                            rowLose += 1;
                        }
                        else
                        {
                            if(z >= k - 1)
                            {
                                lose = true;
                            }
                            break;
                        }
                    }

                    for(int z = 1; x + z < board.length & y - z >= 0; z++)
                    {
                        if(board[x + z][y] == opponent && board[x][y - z] == opponent)
                        {
                            rowLose += 1;
                        }
                        else
                        {
                            if(z >= k - 1)
                            {
                                lose = true;
                            }
                            break;
                        }
                    }

                    if(rowLose > maxRowLose)
                    {
                        maxRowLose = rowLose;
                    }
                    rowLose = 0;
                }
            }
        }

        if(win)
        {
            drew = false;
            tempWins += 1;
            value = 5000 + depth;
        }
        else if(lose)
        {
            drew = false;
            tempLosses += 1;
            value = -5000 + depth;
        }
        else
        {
            draw = true;
            for(int x = 0; x < board.length; x++)
            {
                for(int y = 0; y < board[0].length; y++)
                {
                    if(board[x][y] == '-')
                    {
                        draw = false;
                    }
                }
            }

            if(draw)
            {
                drew = true;
                tempDraws += 1;
                value = 0;
            }
            else
            {
                value = maxRowWin - maxRowLose;
            }
        }

        //printTemporalBoard(board);
        //System.out.println();
        //System.out.println("Value: " + value);
        //System.out.println();
        return value;
    }

    static void printTemporalBoard(char[][] board)
    {
        for(int x = 0; x < board.length; x++)
        {
            System.out.print("| ");
            for(int y = 0; y < board[0].length; y++)
            {
                System.out.print(board[x][y] + " | ");
            }
            System.out.println();
        }
    }
}
