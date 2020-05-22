package objects;

import commonUsefulFunctions.UsefulFunction;
import constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class Graph implements Cloneable
{
    private List<DekstraNode> nodes;

    public Graph()
    {
        nodes = new ArrayList<>();
    }

    public Graph(List<DekstraNode> nodes)
    {
        this.nodes = nodes;
    }

    public Graph(Graph graph)
    {
        nodes = new ArrayList<>();
        UsefulFunction.fillUpListByList(nodes, graph.nodes);
        nodes.stream().forEach(node -> {
            node.getParents().clear();
            node.setBestWeight(Constants.INF);
            node.setWasUsedInForwardAlthm(false);
            node.setWasUsedInBackPathsFrom(false);
            node.setInThread(false);
            node.getParentsCorrespondingCheckers().clear();
            node.setBackPathIndex(0);
        });
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

    public int getMaxGraphWidth() {
        if(nodes == null) return 0;
        if(nodes.isEmpty()) return 0;

        //In the beginning "maxGraphWidth" equals 1 because "nodes"(graph) is not empty. Therefore, if even there will be only one node in the graph, graph width gotta be 1
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

    @Override
    public Graph clone() throws CloneNotSupportedException
    {
        if (this != null)
        {
            if (this instanceof Graph)
            {
                return new Graph(this);
            }
        }

        return null;
    }
}
