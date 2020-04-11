package objects;

import commonUsefulFunctions.UsefulFunction;

import java.util.ArrayList;
import java.util.List;

public class Graph
{
    private static List<DekstraNode> nodes;

    public Graph()
    {
        nodes = new ArrayList<>();
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

        UsefulFunction.throwException("The node with such number is not there");
        return null;
    }

    public int getMaxGraphWidth() {
        if(nodes == null) return 0;
        if(nodes.isEmpty()) return 0;

        //In the beginning "maxGraphWidth" equals 1 because "nodes"(graph) is not empty. Therefore, if even there will be only one node in the graph, graph width is gonna be 1
        int maxGraphWidth = 1;

        for (DekstraNode node : nodes){
            List nextNodesList = node.getNextNodes();

            if(nextNodesList == null) continue;

            int nextNodesAmount = nextNodesList.size();

            if(nextNodesAmount > maxGraphWidth) {
                maxGraphWidth = nextNodesAmount;
            }
        }

        return maxGraphWidth;
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
