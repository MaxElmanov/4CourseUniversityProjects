package objects;

import functions.UsefulFunction;

import java.util.LinkedList;
import java.util.List;

public class Graph
{
    private static List<DekstraNode> nodes;

    public Graph()
    {
        nodes = new LinkedList<>();
    }

    public Graph(List<DekstraNode> nodes)
    {
        this.nodes = nodes;
    }

    public List<DekstraNode> Nodes()
    {
        return nodes;
    }

    public void add(DekstraNode node)
    {
        nodes.add(node);
    }

    public static DekstraNode getNodeByNumber(Integer number)
    {
        for (DekstraNode node : nodes) {
            if (node.getNumber() == number) {
                return node;
            }
        }

//        UsefulFunction.throwException("The node with such number is not there");
        return null;
    }

    @Override
    public String toString()
    {
        System.out.println("Graph");
        StringBuilder builder = new StringBuilder();

        if(nodes != null) {
            for (DekstraNode node : nodes) {
                builder.append(node);
                builder.append("\n");
            }
        }

        return builder.toString();
    }
}
