import static org.junit.Assert.*;

public class TreeTest {

    @org.junit.Test
    public void toString1() {
        Tree tree = Tree.parse("(A:0.1,B:0.2,(C:0.3,D:0.4):0.5, E:0.6);");
        System.out.println(tree);

    }

    @org.junit.Test
    public void parseTest() {
        Tree tree = Tree.parse("(A:0.1,B:0.2,(C:0.3,D:0.4):0.5);");
        assertEquals(0.1, tree.getRoot().children.get(0).distance, 0.0001);

        assertEquals(0.5, tree.getRoot().children.get(2).distance, 0.0001);
        assertEquals(0.3, tree.getRoot().children.get(2).children.get(0).distance, 0.0001);

    }

    @org.junit.Test
    public void parseTest2() {
        Tree tree = Tree.parse("(A:0.1,B:0.2,(C:0.3,D:0.4):0.5, E:0.6);");
        assertEquals(0.6, tree.getRoot().children.get(3).distance, 0.0001);
    }

    @org.junit.Test
    public void parseTest3() {
        Tree tree = Tree.parse("( ( ( ( ( ( 0 : 0.1 , 7 : 0.1 ) : 0.2 , 6 : 0.3 ) : 0.6 , 5 : 0.9 ) : 0.8 , 4 : 0.7 ) : 0.2, 3 : 0.7 ) : 0.7, ( 1 : 0.2 , ( 8 : 0.4 , 9 : 0.3) : 0.6 ) : 0.6, 2 :0.8);");
        assertEquals(0.6, tree.getRoot().children.get(3).distance, 0.0001);
    }
}