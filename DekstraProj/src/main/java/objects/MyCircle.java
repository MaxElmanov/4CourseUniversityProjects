package objects;

import constants.Constants;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MyCircle extends Circle
{
    private Graph graph;

    public MyCircle(double centerX, double centerY, double radius, Color nodeColor, Graph graph)
    {
        super(centerX, centerY, radius);
        this.graph = graph;
        this.setCursor(Cursor.HAND);
        this.setFill(nodeColor);
    }

    public Text getUnusedNodeNumberAsText(double x, double y)
    {
        Text text = new Text();

        for (DekstraNode node : graph.Nodes()){
            if(!node.isSetUpOnCanvas()) {
                node.setUpOnCanvas(true);
                node.setX(x);
                node.setY(y);

                text.setX(x - Constants.NODE_NUMBER_OFFSET_ON_CANVAS);
                text.setY(y - Constants.NODE_NUMBER_OFFSET_ON_CANVAS);
                text.setFont(Font.font(Constants.defaultFontFamily, Constants.bigFontSize));
                text.setText(String.valueOf(node.getNumber()));
                break;
            }
        }

        return text;
    }
}
