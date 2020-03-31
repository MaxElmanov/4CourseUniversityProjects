package logics;

import constants.AlertCommands;
import constants.Constants;
import commonUsefulFunctions.Timer;
import commonUsefulFunctions.UsefulFunction;
import objects.DekstraNode;
import objects.Graph;

import java.util.*;
import java.util.concurrent.*;

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
    private int pathNumber;
    private int amountAllBackPaths;
    private long algorithmSpentTime;

    //for amount back paths getting
    private List<DekstraNode> listNodesWithAddedBPI;
    private List<DekstraNode> listNotToUseNodesWithAddedBPI;

    //finding all back paths by multi thread
    private ExecutorService service;
    private List<Future<Integer>> futures;
    private List<Integer> results;

    static {
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
        listNodesWithAddedBPI = new ArrayList<>();
        listNotToUseNodesWithAddedBPI = new ArrayList<>();
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

    public long getAlgorithmSpentTime()
    {
        return algorithmSpentTime;
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

    private boolean areThereNodesWhichNotToBeUsedInForwardAlgthm()
    {
        for (DekstraNode node : graph.Nodes()) {
            if (node.wasUsedInForwardAlthm() == false) {
                return true;
            }
        }

        return false;
    }

    public AlertCommands DO(int rootPoint, int targetPoint) throws ExecutionException, InterruptedException
    {
        map.clear();
        init(rootPoint, targetPoint);

        while (true) {
            DekstraNode minWeightNode = getMinWeightNodeExcept(null);

            if (!areThereNodesWhichNotToBeUsedInForwardAlgthm()) {
                System.out.println("Algorithm is finished");
                System.out.println("minimal path from " + rootPoint + " to " + targetPoint + " = <" + Graph.getNodeByNumber(targetPoint).getBestWeight() + ">\n");
                break;
            }

            List<Integer> nextNodes = minWeightNode.getNextNodes();

            //if "minWeightNode" doesn't have next nodes
            boolean goOutFromLoop = false;
            while (nextNodes == null || nextNodes.isEmpty()) {
                minWeightNode = getMinWeightNodeExcept(minWeightNode);

                if(minWeightNode == null) {
                    goOutFromLoop = true;
                    break;
                }

                nextNodes = minWeightNode.getNextNodes();
            }

            //go out from the loop if "getMinWeightNodeExcept(minWeightNode)" function can't find "minWeightNode" among all nodes
            if(goOutFromLoop) {
                break;
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

        DekstraNode rootNode = Graph.getNodeByNumber(rootPoint);
        DekstraNode targetNode = Graph.getNodeByNumber(targetPoint);

        //check for "rootNode" and "targetNode" identity
        if (rootNode.equals(targetNode)) return AlertCommands.RIGHTS_RESULT;

        //check for "targetNode" has not been achieved
        if (targetNode.getBestWeight() >= Constants.INF) return AlertCommands.WARNING_RESULT;

        //region subGraph forming
        List<DekstraNode> subGraph = new ArrayList<>();
        subGraph.add(rootNode);
        subGraph.add(targetNode);
        fillUpSubGraphFromRootToTargetNode(rootNode, targetNode, subGraph);
        //endregion

        //region get general back path in subGraph
        rootNode.setBackPathIndex(1);
        amountAllBackPaths = getAmountBackPaths(subGraph, rootNode, targetNode);//set global variable "amountAllBackPaths"
        //endregion

        //time counter function
        pinpoint_time(targetNode);

        System.out.println("\namount back paths = " + amountAllBackPaths);

        UsefulFunction.printMap(map);
        System.out.println("max graph width = " + graph.getMaxGraphWidth());
//        DekstraBackPathsFinderThread_2_TEST.printMap();
        //DekstraBackPathsFinderThread_2.printMap();

        return AlertCommands.RIGHTS_RESULT;
    }

    private void fillUpSubGraphFromRootToTargetNode(DekstraNode rootNode, DekstraNode targetOrParentNode, List<DekstraNode> subGraph)
    {
        for (Integer parentNumber : targetOrParentNode.getParents()){
            DekstraNode parentNode = Graph.getNodeByNumber(parentNumber);

            if(parentNode.equals(rootNode) || subGraph.contains(parentNode)) continue;

            subGraph.add(parentNode);
            fillUpSubGraphFromRootToTargetNode(rootNode, parentNode, subGraph);
        }
    }

    private int getAmountBackPaths(List<DekstraNode> subGraph, DekstraNode rootNode, DekstraNode targetNode)
    {
        boolean listNodesWithAddedBPIWasChanged = false;

        for (Integer nextNodeNumber : rootNode.getNextNodes()) {
            DekstraNode nextNode = Graph.getNodeByNumber(nextNodeNumber);
            DekstraNode tempResultNode = null;
            //it means that nextNode index hasn't counted yet
            if (nextNode.getBackPathIndex() == 0) {
                tempResultNode = countParentIndexes(nextNode);
            }

            if (tempResultNode != null) {
                listNodesWithAddedBPI.add(tempResultNode);
                listNodesWithAddedBPIWasChanged = true;
            }
        }

        //"rootNode" which not to add any "tempResultNode" into "listNodesWithAddedBPI" should be blocked for continue using
        if (listNodesWithAddedBPIWasChanged) {
            listNotToUseNodesWithAddedBPI.add(rootNode);
        }

        //loop checking for unfilled BackPathIndex of nodes [rootNode = lastNodeWithAddedBackPathIndex]
        for (DekstraNode nextNode : subGraph) {
            if (nextNode.getBackPathIndex() == 0) {
                DekstraNode lastNodeWithSetBPI = getRandomNodeWithBPI(listNodesWithAddedBPI, listNotToUseNodesWithAddedBPI);
                UsefulFunction.printList(listNodesWithAddedBPI);
                System.out.println("lastNodeWithSetBPI = " + lastNodeWithSetBPI);
                return getAmountBackPaths(subGraph, lastNodeWithSetBPI, targetNode);
            }
        }

        return targetNode.getBackPathIndex();
    }

    private DekstraNode getRandomNodeWithBPI(List<DekstraNode> listNodesWithAddedBPI, List<DekstraNode> listNotToUseNodesWithAddedBPI)
    {
        Random rand = new Random();
        int size = listNodesWithAddedBPI.size();

        while (size <= 0) {
            UsefulFunction.throwException("You can't get a random number because list is empty");
        }

        int randomIndex = rand.nextInt(size);
        DekstraNode randomNode = listNodesWithAddedBPI.get(randomIndex);

        while (listNotToUseNodesWithAddedBPI.contains(randomNode)) {
            randomIndex = rand.nextInt(size);
            randomNode = listNodesWithAddedBPI.get(randomIndex);
        }

        return randomNode;
    }

    private DekstraNode countParentIndexes(DekstraNode node)
    {
        //checking for "parentNodeNumber" equals 0
        for (Integer parentNodeNumber : node.getParents()) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
            if (parentNode.getBackPathIndex() == 0) {
                parentNode.setZeroBackPathIndex();//on the off-chance
                return null;
            }
        }

        //it will be invoked if all parents of "node" don't equal 0
        for (Integer parentNodeNumber : node.getParents()) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);
            node.addBackPathIndex(parentNode.getBackPathIndex());
        }

        return node;
    }

    private DekstraNode getMinWeightNodeExcept(DekstraNode exceptNode)
    {
        List<DekstraNode> graphNodes = graph.Nodes();
        DekstraNode minWeightNode = null;

        //1 loop - locking for minimal weight node which was not used in forward algorithm
        for (DekstraNode node : graphNodes) {
            if (!node.wasUsedInForwardAlthm()) {
                if(exceptNode != null && node.equals(exceptNode)) {
                    continue;
                }

                minWeightNode = node;

                for (DekstraNode insideNode : graphNodes) {
                    if (!insideNode.wasUsedInForwardAlthm() && insideNode.getBestWeight() < minWeightNode.getBestWeight()) {
                        minWeightNode = insideNode;
                    }
                }

                break;
            }
        }

        if(minWeightNode != null) {
            minWeightNode.setWasUsedInForwardAlthm(true);
        }

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

        algorithmSpentTime = Timer.stop();
        System.out.println("Spent time = " + algorithmSpentTime + " mcs");
    }

    private void getAllBackPaths_Pre_Recursion(DekstraNode node)
    {
        UsefulFunction.fillUpMapForManyParents(map, pathNumber, node.getNumber(), amountAllBackPaths);
        getAllBackPaths_recursion(node);
    }

    private void getAllBackPaths_recursion(DekstraNode currentNode)
    {
        int currentNodeNumber = currentNode.getNumber();
        List<Integer> currentNodeParentsNumbers = currentNode.getParents();

        if (currentNodeParentsNumbers.isEmpty()) {
            DekstraNode nextNodeWithManyParents = getNextNodeWithManyParents(currentNode, map.get(pathNumber), null);

            //new pathNumber (pathNumber)
            pathNumber++;

            //e.x. last parents = 4 for nextNode = 5
            while (nextNodeWithManyParents != null && nextNodeWithManyParents.allParentsCorrespondingCheckersAreTrue()) {
                nextNodeWithManyParents.setFalseForAllCorrespondingParents();
                //removing "nextNodeWithManyParents" node with many parents
                UsefulFunction.removeExistingItemFromListByIndex(paths, nextNodeWithManyParents.getNumber(), Arrays.asList(startPoint, endPoint));
                //previous pathNumber (pathNumber)
                //  and nodes of "listOfMap" with 0 or 1 parents are cleaned too if they are in nextNode
                nextNodeWithManyParents = cleanNodesWithSingleOrLessParentsFromPaths(nextNodeWithManyParents, map.get(pathNumber - 1));
            }

            if (pathNumber < amountAllBackPaths) UsefulFunction.fillUpMapByList(map, pathNumber, paths);

            return;
        }

        for (Integer parentNodeNumber : currentNodeParentsNumbers) {
            DekstraNode parentNode = Graph.getNodeByNumber(parentNodeNumber);

            if ((nodeParentsHaveManyParents(parentNode) || currentNodeParentsNumbers.size() > 1) && !UsefulFunction.listContainsElement(paths, currentNode.getNumber())) {

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

            if (!UsefulFunction.listContainsElement(map.get(pathNumber), parentNode.getNumber())) {
                UsefulFunction.fillUpMap(map, pathNumber, parentNodeNumber);
            }
            else {
                UsefulFunction.fillUpMap(map, pathNumber, currentNodeNumber);
                UsefulFunction.fillUpMap(map, pathNumber, parentNodeNumber);
            }

            getAllBackPaths_recursion(parentNode);
        }
    }

    private boolean nodeParentsHaveManyParents(DekstraNode parentNode)
    {
        List<Integer> parentNodeParents = parentNode.getParents();

        if (parentNodeParents.size() <= 0) {
            return false;
        }

        if (parentNodeParents.size() == 1) {
            int parentNodeIndex = parentNodeParents.get(0);
            DekstraNode parentNode_parent = Graph.getNodeByNumber(parentNodeIndex);
            nodeParentsHaveManyParents(parentNode_parent);
        }

        if (parentNodeParents.size() > 1) {
            return true;
        }

        return false;
    }

    private DekstraNode cleanNodesWithSingleOrLessParentsFromPaths(DekstraNode nextNodeWithManyParents, List<Integer> listOfMap)
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

    private DekstraNode getNextNodeWithManyParents(DekstraNode currentNode, List<Integer> listInMap, List<Integer> alreadyUsedNodesNumbers)
    {
        if(alreadyUsedNodesNumbers == null || alreadyUsedNodesNumbers.isEmpty()) {
            alreadyUsedNodesNumbers = new ArrayList<>();
        }

        if (listInMap != null){
            if(listInMap.contains(currentNode.getNumber())) {
                return goThroughNextNodesBy(currentNode, listInMap, alreadyUsedNodesNumbers);
            }
        }

        return null;
    }

    private DekstraNode goThroughNextNodesBy(DekstraNode currentNode, List<Integer> listInMap, List<Integer> alreadyUsedNodesNumbers)
    {
        for (Integer nextNodeNumber : currentNode.getNextNodes()) {
            if (listInMap.contains(nextNodeNumber) && !alreadyUsedNodesNumbers.contains(nextNodeNumber)) {

                DekstraNode nextNode = Graph.getNodeByNumber(nextNodeNumber);

                if(currentNode.equals(nextNode)) continue;

                if (nextNode.getParents().size() > 1) {
                    return nextNode;
                }
                else {
                    alreadyUsedNodesNumbers.add(currentNode.getNumber());
                    return goThroughNextNodesBy(nextNode, listInMap, alreadyUsedNodesNumbers);
                }
            }
        }

        return null;
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
            if (pathNumber == null) break;
            listOfUsedPathNumbers.add(pathNumber);
            //----------------------------------

            threads.add(new DekstraBackPathsFinderThread_2(pathNumber, parentNode));
            new DekstraBackPathsFinderThread_2_TEST(pathNumber, parentNode).CALL();
        }

        try {
            for (DekstraBackPathsFinderThread_2 thread : threads) {
                futures.add(service.submit(thread));
            }
            for (Future<Integer> future : futures) {
                future.get();
            }
        }
        finally {
            service.shutdown();
        }

        System.out.println("MultiThreads. Inside function spent time = " + Timer.stop());
    }


}
