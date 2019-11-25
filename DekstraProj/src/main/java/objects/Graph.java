package objects;

import functions.UsefulFunction;

import java.util.LinkedList;
import java.util.List;

public class Graph
{
    private List<DekstraNode> nodes;

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

    public DekstraNode getNodeByNumber(Integer number)
    {
        for (DekstraNode node : nodes) {
            if (node.getNumber() == number) {
                return node;
            }
        }

        UsefulFunction.throwException("The node with such number is not there");
        return null;
    }

    @Override
    public String toString()
    {
        System.out.println("Graph");
        StringBuilder builder = new StringBuilder();

        for (DekstraNode node : nodes) {
            builder.append(node + "\n");
        }

        return builder.toString();
    }

    private int findIndexInListBy(int nodeNumber)
    {
        for (int i = 0; i < nodes.size(); i++){
            if(nodes.get(i).getNumber() == nodeNumber) {
                return i;
            }
        }

        UsefulFunction.throwException("Node with such number doesn't exist");
        return -1;
    }
}
