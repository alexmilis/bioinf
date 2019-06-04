import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Parsimony {

    private static final String STARTTREE = "( 0 , 1 , 2 );\n";

    private static Path topologies = Paths.get("resources/topologies.txt");

    public static void main(String[] args) {
        Path infile = Paths.get("resources/first10.fasta");
//        Path infile = Paths.get("resources/first6.fasta");
        List<List<Character>> matrix = new ArrayList<>();
        Map<Integer, String> names = new HashMap<>();

        System.out.println(new Date());


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

        filterColumns(matrix);

        int informativeSites = matrix.get(0).size();
        int treesSize = generateTrees(matrix.size());

        System.out.println(new Date());
        System.gc();
        List<Integer> results = new LinkedList<>();


        try {
            BufferedReader reader = new BufferedReader(new FileReader(topologies.toString()));

            for(int i = 0; i < treesSize; i++){
                String treeString = reader.readLine();
                int sum = 0;
                for (int j = 0; j < informativeSites; j++){
                    for (int k = 0; k < matrix.size(); k++){
                        treeString = treeString.replace(String.format(" %d ", k), String.format(" %c ", matrix.get(k).get(j)));
                    }
                    Tree tree = Tree.parse(treeString);
                    sum += tree.getChanges();
                }
//                System.out.println(sum);
                results.add(sum);
            }

        } catch (IOException ex){
            System.out.println("Cannot find file with topologies!");
            System.exit(1);
        }

        System.out.println(results);

        System.out.println(new Date());

        int index = results.indexOf(results.stream().min(Comparator.comparingInt(Integer::intValue)).get());
        System.out.println(String.format("Number of changes: %d", results.get(index)));
        System.out.println(String.format("The best tree is tree %d ", index));

        try (Stream<String> lines = Files.lines(topologies)) {
            String tree = lines.skip(index).findFirst().get();
            System.out.println(tree);
        } catch (IOException ex){

        }


    }


    private static int generateTrees(int n){
        List<String> trees = new ArrayList<>();

        trees.add(STARTTREE);

        for(int i = 3; i < n; i++){
            List<String> current = new ArrayList<>();
            for (String tree : trees){
                for(int j = 0; j < i; j++){
                    current.add(tree.replaceFirst(String.format(" %d ", j), String.format(" ( %d , %d ) ", j, i)));
                }
            }
            trees.clear();
            trees.addAll(current);
            System.out.println(trees.size());
        }

        try {
            OutputStream out = new BufferedOutputStream(Files.newOutputStream(topologies, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));

            for (String tree : trees){
                out.write(tree.getBytes());
                out.flush();
            }
            out.close();
        } catch (IOException ex){
            System.out.println("Cannot write to topologies file!");
            System.exit(1);
        }

        return trees.size();
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
