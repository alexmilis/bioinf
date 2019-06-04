import org.junit.Test;

import static org.junit.Assert.*;

public class TreeTest {

    Tree tree;

    @Test
    public void parse() {
        Tree tree = Tree.parse("( ( a , b ) , c , d );");
        assertEquals(3, tree.getRoot().children.size());
    }

    @Test
    public void testtoString(){
        Tree tree = Tree.parse("( ( a , b ) , c , d );");
        System.out.println(tree);
    }
}