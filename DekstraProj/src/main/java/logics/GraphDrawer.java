package logics;

import constants.Constants;
import functions.UsefulFunction;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import objects.DekstraNode;
import objects.Graph;

import java.util.List;
import java.util.Random;

public class GraphDrawer
{
    private static GraphicsContext gc;
    private static Graph graph;
    private static Canvas canvas;
    private static Pane pane;
    private static double spacingInRow;
    private static double spacingInColumn;

    private static boolean[][] cellsOnCanvas = new boolean[Constants.NODE_AMOUNT_IN_ROW][Constants.NODE_AMOUNT_IN_COLUMN];

    private static void init(Canvas canvas, GraphicsContext context, Pane pane, Graph graph)
    {
        GraphDrawer.gc = context;
        GraphDrawer.graph = graph;
        GraphDrawer.canvas = canvas;
        GraphDrawer.pane = pane;

        //instead of WEIGHT value can be used HEIGHT. It doesn't matter.
        GraphDrawer.spacingInRow = ((Constants.SCREEN_WEIGHT * Constants.CANVAS_WEIGHT_IN_PERCENT) - (Constants.NODE_AMOUNT_IN_ROW * Constants.NODE_RADIUS)) / Constants.SPACING_AMOUNT_IN_ROW;
        GraphDrawer.spacingInColumn = ((Constants.SCREEN_HEIGHT * Constants.CANVAS_HEIGHT_IN_PERCENT) - (Constants.NODE_AMOUNT_IN_COLUMN * Constants.NODE_RADIUS)) / Constants.SPACING_AMOUNT_IN_COLUMN;


    }

//    public static void drawGraphEdges(Canvas canvas, GraphicsContext gc, Graph graph)
//    {
//        init(canvas, gc, graph);
//
//        //region Nodes drawing
//        gc.setFill(Constants.NODE_COLOR);
//        for (DekstraNode node : graph.Nodes()){
//            //region Random indexes getting
//            int randomRowIndex = getRandomIndex(0, Constants.NODE_AMOUNT_IN_ROW - 1); //[-1] because ,for example, nodes amount in row equals 7, but array starts from 0 index and last index of array = (7 - 1)
//            int randomColumnIndex = getRandomIndex(0, Constants.NODE_AMOUNT_IN_COLUMN - 1);
//
//            while(theRandomCellHasBeenOccupiedOnCanvas(randomRowIndex, randomColumnIndex)){
//                randomRowIndex = getRandomIndex(0, Constants.NODE_AMOUNT_IN_ROW - 1);
//                randomColumnIndex = getRandomIndex(0, Constants.NODE_AMOUNT_IN_COLUMN - 1);
//            }
//
//            cellsOnCanvas[randomRowIndex][randomColumnIndex] = true;
//            //endregion
//
//            double coordX = (randomRowIndex * spacingInRow) + (randomRowIndex * Constants.NODE_RADIUS) + spacingInRow;
//            double coordY = (randomColumnIndex * spacingInColumn) + (randomColumnIndex * Constants.NODE_RADIUS) + spacingInColumn;
//
//            node.setX(coordX);
//            node.setY(coordY);
//
//            gc.fillOval(coordX, coordY, Constants.NODE_RADIUS, Constants.NODE_RADIUS);
//            //region Node number
//            Text text = new Text(String.valueOf(node.getNumber()));
//            text.setFont(Font.font(Constants.defaultFontFamily));
//            gc.fillText(text.getText(), node.getX() - Constants.NODE_NUMBER_OFFSET_ON_CANVAS, node.getY() - Constants.NODE_NUMBER_OFFSET_ON_CANVAS);
//            //endregion
//        }
//        //endregion
//
//        //region Edges drawing
//        gc.setLineWidth(Constants.EDGE_WIDTH_ON_CANVAS);
//        gc.setStroke(Constants.EDGE_COLOR);
//
//        for (DekstraNode node : graph.Nodes()){
//            List<Integer> nextNodesNumbers = node.getNextNodes();
//
//            if(nextNodesNumbers == null || nextNodesNumbers.isEmpty()) continue;
//
//            for (Integer nextNodeNumber : nextNodesNumbers){
//                DekstraNode nextNode = Graph.getNodeByNumber(nextNodeNumber);
//
//                gc.beginPath();
//                gc.moveTo(node.getX() + Constants.EDGE_OFFSET_ON_CANVAS, node.getY() + Constants.EDGE_OFFSET_ON_CANVAS);
//                gc.lineTo(nextNode.getX() + Constants.EDGE_OFFSET_ON_CANVAS, nextNode.getY() + Constants.EDGE_OFFSET_ON_CANVAS);
//                gc.stroke();
//            }
//        }
//        //endregion
//    }

    public static void drawGraphEdges(Graph graph, Pane pane)
    {
        init(null, null, pane, graph);

        //region Edges drawing
        for (DekstraNode startNode : graph.Nodes()) {
            List<Integer> nextNodesNumbers = startNode.getNextNodes();

            if (nextNodesNumbers == null || nextNodesNumbers.isEmpty()) continue;

            for (int nextNodeIndex = 0; nextNodeIndex < nextNodesNumbers.size(); nextNodeIndex++) {
                DekstraNode nextNode = Graph.getNodeByNumber(nextNodesNumbers.get(nextNodeIndex));

                double startX = startNode.getX();
                double startY = startNode.getY();
                double endX = nextNode.getX();
                double endY = nextNode.getY();

                //check for startNode equals nextNode. They are identical
                if (startNode.equals(nextNode)) {
                    //region circle settings
                    //startX = endX and startY = endY. It doesn't matter what to use
                    double circleCenterX = startX + Constants.ONE_NODE_RADIUS_CIRCLE_EDGE_ON_CANVAS - Constants.ONE_NODE_CIRCLE_EDGE_STRANGE_OFFSET;
                    double circleCenterY = startY + Constants.ONE_NODE_RADIUS_CIRCLE_EDGE_ON_CANVAS - Constants.ONE_NODE_CIRCLE_EDGE_STRANGE_OFFSET;
                    Circle circleOneNodeEdge = new Circle(circleCenterX, circleCenterY, Constants.ONE_NODE_RADIUS_CIRCLE_EDGE_ON_CANVAS, Constants.NODE_COLOR);
                    circleOneNodeEdge.setFill(Constants.CANVAS_BACKGROUND_COLOR);
                    circleOneNodeEdge.setStroke(Constants.EDGE_COLOR);
                    circleOneNodeEdge.setStrokeWidth(Constants.EDGE_WIDTH_ON_CANVAS);
                    int weight = startNode.getWeights().get(nextNodeIndex);
                    Text nodeWeight_txt = getEdgeWeightForCircleLineAsText(circleCenterX, circleCenterY, Constants.ONE_NODE_RADIUS_CIRCLE_EDGE_ON_CANVAS, weight);
                    pane.getChildren().addAll(circleOneNodeEdge, nodeWeight_txt);
                    //endregion
                }
                else if (nextNodeHasEdgeToStartNode(startNode, nextNode)) {
                    //region Polyline settings
                    double[] polylinePoints = getListPointsForPolyline(startX, startY, endX, endY);
                    Polyline polyline = new Polyline(polylinePoints);
                    polyline.setFill(Constants.CANVAS_BACKGROUND_COLOR);
                    polyline.setStroke(Constants.EDGE_COLOR);
                    polyline.setStrokeWidth(Constants.EDGE_WIDTH_ON_CANVAS);
                    int weight = startNode.getWeights().get(nextNodeIndex);
                    double biasedCoordX = polylinePoints[2];
                    double biasedCoordY = polylinePoints[3];
                    Text nodeWeight_txt = getEdgeWeightForCurveLineAsText(biasedCoordX, biasedCoordY, weight);
                    pane.getChildren().addAll(polyline, nodeWeight_txt);
                    //endregion
                }
                else {
                    //region Line settings
                    Line line = new Line(startX, startY, endX, endY);
                    line.setStrokeWidth(Constants.EDGE_WIDTH_ON_CANVAS);
                    line.setStroke(Constants.EDGE_COLOR);
                    int weight = startNode.getWeights().get(nextNodeIndex);
                    Text nodeWeight_txt = getEdgeWeightForDirectLineAsText(startX, startY, endX, endY, weight);
                    pane.getChildren().addAll(line, nodeWeight_txt);
                    //endregion
                }
            }
        }
        //endregion
    }

    private static double[] getListPointsForPolyline(double startX, double startY, double endX, double endY)
    {
        double[] polylinePoints = new double[6]; // size = 6 because polyline will have 3 point and each of these points have 2 coordinates [points(3) * coordinates(2) = 6]
        //Point 1
        polylinePoints[0] = startX;
        polylinePoints[1] = startY;

        double pointsWeight = 0, pointsHeight = 0, lineCenterX = 0, lineCenterY = 0;

        if(startX <= endX) {
            pointsWeight = endX - startX;
            lineCenterX = pointsWeight / 2 + startX;
        }
        else{
            pointsWeight = startX - endX;
            lineCenterX = pointsWeight / 2 + endX;
        }

        if(startY <= endY) {
            pointsHeight = endY - startY;
            lineCenterY = pointsHeight / 2 + startY;
        }
        else{
            pointsHeight = startY - endY;
            lineCenterY = pointsHeight / 2 + endY;
        }

        double hypotenuseLength = Math.sqrt(pointsWeight*pointsWeight + pointsHeight*pointsHeight);
        double degrees = Math.toDegrees(pointsHeight / hypotenuseLength);

        if(degrees > Constants.DEGREES_0 && degrees < Constants.DEGREES_90 || degrees > Constants.DEGREES_180 && degrees < Constants.DEGREES_270) { //[+X, +Y] OR [-X, -Y]
            lineCenterX += Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
            lineCenterY += Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
        }
        else if (degrees > Constants.DEGREES_90 && degrees < Constants.DEGREES_180 || degrees > Constants.DEGREES_270){ //[-X, +Y] OR [+X, -Y]
            lineCenterX -= Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
            lineCenterY += Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
        }
        else if(degrees == Constants.DEGREES_0 || degrees == Constants.DEGREES_180 ) { //[-Y] OR [+Y]
            lineCenterY -= Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
        }
        else if(degrees == Constants.DEGREES_90 || degrees == Constants.DEGREES_270 ) {//[+X] OR [-X]
            lineCenterY += Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
        }
        else{
            UsefulFunction.throwException("Strange situation. Program couldn't figure out degrees for a curve line.");
        }

        //Point 2
        polylinePoints[2] = lineCenterX;
        polylinePoints[3] = lineCenterY;

        //Point 3
        polylinePoints[4] = endX;
        polylinePoints[5] = endY;

        return polylinePoints;
    }

    private static boolean nextNodeHasEdgeToStartNode(DekstraNode startNode, DekstraNode nextNode)
    {
        if(nextNode.getNextNodes().contains(startNode.getNumber()) && !nextNode.madeCurveLine()){
            startNode.setMadeCurveLine(true);
            return true;
        }

        return false;
    }

    private static Text getEdgeWeightForCircleLineAsText(double circleCenterX, double circleCenterY, double radius, int weight)
    {
        //region Get weight text coordinates (X,Y)
        double textX = 0, textY = 0;

        textX = circleCenterX + radius - Constants.ONE_NODE_CIRCLE_EDGE_STRANGE_OFFSET;
        textY = circleCenterY + radius - Constants.ONE_NODE_CIRCLE_EDGE_STRANGE_OFFSET;

        //endregion

        Text text = new Text(String.valueOf(weight));
        text.setX(textX);
        text.setY(textY);
        text.setFont(Font.font(Constants.defaultFontFamily, Constants.defaultFontSize));

        return text;
    }

    private static Text getEdgeWeightForCurveLineAsText(double textX, double textY, int weight)
    {
        Text text = new Text(String.valueOf(weight));
        text.setX(textX);
        text.setY(textY);
        text.setFont(Font.font(Constants.defaultFontFamily, Constants.defaultFontSize));

        return text;
    }

    private static Text getEdgeWeightForDirectLineAsText(double startX, double startY, double endX, double endY, int weight)
    {
        //region Get weight text coordinates (X,Y)
        double diffX = 0, diffY = 0, textX = 0, textY = 0;

        //set up X
        if (startX <= endX) {
            diffX = (endX - startX) / 2;
            textX = startX + diffX;
        }
        else {
            diffX = (startX - endX) / 2;
            textX = endX + diffX;
        }

        //set up Y
        if (startY <= endY) {
            diffY = (endY - startY) / 2;
            textY = startY + diffY;
        }
        else {
            diffY = (startY - endY) / 2;
            textY = endY + diffY;
        }
        //endregion

        Text text = new Text(String.valueOf(weight));
        text.setX(textX);
        text.setY(textY);
        text.setFont(Font.font(Constants.defaultFontFamily, Constants.defaultFontSize));

        return text;
    }

    private static int getRandomIndex(int from, int to)
    {
        int diff = to - from;
        int randomIndex = new Random().nextInt(diff + 1);
        randomIndex += from;
        return randomIndex;
    }

    private static boolean theRandomCellHasBeenOccupiedOnCanvas(int randomRowIndex, int randomColumnIndex)
    {
        if (cellsOnCanvas[randomRowIndex][randomColumnIndex]) {
            return true;
        }

        return false;
    }

}
