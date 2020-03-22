package logics;

import constants.Constants;
import functions.UsefulFunction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import objects.DekstraNode;
import objects.Graph;

import java.util.Arrays;
import java.util.List;

public class GraphDrawer
{
    private static Graph graph;
    private static GridPane grid;
    private static Pane canvas;

    private static void init(Pane canvas, Graph graph, GridPane grid)
    {
        GraphDrawer.graph = graph;
        GraphDrawer.canvas = canvas;
        GraphDrawer.grid = grid;
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

    public static void drawGraphEdges(Graph graph, Pane canvas, GridPane grid)
    {
        init(canvas, graph, grid);

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
                    circleOneNodeEdge.setId(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID);
                    circleOneNodeEdge.setFill(Constants.CANVAS_BACKGROUND_COLOR);
                    circleOneNodeEdge.setStroke(Constants.EDGE_COLOR);
                    circleOneNodeEdge.setStrokeWidth(Constants.EDGE_WIDTH_ON_CANVAS);
                    int weight = startNode.getWeights().get(nextNodeIndex);
                    Text nodeWeight_txt = getEdgeWeightForCircleLineAsText(circleCenterX, circleCenterY, Constants.ONE_NODE_RADIUS_CIRCLE_EDGE_ON_CANVAS, weight);
                    nodeWeight_txt.setId(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID);
                    //Polyline lineTip = getTipPolyline(startX, startY, endX, endY);
                    canvas.getChildren().addAll(circleOneNodeEdge, nodeWeight_txt);
                    //endregion
                }
                else if (nextNodeHasEdgeToStartNode(startNode, nextNode)) {
                    //region Polyline settings
                    double[] polylinePoints = getListPointsForPolyline(startX, startY, endX, endY);
                    Polyline polyline = new Polyline(polylinePoints);
                    polyline.setId(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID);
                    polyline.setFill(Constants.CANVAS_BACKGROUND_COLOR);
                    polyline.setStroke(Constants.EDGE_COLOR);
                    polyline.setStrokeWidth(Constants.EDGE_WIDTH_ON_CANVAS);
                    int weight = startNode.getWeights().get(nextNodeIndex);
                    double biasedCoordX = polylinePoints[2];
                    double biasedCoordY = polylinePoints[3];
                    Text nodeWeight_txt = getEdgeWeightForCurveLineAsText(biasedCoordX, biasedCoordY, weight);
                    nodeWeight_txt.setId(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID);
                    Polyline lineTip = getTipPolyline(startX, startY, endX, endY);
                    lineTip.setId(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID);
                    canvas.getChildren().addAll(polyline, nodeWeight_txt, lineTip);
                    //endregion
                }
                else {
                    //region Line settings
                    Line line = new Line(startX, startY, endX, endY);
                    line.setId(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID);
                    line.setStrokeWidth(Constants.EDGE_WIDTH_ON_CANVAS);
                    line.setStroke(Constants.EDGE_COLOR);
                    int weight = startNode.getWeights().get(nextNodeIndex);
                    Text nodeWeight_txt = getEdgeWeightForDirectLineAsText(startX, startY, endX, endY, weight);
                    nodeWeight_txt.setId(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID);
                    //Tip getting
                    Polyline lineTip = getTipPolyline(startX, startY, endX, endY);
                    lineTip.setId(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID);
                    canvas.getChildren().addAll(line, nodeWeight_txt, lineTip);
                    //endregion
                }
            }
        }
        //endregion
    }

    private static Polyline getTipPolyline(double startX, double startY, double endX, double endY)
    {
        double leftTipX = 0, leftTipY = 0, rightTipX = 0, rightTipY = 0, newEndX = 0, newEndY = 0;

        double pointsHeight = 0, pointsWeight = 0, lineCenterX = 0, lineCenterY = 0;
        //region Height and weight getting and line coordinates center
        if (startX <= endX) {
            pointsWeight = endX - startX;
            lineCenterX = pointsWeight / 2 + startX;
        }
        else {
            pointsWeight = startX - endX;
            lineCenterX = pointsWeight / 2 + endX;
        }

        if (startY <= endY) {
            pointsHeight = endY - startY;
            lineCenterY = pointsHeight / 2 + startY;
        }
        else {
            pointsHeight = startY - endY;
            lineCenterY = pointsHeight / 2 + endY;
        }
        //endregion

        if(startX == 0 && startY == 0 && endX == 0 && endY == 0) {
            int a = 5;
        }

        double degree = getAngleDirectionToCenterInDegrees(startX, startY, endX, endY);

        //Calculate direction. Get what side arrow will point? BOTTOM and RIGHT is not necessary because ,for example, if UP is true then BOTTOM is false, etc
        //boolean isUp = isDirectUp(startY, endY);
        //boolean isLeft = isDirectLeft(startX, endX);

        //region Arrow left and right tips coordinates getting
        if (degree == Constants.DEGREES_0 || degree == Constants.DEGREES_360) {
            //region Horizontal direct 0
            newEndX = endX + Constants.NODE_RADIUS;
            newEndY = endY;
            leftTipX = newEndX + Constants.DISTANCE_FROM_LINE_ARROW_TIP;
            leftTipY = newEndY + Constants.SIDE_DISTANCE_FROM_LINE;
            rightTipX = leftTipX;
            rightTipY = newEndY - Constants.SIDE_DISTANCE_FROM_LINE;
            //endregion
        }
        else if (degree == Constants.DEGREES_90) { // vertical direct 90
            //region Vertical direct 90
            newEndX = endX;
            newEndY = endY - Constants.NODE_RADIUS;
            leftTipX = newEndX - Constants.SIDE_DISTANCE_FROM_LINE;
            leftTipY = newEndY + Constants.DISTANCE_FROM_LINE_ARROW_TIP;
            rightTipX = newEndX + Constants.SIDE_DISTANCE_FROM_LINE;
            rightTipY = leftTipY;
            //endregion
        }
        else if (degree == Constants.DEGREES_180) { // horizontal direct 180
            //region Horizontal direct 180
            newEndX = endX - Constants.NODE_RADIUS;
            newEndY = endY;
            leftTipX = newEndX - Constants.DISTANCE_FROM_LINE_ARROW_TIP;
            leftTipY = newEndY - Constants.SIDE_DISTANCE_FROM_LINE;
            rightTipX = leftTipX;
            rightTipY = newEndY + Constants.SIDE_DISTANCE_FROM_LINE;
            //endregion
        }
        else if (degree == Constants.DEGREES_270) { // vertical direct 270
            //region Vertical direct 270
            newEndX = endX;
            newEndY = endY + Constants.NODE_RADIUS;
            leftTipX = newEndX - Constants.SIDE_DISTANCE_FROM_LINE;
            leftTipY = newEndY - Constants.DISTANCE_FROM_LINE_ARROW_TIP;
            rightTipX = newEndX + Constants.SIDE_DISTANCE_FROM_LINE;
            rightTipY = leftTipY;
            //endregion
        }
        else if (degree > Constants.DEGREES_0 && degree < Constants.DEGREES_90) {
            //region 0-90
            double radiusOffsetX = Constants.NODE_RADIUS * Math.cos(Math.toRadians(degree));
            newEndX = endX + radiusOffsetX;
            double radiusOffsetY = Constants.NODE_RADIUS * Math.sin(Math.toRadians(degree));
            newEndY = endY - radiusOffsetY;
            //arrowHypotenuse is identical for arrow left and right sides
            double arrowHypotenuse = Math.sqrt(Math.pow(Constants.DISTANCE_FROM_LINE_ARROW_TIP, 2) + Math.pow(Constants.SIDE_DISTANCE_FROM_LINE, 2));
            leftTipX = newEndX + arrowHypotenuse;
            leftTipY = newEndY;
            rightTipX = newEndX;
            rightTipY = newEndY - arrowHypotenuse;

            //LEFT
            double leftTipOffsetY = Math.sin(Math.toRadians(Constants.DEGREES_45_ARROW_TIP_ANGLE - degree)) * arrowHypotenuse;
            leftTipY += leftTipOffsetY;
            double leftTipOffsetX = (leftTipX - newEndX) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(leftTipOffsetY, 2)));
            leftTipX -= leftTipOffsetX;
            //RIGHT
            double rightTipOffsetX = Math.sin(Math.toRadians(Constants.DEGREES_90 - Constants.DEGREES_45_ARROW_TIP_ANGLE - degree)) * arrowHypotenuse;
            rightTipX += rightTipOffsetX;
            double rightTipOffsetY = (newEndY - rightTipY) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(rightTipOffsetX, 2)));
            rightTipY += rightTipOffsetY;
            //endregion
        }
        else if (degree > Constants.DEGREES_90 && degree < Constants.DEGREES_180) {
            //region 90-180
            double newDegrees = Constants.DEGREES_180 - degree;
            newEndX = endX - Constants.NODE_RADIUS * Math.cos(Math.toRadians(newDegrees));
            newEndY = endY - Constants.NODE_RADIUS * Math.sin(Math.toRadians(newDegrees));
            //arrowHypotenuse is identical for left and right sides
            double arrowHypotenuse = Math.sqrt(Math.pow(Constants.DISTANCE_FROM_LINE_ARROW_TIP, 2) + Math.pow(Constants.SIDE_DISTANCE_FROM_LINE, 2));
            leftTipX = newEndX;
            leftTipY = newEndY - arrowHypotenuse;
            rightTipX = newEndX - arrowHypotenuse;
            rightTipY = newEndY;

            double quarterDegree = degree - Constants.DEGREES_90;
            //LEFT
            double leftTipOffsetX = Math.sin(Math.toRadians(quarterDegree - Constants.DEGREES_45_ARROW_TIP_ANGLE)) * arrowHypotenuse;
            leftTipX -= leftTipOffsetX;
            double leftTipOffsetY = (newEndY - leftTipY) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(leftTipOffsetX, 2)));
            leftTipY += leftTipOffsetY;
            //RIGHT
            if(quarterDegree < 45) {
                double rightTipOffsetY = Math.sin(Math.toRadians(Constants.DEGREES_90 - quarterDegree - Constants.DEGREES_45_ARROW_TIP_ANGLE)) * arrowHypotenuse;
                rightTipY -= rightTipOffsetY;
                double rightTipOffsetX = (newEndX - rightTipX) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(rightTipOffsetY, 2)));
                rightTipX += rightTipOffsetX;
            }
            //quarterDegree >= 45
            else{
                double rightTipOffsetY = Math.sin(Math.toRadians(Constants.DEGREES_45_ARROW_TIP_ANGLE - (Constants.DEGREES_90 - quarterDegree))) * arrowHypotenuse;
                rightTipY += rightTipOffsetY;
                double rightTipOffsetX = (newEndX - rightTipX) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(rightTipOffsetY, 2)));
                rightTipX += rightTipOffsetX;
            }
            //endregion
        }
        else if (degree > Constants.DEGREES_180 && degree < Constants.DEGREES_270) {
            //region 180-270
            double newDegrees = degree - Constants.DEGREES_180;
            newEndX = endX - Constants.NODE_RADIUS * Math.cos(Math.toRadians(newDegrees));
            newEndY = endY + Constants.NODE_RADIUS * Math.sin(Math.toRadians(newDegrees));
            //arrowHypotenuse is identical for left and right sides
            double arrowHypotenuse = Math.sqrt(Math.pow(Constants.DISTANCE_FROM_LINE_ARROW_TIP, 2) + Math.pow(Constants.SIDE_DISTANCE_FROM_LINE, 2));
            leftTipX = newEndX - arrowHypotenuse;
            leftTipY = newEndY;
            rightTipX = newEndX;
            rightTipY = newEndY + arrowHypotenuse;

            double quarterDegree = degree - Constants.DEGREES_180;
            if(quarterDegree < 45) {
                //LEFT
                double leftTipOffsetY = Math.sin(Math.toRadians(Constants.DEGREES_45_ARROW_TIP_ANGLE - quarterDegree)) * arrowHypotenuse;
                leftTipY -= leftTipOffsetY;
                double leftTipOffsetX = (newEndX - leftTipX) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(leftTipOffsetY, 2)));
                leftTipX += leftTipOffsetX;
                //RIGHT
                double rightTipOffsetX = Math.sin(Math.toRadians(Constants.DEGREES_90 - (quarterDegree + Constants.DEGREES_45_ARROW_TIP_ANGLE))) * arrowHypotenuse;
                rightTipX -= rightTipOffsetX;
                double rightTipOffsetY = (rightTipY - newEndY) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(rightTipOffsetX, 2)));;
                rightTipY -= rightTipOffsetY;
            }
            //quarterDegree >= 45
            else{
                //LEFT
                double leftTipOffsetY = Math.sin(Math.toRadians(quarterDegree - Constants.DEGREES_45_ARROW_TIP_ANGLE)) * arrowHypotenuse;
                leftTipY += leftTipOffsetY;
                double leftTipOffsetX = (newEndX - leftTipX) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(leftTipOffsetY, 2)));
                leftTipX += leftTipOffsetX;
                //RIGHT
                double rightTipOffsetX = Math.sin(Math.toRadians(Constants.DEGREES_45_ARROW_TIP_ANGLE - (Constants.DEGREES_90 - quarterDegree))) * arrowHypotenuse;
                rightTipX += rightTipOffsetX;
                double rightTipOffsetY = (rightTipY - newEndY) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(rightTipOffsetX, 2)));;
                rightTipY -= rightTipOffsetY;
            }
            //endregion
        }
        else if (degree > Constants.DEGREES_270 && degree < Constants.DEGREES_360) { //
            //region 270-360
            double newDegrees = Constants.DEGREES_360 - degree;
            newEndX = endX + Constants.NODE_RADIUS * Math.cos(Math.toRadians(newDegrees));
            newEndY = endY + Constants.NODE_RADIUS * Math.sin(Math.toRadians(newDegrees));
            //arrowHypotenuse is identical for left and right sides
            double arrowHypotenuse = Math.sqrt(Math.pow(Constants.DISTANCE_FROM_LINE_ARROW_TIP, 2) + Math.pow(Constants.SIDE_DISTANCE_FROM_LINE, 2));
            leftTipX = newEndX;
            leftTipY = newEndY + arrowHypotenuse;
            rightTipX = newEndX + arrowHypotenuse;
            rightTipY = newEndY;

            double quarterDegree = degree - Constants.DEGREES_270;
            if(quarterDegree < 45) {
                //LEFT
                double leftTipOffsetX = Math.sin(Math.toRadians(Constants.DEGREES_45_ARROW_TIP_ANGLE - quarterDegree)) * arrowHypotenuse;
                leftTipX -= leftTipOffsetX;
                double leftTipOffsetY = (leftTipY - newEndY) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(leftTipOffsetX, 2)));
                leftTipY -= leftTipOffsetY;
                //RIGHT
                double rightTipOffsetY = Math.sin(Math.toRadians(Constants.DEGREES_90 - (quarterDegree + Constants.DEGREES_45_ARROW_TIP_ANGLE))) * arrowHypotenuse;
                rightTipY += rightTipOffsetY;
                double rightTipOffsetX = (rightTipX - newEndX) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(rightTipOffsetY, 2)));
                rightTipX -= rightTipOffsetX;
            }
            //quarterDegree >= 45
            else{
                //LEFT
                double leftTipOffsetX = Math.sin(Math.toRadians(quarterDegree - Constants.DEGREES_45_ARROW_TIP_ANGLE)) * arrowHypotenuse;
                leftTipX += leftTipOffsetX;
                double leftTipOffsetY = (leftTipY - newEndY) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(leftTipOffsetX, 2)));
                leftTipY -= leftTipOffsetY;
                //RIGHT
                double rightTipOffsetY = Math.sin(Math.toRadians(Constants.DEGREES_45_ARROW_TIP_ANGLE - (Constants.DEGREES_90 - quarterDegree))) * arrowHypotenuse;
                rightTipY -= rightTipOffsetY;
                double rightTipOffsetX = (rightTipX - newEndX) - (Math.sqrt(Math.pow(arrowHypotenuse, 2) - Math.pow(rightTipOffsetY, 2)));
                rightTipX -= rightTipOffsetX;
            }
            //endregion
        }
        else {
            UsefulFunction.throwException("Strange situation. Program couldn't figure out degree for a curve line.");
        }
        //endregion

        Polyline lineTipPoints = new Polyline(leftTipX, leftTipY, newEndX, newEndY, rightTipX, rightTipY);
        lineTipPoints.setStrokeWidth(Constants.EDGE_WIDTH_ON_CANVAS);
        lineTipPoints.setFill(Constants.CANVAS_BACKGROUND_COLOR);
        lineTipPoints.setStroke(Constants.EDGE_ARROW_TIP_COLOR);
        return lineTipPoints;
    }

    private static boolean isDirectUp(double startY, double endY)
    {
        if (endY < startY) {
            return true;
        }
        else {
            return false;
        }
    }

    private static boolean isDirectLeft(double startX, double endX)
    {
        if (endX < startX) {
            return true;
        }
        else {
            return false;
        }
    }

    private static double[] getListPointsForPolyline(double startX, double startY, double endX, double endY)
    {
        double[] polylinePoints = new double[6]; // size = 6 because polyline will have 3 point and each of these points have 2 coordinates [points(3) * coordinates(2) = 6]
        //Point 1
        polylinePoints[0] = startX;
        polylinePoints[1] = startY;


        double pointsWeight = 0, pointsHeight = 0, lineCenterX = 0, lineCenterY = 0;

        //region Height and weight getting and Line center
        if (startX <= endX) {
            pointsWeight = endX - startX;
            lineCenterX = pointsWeight / 2 + startX;
        }
        else {
            pointsWeight = startX - endX;
            lineCenterX = pointsWeight / 2 + endX;
        }

        if (startY <= endY) {
            pointsHeight = endY - startY;
            lineCenterY = pointsHeight / 2 + startY;
        }
        else {
            pointsHeight = startY - endY;
            lineCenterY = pointsHeight / 2 + endY;
        }
        //endregion

        //double hypotenuseLength = Math.sqrt(pointsWeight*pointsWeight + pointsHeight*pointsHeight);
//        double degrees = Math.toDegrees(pointsHeight / hypotenuseLength);
        double degrees = getAngleDirectionFromCenterInDegrees(startX, startY, endX, endY);

        if (degrees > Constants.DEGREES_0 && degrees < Constants.DEGREES_90 || degrees > Constants.DEGREES_180 && degrees < Constants.DEGREES_270) { //[+X, +Y] OR [-X, -Y]
            lineCenterX += Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
            lineCenterY += Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
        }
        else if (degrees > Constants.DEGREES_90 && degrees < Constants.DEGREES_180 || degrees > Constants.DEGREES_270) { //[-X, +Y] OR [+X, -Y]
            lineCenterX -= Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
            lineCenterY += Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
        }
        else if (degrees == Constants.DEGREES_0 || degrees == Constants.DEGREES_180 || degrees == Constants.DEGREES_360) { //[-Y] OR [+Y]
            lineCenterY -= Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
        }
        else if (degrees == Constants.DEGREES_90 || degrees == Constants.DEGREES_270) {//[+X] OR [-X]
            lineCenterY += Constants.CURVE_EDGE_OFFSET_ON_CANVAS;
        }
        else {
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

    /**
     * 10 20 30 40 50 60
     * 10         end point
     * 20         o
     * 30        /
     * 40      / angle
     * 50     o-------
     * 60 start point
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return angle in degrees
     */
    public static double getAngleDirectionFromCenterInDegrees(double startX, double startY, double endX, double endY)
    {
        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(endX - startX, endY - startY);

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
        theta -= Math.PI / 2;

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    /**
     * 10 20 30 40 50 60
     * 10          o start point
     * 20         /
     * 30       / angle
     * 40      o-------
     * 50    end
     * 60
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return angle in degrees
     */
    public static double getAngleDirectionToCenterInDegrees(double startX, double startY, double endX, double endY)
    {
        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(endX - startX, endY - startY);

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
        theta += Math.PI / 2;

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    private static boolean nextNodeHasEdgeToStartNode(DekstraNode startNode, DekstraNode nextNode)
    {
        List<Integer> nextNodesNumbers = nextNode.getNextNodes();

        if (nextNodesNumbers == null || nextNodesNumbers.isEmpty()) return false;

        if (nextNodesNumbers.contains(startNode.getNumber()) && !nextNode.madeCurveLine()) {
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

    public static void main(String[] args)
    {
        double degrees = getAngleDirectionFromCenterInDegrees(30, 30, 50, 30);
        double degrees2 = getAngleDirectionFromCenterInDegrees(30, 30, 50, 10);
        double degrees3 = getAngleDirectionFromCenterInDegrees(30, 30, 30, 10);
        double degrees4 = getAngleDirectionFromCenterInDegrees(30, 30, 10, 10);
        double degrees5 = getAngleDirectionFromCenterInDegrees(30, 30, 10, 30);
        double degrees6 = getAngleDirectionFromCenterInDegrees(30, 30, 10, 50);
        double degrees7 = getAngleDirectionFromCenterInDegrees(30, 30, 30, 50);
        double degrees8 = getAngleDirectionFromCenterInDegrees(30, 30, 50, 50);

        System.out.println(degrees);
        System.out.println(degrees2);
        System.out.println(degrees3);
        System.out.println(degrees4);
        System.out.println(degrees5);
        System.out.println(degrees6);
        System.out.println(degrees7);
        System.out.println(degrees8);

        System.out.println();

        double degrees9 = getAngleDirectionToCenterInDegrees(50, 30, 30, 30);
        double degrees10 = getAngleDirectionToCenterInDegrees(50, 10, 30, 30);
        double degrees11 = getAngleDirectionToCenterInDegrees(30, 10, 30, 30);
        double degrees12 = getAngleDirectionToCenterInDegrees(10, 10, 30, 30);
        double degrees13 = getAngleDirectionToCenterInDegrees(10, 30, 30, 30);
        double degrees14 = getAngleDirectionToCenterInDegrees(10, 50, 30, 30);
        double degrees15 = getAngleDirectionToCenterInDegrees(30, 50, 30, 30);
        double degrees16 = getAngleDirectionToCenterInDegrees(50, 50, 30, 30);

        System.out.println(degrees9);
        System.out.println(degrees10);
        System.out.println(degrees11);
        System.out.println(degrees12);
        System.out.println(degrees13);
        System.out.println(degrees14);
        System.out.println(degrees15);
        System.out.println(degrees16);
    }

    public static void clearGraphEdges(Pane canvas, int nodeNumber)
    {
        clearCanvasObjectsWithID(Constants.EDGES_OBJECT_POLY_LINE_TEXT_CIRCLE_ID, Constants.NODE_NUMBER + nodeNumber);
    }

    private static void clearCanvasObjectsWithID(String... IDsToRemoveInArray)
    {
        if (canvas == null) return;
        if (canvas.getChildren() == null) return;
        if (canvas.getChildren().isEmpty()) return;

        ObservableList<Node> gridNodesList = canvas.getChildren();
        //convert array to list
        List<String> IDsToRemove = Arrays.asList(IDsToRemoveInArray);

        if(IDsToRemove == null || IDsToRemove.isEmpty()) return;

        //region Clear UI objects
        boolean nodeIdMustBeRemoved = false;
        ObservableList<Node> newGridNodesList = FXCollections.observableArrayList();
        for (Node node : gridNodesList) {
            String node_ID = node.getId();

            for (String idToRemove : IDsToRemove){
                if (idToRemove.equalsIgnoreCase(node_ID)) {
                    nodeIdMustBeRemoved = true;
                    break;
                }
            }

            if(nodeIdMustBeRemoved == false) {
                newGridNodesList.add(node);
            }

            nodeIdMustBeRemoved = false;
        }

        gridNodesList.clear();

        for (Node savedNode : newGridNodesList) {
            gridNodesList.add(savedNode);
        }
        //endregion
    }
}
