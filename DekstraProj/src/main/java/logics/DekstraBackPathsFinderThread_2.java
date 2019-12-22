package logics;

import functions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.*;
import java.util.concurrent.*;

public class DekstraBackPathsFinderThread_2 implements Callable<Integer> //Runnable
{
    //static
    private static Graph graph;
    private static ConcurrentMap<Integer, List<Integer>>map;
    private static DekstraNode targetNode;
    private static DekstraNode rootNode;
    private static Vector<Integer> listOfUsedPathNumbers;
    private static Vector<Future<Integer>>futures;
    private static Vector<Integer> results;
    private static ExecutorService service;

    //object
    private DekstraNode node;
    private DekstraNode firstParentNode;
    private Integer pathNumber = null;
    private ConcurrentMap<Integer, List<Integer>>nodesCheckers;
    private List<Integer> readyListOfMap;

    static {
        listOfUsedPathNumbers = new Vector<>();
        map = new ConcurrentHashMap<>();
    }

    public DekstraBackPathsFinderThread_2(DekstraNode readyListOfMapStartNode, List<Integer> readyListOfMap, ConcurrentMap<Integer, List<Integer>> nodesCheckers)
    {
        this.node = readyListOfMapStartNode;
        this.nodesCheckers = nodesCheckers;
        this.readyListOfMap = readyListOfMap;
    }

    public DekstraBackPathsFinderThread_2(DekstraNode nextNode)
    {
        this.node = nextNode;
        this.nodesCheckers = new ConcurrentHashMap<>();
    }

    @Override
    public Integer call() throws ExecutionException, InterruptedException
    {
        synchronized (graph) {
            System.out.println(Thread.currentThread().getName());

            pathNumber = UsefulFunction.generateNewPathNumberOf(map, listOfUsedPathNumbers);
            listOfUsedPathNumbers.add(pathNumber);

            if (readyListOfMap != null && !readyListOfMap.isEmpty()) {
                UsefulFunction.fillUpMapByList(map, pathNumber, readyListOfMap);
            }

            while (node != null) {
                UsefulFunction.fillUpMap(map, pathNumber, node.getNumber());

                if (node.getParents().isEmpty()) {
                    break;
                }
                else {
                    node = getNodeNumberIsNotInThread(node);
                }
            }

            List<Integer> listOfMap = map.get(pathNumber);
// [listOfMap.size()-2] == number after rootNode 1->[15, 14, 2, 3, 4]
            DekstraNode nextNode = Graph.getNodeByNumber(listOfMap.get(listOfMap.size() - 2));

            DekstraNode readyListOfMapStartNode = getMeanNodeOfReadyListOfMap(nextNode, listOfMap);
            List<Integer> readyListOfMap = getReadyListOfMap(readyListOfMapStartNode, listOfMap);

            if (readyListOfMap != null) {
                futures.add(service.submit(new DekstraBackPathsFinderThread_2(readyListOfMapStartNode, readyListOfMap, nodesCheckers)));
            }

        } //synchronized

        return pathNumber;
    }

    private List<Integer> getReadyListOfMap(DekstraNode readyListOfMapStartNode, List<Integer> listOfMap)
    {

        if (readyListOfMapStartNode == null) return null;


        List<Integer> readyListOfMap = new ArrayList<>();
        boolean needToStartAdding = false;

//"int i = listOfMap.size() - 2" because don't need to start from "rootNode" [1]. We should commence from "rootNode" "nextNodes" [2, 3, 4, 14, 15]
        for (int i = listOfMap.size() - 2; i >= 0; i--) {
            int numberOfList = listOfMap.get(i);

            if (needToStartAdding && numberOfList != targetNode.getNumber()) {
                readyListOfMap.add(numberOfList);
            }

            if (readyListOfMapStartNode.getNumber() == numberOfList) {
                needToStartAdding = true;
            }
        }

        return readyListOfMap.isEmpty()
               ? null
               : readyListOfMap;
    }

    private DekstraNode getNodeNumberIsNotInThread(DekstraNode node)
    {
        List<Integer> parentNodes = node.getParents();

        if (parentNodes == null || parentNodes.isEmpty()) {
            return null;
        }
        else if (parentNodes.size() == 1) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodes.get(0));

            return parentNode;
        }
        else { //parentNodes.size() > 1
            List<Integer> nodeCheckersList = nodesCheckers.get(node.getNumber());

            if (nodeCheckersList == null || nodeCheckersList.isEmpty()) {

                for (int i = 0; i < parentNodes.size(); i++) {
                    int parentNodeNumber = parentNodes.get(i);
                    DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
                    if (parentNode != null) {
                        UsefulFunction.fillUpMap(nodesCheckers, node.getNumber(), parentNodeNumber);

                        return parentNode;
                    }
                }

            }
            else {
                for (int i = 0; i < parentNodes.size(); i++) {
                    int parentNodeNumber = parentNodes.get(i);

                    if (!nodeCheckersList.contains(parentNodeNumber)) {
                        UsefulFunction.fillUpMap(nodesCheckers, node.getNumber(), parentNodeNumber);
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
        List<Integer> parentNodes = nextNode.getParents();

        if (nextNode.equals(targetNode) || parentNodes.size() < 1) {
            return null;
        }
        else if (parentNodes.size() == 1) {
            DekstraNode newNextNode = getNextNodeWithManyParentsFrom(listOfMap, nextNode);
            return getMeanNodeOfReadyListOfMap(newNextNode, listOfMap);
        }
        else { //parentNodes.size() > 1
            if (areAllNodesCheckersUsed(nextNode)) {
                DekstraNode newNextNode = getNextNodeWithManyParentsFrom(listOfMap, nextNode);

                if (newNextNode != null && !newNextNode.equals(targetNode)) { //newNextNode != null and targetNode
                    clearAllNodeCheckersFor(nextNode);

                    return newNextNode;
                }
            }
            else {
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
        if (listInMap != null && listInMap.contains(currentNode.getNumber())) {
            for (Integer nextNodeNumber : currentNode.getNextNodes()) {

                if (listInMap.contains(nextNodeNumber)) {
                    DekstraNode nextNode = Graph.getNodeByNumber(nextNodeNumber);
                    if (nextNode.getParents().size() > 1) {
                        return nextNode;
                    }
                    else {
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
        if (nextNode.getParents().size() == nodesCheckers.get(nextNode.getNumber()).size()) {
            return true;
        }

        return false;
    }

// @Override
// public void run()
// {
// DekstraNode parentNode = null;
//// System.out.println(Thread.currentThread().getName());
//// System.out.println("pathNumber= " + pathNumber);
//
// parentNode = getNodeNumberIsNotInThread(targetNode);
//
// while (parentNode != null) {
// synchronized (graph) {
// UsefulFunction.fillUpMap(map, pathNumber, parentNode.getNumber());
// }
//
// if (parentNode.getParents().isEmpty()) {
// return; //end of back path [return ~ break]
// }
// else {
// parentNode = getNodeNumberIsNotInThread(parentNode);
// }
// }
//
//// System.out.println("pathNumber end= " + pathNumber);
// }

    public static void setGraph(Graph graph)
    {
        DekstraBackPathsFinderThread_2.graph = graph;
    }

    public static void setMap(Map<Integer, List<Integer>> map)
    {
        for (Map.Entry<Integer, List<Integer>>entry:
    map.entrySet()){
        List<Integer> tempList = new ArrayList<>();
        for (Integer number : entry.getValue()) {
            tempList.add(number);
        }
        DekstraBackPathsFinderThread_2.map.put(entry.getKey(), tempList);
    }
    }

    public static void setTargetNode(DekstraNode targetNode)
    {
        DekstraBackPathsFinderThread_2.targetNode = targetNode;
    }

    public static void setRootNode(DekstraNode rootNode)
    {
        DekstraBackPathsFinderThread_2.rootNode = rootNode;
    }

    public static void setFutures(List<Future<Integer>> futures)
    {
        DekstraBackPathsFinderThread_2.futures = new Vector(futures);
    }

    public static void setResults(List<Integer> results)
    {
        DekstraBackPathsFinderThread_2.results = new Vector(results);
    }

    public static void setService(ExecutorService service)
    {
        DekstraBackPathsFinderThread_2.service = service;
    }

    public static void printMap()
    {
        StringBuilder builder;

        for (Map.Entry<Integer, List<Integer>>entry:
    map.entrySet()){
        builder = new StringBuilder();

        builder.append("key: " + entry.getKey());
        builder.append(", path: ");
        for (Integer number : entry.getValue()) {
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