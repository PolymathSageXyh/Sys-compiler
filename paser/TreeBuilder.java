package paser;

import lexer.SyntaxType;
import paser.nodes.ErrorNode;
import paser.nodes.Node;
import paser.nodes.TokenNode;
import error.ErrorType;
import java.util.Stack;

public class TreeBuilder {
    public Stack<Node> children;
    private Stack<Mypair<SyntaxType, Integer>> parents;

    public TreeBuilder() {
        this.children = new Stack<>();
        this.parents = new Stack<>();
    }

    public void buildNode(SyntaxType type) {
        parents.push(Mypair.of(type, children.size()));
    }

    public void buildNodeAt(int position, SyntaxType type) {
        parents.push(Mypair.of(type, position));
    }

    public void finishNode(Node node) {
        Mypair<SyntaxType, Integer> temp = parents.peek();
        parents.pop();
        while (children.size() > temp.getSecond()) {
            node.addChild(children.peek());
            children.pop();
        }
        node.setNonLeafNode(temp.first);
        children.push(node);
    }

    public int getChildrenSize() {
        return children.size();
    }

    public void terminalSymbol(SyntaxType type, int line, String value) {
        children.push(new TokenNode(type, line, value));
    }

    public Node root() {
        if (parents.size() == 0) {
            return children.peek();
        } else {
            System.out.println("Parent is not empty, go error!\n");
            return children.peek();
        }
    }

    public void addErrorNode(ErrorType errorType, int line) {
        Node node = new ErrorNode(errorType, line);
        node.setLeafNode(SyntaxType.ERROR, line);
        children.push(node);
    }


}
