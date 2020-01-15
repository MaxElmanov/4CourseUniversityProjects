package objects;

import java.util.ArrayList;
import java.util.List;

public class DekstraNode extends Node
{
    //canvas UI
    private double X;
    private double Y;
    private boolean setUpOnCanvas;
    private boolean madeCurveLine;

    private Node node;
    private List<Integer> parents;
    private Integer bestWeight;
    private boolean wasUsedInForwardAlthm;
    //1 thread algthm
    private boolean wasUsedInBackPathsFrom;
    //multi threading algthm
    private boolean isInThread;
    //1 thread algthm && multi threading algthm
    private List<Boolean> parentsCorrespondingCheckers;
    private int backPathIndex;

    public DekstraNode(Node node)
    {
        super(node.getNumber(), node.getNextNodes(), node.getWeights());
        this.node = node;
        parents = new ArrayList<>();
        parentsCorrespondingCheckers = new ArrayList<>();
        setUpOnCanvas = false;
        madeCurveLine = false;
    }

    public double getX()
    {
        return X;
    }
    public void setX(double x)
    {
        X = x;
    }
    public double getY()
    {
        return Y;
    }
    public void setY(double y)
    {
        Y = y;
    }

    public boolean isSetUpOnCanvas()
    {
        return setUpOnCanvas;
    }
    public void setUpOnCanvas(boolean setUpOnCanvas)
    {
        this.setUpOnCanvas = setUpOnCanvas;
    }

    public boolean madeCurveLine()
    {
        return madeCurveLine;
    }
    public void setMadeCurveLine(boolean madeCurveLine)
    {
        this.madeCurveLine = madeCurveLine;
    }

    public List<Integer> getParents()
    {
        return parents;
    }

    public List<Boolean> getParentsCorrespondingCheckers()
    {
        return parentsCorrespondingCheckers;
    }

    public void setParentCorrespondingChecker(int index, boolean value)
    {
        parentsCorrespondingCheckers.set(index, value);
    }

    public boolean allParentsCorrespondingCheckersAreTrue()
    {
        for (Boolean pcc : parentsCorrespondingCheckers) {
            if (pcc == false) {
                return false;
            }
        }

        return true;
    }

    public void setFalseForAllCorrespondingParents()
    {
        for (int i = 0; i < parentsCorrespondingCheckers.size(); i++) {
            setParentCorrespondingChecker(i, false);
        }
    }

    public Integer getBestWeight()
    {
        return bestWeight;
    }

    public void setBestWeight(Integer bestWeight)
    {
        this.bestWeight = bestWeight;
    }

    public Boolean wasUsedInForwardAlthm()
    {
        return wasUsedInForwardAlthm;
    }

    public void setWasUsedInForwardAlthm(Boolean wasUsedInForwardAlthm)
    {
        this.wasUsedInForwardAlthm = wasUsedInForwardAlthm;
    }

    public boolean isInThread()
    {
        return isInThread;
    }

    public void setInThread(boolean inThread)
    {
        isInThread = inThread;
    }

    public void addParent(Integer parentNumber)
    {
        parents.add(parentNumber);
        parentsCorrespondingCheckers.add(false); //set up false value because it's default
    }

    public void addBackPathIndex(int index)
    {
        backPathIndex += index;
    }

    public void setBackPathIndex(int value)
    {
        backPathIndex = value;
    }

    public int getBackPathIndex()
    {
        return backPathIndex;
    }

    public void setZeroBackPathIndex()
    {
        backPathIndex = 0;
    }

    public boolean isWasUsedInBackPathsFrom()
    {
        return wasUsedInBackPathsFrom;
    }

    public void setWasUsedInBackPathsFrom(boolean wasUsedInBackPathsFrom)
    {
        this.wasUsedInBackPathsFrom = wasUsedInBackPathsFrom;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("Node {number:" + getNumber() + ", ");
        if(getNextNodes() != null) {
            builder.append("nextNodes:");
            for (Integer nextNodeNumber : getNextNodes()){
                builder.append(nextNodeNumber + " ");
            }
        }
        if(getWeights() != null) {
            builder.append(", ");
            builder.append("weights::");
            for (Integer nextNodeWeight : getWeights()) {
                builder.append(nextNodeWeight + " ");
            }
        }
        if(getParents() != null) {
            builder.append(", ");
            builder.append("parents:");
            for (Integer parentNumber : getParents()) {
                builder.append(parentNumber + " ");
            }
        }
        builder.append("\n");

        return builder.toString();
    }
}
