import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

class Node{
    int r;   //represent the solution row
    Node U;
    Node D;
    Node L;
    Node R;
    Node C;  //the header
    int count;  //Used to store the count of 1 in column
}


public class Solution {
    private static final int colmns = 16 * 16 * 4; //16 * 16 * 4
    private static final int size = 16 * 16;
    private Node head;
    private Node[] cols;
    private int[] solution = new int[size];
    private boolean flag =false;



    char[][] sudoku1;
    private String[][] sudokuArray = new String[16][16];

    public void solveSudoku(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for(char[] t : board){
            for(char tt : t) {
                sb.append(tt);
            }
        }
        sudoku1 = board;
        init();
        construct(sb.toString().trim());
        search(0);

    }


    public void init(){
        head = new Node();
        head.L = head;
        head.R = head;
        cols = new Node[colmns];
        for(int i = 0 ; i < cols.length ; i++){
            Node temp = new Node();
            temp.C = temp;
            temp.U = temp;
            temp.D = temp;
            temp.R = head;
            temp.L = head.L;
            temp.r = i;
            head.L.R = temp;
            head.L = temp;
            cols[i] = temp;
        }
    }


    public void construct(String puzzle){
        if(puzzle.length() != size){
            return;
        }
        for(int i = 0 ; i < puzzle.length() ; i++){
            int r = i / 16;
            int c = i % 16;
            String[] board = convertToNum(puzzle);
            String current = board[i];

            if(current.equals("-")){
                for(int val = 0 ; val <= 15 ; val++){
                    link(r*10000 + c * 100 + val , getColumnIndex(r,c,val));
                }
            }else{
                int number = Integer.parseInt(current);
                link(r*10000 + c * 100 + number , getColumnIndex(r,c,number));
            }
        }
    }


    public void link(int r , int[] cs){
        Node rowHead = null;

        for(int i = 0 ; i < cs.length ; i++){
            int c = cs[i];
            Node columnHeader = cols[c].C;
            Node temp = new Node();
            temp.r = r;
            if(i == 0){
                rowHead = temp;
                rowHead.R = rowHead;
                rowHead.L = rowHead;
            }
            ///////////
            temp.R = rowHead;
            temp.L = rowHead.L;
            rowHead.L.R = temp;
            rowHead.L = temp;

            ///////////
            temp.D = columnHeader;
            temp.U = columnHeader.U;

            //set the columnHeader
            temp.C = columnHeader;
            columnHeader.U.D = temp;
            columnHeader.U = temp;
            columnHeader.count++;

        }
    }
    /**
     * remove the column c
     * @param c  the column obj
     */
    public void remove(Node c){

        //Remove The column head
        Node cHead = c.C;
        cHead.R.L = cHead.L;
        cHead.L.R = cHead.R;
        for(Node row = cHead.D ; row != cHead ; row = row.D){
            //delete the row
            for(Node rowNode = row.R ; rowNode != row ; rowNode = rowNode.R){
                rowNode.D.U = rowNode.U;
                rowNode.U.D = rowNode.D;
                rowNode.C.count--;
            }
        }

    }

    /**
     * resume the column
     * @param c
     */
    public void resume(Node c){

        Node cHead = c.C;

        //resume the row
        for(Node row = cHead.U ; row != cHead ; row = row.U){
            for(Node rowNode = row.L ; rowNode != row ; rowNode = rowNode.L){
                rowNode.D.U = rowNode;
                rowNode.U.D = rowNode;
                rowNode.C.count++;
            }
        }
        //resume the head
        cHead.R.L = cHead;
        cHead.L.R = cHead;
    }

    /**
     *
     * @return the column header
     */
    public Node chooseColumn(){
        int min = Integer.MAX_VALUE;
        Node result = null;
        for(Node c = head.R ; c != head ; c = c.R){
            if(c.count < min){
                result = c;
                min = c.count;
            }
        }
        return result;
    }

    public boolean search(int k){

        if(flag == true)
            return true;
        if(head.R == head){
            printSolution();
            flag = true;
            return true;
        }
        Node c = chooseColumn();
        remove(c);  //remove the c

        for(Node solutionRow = c.D ; solutionRow != c ; solutionRow = solutionRow.D){
            solution[k] = solutionRow.r;  //add the solution

            //remove the column in the right
            for(Node rightNode = solutionRow.R ; rightNode != solutionRow ; rightNode = rightNode.R){
                remove(rightNode);
            }

            //continue searching
            search(k+1);

            //after searching , resume the state
            for(Node leftNode = solutionRow.L ; leftNode != solutionRow ; leftNode = leftNode.L){
                resume(leftNode);
            }
        }

        resume(c);

        return false;
    }



    private String[] convertToNum(String puzzle) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < puzzle.length(); i++){
            if (puzzle.charAt(i) == 'A'){
                sb.append("10");
            }
            else if (puzzle.charAt(i) == 'B'){
                sb.append("11");
            }
            else if (puzzle.charAt(i) == 'C'){
                sb.append("12");
            }
            else if (puzzle.charAt(i) == 'D'){
                sb.append("13");
            }
            else if (puzzle.charAt(i) == 'E'){
                sb.append("14");
            }
            else if (puzzle.charAt(i) == 'F'){
                sb.append("15");
            }
            else{
                sb.append(puzzle.charAt(i));
            }
            sb.append(" ");
        }

        String board = sb.toString().trim();
        String[] newBoard = board.split(" ");
        return newBoard;
    }


    /**
     * given the row and column and the value , return the four column index of it
     * @param r
     * @param c
     * @param val
     * @return System.out.println(java.util.Arrays.toString(solution));
     */
    public int[] getColumnIndex(int r , int c , int val){
        int[] array = new int[4];
        array[0] = r * 16 + val;
        array[1] = 16 * 16 + c * 16 + val;
        int tr = r / 4;
        int tc = c / 4;
        int b = tr * 4 + tc;
        array[2] = 2 * 16 * 16 + b * 16 + val;
        array[3] = 3 * 16 * 16 + r * 16 + c;
        return array;
    }

    public void printSolution(){

        for(int i = 0 ; i < solution.length ; i++){
            int r = solution[i] / 10000;
            int c = solution[i] / 100 % 100;
            int val = solution[i] % 100;
            sudokuArray[r][c] = Integer.toHexString(val).toUpperCase();
        }

        for(String[] row : sudokuArray){
            for(String cell : row){
                System.out.print(cell + "   ");

            }
            System.out.println();
        }
    }

    public static void main(String[] args){
        File file = new File(args[0]);  //input file
        Reader reader = null;
        char[][] newBoard = new char[16][16];
        StringBuilder sb = new StringBuilder();


        try {
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1){
                if (((char) tempchar) != '\n') {
                    sb.append((char) tempchar);
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String board = sb.toString().trim().replaceAll("\\s++", "");
        System.out.println("Initial Sudoku Board: ");
        for (int i = 0; i < 16; i++){
            for (int j = 0; j < 16; j++){
                newBoard[i][j] = board.charAt(i * 16 + j);
                System.out.print(newBoard[i][j] + "   ");
            }
            System.out.println();
        }

        System.out.println("\nResult: \n");


        Solution solution = new Solution();
        //start
        final long startTime = System.currentTimeMillis();

        solution.solveSudoku(newBoard);
        //end
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms" );

    }
}

