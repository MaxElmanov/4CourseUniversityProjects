package objects;

import javafx.scene.Cursor;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import logics.GraphDrawer;

import javax.swing.*;
import java.util.List;

public class TextNodeNumberOnCanvas extends Text
{
    private DekstraNode startNode;
    private Graph graph;
    private Graph tempGraph;
    private Pane canvas;
    private static GridPane grid;
    private int nextNodeIndex;

    public TextNodeNumberOnCanvas(DekstraNode startNode, Graph graph, Graph tempGraph, Pane canvas, GridPane grid, int nextNodeIndex)
    {
        super();
        this.startNode = startNode;
        this.graph = graph;
        this.tempGraph = tempGraph;
        this.canvas = canvas;
        this.grid = grid;
        this.nextNodeIndex = nextNodeIndex;
        initThis();
        setUpSettings();
    }

    private void initThis()
    {
        this.setCursor(Cursor.HAND);
    }

    private void setUpSettings()
    {
        this.setOnMouseClicked(event -> {
            getUserNewWeight();
        });
    }

    private void getUserNewWeight()
    {
        try
        {
            List<Integer> nextNodesWeights = startNode.getWeights();

            String newWeightString = JOptionPane.showInputDialog(null, "New weight", String.valueOf(nextNodesWeights.get(nextNodeIndex)));
            int newWeightInt = Integer.parseUnsignedInt(newWeightString);

            nextNodesWeights.set(nextNodeIndex, newWeightInt);

            GraphDrawer.clearGraphEdges(canvas, null);
            GraphDrawer.drawGraphEdges(graph, tempGraph, canvas, grid);
        }
        catch (NumberFormatException exp)
        {
            getUserNewWeight();
        }
    }
}
