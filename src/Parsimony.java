import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Parsimony {

    private static final String STARTTREE = "(0, 1, 2)";

//    input file: puno seq odjednom
//    > naziv seq
//    seq

    public static void main(String[] args) {

        Path infile = Paths.get("resources/HIV1_FLT_2017_env_DNA.fasta");
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
            }

            reader.close();
            System.out.println(matrix.size());
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("Cannot read file");
            System.exit(1);
        }

        filterColumns(matrix);

        generateTrees(matrix.size());



    }

    private static void generateTrees(int n){

//        todo

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
