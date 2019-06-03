import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Parsimony {

    private static final String STARTTREE = "( 0 , 1 , 2 )";

    public static void main(String[] args) {

        Path infile = Paths.get("resources/first10.fasta");
        List<List<Character>> matrix = new ArrayList<>();
        Map<Integer, String> names = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(infile.toString()));
            String line = reader.readLine();

            int i = 0;

            while(line != null){
                names.put(i++, line);
                line = reader.readLine();
                matrix.add(line.chars().mapToObj(c -> (char)c).collect(Collectors.toList()));
                line = reader.readLine();
            }

            reader.close();
            System.out.println(matrix.size());
        } catch (Exception ex) {
            System.out.println("Cannot read file");
            System.exit(1);
        }

        System.out.println(matrix.get(0).size());
        filterColumns(matrix);
        System.out.println(matrix.get(0).size());

        generateTrees(matrix.size());



    }

//    private static void generateTrees(int n){
//        Tree start = new Tree();
//        List<Tree> trees = new ArrayList<>();
//
//        trees.add(start);
//
//        for(int i = 3; i <= n; i++){
//            List<Tree> current = new ArrayList<>();
//            for (Tree tree : trees){
//                for(int j = 0; j < i; j++){
//                    current.add(tree.deep)
//                }
//            }
//        }
//
////        todo
//
//    }

    private static void generateTrees(int n){
        List<String> trees = new ArrayList<>();

        trees.add(STARTTREE);

        for(int i = 3; i <= n; i++){
            List<String> current = new ArrayList<>();
            for (String tree : trees){
                for(int j = 0; j < i; j++){
                    current.add(tree.replaceFirst(String.format(" %d ", j), String.format(" ( %d , %d ) ", j, i)));
                }
            }
            trees.clear();
            trees.addAll(current);
//            System.out.println(trees);
            System.out.println(trees.size());
        }

    }



    private static void filterColumns(List<List<Character>> matrix) {
        int columns = matrix.get(0).size();
        boolean[] informative = new boolean[columns];

        for(int j = 0; j < columns ; j++){
            Map<Character, Integer> diff = new HashMap<>();
            for(List<Character> row : matrix){
                if (row.get(j).equals('-')){
                    continue;
                }
                if (!diff.containsKey(row.get(j))){
                    diff.put(row.get(j), 1);
                    if (diff.size() >= 2 && diff.values().stream().filter(n -> n >= 2).mapToInt(Integer::intValue).sum() >= 4){
                        informative[j] = true;
                        break;
                    }
                } else {
                    diff.put(row.get(j), diff.get(row.get(j)) + 1);
                }
            }
        }

        for (List<Character> row : matrix){
            for (int i = columns - 1; i >= 0; i--){
                if (!informative[i]){
                    row.remove(i);
                }
            }
        }
    }


}