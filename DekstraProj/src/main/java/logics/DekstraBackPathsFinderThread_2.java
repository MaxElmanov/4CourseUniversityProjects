package logics;

import functions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DekstraBackPathsFinderThread_2 implements Callable<Integer> //Runnable
{
    //static
    private static Graph graph;
    private static Integer amountBackPaths;
    private static Map<Integer, List<Integer>> map;
    private static Integer amountThreads;
    private static int count;

    //object
    private DekstraNode node;
    private int pathNumber;
    private int startParentNumber;
    private Integer numberMapElement;
    private boolean stopMe = false;
    private Lock lock;

    //finding all back paths by multi thread
    private static ExecutorService service;
    private static List<Future<Integer>> futures;
    private static List<Integer> results;

    public DekstraBackPathsFinderThread_2(int pathNumber)
    {
        this.pathNumber = pathNumber;
        //init();
    }

    public DekstraBackPathsFinderThread_2(int pathNumber, int startParentNumber)
    {
        this.pathNumber = pathNumber;
        this.startParentNumber = startParentNumber;
        //init();
    }

//    static {
//        futures = new ArrayList<>();
//        results = new ArrayList<>();
//    }

    private void init()
    {
        this.lock = new ReentrantLock();
    }

    public static void GO() throws ExecutionException, InterruptedException
    {
        service = Executors.newFixedThreadPool(amountBackPaths);
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
        futures.add(service.submit(new DekstraBackPathsFinderThread_2(entry.getKey())));
            //service.execute(new DekstraBackPathsFinderThread_2(entry.getKey()));
        }

        for (Future<Integer> future : futures) {
            future.get();
        }

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            int pathNumber = entry.getKey();
            int startParentNumber = count++;
            new DekstraBackPathsFinderThread_2(pathNumber, startParentNumber).call();
        }
    }

//    @Override
//    public void run()
//    {
//        DekstraNode parentNode = null;
//        synchronized (graph) {
//            System.out.println(Thread.currentThread().getName());
//            System.out.println("pathNumber= " + pathNumber);
//
//            parentNode = graph.getNodeByNumber(map.get(pathNumber).get(0));
//        }
//
//        while (!stopMe && parentNode != null) {
//            if (parentNode.isInThread()) {
//                synchronized (graph) {
//                    parentNode = checkNodeNumberIsNotInThread(parentNode);
//                }
//            }
//            else {
//                synchronized (graph) {
//                    UsefulFunction.fillupMap(map, pathNumber, parentNode.getNumber());
//
//                    if (parentNode.getParents().isEmpty()) {
//                        stopMe = true;
//                        parentNode = null;
//                        continue;
//                    }
//                    else {
//                        parentNode = checkNodeNumberIsNotInThread(parentNode);
//                    }
//
//                }
//            }
//        }
//    }

    @Override
    public Integer call() throws InterruptedException
    {
        DekstraNode parentNode = null;
        synchronized (graph) {
            System.out.println(Thread.currentThread().getName());
            System.out.println("pathNumber= " + pathNumber);

            parentNode = graph.getNodeByNumber(map.get(pathNumber).get(0));
        }

        while (!stopMe && parentNode != null) {
            synchronized (graph) {
                if (parentNode.isInThread()) {
                    parentNode = checkNodeNumberIsNotInThread(parentNode);
                }
                else {
                    UsefulFunction.fillupMap(map, pathNumber, parentNode.getNumber());

                    if (parentNode.getParents().isEmpty()) {
                        //stopMe = true;
                        //parentNode = null;
                        break;
                    }
                    else {
                        parentNode = checkNodeNumberIsNotInThread(parentNode);
                    }
                }
            }
        }

        return pathNumber;
    }

    private DekstraNode checkNodeNumberIsNotInThread(DekstraNode node)
    {
        for (int i = 0; i < node.getParents().size(); i++) {
            DekstraNode parentNode = graph.getNodeByNumber(node.getParents().get(i));

            if (!parentNode.isInThread() && !node.getParentsCorrespondingCheckers().get(i)) {
                if (node.getParents().size() > 1 && parentNode.getParents().size() <= 1) {
                    node.getParentsCorrespondingCheckers().set(i, true);
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

    public static void setAmountBackPaths(int amountAllBackPaths)
    {
        DekstraBackPathsFinderThread_2.amountBackPaths = amountAllBackPaths;
    }

    public static void setMap(Map<Integer, List<Integer>> map)
    {
        DekstraBackPathsFinderThread_2.map = map;
    }

    public static void setAmountThreads(int amountThreads)
    {
        DekstraBackPathsFinderThread_2.amountThreads = amountThreads;
    }

    public static void shutdown()
    {
        service.shutdown();
    }
}
