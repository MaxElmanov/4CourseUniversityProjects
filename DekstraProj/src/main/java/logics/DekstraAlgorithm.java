package logics;

import constants.AlertCommands;
import constants.Constants;
import functions.Timer;
import functions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DekstraAlgorithm
{
    //all algthm
    private Graph graph;
    private int startPoint;
    private int endPoint;

    //finding all back paths by 1 thread
    private static Map<Integer, List<Integer>> map;
    private static List<DekstraBackPathsFinderThread_2> threads;
    private static List<Integer> listOfUsedPathNumbers;

    private List<Integer> paths;
    private int count;
    private int amountAllBackPaths;

    //finding all back paths by multi thread
    private ExecutorService service;
    private List<Future<Integer>> futures;
    private List<Integer> results;

    static{
        listOfUsedPathNumbers = new ArrayList<>();
        threads = new ArrayList<>();
        map = new HashMap<>();
    }

    public DekstraAlgorithm(Graph graph)
    {
        this.graph = graph;
        futures = new ArrayList<>();
        results = new ArrayList<>();
        paths = new ArrayList<>();
    }

    public int getAmountAllBackPaths()
    {
        return amountAllBackPaths;
    }

    public static Map<Integer, List<Integer>> getMap()
    {
        return map;
    }

    public String getBestPathWeight()
    {
        return String.valueOf(Graph.getNodeByNumber(endPoint).getBestWeight());
    }

    private void init(int startPoint, int endPoint)
    {
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        for (DekstraNode node : graph.Nodes()) {
            if (node.getNumber() == startPoint) {
                node.setBestWeight(0);
                continue;
            }
            node.setBestWeight(Constants.INF);
        }
    }

    public AlertCommands DO(int rootPoint, int targetPoint) throws ExecutionException, InterruptedException
    {
        init(rootPoint, targetPoint);

        while (true) {
            DekstraNode minWeightNode = getMinWeightNode();

            if (minWeightNode.getNumber() == targetPoint) {
                System.out.println("Algorithm is finished");
                System.out.println("minimal path from " + rootPoint + " to " + targetPoint + " = <" + Graph.getNodeByNumber(targetPoint).getBestWeight() + ">\n");
                break;
            }

            List<Integer> nextNodes = minWeightNode.getNextNodes();

            if(nextNodes == null || nextNodes.isEmpty()) {
                return AlertCommands.ERROR_RESULT;
            }

            List<Integer> weightsToNextNodes = minWeightNode.getWeights();
            for (int i = 0; i < nextNodes.size(); i++) {
                DekstraNode currentNode = Graph.getNodeByNumber(nextNodes.get(i));
                int newPotentialWeight = minWeightNode.getBestWeight() + weightsToNextNodes.get(i);

                if (newPotentialWeight <= currentNode.getBestWeight()) {
                    currentNode.setBestWeight(newPotentialWeight);
                    currentNode.addParent(minWeightNode.getNumber());
                }
            }
        }

        DekstraNode targetNode = Graph.getNodeByNumber(targetPoint);
        DekstraNode rootNode = Graph.getNodeByNumber(rootPoint);

        if(!necessaryPathExists(rootNode, targetNode)) {
            return AlertCommands.ERROR_RESULT;
        }

        amountAllBackPaths = getAmountBackPaths(rootNode, targetNode);//set global variable amo amountAllBackPaths

        //time counter function
        pinpoint_time(targetNode);

        System.out.println("\namount back paths = " + amountAllBackPaths);

        UsefulFunction.printMap(map);
//        DekstraBackPathsFinderThread_2_TEST.printMap();
        //DekstraBackPathsFinderThread_2.printMap();

        return AlertCommands.RIGHTS_RESULT;
    }

    private boolean necessaryPathExists(DekstraNode rootNode, DekstraNode targetNode)
    {
        for (Integer parentNumber : rootNode.getParents()){
            DekstraNode parentNode = Graph.getNodeByNumber(parentNumber);

            if(parentNode.equals(targetNode)) {
                return true;
            }

            return necessaryPathExists(parentNode, targetNode);
        }

        return false;
    }

    private int getAmountBackPaths(DekstraNode startNode, DekstraNode endNode)
    {
        startNode.addBackPathIndex(1);

        for (DekstraNode node : graph.Nodes()) {
            //it means that node index hasn't counted yet
            if (node.getBackPathIndex() == 0) {
                countParentIndexes(node);
            }
        }

        //loop checking for unfilled BackPathIndex of nodes
        for (DekstraNode node : graph.Nodes()) {
            if (node.getBackPathIndex() == 0) {
                getAmountBackPaths(startNode, endNode);
            }
        }

        return endNode.getBackPathIndex();
    }

    private void countParentIndexes(DekstraNode node)
    {
        //checking for "parentNodeNumber" equals 0
        for (Integer parentNodeNumber : node.getParents()) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
            if (parentNode.getBackPathIndex() == 0) {
                parentNode.setZeroBackPathIndex();//on the off-chance
                return;
            }
        }

        //it will be invoked if all parents of "node" don't equal 0
        for (Integer parentNodeNumber : node.getParents()) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
            node.addBackPathIndex(parentNode.getBackPathIndex());
        }
    }

    private DekstraNode getMinWeightNode()
    {
        List<DekstraNode> graphNodes = graph.Nodes();
        DekstraNode minWeightNode = null;

        //1 loop - locking for minimal weight node which was not used in forward algorithm
        for (DekstraNode node : graphNodes) {
            if (!node.wasUsedInForwardAlthm()) {
                minWeightNode = node;

                for (DekstraNode insideNode : graphNodes) {
                    if (!insideNode.wasUsedInForwardAlthm() && insideNode.getBestWeight() < minWeightNode.getBestWeight()) {
                        minWeightNode = insideNode;
                    }
                }

                break;
            }
        }

        minWeightNode.setWasUsedInForwardAlthm(true);

        return minWeightNode;
    }

    private void pinpoint_time(DekstraNode endNode) throws ExecutionException, InterruptedException
    {
        //TimeUnit time = TimeUnit.NANOSECONDS;

        Timer.start();

        //1 thread with recursion finding back paths method
        getAllBackPaths_Pre_Recursion(endNode);

        //1 thread finding back paths method
        //getAllBackPaths(endNode);

        //multi thread finding back paths method
        //getAllBackPaths_multiThreads(endNode); //sometimes 4->6 is existing only in 4 or 5 back path. Necessary to fix it

        //As 1 thread example
        //getAllBackPaths_multiThreads_TEST(endNode);

        //multi + recursion thread finding back paths method
        //getAllBackPaths_multiThreads_recursion(endNode);

        //Thread.sleep(2000);

        System.out.println("Spent time = " + (Timer.stop()) + " mcs");
    }

    private void getAllBackPaths_Pre_Recursion(DekstraNode node)
    {
        UsefulFunction.fillUpMapForManyParents(map, count, node.getNumber(), amountAllBackPaths);
        getAllBackPaths_recursion(node);
    }

    private void getAllBackPaths_recursion(DekstraNode currentNode)
    {
        int currentNodeNumber = currentNode.getNumber();
        List<Integer> currentNodeParentsNumbers = currentNode.getParents();

        if (currentNodeParentsNumbers.isEmpty()) {
            DekstraNode nextNodeWithManyParents = getNextNodeWithManyParents(currentNode, map.get(count));

            //new count (pathNumber)
            count++;

            //e.x. last parents = 4 for nextNode = 5
            while (nextNodeWithManyParents != null && nextNodeWithManyParents.allParentsCorrespondingCheckersAreTrue()) {
                nextNodeWithManyParents.setFalseForAllCorrespondingParents();
                //removing "nextNodeWithManyParents" node with many parents
                UsefulFunction.removeExistingItemFromListByIndex(paths, nextNodeWithManyParents.getNumber(), Arrays.asList(startPoint, endPoint));
                //previous count (pathNumber)
                //  and nodes of "listOfMap" with 0 or 1 parents are cleaned too if they are in nextNode
                nextNodeWithManyParents = cleanUnnecessaryNodesFromPaths(nextNodeWithManyParents, map.get(count - 1));
            }

            if (count < amountAllBackPaths) UsefulFunction.fillUpMapByList(map, count, paths);

            return;
        }

        for (Integer parentNodeNumber : currentNodeParentsNumbers) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);

            if (!parentNode.getParents().isEmpty() && !UsefulFunction.elementExistsIn(paths, currentNode.getNumber())) {

                paths.add(currentNode.getNumber());
                break;
            }
        }

        for (int i = 0; i < currentNodeParentsNumbers.size(); i++) {
            int parentNodeNumber = currentNodeParentsNumbers.get(i);
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);

            if (currentNode.getParents().size() > 1) {
                currentNode.setParentCorrespondingChecker(i, true);
            }

            if (!UsefulFunction.elementExistsIn(map.get(count), parentNode.getNumber())) {
                UsefulFunction.fillUpMap(map, count, parentNodeNumber);
            }
            else {
                UsefulFunction.fillUpMap(map, count, currentNodeNumber);
                UsefulFunction.fillUpMap(map, count, parentNodeNumber);
            }

            getAllBackPaths_recursion(parentNode);
        }
    }

    private DekstraNode cleanUnnecessaryNodesFromPaths(DekstraNode nextNodeWithManyParents, List<Integer> listOfMap)
    {
        int startIndex = UsefulFunction.getExistingElementIndexIn(listOfMap, nextNodeWithManyParents);

        for (int i = startIndex; i >= 0; i--) {
            int nextNodeNumber = listOfMap.get(i);

            if (paths.contains(nextNodeNumber)) {
                DekstraNode nextNode = Graph.getNodeByNumber(nextNodeNumber);

                //removing until don't run into element with many parents
                if (nextNode.getParents().size() <= 1) {
                    UsefulFunction.removeExistingItemFromListByIndex(paths, nextNodeNumber, Arrays.asList(startPoint, endPoint));
                }
                else {
                    return nextNode;
                }
            }
        }

        return null;
    }

    private DekstraNode getNextNodeWithManyParents(DekstraNode currentNode, List<Integer> listInMap)
    {
        if (listInMap != null && listInMap.contains(currentNode.getNumber())) {
            return goThroughNextNodesBy(currentNode, listInMap);
        }

        return null;
    }

    private DekstraNode goThroughNextNodesBy(DekstraNode currentNode, List<Integer> listInMap)
    {
        for (Integer nextNodeNumber : currentNode.getNextNodes()) {
            if (listInMap.contains(nextNodeNumber)) {

                DekstraNode nextNode = Graph.getNodeByNumber(nextNodeNumber);
                if (nextNode.getParents().size() > 1) {
                    return nextNode;
                }
                else {
                    return goThroughNextNodesBy(nextNode, listInMap);
                }
            }
        }

        return null;
    }

    private void getAllBackPaths(DekstraNode node, int amountAllBackPaths)
    {
        int backPathIndex = 0;

        UsefulFunction.fillUpMapForManyParents(map, backPathIndex, node.getNumber(), amountAllBackPaths);

        for (Integer parentNumber : node.getParents()) {
            DekstraNode currentNode = graph.getNodeByNumber(parentNumber);

            while (currentNode != null) {

                if (currentNode.getParents().size() > 1) {
                    UsefulFunction.fillUpMapForManyParents(map, backPathIndex, currentNode.getNumber(), amountAllBackPaths);
                    for (Integer innerParentNumber : currentNode.getParents()) {
                        //processing inner parent numbers (i don't know how to do)
                    }
                }
                else {
                    UsefulFunction.fillUpMap(map, backPathIndex, currentNode.getNumber());
                }

                if (currentNode.getParents().isEmpty()) {
                    backPathIndex++;
                    currentNode = null;
                }
                else {
                    currentNode = graph.getNodeByNumber(currentNode.getParents().get(0));
                }
            }
        }

    }

//    private void getAllBackPaths_multiThreads_TEST(DekstraNode node) throws ExecutionException, InterruptedException
//    {
//        DekstraBackPathsFinderThread_2_TEST.setGraph(graph);
//
//        UsefulFunction.fillUpMapForManyParents(map, 0, node.getNumber(), amountAllBackPaths); //first element is belong to every back path
//        DekstraBackPathsFinderThread_2_TEST.setMap(map);
//
//        DekstraBackPathsFinderThread_2_TEST.setTargetNode(node);
//        DekstraNode rootNode = Graph.getNodeByNumber(startPoint);
//        DekstraBackPathsFinderThread_2_TEST.setRootNode(rootNode);
//
//        node.setInThread(true);
//
//        Timer.start();
//
//        //for (Integer parentNodeNumber : node.getParents()) {
//            DekstraNode parentNode = Graph.getNodeByNumber(10);
//            new DekstraBackPathsFinderThread_2_TEST(parentNode).CALL();
//        //}
//
//        System.out.println("MultiThreads. Inside function spent time = " + Timer.stop());
//    }

    private void getAllBackPaths_multiThreads(DekstraNode node) throws ExecutionException, InterruptedException
    {
        DekstraBackPathsFinderThread_2.setGraph(graph);
        DekstraBackPathsFinderThread_2_TEST.setGraph(graph);

        DekstraBackPathsFinderThread_2.setAmountAllBackPaths(amountAllBackPaths);
        DekstraBackPathsFinderThread_2_TEST.setAmountAllBackPaths(amountAllBackPaths);

        UsefulFunction.fillUpMapForManyParents(map, 0, node.getNumber(), amountAllBackPaths); //first element is belong to every back path
        DekstraBackPathsFinderThread_2.setMap(map);

        DekstraBackPathsFinderThread_2.setTargetNode(node);
        DekstraBackPathsFinderThread_2_TEST.setTargetNode(node);

        DekstraNode rootNode = Graph.getNodeByNumber(startPoint);
        DekstraBackPathsFinderThread_2.setRootNode(rootNode);
        DekstraBackPathsFinderThread_2_TEST.setRootNode(rootNode);

        DekstraBackPathsFinderThread_2.setThreads(threads);
        DekstraBackPathsFinderThread_2_TEST.setThreads(threads);

        DekstraBackPathsFinderThread_2_TEST.setListOfUsedPathNumbers(listOfUsedPathNumbers);

        service = Executors.newFixedThreadPool(node.getParents().size());

        node.setInThread(true);

        Timer.start();

        for (Integer parentNodeNumber : node.getParents()) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
            //futures.add(service.submit(new DekstraBackPathsFinderThread_2(parentNode)));

            //generate new pathNumber
            Integer pathNumber = UsefulFunction.generateNewPathNumberRangeFrom0To(amountAllBackPaths, listOfUsedPathNumbers);
            if(pathNumber == null) break;
            listOfUsedPathNumbers.add(pathNumber);
            //----------------------------------

            threads.add(new DekstraBackPathsFinderThread_2(pathNumber, parentNode));
            new DekstraBackPathsFinderThread_2_TEST(pathNumber, parentNode).CALL();
        }

        try {
            for (DekstraBackPathsFinderThread_2 thread : threads){
                futures.add(service.submit(thread));
            }
            for (Future<Integer> future : futures){
                future.get();
            }
        }
        finally {
            service.shutdown();
        }

        System.out.println("MultiThreads. Inside function spent time = " + Timer.stop());
    }
}
