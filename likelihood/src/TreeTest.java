import static org.junit.Assert.*;

public class TreeTest {

    @org.junit.Test
    public void toString1() {

    }

    @org.junit.Test
    public void parseTest() {
        Tree tree = Tree.parse("(A:0.1,B:0.2,(C:0.3,D:0.4):0.5);");
        assertEquals("0.1", tree.getRoot().children.get(0).distance);

    }
}