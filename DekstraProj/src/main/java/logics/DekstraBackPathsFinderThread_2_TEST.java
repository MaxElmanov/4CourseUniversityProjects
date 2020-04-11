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
    private static ConcurrentMap<Integer, List<Integer>> map;
    private static DekstraNode targetNode;
    private static DekstraNode rootNode;
    private static List<Integer> listOfUsedPathNumbers;
    private static List<DekstraBackPathsFinderThread_2_TEST> threads;
    private static Integer amountAllBackPaths;

    //object
    private DekstraNode currentNode;
    private Integer pathNumber = null;
    private ConcurrentMap<Integer, List<Integer>> nodesCheckers;
    private List<Integer> readyListOfMap;
    private List<Integer> listOfMap;

    static
    {
//        listOfUsedPathNumbers = new Vector<>();
        map = new ConcurrentHashMap<>();
    }

    public DekstraBackPathsFinderThread_2_TEST(Integer pathNumber, DekstraNode readyListOfMapStartNode, List<Integer> readyListOfMap, ConcurrentMap<Integer, List<Integer>> nodesCheckers)
    {
        this.pathNumber = pathNumber;
        this.currentNode = readyListOfMapStartNode;
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
        listOfMap.add(targetNode.getNumber());
    }

    public Integer call()
    {
        synchronized (graph)
        {
//            System.out.println(Thread.currentThread().getName());
//
//            pathNumber = UsefulFunction.generateNewPathNumberOf(map, listOfUsedPathNumbers);
//            if (pathNumber == null) return pathNumber;
//            listOfUsedPathNumbers.add(pathNumber);

            if (readyListOfMap != null && !readyListOfMap.isEmpty())
            {
                //UsefulFunction.fillUpMapByReverseList(map, pathNumber, readyListOfMap);
                UsefulFunction.fillUpListByReverseCollection(listOfMap, readyListOfMap);
            }

            while (currentNode != null)
            {
                //UsefulFunction.fillUpMap(map, pathNumber, currentNode.getNumber());
                listOfMap.add(currentNode.getNumber());

                List<Integer> parentNodes = currentNode.getParents();

                if (parentNodes == null || parentNodes.isEmpty() || currentNode.equals(rootNode))
                {
                    //break;
                    return pathNumber;
                }
                else
                {
                    currentNode = getNodeNumberIsNotInThread(currentNode);
                }
            }

            //List<Integer> listOfMap = map.get(pathNumber);
            // [listOfMap.size()-2] == number after rootNode 1->[15, 14, 2, 3, 4]
            DekstraNode nextNode = Graph.getNodeByNumber(listOfMap.get(listOfMap.size() - 2));

            DekstraNode readyListOfMapStartNode = getMeanNodeOfReadyListOfMap(nextNode, listOfMap);
            List<Integer> readyListOfMap = getReadyListOfMap(readyListOfMapStartNode, listOfMap);

            if (readyListOfMapStartNode != null)
            {
                //generate new pathNumber
                Integer pathNumber = UsefulFunction.generateNewPathNumberRangeFrom0To(amountAllBackPaths, listOfUsedPathNumbers);
                if (pathNumber == null) return pathNumber;
                listOfUsedPathNumbers.add(pathNumber);
                //-----------------------------------

                threads.add(new DekstraBackPathsFinderThread_2_TEST(pathNumber, readyListOfMapStartNode, readyListOfMap, nodesCheckers));
                //new DekstraBackPathsFinderThread_2_TEST(pathNumber, readyListOfMapStartNode, readyListOfMap, nodesCheckers).start();
            }
        }

        return pathNumber;
    }

    private List<Integer> getReadyListOfMap(DekstraNode readyListOfMapStartNode, List<Integer> listOfMap)
    {
        if (readyListOfMapStartNode == null) return null;

        List<Integer> readyListOfMap = new ArrayList<>();
        boolean needToStartAdding = false;

        //"int i = listOfMap.size() - 2" because don't need to start from "rootNode" [1]. We should commence from "rootNode" "nextNodes" [2, 3, 4, 14, 15]
        for (int i = listOfMap.size() - 2; i >= 0; i--)
        {
            int numberOfList = listOfMap.get(i);

            if (needToStartAdding && numberOfList != targetNode.getNumber())
            {
                readyListOfMap.add(numberOfList);
            }

            if (readyListOfMapStartNode.getNumber() == numberOfList)
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
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodes.get(0));

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
                    DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
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
                        DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);

                        return parentNode;
                    }
                }
            }
        }

        return null;
    }

    private DekstraNode getMeanNodeOfReadyListOfMap(DekstraNode nextNode, List<Integer> listOfMap)
    {
        if (nextNode == null)
        {
            return null;
        }

        List<Integer> parentNodes = nextNode.getParents();

        if (nextNode.equals(targetNode) || parentNodes.size() < 1)
        {
            return null;
        }
        else if (parentNodes.size() == 1)
        {
            DekstraNode newNextNode = getNextNodeWithManyParentsFrom(listOfMap, nextNode);
            return getMeanNodeOfReadyListOfMap(newNextNode, listOfMap);
        }
        else
        { //parentNodes.size() > 1
            if (areAllNodesCheckersUsed(nextNode))
            {
                DekstraNode newNextNode = getNextNodeWithManyParentsFrom(listOfMap, nextNode);

                if (newNextNode != null && !newNextNode.equals(targetNode))
                {
                    clearAllNodeCheckersFor(nextNode);
                    return getMeanNodeOfReadyListOfMap(newNextNode, listOfMap);
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
                    DekstraNode nextNode = Graph.getNodeByNumber(nextNodeNumber);
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
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet())
        {
            List<Integer> tempList = new ArrayList<>();
            for (Integer number : entry.getValue())
            {
                tempList.add(number);
            }
            DekstraBackPathsFinderThread_2_TEST.map.put(entry.getKey(), tempList);
        }
    }

    public static void setTargetNode(DekstraNode targetNode)
    {
        DekstraBackPathsFinderThread_2_TEST.targetNode = targetNode;
    }

    public static void setRootNode(DekstraNode rootNode)
    {
        DekstraBackPathsFinderThread_2_TEST.rootNode = rootNode;
    }

    public static void setThreads(List<DekstraBackPathsFinderThread_2_TEST> threads)
    {
        DekstraBackPathsFinderThread_2_TEST.threads = threads;
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

// public static void shutdown()
// {
// service.shutdown();
// }
}
