package objects;

import java.util.ArrayList;
import java.util.List;

public class Node
{
    private int number;
    private List<Integer> nextNodes = new ArrayList<>();
    private List<Integer> weights;

    public Node()
    {
    }

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

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("Node{" + "number=" + number);

        builder.append(prepareArray(nextNodes, ", next="));
        builder.append(prepareArray(weights, ", weights="));

        builder.append("}");

        return builder.toString();
    }

    public StringBuilder prepareArray(List<Integer> list, String beginText){
        StringBuilder builder = new StringBuilder();
        builder.append(beginText);

        for (Integer elem : list){
            builder.append(elem + ", ");
        }

        StringBuilder temp = new StringBuilder(builder.substring(0, builder.length() - 2));
        return new StringBuilder(String.format("%-20.20s", temp));
    }
}
