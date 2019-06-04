import org.junit.Test;

import static org.junit.Assert.*;

public class TreeTest {

    Tree tree;

    @Test
    public void testParseSimple() {
        Tree tree = Tree.parse("( ( a , b ) , c , d );");
        assertEquals(3, tree.getRoot().children.size());
    }

    @Test
    public void testToStringSimple(){
        Tree tree = Tree.parse("( ( a , b ) , c , d );");
        System.out.println(tree);
    }

    @Test
    public void testParseComplex(){
        Tree tree = Tree.parse("( ( ( a , b ) , ( c , d ) ) , e , ( f , g ) )");
        assertEquals(3, tree.getRoot().children.size());
        assertEquals(2, tree.getRoot().children.get(0).children.size());
        assertEquals(2, tree.getRoot().children.get(0).children.get(0).children.size());
        assertEquals("a", tree.getRoot().children.get(0).children.get(0).children.get(0).value);
        assertEquals("d", tree.getRoot().children.get(0).children.get(1).children.get(1).value);
    }

    @Test
    public void testToStringComplex(){
        Tree tree = Tree.parse("( ( ( a , b ) , ( c , d ) ) , e , ( f , g ) )");
        System.out.println(tree);
    }
}