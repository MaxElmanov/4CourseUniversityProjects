package logics;

import functions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class DekstraBackPathsFinderThread_2 implements Callable<Integer> //Runnable
{
    //static
    private static Graph graph;
    private static Map<Integer, List<Integer>> map;
    private static DekstraNode rootNode;

    //object
//    private DekstraNode node;
    private int pathNumber;
//    private boolean stopMe = false;

    //finding all back paths by multi thread
//    private static ExecutorService service;
//    private static List<Future<Integer>> futures;
//    private static List<Integer> results;

    public DekstraBackPathsFinderThread_2(int pathNumber)
    {
        this.pathNumber = pathNumber;
    }

    @Override
    public Integer call()
    {
        DekstraNode parentNode = null;
        synchronized (graph) {
            System.out.println(Thread.currentThread().getName());
            System.out.println("pathNumber= " + pathNumber);

            parentNode = checkNodeNumberIsNotInThread(rootNode);
        }

        while (parentNode != null) {
            UsefulFunction.fillupMap(map, pathNumber, parentNode.getNumber());

            if (parentNode.getParents().isEmpty()) {
                break;
            }
            else {
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

    private DekstraNode checkNodeNumberIsNotInThread(DekstraNode node)
    {
        List<Integer> parentNodes = node.getParents();
        List<Boolean> parentsCheckers = node.getParentsCorrespondingCheckers();

        for (int i = 0; i < parentNodes.size(); i++) {
            DekstraNode parentNode = graph.getNodeByNumber(parentNodes.get(i));

            if (!parentNode.isInThread() && !parentsCheckers.get(i)) {
                if (parentNodes.size() > 1 && parentNode.getParents().size() <= 1) {
                    parentsCheckers.set(i, true);
                }

                return parentNode;
            }
        }

        return null;
    }

    public static void setGraph(Graph graph)
    {
        DekstraBackPathsFinderThread_2.graph = graph;
    }

    public static void setMap(Map<Integer, List<Integer>> map)
    {
        DekstraBackPathsFinderThread_2.map = map;
    }

    public static void setRootNode(DekstraNode rootNode)
    {
        DekstraBackPathsFinderThread_2.rootNode = rootNode;
    }

//    public static void shutdown()
//    {
//        service.shutdown();
//    }
}
