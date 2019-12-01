package objects;

import java.util.ArrayList;
import java.util.List;

public class Node
{
    private int number;
    private List<Integer> nextNodes = new ArrayList<>();
    private List<Integer> nextNodesUsedInBackPathsForm= new ArrayList<>();
    private List<Integer> weights;

    public Node(){}

    public Node(int number, List<Integer> nextNodes, List<Integer> weights)
    {
        this.number = number;
        this.nextNodes = nextNodes;
        this.weights = weights;
    }

    public int getNumber()
    {
        return number;
    }
    public List<Integer> getNextNodes()
    {
        return nextNodes;
    }
    public List<Integer> getWeights()
    {
        return weights;
    }

    public List<Integer> getNextNodesUsedInBackPathsForm()
    {
        return nextNodesUsedInBackPathsForm;
    }
    public void setNextNodesUsedInBackPathsForm(Integer nodeNumber)
    {
        this.nextNodesUsedInBackPathsForm.add(nodeNumber);
    }

//    @Override
//    public String toString()
//    {
//        StringBuilder builder = new StringBuilder("Node{" + "number=" + number);
//
//        builder.append(prepareArray(nextNodes, ", next="));
//        builder.append(prepareArray(weights, ", weights="));
//
//        builder.append("}");
//
//        return builder.toString();
//    }
}
