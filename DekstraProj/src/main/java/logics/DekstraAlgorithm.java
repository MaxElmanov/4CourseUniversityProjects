package logics;

import constants.Constants;
import functions.Timer;
import functions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.*;
import java.util.concurrent.*;

public class DekstraAlgorithm
{
    private Graph graph;

    //finding all back paths by 1 thread
    private static Map<Integer, List<Integer>> map;
    private List<Integer> paths;
    int count = 0;
    //finding all back paths by multi thread
    private ExecutorService service;
    private List<Future<Integer>> futures;
    private List<Integer> results;

    public DekstraAlgorithm(Graph graph)
    {
        this.graph = graph;

        futures = new ArrayList<>();
        results = new ArrayList<>();
        map = new HashMap<>();
        paths = new ArrayList<>();
    }

    private void init(int startPoint)
    {
        for (DekstraNode node : graph.Nodes()) {
            if (node.getNumber() == startPoint) {
                node.setBestWeight(0);
                continue;
            }
            node.setBestWeight(Constants.INF);
        }
    }

    public void DO(int startPoint, int endPoint) throws ExecutionException, InterruptedException
    {
        init(startPoint);

        while (true) {
            DekstraNode minWeightNode = getMinWeightNode();

            if (minWeightNode.getNumber() == endPoint) {
                System.out.println("Algorithm is finished");
                System.out.println("minimal path from " + startPoint + " to " + endPoint + " = <" + graph.getNodeByNumber(endPoint).getBestWeight() + ">\n");
                break;
            }

            List<Integer> nextNodes = minWeightNode.getNextNodes();
            List<Integer> weightsToNextNodes = minWeightNode.getWeights();
            for (int i = 0; i < nextNodes.size(); i++) {
                DekstraNode currentNode = graph.getNodeByNumber(nextNodes.get(i));
                int newPotentialWeight = minWeightNode.getBestWeight() + weightsToNextNodes.get(i);

                if (newPotentialWeight <= currentNode.getBestWeight()) {
                    currentNode.setBestWeight(newPotentialWeight);
                    currentNode.addParent(minWeightNode.getNumber());
                }
            }
        }

        DekstraNode targetNode = graph.getNodeByNumber(endPoint);
        Integer amountBackPaths = getAmountBackPathsFrom(endPoint);

        //time counter function
        pinpoint_time(targetNode, amountBackPaths);

        System.out.println("\namount back paths = " + amountBackPaths);

        UsefulFunction.printMap(map);
    }

    private void pinpoint_time(DekstraNode targetNode, Integer amountBackPaths) throws ExecutionException, InterruptedException
    {
        //TimeUnit time = TimeUnit.NANOSECONDS;

        Timer.start();

        //1 thread with recursion finding back paths method
        //getAllBackPaths_recursion(targetNode, amountBackPaths);

        //1 thread finding back paths method
        //getAllBackPaths(targetNode, amountBackPaths);

        //multi thread finding back paths method
        //getAllBackPaths_multiThreads(targetNode, amountBackPaths); //sometimes 4->6 is existing only in 4 or 5 back path. Necessary to fix it

        //multi + recursion thread finding back paths method
        getAllBackPaths_multiThreads_recursion(targetNode, amountBackPaths);

        //Thread.sleep(2000);

        System.out.println("Spent time = " + (Timer.stop()) + " mcs");
    }

    //difficulty equals n^3-n^2+m^2+3m
    //In fact, value (2m^2 + n^3) for (n = 6, m = 5) our initial graph = 266
    //Spend time = 986 mcs
    private void getAllBackPaths_recursion(DekstraNode node, int amountAllBackPaths)
    {
        int number = node.getNumber();

        if (node.getParents() == null || node.getParents().size() <= 0) {

            List<Integer> tempPaths = new ArrayList<>();

            for (Integer p : map.get(count)) {
                tempPaths.add(p);
            }
            for (Integer p : paths) {
                tempPaths.add(p);
            }

            map.put(count, tempPaths);
            paths = new ArrayList<>();
            count++;
            return;
        }

        if (node.getParents().size() > 1) {
            for (Integer key = count; key < amountAllBackPaths; key++) {
                if (map.get(key) != null) {
                    UsefulFunction.fillupMap(map, key, node.getNumber());
                }
                else {
                    map.put(key, Arrays.asList(node.getNumber()));
                }
            }
        }

        for (Integer parentNodeNumber : node.getParents()) {
            DekstraNode parentNode = graph.getNodeByNumber(parentNodeNumber);

            if (parentNode.getParents() == null || parentNode.getParents().size() == 0 || parentNode.getParents().size() == 1) {
                paths.add(parentNode.getNumber());
            }

            getAllBackPaths_recursion(parentNode, amountAllBackPaths);
        }
    }

    private void getAllBackPaths(DekstraNode node, int amountAllBackPaths)
    {
        int backPathIndex = 0;

        UsefulFunction.fillUpMapForManyParents(map, backPathIndex, node.getNumber(), amountAllBackPaths);

        for (Integer parentNumber : node.getParents()){
            DekstraNode currentNode = graph.getNodeByNumber(parentNumber);

            while(currentNode != null) {

                if(currentNode.getParents().size() > 1) {
                    UsefulFunction.fillUpMapForManyParents(map, backPathIndex, currentNode.getNumber(), amountAllBackPaths);
                    for (Integer innerParentNumber : currentNode.getParents()){
                        //processing inner parent numbers (i don't know how to do)
                    }
                }
                else{
                    UsefulFunction.fillupMap(map, backPathIndex, currentNode.getNumber());
                }

                if(currentNode.getParents().isEmpty()) {
                    backPathIndex++;
                    currentNode = null;
                }
                else{
                    currentNode = graph.getNodeByNumber(currentNode.getParents().get(0));
                }
            }
        }

    }

    //difficulty equals (2*n^3*m)
    //In fact, value 2*n^3*m for (n = 6, m = 5) our initial graph = 2160
    //Spend time = 2333 mcs (HashMap)
    //Spend time = 2420 mcs (TreeMap)
    private void getAllBackPaths_multiThreads(DekstraNode node, int amountAllBackPaths) throws ExecutionException, InterruptedException
    {
        int backPathIndex = 0;
        DekstraBackPathsFinderThread_2.setGraph(graph);
        DekstraBackPathsFinderThread_2.setMap(map);
        DekstraBackPathsFinderThread_2.setAmountBackPaths(amountAllBackPaths);
//        DekstraBackPathsFinderThread_2.setAmountThreads(3);

        node.setInThread(true);
        UsefulFunction.fillUpMapForManyParents(map, backPathIndex, node.getNumber(), amountAllBackPaths); //first element is belong to every back path

        service = Executors.newFixedThreadPool(amountAllBackPaths);

        Timer.start();
        try {
            for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
                futures.add(service.submit(new DekstraBackPathsFinderThread_2(entry.getKey())));
            }
            for (Future<Integer> future : futures) {
                results.add(future.get());
            }
        }
        finally {
            service.shutdown();
        }

        System.out.println("MultiThreads. Inside function spent time = " + Timer.stop());

        //DekstraBackPathsFinderThread_2.shutdown();
    }

    //Spend time = 694 mcs (HashMap)
    //Spend time = 799 mcs (TreeMap)
    private void getAllBackPaths_multiThreads_recursion(DekstraNode node, int amountAllBackPaths) throws ExecutionException, InterruptedException
    {
        DekstraBackPathsFinderThread.setGraph(graph);
        DekstraBackPathsFinderThread.setMap(map);
        DekstraBackPathsFinderThread.setAmountBackPaths(amountAllBackPaths);
        DekstraBackPathsFinderThread.pre_run(amountAllBackPaths);
        Integer numberMapElement = 0;

        Timer.start();
        DekstraBackPathsFinderThread.generate(node.getNumber(), numberMapElement);

        DekstraBackPathsFinderThread.shutdown();

        System.out.println("multiThreads_recursion. Inside function spent time = " + Timer.stop() + " mcs");
    }

    private void setBackPathsForNodesHaveManyParents(DekstraNode node, int amountAllBackPaths)
    {
        int backPathIndex = 0;

        for (Integer parentNumber : node.getParents()){
            DekstraNode parentNode = graph.getNodeByNumber(parentNumber);

            if(parentNode.getParents().size() > 1) {

                parentNode.setInThread(true);

                UsefulFunction.fillUpMapForManyParents(map, backPathIndex, parentNode.getNumber(), amountAllBackPaths);
                setBackPathsForNodesHaveManyParents(parentNode, amountAllBackPaths);
            }

            backPathIndex++;
        }
    }

    private int getAmountBackPathsFrom(Integer numberStartNode)
    {
        DekstraNode node = graph.getNodeByNumber(numberStartNode);
        int backPathCounter = node.getParents().size();

        for (Integer parentNumber : node.getParents()) {
            DekstraNode parentNode = graph.getNodeByNumber(parentNumber);

            if (parentNode.getParents().size() > 1) {
                backPathCounter += parentNode.getParents().size() - 1;
            }
        }

        return backPathCounter;
    }

    private DekstraNode getMinWeightNode()
    {
        DekstraNode minWeightNode = null;

        for (DekstraNode node : graph.Nodes()) {
            if (!node.wasUsed()) {
                minWeightNode = node;
                break;
            }
        }

        for (DekstraNode node : graph.Nodes()) {
            if (minWeightNode.getBestWeight() > node.getBestWeight() && !node.wasUsed()) {
                minWeightNode = node;
            }
        }

        minWeightNode.setWasUsed(true);

        return minWeightNode;
    }
}
