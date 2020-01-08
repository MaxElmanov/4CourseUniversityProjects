package logics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import objects.Graph;

public class GraphDrawer
{
    private static GraphicsContext gc;
    private static Graph graph;

    private static void init(GraphicsContext context, Graph graph)
    {
        GraphDrawer.gc = context;
        GraphDrawer.graph = graph;
    }

    public static void drawGraph(GraphicsContext gc, Graph graph)
    {
        init(gc, graph);

        gc.setStroke(Color.FORESTGREEN.brighter());
        gc.setLineWidth(5);
        gc.strokeOval(30, 30, 80, 80);
        gc.setFill(Color.FORESTGREEN);
        gc.fillOval(130, 30, 80, 80);
    }


}
