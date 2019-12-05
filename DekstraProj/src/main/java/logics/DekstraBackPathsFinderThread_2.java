package logics;

import functions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class DekstraBackPathsFinderThread_2 implements Callable<Integer> //Runnable
{
    //static
    private static Graph graph;
    private static Map<Integer, List<Integer>> map;
    private static DekstraNode rootNode;
    private static List<Integer> listOfUsedPathNumbers;

    //object
//    private DekstraNode node;
    private Integer pathNumber = null;
//    private boolean stopMe = false;

    //finding all back paths by multi thread
//    private static ExecutorService service;
//    private static List<Future<Integer>> futures;
//    private static List<Integer> results;

    static{
        listOfUsedPathNumbers = new ArrayList<>();
    }

    public DekstraBackPathsFinderThread_2(int pathNumber) {
        this.pathNumber = pathNumber;
    }

    public DekstraBackPathsFinderThread_2() {}

    @Override
    public Integer call() {
        DekstraNode parentNode = null;
        //System.out.println(Thread.currentThread().getName());
        //System.out.println("pathNumber= " + pathNumber);

        parentNode = checkNodeNumberIsNotInThread(rootNode);

        while (parentNode != null) {
            synchronized (graph) {
                UsefulFunction.fillupMap(map, pathNumber, parentNode.getNumber());
            }

            if (parentNode.getParents().isEmpty()) {
                break; //end of back path
            } else {
                parentNode = checkNodeNumberIsNotInThread(parentNode);
            }
        }

        return pathNumber;
    }

//    @Override
//    public void run()
//    {
//        DekstraNode parentNode = null;
//        synchronized (graph) {
//            System.out.println(Thread.currentThread().getName());
//            System.out.println("pathNumber= " + pathNumber);
//
//            parentNode = checkNodeNumberIsNotInThread(rootNode);
//        }
//
//        while (parentNode != null) {
//            UsefulFunction.fillupMap(map, pathNumber, parentNode.getNumber());
//
//            if (parentNode.getParents().isEmpty()) {
//                break;
//            }
//            else {
//                parentNode = checkNodeNumberIsNotInThread(parentNode);
//            }
//        }
//    }

//    private DekstraNode checkNodeNumberIsNotInThread(DekstraNode node)
//    {
//        List<Integer> parentNodes = node.getParents();
//        List<Boolean> parentsCheckers = node.getParentsCorrespondingCheckers();
//
//        for (int i = 0; i < parentNodes.size(); i++) {
//            DekstraNode parentNode = graph.getNodeByNumber(parentNodes.get(i));
//
//            if (!parentNode.isInThread() && !parentsCheckers.get(i)) {
//                if (parentNodes.size() > 1 && parentNode.getParents().size() <= 1) {
//                    parentsCheckers.set(i, true);
//                }
//
//                return parentNode;
//            }
//        }
//
//        return null;
//    }

    private DekstraNode checkNodeNumberIsNotInThread(DekstraNode node) {
        synchronized (graph) {
            //rootNode(13) is not checked because it exists in all back paths
            for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {

                DekstraNode newNotUsedNode = null;
                int key = entry.getKey();

                if (!listOfUsedPathNumbers.contains(key)) {
                    newNotUsedNode = getNodeIsNotInThread(entry.getValue(), node);
                } else {
                    System.out.println("path number = " + pathNumber);
                    newNotUsedNode = getNodeIsNotInThread(map.get(key), node);
                }

                if (newNotUsedNode == null) {
                    break;
                } else {
                    this.pathNumber = key;
                    listOfUsedPathNumbers.add(key);
                    return newNotUsedNode;
                }
            }
        }

        return null;
    }

    private DekstraNode getNodeIsNotInThread(List<Integer> listOfMap, DekstraNode node) {
        List<Integer> parentNodes = node.getParents();

        for (Integer parentNodeNumber : parentNodes) {

            if (listOfMap.contains(parentNodeNumber)) {
                getNodeIsNotInThread(listOfMap, Graph.getNodeByNumber(parentNodeNumber));
            } else {
                return Graph.getNodeByNumber(parentNodeNumber);
            }
        }

        return null;
    }


    public static void setGraph(Graph graph) {
        DekstraBackPathsFinderThread_2.graph = graph;
    }

    public static void setMap(Map<Integer, List<Integer>> map) {
        DekstraBackPathsFinderThread_2.map = map;
    }

    public static void setRootNode(DekstraNode rootNode) {
        DekstraBackPathsFinderThread_2.rootNode = rootNode;
    }

//    public static void shutdown()
//    {
//        service.shutdown();
//    }
}
