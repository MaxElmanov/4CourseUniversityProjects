package objects;

import constants.Constants;
import commonUsefulFunctions.UsefulFunction;
import javafx.scene.Cursor;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logics.GraphDrawer;

public class MyCircleNode extends Circle
{
    private Graph graph;
    private GridPane grid;
    private Pane canvas;
    private int number;
    private boolean mouseWasPressed = false;

    public MyCircleNode(double centerX, double centerY, double radius, Color nodeColor, Graph graph, Pane canvas, GridPane grid)
    {
        super(centerX, centerY, radius);
        this.graph = graph;
        this.canvas = canvas;
        this.grid = grid;
        this.setCursor(Cursor.HAND);
        this.setFill(nodeColor);

        this.number = getUnusedNodeNumberAsInteger(centerX, centerY);
    }

    private int getUnusedNodeNumberAsInteger(double x, double y)
    {
        for (DekstraNode node : graph.Nodes()){
            if(!node.isSetUpOnCanvas()) {
                node.setUpOnCanvas(true);
                node.setX(x);
                node.setY(y);

                return node.getNumber();
            }
        }

        UsefulFunction.throwException("Error: There are not free node in graph, but class Launcher attempted to create new circle object");
        return -1;
    }

    public Text getUnusedNodeNumberAsText()
    {
        Text text = new Text();
        text.setId(Constants.NODE_NUMBER + this.number);

        text.setX(this.getCenterX() - Constants.NODE_NUMBER_OFFSET_ON_CANVAS);
        text.setY(this.getCenterY() - Constants.NODE_NUMBER_OFFSET_ON_CANVAS);
        text.setFont(Font.font(Constants.defaultFontFamily, Constants.bigFontSize));
        text.setText(String.valueOf(this.number));

        return text;
    }

    public void setUpSettings()
    {
        this.setOnMouseClicked(event -> {
            System.out.println("click");
            return;
        });

        this.setOnMousePressed(event -> {
            System.out.println("press");
            System.out.println(" number: " + this.number);
            System.out.println(" old x: " + this.getCenterX());
            System.out.println(" old y: " + this.getCenterY());
            System.out.println();

            mouseWasPressed = true;
            event.consume();
        });

//        this.setOnMouseDragged(event -> {
//            System.out.println("dragged");
//            System.out.println(" event x: " + event.getX());
//            System.out.println(" event y: " + event.getY());
//            System.out.println();
//
//            UsefulFunction.removeCanvasObjectsByID(canvas, Constants.TEMP_YELLOW_NODE_ID);
//
//            MyCircleNode tempCircleNode = new MyCircleNode(event.getX(), event.getY(), Constants.NODE_RADIUS, Constants.TEMP_NODE_COLOR, graph, canvas, grid);
//            tempCircleNode.setId(Constants.TEMP_YELLOW_NODE_ID);
//            tempCircleNode.setCenterX(event.getX());
//            tempCircleNode.setCenterY(event.getY());
//
//
//
//            Text text = this.getUnusedNodeNumberAsText();
//            canvas.getChildren().add(text);
//            event.consume();
//        });

        this.setOnMouseReleased(event -> {
            System.out.println("release");
            //if (event.isControlDown()){
                this.setCenterX(event.getX());
                this.setCenterY(event.getY());

                DekstraNode node = graph.getNodeByNumber(this.number);
                node.setX(this.getCenterX());
                node.setY(this.getCenterY());

                GraphDrawer.clearGraphEdges(canvas, this.number);
                GraphDrawer.drawGraphEdges(graph, canvas, grid);

                Text text = this.getUnusedNodeNumberAsText();
                canvas.getChildren().add(text);

                System.out.println(" new x: " + this.getCenterX());
                System.out.println(" new y: " + this.getCenterY());
                System.out.println();

                mouseWasPressed = false;
                event.consume();
            //}


        });
    }
}
