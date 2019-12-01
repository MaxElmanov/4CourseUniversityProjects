package launcher;

import logics.DekstraAlgorithm;
import objects.DekstraNode;
import objects.Graph;
import objects.Node;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Launcher
{
    public static void main(String[] args) throws ExecutionException, InterruptedException
    {
        Graph graph = new Graph();
//        graph.add(new DekstraNode(new Node(1, Arrays.asList(2, 3),       Arrays.asList(2, 4))));
//        graph.add(new DekstraNode(new Node(2, Arrays.asList(4, 5),       Arrays.asList(5, 2))));
//        graph.add(new DekstraNode(new Node(3, Arrays.asList(4, 6),       Arrays.asList(3, 1))));
//        graph.add(new DekstraNode(new Node(4, Arrays.asList(2, 3, 5, 6), Arrays.asList(5, 3, 3, 2))));
//        graph.add(new DekstraNode(new Node(5, Arrays.asList(2, 4, 6),    Arrays.asList(2, 3, 1))));
//        graph.add(new DekstraNode(new Node(6, Arrays.asList(3, 4, 5),    Arrays.asList(1, 2, 1))));
        //---------------------------------------------------------------------------------------------------------------------------------------
        //1
        graph.add(new DekstraNode(new Node(1,  Arrays.asList(2, 3, 4, 14, 15),   Arrays.asList(1, 1, 1, 3, 3))));
        graph.add(new DekstraNode(new Node(2,  Arrays.asList(5, 10),   Arrays.asList(1, 4))));
        graph.add(new DekstraNode(new Node(3,  Arrays.asList(5),   Arrays.asList(1))));
        graph.add(new DekstraNode(new Node(4,  Arrays.asList(5),   Arrays.asList(1))));
        graph.add(new DekstraNode(new Node(5,  Arrays.asList(6, 7, 8),   Arrays.asList(1, 1, 1))));
        graph.add(new DekstraNode(new Node(6,  Arrays.asList(9),   Arrays.asList(1))));
        graph.add(new DekstraNode(new Node(7,  Arrays.asList(9),   Arrays.asList(1))));
        graph.add(new DekstraNode(new Node(8,  Arrays.asList(9),   Arrays.asList(1))));
        graph.add(new DekstraNode(new Node(9,  Arrays.asList(10, 11, 12),   Arrays.asList(1, 1, 1))));
        graph.add(new DekstraNode(new Node(10,  Arrays.asList(13),   Arrays.asList(1))));
        graph.add(new DekstraNode(new Node(11,  Arrays.asList(13),   Arrays.asList(1))));
        graph.add(new DekstraNode(new Node(12,  Arrays.asList(13),   Arrays.asList(1))));
        graph.add(new DekstraNode(new Node(13,  null,   null)));
        graph.add(new DekstraNode(new Node(14,  Arrays.asList(13),   Arrays.asList(3))));
        graph.add(new DekstraNode(new Node(15,  Arrays.asList(13),   Arrays.asList(3))));

        DekstraAlgorithm algorithm = new DekstraAlgorithm(graph);
        algorithm.DO(1, 13);
//        System.out.println(graph);
    }
}
