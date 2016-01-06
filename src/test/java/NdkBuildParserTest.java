import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by jomof on 1/4/16.
 */
public class NdkBuildParserTest {


    @Test
    public void simpleAssign() throws FileNotFoundException {
        expectParsed("a:=b", "(:= a b)");
    }

    @Test
    public void compoundAssign() throws FileNotFoundException {
        expectParsed("a:=b c", "(:= a [b c ])");
    }

    private void expectParsed(String target, String expected) {
        NdkBuildParser parser = new NdkBuildParser();
        String result = treeString(parser.parse(target));
        if (!result.equals(expected)) {
            throw new RuntimeException(String.format("Expected %s but got %s", expected, result));
        }
    }

    private String treeString(NdkBuildParser.Node node) {
        StringBuilder sb = new StringBuilder();
        treeStringBuilder(node, sb);
        return sb.toString();
    }

    private void treeStringBuilder(NdkBuildParser.Node node, StringBuilder sb) {
        switch(node.type) {
            case TYPE_BLOCK_EXPRESSION: {
                sb.append("[");
                for(NdkBuildParser.Node child : ((NdkBuildParser.BlockExpression)node).expressions) {
                    treeStringBuilder(child, sb);
                    sb.append(" ");
                }
                sb.append("]");
                return;
            }
            case TYPE_IDENTIFIER_EXPRESSION: {
                sb.append(((NdkBuildParser.IdentifierExpression)node).identifier);
                return;
            }
            case TYPE_SIMPLE_ASSIGN_EXPRESSION: {
                sb.append("(:= ");
                NdkBuildParser.AssignmentExpression expr = (NdkBuildParser.AssignmentExpression) node;
                treeStringBuilder(expr.left, sb);
                sb.append(" ");
                treeStringBuilder(expr.right, sb);
                sb.append(")");
                return;
            }
            default:
                throw new RuntimeException(node.type.toString());
        }
    }

}