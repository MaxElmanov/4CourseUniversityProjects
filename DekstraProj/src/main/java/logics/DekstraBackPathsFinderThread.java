package logics;

import objects.DekstraNode;
import objects.Graph;

import java.util.*;
import java.util.concurrent.*;

public class DekstraBackPathsFinderThread implements Callable<Integer> //Runnable
{
    //static
    private static Graph graph;
    private static Integer amountBackPaths;
    private static Map<Integer, List<Integer>> map = new HashMap<>();

    //object
    private DekstraNode node;
    private Integer numberMapElement;
    private boolean stopMe = false;

    //finding all back paths by multi thread
    private static ExecutorService service;
    private static List<Future<Integer>> futures;
    private static List<Integer> results;

    public DekstraBackPathsFinderThread(DekstraNode node, Integer numberMapElement)
    {
        this.node = node;
        this.numberMapElement = numberMapElement;
    }

    public static void setGraph(Graph graph)
    {
        DekstraBackPathsFinderThread.graph = graph;
    }

    public static void setAmountBackPaths(int amountAllBackPaths)
    {
        DekstraBackPathsFinderThread.amountBackPaths = amountAllBackPaths;
    }

    public static void setMap(Map<Integer, List<Integer>> map)
    {
        DekstraBackPathsFinderThread.map = map;
    }

    public synchronized static void generate(Integer parentNumber, Integer numMapElem) throws ExecutionException, InterruptedException
    {
        DekstraNode parentNode = graph.getNodeByNumber(parentNumber);
        parentNode.setInThread(true);
        DekstraBackPathsFinderThread task = new DekstraBackPathsFinderThread(parentNode, numMapElem);
        service.submit(task);
        //futures.add(service.submit(task));
        //new Thread(task).start();
    }

//    @Override
//    public void run()
//    {
//        while (!stopMe) {
//            //synchronized (graph) {
//                if (node.isInThread()) {
//                    try {
//                        method(node);
//                    }
//                    catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                    catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            //}
//        }
//    }

    @Override
    public Integer call() throws ExecutionException, InterruptedException
    {
        while (!stopMe) {
            //synchronized (graph) {
            if (node.isInThread()) {
                try {
                    method(node);
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //}
        }

        return numberMapElement;
    }

    private void method(DekstraNode node) throws ExecutionException, InterruptedException
    {
        if (node.getParents().size() > 1) {
            fillUpMap(numberMapElement, node.getNumber(), true);

            synchronized (graph) {
                for (int i = 0; i < node.getParents().size(); i++) {
                    Integer parentNumber = node.getParents().get(i);

                    if (i == 0) {
                        DekstraNode parentNode = graph.getNodeByNumber(parentNumber);
                        method(parentNode);
                        continue;
                    }

                    DekstraBackPathsFinderThread.generate(parentNumber, ++numberMapElement);
                }
            }
        }
        else if (node.getParents().size() == 1) {
            fillUpMap(numberMapElement, node.getNumber(), false);
            DekstraNode parentNode = graph.getNodeByNumber(node.getParents().get(0));
            method(parentNode);
        }
        else { //node.getParents().size() == 0
            fillUpMap(numberMapElement, node.getNumber(), false);
            synchronized (graph) {
                this.stopMe = true;
            }
        }
    }

    private synchronized void fillUpMap(Integer numberMapElement, Integer elementNumber, boolean hasManyParents)
    {
        List<Integer> tempList = new ArrayList<>();

        if (hasManyParents) {
            if (map.isEmpty()) {
                for (int i = 0; i < amountBackPaths; i++) {
                    map.put(i, Arrays.asList(elementNumber));
                }
            }
            else {
                for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
                    if (entry.getKey() < numberMapElement) {
                        continue;
                    }
                    else {
                        tempList.clear();
                    }

                    for (Integer containingNum : entry.getValue()) {
                        tempList.add(containingNum);
                    }

                    tempList.add(elementNumber);
                    map.put(entry.getKey(), tempList);
                }
            }
        }
        else {
            for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
                if (entry.getKey() == numberMapElement) {
                    for (Integer containingNumber : entry.getValue()) {
                        tempList.add(containingNumber);
                    }
                    break;
                }
            }

            tempList.add(elementNumber);
            map.put(numberMapElement, tempList);
        }
    }

    public static void pre_run(Integer threadCount)
    {
        service = Executors.newFixedThreadPool(threadCount);
        futures = new ArrayList<>();
        results = new ArrayList<>();
    }

    public static void shutdown() throws ExecutionException, InterruptedException
    {
        try{
            for (Future<Integer> future : futures) {
                future.get();
            }
        }
        finally {
            service.shutdown();
        }
    }
}
