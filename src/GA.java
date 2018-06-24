import java.lang.reflect.Array;
import java.util.*;

public class GA {
    final static double MUTATION_RATE = 0.001;//mutation probability(%)
    final static int STARTING_POPULATION = 40;
    final static int MAN_SELECTED_PER_GEN = 20; //MAX SELECTED PER GENERATION
    final static int MIN_SELECTED_PER_GEN = 10;
    final static double MATING_PROBABILITY = 0.8;
    public static void main(String[] args) {
        System.out.println("Input count of queen");
        Scanner sc = new Scanner(System.in);
        new GA().run(sc.nextInt());
    }
    public void run(int size){
        //mutation probability
        long startTIme = System.currentTimeMillis();
        Board board;
        ArrayList<Board> childs = new ArrayList<>();
        for(int i = 0;i < STARTING_POPULATION;i++){
            int[] b = Board.generateQueens(size);
            childs.add(new Board(b,getHeuristiCost(b)));
        }
        while ((board = getMinCostBoard(childs)).cost != 0) {
            Board.printBoard(Board.queensOnBorad(board.board));
            childs = crossover(childs);
            System.out.println("POPULATION : "+childs.size());
        }
        System.out.println("----------------------------------------------------------");
        Board.printBoard(Board.queensOnBorad(board.board));
        System.out.println("Spend TIme : " + (System.currentTimeMillis()-startTIme) + "ms");
    }
    private Board getMinCostBoard(List<Board> list){
        int min = Integer.MAX_VALUE;
        Board board = null;
        for(Board b:list){
            if(b.cost < min){
                min = b.cost;
                board = b;
            }
        }
        return board;
    }
    private ArrayList<Board> crossover(List<Board> list){
        Random r = new Random();
        ArrayList<Board> selecedList = new ArrayList<>();
        Collections.sort(list,new CompareBoard());
        ArrayList<Board> newList = new ArrayList<>();
        //compute root-mean-square error
        float avg = 0;
        for(Board b:list){
            avg+=b.cost;
        }
        avg/=list.size();
        for(Board b:list){
            if(b.cost >= avg || selecedList.size() < MIN_SELECTED_PER_GEN){
                selecedList.add(b);
            }
            if(selecedList.size() > MAN_SELECTED_PER_GEN)
                break;
        }
        for(int i = 0;i < selecedList.size();i++){
            if(r.nextDouble() > MATING_PROBABILITY){
                newList.add(selecedList.get(i));
            }else{
                int index;
                while((index = r.nextInt(selecedList.size())) != i);
                newList.addAll(crossover(selecedList.get(i),selecedList.get(index)));
            }
        }
        return newList;
    }
    private ArrayList<Board> crossover(Board board1,Board board2){
        Random r = new Random();
        int cutPoint = r.nextInt(board1.board.length);
        ArrayList<Board> list = new ArrayList<>();
        int[] newBoard1 = concatenate(Arrays.copyOfRange(board1.board,0,cutPoint),Arrays.copyOfRange(board2.board,cutPoint,board2.board.length));
        int[] newBoard2 = concatenate(Arrays.copyOfRange(board2.board,0,cutPoint),Arrays.copyOfRange(board1.board,cutPoint,board1.board.length));
        //mutation
        if(r.nextDouble() > MUTATION_RATE){
            newBoard1[r.nextInt(newBoard1.length)] = r.nextInt(newBoard1.length);
        }
        if(r.nextDouble() > MUTATION_RATE){
            newBoard1[r.nextInt(newBoard2.length)] = r.nextInt(newBoard2.length);
        }
        //return two childs
        list.add(new Board(newBoard1,getHeuristiCost(newBoard1)));
        list.add(new Board(newBoard2,getHeuristiCost(newBoard2)));
        return list;
    }
    //merge two array
    public int[] concatenate(int[] a, int[] b) {
        int aLen = a.length;
        int bLen = b.length;

        int[] c = (int[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
    private int getHeuristiCost(int[] board) {
        int cost = 0;
        int limit = board.length - 1;
        for (int i = 0; i < limit; i++) {
            for (int j = i + 1; j < board.length; j++) {
                if (board[i] == board[j]) {
                    cost++;
                } else if (Math.abs(board[i] - board[j]) == Math.abs( i - j)) {
                    cost++;
                }
            }
        }
        return cost;
    }
    class CompareBoard implements Comparator{

        @Override
        public int compare(Object o1, Object o2) {
            if(((Board)o1).cost > ((Board)o2).cost){
                return 1;
            }
            return 0;
        }
    }
}
