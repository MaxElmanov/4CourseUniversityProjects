package objects;

import java.util.ArrayList;
import java.util.List;

public class DekstraNode extends Node
{
    private Node node;
    private List<Integer> parents;
    private Integer bestWeight;
    private boolean wasUsed;
    //for multi threading
    private boolean isInThread;
    private List<Boolean> parentsCheckers;

    public DekstraNode(Node node)
    {
        super(node.getNumber(), node.getNextNodes(), node.getWeights());
        this.node = node;
        parents = new ArrayList<>();
        parentsCheckers = new ArrayList<>();
    }

    public List<Integer> getParents()
    {
        return parents;
    }

    public List<Boolean> getParentsCorrespondingCheckers()
    {
        return parentsCheckers;
    }

    public Integer getBestWeight()
    {
        return bestWeight;
    }
    public void setBestWeight(Integer bestWeight)
    {
        this.bestWeight = bestWeight;
    }

    public Boolean wasUsed()
    {
        return wasUsed;
    }
    public void setWasUsed(Boolean wasUsed)
    {
        this.wasUsed = wasUsed;
    }

    public boolean isInThread()
    {
        return isInThread;
    }
    public void setInThread(boolean inThread)
    {
        isInThread = inThread;
    }

    public void addParent(Integer parentNumber) {
        parents.add(parentNumber);
        parentsCheckers.add(false); //set up false value because it's default
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("DekstraNode {" + "node=" + node);
        builder.append(", bestWeight=" + bestWeight + ", ");
        builder.append(prepareArray(parents, "parents="));

        builder.append("}");

        return builder.toString();
    }
}
