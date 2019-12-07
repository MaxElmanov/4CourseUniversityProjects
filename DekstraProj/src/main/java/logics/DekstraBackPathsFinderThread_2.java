package logics;

import functions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.*;

public class DekstraBackPathsFinderThread_2 implements Callable<Integer> //Runnable
{
    //static
    private static Graph graph;
    private static ConcurrentMap<Integer, List<Integer>> map;
    private static DekstraNode rootNode;
    private static Vector<Integer> listOfUsedPathNumbers;
    private static Vector<Future<Integer>> futures;
    private static Vector<Integer> results;
    private static ExecutorService service;

    //object
    private DekstraNode node;
    private DekstraNode firstParentNode;
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

    public DekstraBackPathsFinderThread_2()
    {
    }

    public DekstraBackPathsFinderThread_2(DekstraNode node)
    {
        this.node = node;
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
                node = checkNodeNumberIsNotInThread(node);
            }
        }

        synchronized (graph) {
            if (firstParentNode.getParents().size() > 1) {
                for (Integer parentNodeNumber : firstParentNode.getParents()) {
                    DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
                    futures.add(service.submit(new DekstraBackPathsFinderThread_2(parentNode)));
                }
//                for (Future<Integer> future : futures) {
////                    results.add(future.get());
//                    future.get();
//                }
            }
        }

        return pathNumber;
    }

// @Override
// public void run()
// {
// DekstraNode parentNode = null;
////        System.out.println(Thread.currentThread().getName());
////        System.out.println("pathNumber= " + pathNumber);
//
//        parentNode = checkNodeNumberIsNotInThread(rootNode);
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
//                parentNode = checkNodeNumberIsNotInThread(parentNode);
//            }
//        }
//
////        System.out.println("pathNumber end= " + pathNumber);
// }

    private DekstraNode checkNodeNumberIsNotInThread(DekstraNode node)
    {
        List<Integer> parentNodes = node.getParents();

        for (int i = 0; i < parentNodes.size(); i++) {
            int parentNodeNumber = parentNodes.get(i);
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);

            if(node.getParentsCorrespondingCheckers().get(i) == true && parentNodes.size() > 1) {
                continue;
            }
            else{
                node.setParentCorrespondingChecker(i , true);
            }

            return parentNode;
        }

        return null;
    }


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