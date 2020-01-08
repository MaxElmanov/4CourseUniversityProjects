package logics;

import constants.AlertCommands;
import objects.DekstraNode;
import objects.Graph;
import objects.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGraphGenerator
{
    private Graph graph;
    private Integer nodesAmount;
    private Integer edgesAmount;
    private Integer remainedEdgesAmount;
    private Integer weightRangeFrom;
    private Integer weightRangeTo;
    private List<DekstraNode> nodes;
    private List<Integer> edges;

    public RandomGraphGenerator(Graph graph, Integer nodesAmount, Integer edgesAmount, Integer weightsValuesRange_FROM, Integer weightsValuesRange_TO)
    {
        this.graph = graph;
        this.nodesAmount = nodesAmount;
        nodes = new ArrayList<>(nodesAmount);
        this.edgesAmount = edgesAmount;
        this.remainedEdgesAmount = edgesAmount;
        edges = new ArrayList<>(edgesAmount);
        this.weightRangeFrom = weightsValuesRange_FROM;
        this.weightRangeTo = weightsValuesRange_TO;
    }

    public AlertCommands generate()
    {
        if(weightRangeFrom > weightRangeTo){
            return AlertCommands.WARNING_RESULT;
        }

        for (int i = 0; i < nodesAmount; i++) {
            int nodeNumber = i + 1;

            //region Next nodes numbers generation
            List<Integer> nextNodesNumbers = new ArrayList<>();
            for (int j = 0; j < getRandomNextNodesAmount(); j++) {
                int randomNextNodeNumber = getRandomNodeNumber();

                while (nextNodesNumbers.contains(randomNextNodeNumber)) {
                    randomNextNodeNumber = getRandomNodeNumber();
                }

                nextNodesNumbers.add(randomNextNodeNumber);
            }
            //endregion
            //region Next nodes weights generation
            List<Integer> nextNodesWeights = new ArrayList<>();
            for (int k = 0; k < nextNodesNumbers.size(); k++) { //nextNodesNumbers.size() == amount of list "nextNodesWeights" elements
                nextNodesWeights.add(getRandomWeight());
            }
            //endregion

            graph.add(new DekstraNode(new Node(nodeNumber, nextNodesNumbers, nextNodesWeights)));
        }

        return AlertCommands.RIGHTS_RESULT;
    }

    private Integer getRandomWeight()
    {
        int diff = weightRangeTo - weightRangeFrom;
        int randomWeight = new Random().nextInt(diff + 1);
        randomWeight += weightRangeFrom;
        return randomWeight;
    }

    private Integer getRandomNextNodesAmount()
    {
        int randomNextNodeAmount = new Random().nextInt(nodesAmount + 1);
        if(randomNextNodeAmount <= remainedEdgesAmount) {
            remainedEdgesAmount -= randomNextNodeAmount;
            return randomNextNodeAmount;
        }
        else{
            return Math.max(randomNextNodeAmount - remainedEdgesAmount, 0);
        }
    }

    private Integer getRandomNodeNumberExcept(Integer exceptNodeNumber)
    {
        int randomNodeNumber = getRandomNodeNumber();

        while (randomNodeNumber == exceptNodeNumber) {
            randomNodeNumber = getRandomNodeNumber();
        }
        return randomNodeNumber;
    }

    private Integer getRandomNodeNumber()
    {
        int diff = nodesAmount - 1;
        int randomNodeNumber = new Random().nextInt(diff + 1);
        randomNodeNumber += 1;
        return randomNodeNumber;
    }

    public static void main(String[] args)
    {
        for (int i = 0; i <= 100; i++) {
            System.out.println(new RandomGraphGenerator(null, 3, 10, 1, 50).getRandomNodeNumber());
        }
    }
}
