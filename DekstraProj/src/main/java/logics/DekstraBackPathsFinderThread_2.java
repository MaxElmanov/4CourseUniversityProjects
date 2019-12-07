package logics;

import functions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DekstraBackPathsFinderThread_2 implements Callable<Integer> //Runnable
{
    //static
    private static Graph graph;
    private static ConcurrentMap<Integer, List<Integer>> map;
    private static DekstraNode rootNode;
    private static Vector<Integer> listOfUsedPathNumbers;

    //object
// private DekstraNode node;
    private Integer pathNumber = null;
// private boolean stopMe = false;

//finding all back paths by multi thread
// private static ExecutorService service;
// private static List<Future<Integer» futures;
// private static List<Integer> results;

    static {
        listOfUsedPathNumbers = new Vector<>();
        map = new ConcurrentHashMap<>();
    }

    public DekstraBackPathsFinderThread_2(int pathNumber)
    {
        this.pathNumber = pathNumber;
    }

    public DekstraBackPathsFinderThread_2() {}

    @Override
    public Integer call()
    {
        DekstraNode parentNode = null;
        System.out.println(Thread.currentThread().getName());
//        System.out.println("pathNumber= " + pathNumber);

        parentNode = checkNodeNumberIsNotInThread(rootNode);

        while (parentNode != null) {
            UsefulFunction.fillupMap(map, pathNumber, parentNode.getNumber());

            if (parentNode.getParents().isEmpty()) {
                return pathNumber; //end of back path [return ~ break]
            }
            else {
                parentNode = checkNodeNumberIsNotInThread(parentNode);
            }
        }

//        System.out.println("pathNumber end= " + pathNumber);

        return pathNumber;
    }

// @Override
// public void run()
// {
// DekstraNode parentNode = null;
// synchronized (graph) {
// System.out.println(Thread.currentThread().getName());
// System.out.println("pathNumber= " + pathNumber);
//
// parentNode = checkNodeNumberIsNotInThread(rootNode);
// }
//
// while (parentNode != null) {
// UsefulFunction.fillupMap(map, pathNumber, parentNode.getNumber());
//
// if (parentNode.getParents().isEmpty()) {
// break;
// }
// else {
// parentNode = checkNodeNumberIsNotInThread(parentNode);
// }
// }
// }

// private DekstraNode checkNodeNumberIsNotInThread(DekstraNode node)
// {
// List<Integer> parentNodes = node.getParents();
// List<Boolean> parentsCheckers = node.getParentsCorrespondingCheckers();
//
// for (int i = 0; i < parentNodes.size(); i++) {
// DekstraNode parentNode = graph.getNodeByNumber(parentNodes.get(i));
//
// if (!parentNode.isInThread() && !parentsCheckers.get(i)) {
// if (parentNodes.size() > 1 && parentNode.getParents().size() <= 1) {
// parentsCheckers.set(i, true);
// }
//
// return parentNode;
// }
// }
//
// return null;
// }

    private synchronized DekstraNode checkNodeNumberIsNotInThread(DekstraNode node)
    {
        //rootNode(13) is not checked because it exists in all back paths
        DekstraNode newNotUsedNode = null;

        if (!listOfUsedPathNumbers.contains(pathNumber)) {
            Integer generatedPathNumber = UsefulFunction.generateNewPathNumberOf(map, listOfUsedPathNumbers);
            if (generatedPathNumber == null) {
                return null; //map hasn't contain unused key (pathNumber) in another thread
            }
            else {
                listOfUsedPathNumbers.add(generatedPathNumber);
                pathNumber = generatedPathNumber;
            }
        }

        newNotUsedNode = getNodeIsNotInThread(map.get(pathNumber), node);

        return newNotUsedNode == null
               ? null
               : newNotUsedNode;
    }

    private DekstraNode getNodeIsNotInThread(List<Integer> listOfMap, DekstraNode node)
    {
        List<Integer> parentNodes = node.getParents();

        for (Integer parentNodeNumber : parentNodes) {
            if (listOfMap.contains(parentNodeNumber)) {
                getNodeIsNotInThread(listOfMap, Graph.getNodeByNumber(parentNodeNumber));
            }
            else {
                return Graph.getNodeByNumber(parentNodeNumber);
            }
        }

        return null;
    }


    public static void

    setGraph(Graph graph)
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

    public static void setRootNode(DekstraNode rootNode)
    {
        DekstraBackPathsFinderThread_2.rootNode = rootNode;
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