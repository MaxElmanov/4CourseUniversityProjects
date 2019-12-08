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
    private static ConcurrentMap<Integer, List<Integer>> map;
    private static DekstraNode targetNode;
    private static DekstraNode rootNode;
    private static Vector<Integer> listOfUsedPathNumbers;
    private static Vector<Future<Integer>> futures;
    private static Vector<Integer> results;
    private static ExecutorService service;

    //object
    private DekstraNode node;
    private DekstraNode firstParentNode;
    private Integer pathNumber = null;
    private ConcurrentMap<Integer, List<Boolean>> nodesCheckers;

    static {
        listOfUsedPathNumbers = new Vector<>();
        map = new ConcurrentHashMap<>();
    }

    public DekstraBackPathsFinderThread_2(int pathNumber)
    {
        this.pathNumber = pathNumber;
    }

    public DekstraBackPathsFinderThread_2(DekstraNode node)
    {
        this.node = node;
        nodesCheckers = new ConcurrentHashMap<>();
    }

    public DekstraBackPathsFinderThread_2(DekstraNode nextNode, ConcurrentMap<Integer, List<Boolean>> nodesCheckers)
    {
        this(nextNode);
        this.nodesCheckers = nodesCheckers;
    }

    @Override
    public Integer call() throws ExecutionException, InterruptedException
    {
        synchronized (graph) {
            System.out.println(Thread.currentThread().getName());

            firstParentNode = node;
            pathNumber = UsefulFunction.generateNewPathNumberOf(map, listOfUsedPathNumbers);
            listOfUsedPathNumbers.add(pathNumber);
        }

        while (node != null) {
            synchronized (graph) {
                UsefulFunction.fillupMap(map, pathNumber, node.getNumber());
            }

            if (node.getParents().isEmpty()) {
                break;
            }
            else {
                node = getNodeNumberIsNotInThread(node);
            }
        }

        synchronized (graph) {
            List<Integer> listOfMap = map.get(pathNumber);
            fillUpParentNodesCheckersFrom(listOfMap);

            DekstraNode nextNode = Graph.getNodeByNumber(listOfMap.get(listOfMap.size() - 2)); //listOfMap.size() - 2 == number after rootNode 1->[15, 14, 2, 3, 4]

            if (!isItNecessaryToCreateNewThread(nextNode, listOfMap)) return pathNumber;

            futures.add(service.submit(new DekstraBackPathsFinderThread_2(firstParentNode, nodesCheckers)));
        }

        return pathNumber;
    }

    private DekstraNode getNodeNumberIsNotInThread(DekstraNode node)
    {
        List<Integer> parentNodes = node.getParents();

        if (node.equals(targetNode) || parentNodes.isEmpty()) {
            return null;
        }
        else if (parentNodes.size() == 1) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodes.get(0));
            return parentNode == null ? null : parentNode;
        }
        else { //parentNodes.size() > 1
            if(nodesCheckers == null || nodesCheckers.isEmpty()) {

                for (int i = 0; i < parentNodes.size(); i++) {
                    int parentNodeNumber = parentNodes.get(i);
                    DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
                    return parentNode;
                }
            }
            else{
                List<Boolean> nodeCheckersList = nodesCheckers.get(node.getNumber());

                for (int i = 0; i < parentNodes.size(); i++) {
                    int parentNodeNumber = parentNodes.get(i);
                    DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);

                    if (nodeCheckersList.get(i) == false) {
                        nodeCheckersList.set(i, true);
                        return parentNode;
                    }
                }
            }
        }

        return null;
    }

    private boolean isItNecessaryToCreateNewThread(DekstraNode nextNode, List<Integer> listOfMap)
    {
        List<Integer> parentNodes = nextNode.getParents();

        if (nextNode.equals(targetNode) || parentNodes.size() < 1) {
            return false;
        }
        else if (parentNodes.size() == 1) {
            DekstraNode newNextNode = getNextNodeWithManyParentsFrom(listOfMap, nextNode);
            return isItNecessaryToCreateNewThread(newNextNode, listOfMap);
        }
        else { //parentNodes.size() > 1
            if (checkAllNodesCheckersAreTrueOF(nextNode)) {
                DekstraNode newNextNode = getNextNodeWithManyParentsFrom(listOfMap, nextNode);
                setFalseAllCheckersOf(nextNode);
                if (newNextNode != null && !newNextNode.equals(targetNode)) { //newNextNode != null and targetNode
                    return true;
                }
            }
            else {
                return true;
            }
        }

        return false;
    }

    private void setFalseAllCheckersOf(DekstraNode nextNode)
    {
        for (Map.Entry<Integer, List<Boolean>> entry : nodesCheckers.entrySet()) {
            int key = entry.getKey();
            if (key == nextNode.getNumber()) {
                for (Boolean checker : entry.getValue()) {
                    if (checker == true) {
                        checker = false;
                    }
                }
                break;
            }
        }
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
                        return getNextNodeWithManyParentsFrom(listInMap, nextNode);
                    }
                }
            }
        }

        return null;
    }

    private boolean checkAllNodesCheckersAreTrueOF(DekstraNode nextNode)
    {
        for (Map.Entry<Integer, List<Boolean>> entry : nodesCheckers.entrySet()) {
            int key = entry.getKey();
            if (key == nextNode.getNumber()) {
                for (Boolean checker : entry.getValue()) {
                    if (checker == false) {
                        return false;
                    }
                }
                break;
            }
        }

        return true;
    }

    private void fillUpNodesCheckersFrom(DekstraNode node)
    {
        List<Boolean> booleanList = new ArrayList<>();
        for (int i = 0; i < node.getParents().size(); i++){
            booleanList.add(false);
        }
        nodesCheckers.put(node.getNumber(), booleanList);
    }

    private void fillUpParentNodesCheckersFrom(List<Integer> listOfMap)
    {
        for (int i = 0; i < listOfMap.size(); i++){
            DekstraNode node = Graph.getNodeByNumber(listOfMap.get(i));
            List<Integer> parentNodes = node.getParents();

            if(!node.equals(rootNode) && !node.equals(targetNode) && parentNodes.size() > 1) {
                List<Boolean> booleanList = new ArrayList<>();
                for (Integer parentNodeNumber : parentNodes){
                    if(listOfMap.contains(parentNodeNumber)) {
                        booleanList.add(true);
                    }
                    else{
                        booleanList.add(false);
                    }
                }
                nodesCheckers.put(node.getNumber(), booleanList);
            }
        }
    }

    // @Override
// public void run()
// {
// DekstraNode parentNode = null;
////        System.out.println(Thread.currentThread().getName());
////        System.out.println("pathNumber= " + pathNumber);
//
//        parentNode = getNodeNumberIsNotInThread(targetNode);
//
//        while (parentNode != null) {
//            synchronized (graph) {
//                UsefulFunction.fillupMap(map, pathNumber, parentNode.getNumber());
//            }
//
//            if (parentNode.getParents().isEmpty()) {
//                return; //end of back path [return ~ break]
//            }
//            else {
//                parentNode = getNodeNumberIsNotInThread(parentNode);
//            }
//        }
//
////        System.out.println("pathNumber end= " + pathNumber);
// }

    public static void setGraph(Graph graph)
    {
        DekstraBackPathsFinderThread_2.graph = graph;
    }

    public static void setMap(Map<Integer, List<Integer>> map)
    {
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
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

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
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