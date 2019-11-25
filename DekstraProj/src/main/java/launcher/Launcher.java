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
        graph.add(new DekstraNode(new Node(1, Arrays.asList(2, 3),       Arrays.asList(2, 4))));
        graph.add(new DekstraNode(new Node(2, Arrays.asList(4, 5),       Arrays.asList(5, 2))));
        graph.add(new DekstraNode(new Node(3, Arrays.asList(4, 6),       Arrays.asList(3, 1))));
        graph.add(new DekstraNode(new Node(4, Arrays.asList(2, 3, 5, 6), Arrays.asList(5, 3, 3, 2))));
        graph.add(new DekstraNode(new Node(5, Arrays.asList(2, 4, 6),    Arrays.asList(2, 3, 1))));
        graph.add(new DekstraNode(new Node(6, Arrays.asList(3, 4, 5),    Arrays.asList(1, 2, 1))));

        DekstraAlgorithm algorithm = new DekstraAlgorithm(graph);
        algorithm.DO(1, 4);
//        System.out.println(graph);
    }
}
