package logics;

import commonUsefulFunctions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.*;
import java.util.concurrent.*;

public class DekstraBackPathsFinderThread_2_TEST implements Callable<Integer>
{
    //static
    private static Graph graph;
    private static Map<Integer, List<Integer>> map;
    private static DekstraNode targetNode;
    private static DekstraNode rootNode;
    private static List<Integer> listOfUsedPathNumbers;
    //private static List<DekstraBackPathsFinderThread_2_TEST> threads;
    private static Integer amountAllBackPaths;

    //object
    private DekstraNode currentNode;
    private Integer pathNumber;
    private ConcurrentMap<Integer, List<Integer>> nodesCheckers;
    private List<Integer> readyListOfMap;
    private List<Integer> listOfMap;

//    static
//    {
//        //threads = new ArrayList<>();
//        map = new ConcurrentHashMap<>();
//    }

    public DekstraBackPathsFinderThread_2_TEST(Integer pathNumber, DekstraNode nextCurrentNode, List<Integer> readyListOfMap, ConcurrentMap<Integer, List<Integer>> nodesCheckers)
    {
        this.pathNumber = pathNumber;
        this.currentNode = nextCurrentNode;
        this.nodesCheckers = nodesCheckers;
        this.readyListOfMap = readyListOfMap;

        this.listOfMap = new ArrayList<>();
        listOfMap.add(targetNode.getNumber());
    }

    public DekstraBackPathsFinderThread_2_TEST(Integer pathNumber, DekstraNode currentNode)
    {
        this.pathNumber = pathNumber;
        this.currentNode = currentNode;
        this.nodesCheckers = new ConcurrentHashMap<>();

        this.listOfMap = new ArrayList<>();
        //listOfMap.add(targetNode.getNumber());
    }

    public Integer call()
    {
        synchronized (graph)
        {
            System.out.println(Thread.currentThread().getName());

            if (readyListOfMap != null && !readyListOfMap.isEmpty())
            {
                UsefulFunction.fillUpListByReverseList(listOfMap, readyListOfMap);
            }

            while (currentNode != null)
            {
                listOfMap.add(currentNode.getNumber());

                List<Integer> parentNodes = currentNode.getParents();

                if (parentNodes == null || parentNodes.isEmpty() || currentNode.equals(rootNode))
                {
                    break;
                }
                else
                {
                    currentNode = getNodeNumberIsNotInThread(currentNode);
                }
            }

            //map filling up
            UsefulFunction.fillUpMapByList(map, pathNumber, listOfMap);

            //[listOfMap.size()-2] == number after rootNode 1->[15, 14, 2, 3, 4]
            DekstraNode nextNode = graph.getNodeByNumber(listOfMap.get(listOfMap.size() - 2));

            //get new next current node
            DekstraNode nextCurrentNode = getNextCurrentNodeOfReadyListOfMap(nextNode, listOfMap);

            //get already measured subPath for next listOfMap
            List<Integer> readyListOfMap = getReadyListOfMap(nextCurrentNode, listOfMap);

            if (nextCurrentNode != null)
            {
                //region Generate new pathNumber
                Integer pathNumber = UsefulFunction.generateNewPathNumberRangeFrom0To(amountAllBackPaths, listOfUsedPathNumbers);
                if (pathNumber == null) return null;
                listOfUsedPathNumbers.add(pathNumber);
                //endregion

                if (DekstraAlgorithm.getTafu().getThreadsAndFuturesRun_1_Flag())
                {
                    DekstraAlgorithm.getTafu().getThreads_2().add(new DekstraBackPathsFinderThread_2_TEST(pathNumber, nextCurrentNode, readyListOfMap, nodesCheckers));
                }
                else if (DekstraAlgorithm.getTafu().getThreadsAndFuturesRun_2_Flag())
                {
                    DekstraAlgorithm.getTafu().getThreads_1().add(new DekstraBackPathsFinderThread_2_TEST(pathNumber, nextCurrentNode, readyListOfMap, nodesCheckers));
                }
            }
        }

        return pathNumber;
    }

    private List<Integer> getReadyListOfMap(DekstraNode nextCurrentNode, List<Integer> listOfMap)
    {
        if (nextCurrentNode == null) return null;

        List<Integer> readyListOfMap = new ArrayList<>();
        boolean needToStartAdding = false;

        //"int i = listOfMap.size() - 2" because don't need to start from "rootNode" [1]. We should commence from "nextNodes" of "rootNode" [2, 3, 4, 14, 15]
        for (int i = listOfMap.size() - 2; i >= 0; i--)
        {
            int numberOfList = listOfMap.get(i);

            if (needToStartAdding && numberOfList != targetNode.getNumber())
            {
                readyListOfMap.add(numberOfList);
            }

            if (nextCurrentNode.getNumber() == numberOfList)
            {
                needToStartAdding = true;
            }
        }

        return readyListOfMap.isEmpty()
               ? null
               : readyListOfMap;
    }

    private DekstraNode getNodeNumberIsNotInThread(DekstraNode currentNode)
    {
        List<Integer> parentNodes = currentNode.getParents();

        if (parentNodes.size() == 1)
        {
            DekstraNode parentNode = graph.getNodeByNumber(parentNodes.get(0));

            return parentNode;
        }
        else
        { //parentNodes.size() > 1
            List<Integer> nodeCheckersList = nodesCheckers.get(currentNode.getNumber());

            if (nodeCheckersList == null || nodeCheckersList.isEmpty())
            {
                for (int i = 0; i < parentNodes.size(); i++)
                {
                    int parentNodeNumber = parentNodes.get(i);
                    DekstraNode parentNode = graph.getNodeByNumber(parentNodeNumber);
                    if (parentNode != null)
                    {
                        UsefulFunction.fillUpMap(nodesCheckers, currentNode.getNumber(), parentNodeNumber);

                        return parentNode;
                    }
                }
            }
            else
            {
                for (int i = 0; i < parentNodes.size(); i++)
                {
                    int parentNodeNumber = parentNodes.get(i);

                    if (!nodeCheckersList.contains(parentNodeNumber))
                    {
                        UsefulFunction.fillUpMap(nodesCheckers, currentNode.getNumber(), parentNodeNumber);
                        DekstraNode parentNode = graph.getNodeByNumber(parentNodeNumber);

                        return parentNode;
                    }
                }
            }
        }

        return null;
    }

    private DekstraNode getNextCurrentNodeOfReadyListOfMap(DekstraNode nextNode, List<Integer> listOfMap)
    {
        if (nextNode == null) return null;

        List<Integer> parentNodes = nextNode.getParents();

        if (nextNode.equals(targetNode) || parentNodes.size() < 1)
        {
            return null;
        }
        else if (parentNodes.size() == 1)
        {
            DekstraNode newNextNode = getNextNodeWithManyParentsFrom(listOfMap, nextNode);
            return getNextCurrentNodeOfReadyListOfMap(newNextNode, listOfMap);
        }
        else
        { //parentNodes.size() > 1
            if (areAllNodesCheckersUsed(nextNode))
            {
                DekstraNode newNextNode = getNextNodeWithManyParentsFrom(listOfMap, nextNode);

                if (newNextNode != null && !newNextNode.equals(targetNode))
                {
                    clearAllNodeCheckersFor(nextNode);
                    return getNextCurrentNodeOfReadyListOfMap(newNextNode, listOfMap);
                }
            }
            else
            {
                return nextNode;
            }
        }

        return null;
    }

    private void clearAllNodeCheckersFor(DekstraNode nextNode)
    {
        nodesCheckers.get(nextNode.getNumber()).clear();
    }

    private DekstraNode getNextNodeWithManyParentsFrom(List<Integer> listInMap, DekstraNode currentNode)
    {
        if (listInMap != null && listInMap.contains(currentNode.getNumber()))
        {
            for (Integer nextNodeNumber : currentNode.getNextNodes())
            {
                if (listInMap.contains(nextNodeNumber))
                {
                    DekstraNode nextNode = graph.getNodeByNumber(nextNodeNumber);
                    if (nextNode.getParents().size() > 1)
                    {
                        return nextNode;
                    }
                    else
                    {
                        //go to invoking recursion method "getNextNodeWithManyParentsFrom()" because only one of "currentNode.getNextNodes()" exists into "listInMap"
                        return getNextNodeWithManyParentsFrom(listInMap, nextNode);
                    }
                }
            }
        }

        return null;
    }

    private boolean areAllNodesCheckersUsed(DekstraNode nextNode)
    {
        int nextNodeNumber = nextNode.getNumber();

        if (!nodesCheckers.containsKey(nextNodeNumber))
        {
            return false;
        }

        if (nextNode.getParents().size() == nodesCheckers.get(nextNodeNumber).size())
        {
            return true;
        }

        return false;
    }

    public static void setGraph(Graph graph)
    {
        DekstraBackPathsFinderThread_2_TEST.graph = graph;
    }

    public static void setMap(Map<Integer, List<Integer>> map)
    {
        DekstraBackPathsFinderThread_2_TEST.map = map;
    }

    public static void setTargetNode(DekstraNode targetNode)
    {
        DekstraBackPathsFinderThread_2_TEST.targetNode = targetNode;
    }

    public static void setRootNode(DekstraNode rootNode)
    {
        DekstraBackPathsFinderThread_2_TEST.rootNode = rootNode;
    }

    public static void setAmountAllBackPaths(Integer amountAllBackPaths)
    {
        DekstraBackPathsFinderThread_2_TEST.amountAllBackPaths = amountAllBackPaths;
    }

    public static void setListOfUsedPathNumbers(List<Integer> listOfUsedPathNumbers)
    {
        DekstraBackPathsFinderThread_2_TEST.listOfUsedPathNumbers = listOfUsedPathNumbers;
    }

    public static void printMap()
    {
        StringBuilder builder;

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet())
        {
            builder = new StringBuilder();

            builder.append("key: " + entry.getKey());
            builder.append(", path: ");
            for (Integer number : entry.getValue())
            {
                builder.append(number + ", ");
            }
            System.out.println(builder.substring(0, builder.length() - 2));
        }
    }
}
