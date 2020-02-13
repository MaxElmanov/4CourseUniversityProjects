package constants;

import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.awt.*;

public class Constants
{
    public static final Integer INF = 9999999;
    public static final int MIN_GENERATED_NUMBER = 1;
    public static final int MAX_GENERATED_NUMBER = 50;
    //screen options
    private final static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    public static final int SCREEN_WEIGHT = gd.getDisplayMode().getWidth() + 25;
    public static final int SCREEN_HEIGHT = gd.getDisplayMode().getHeight();
    //scene options
    public static final float LEFTSIDE_WEIGHT_IN_PERCENT = 0.2f;
    public static final float LEFTSIDE_HEIGHT_IN_PERCENT = 0.9f;
    public static final float CANVAS_WEIGHT_IN_PERCENT = 0.8f;
    public static final float CANVAS_HEIGHT_IN_PERCENT = 0.9f;
    //UI options
    public static final String defaultFontFamily = "Arial";
    public static final Integer defaultInitialValueForSpinner = 1;
    public static final double defaultFontSize = 12;
    public static final double bigFontSize = 20;
    public static final Color WHITE_THEME_TEXT_COLOR = Color.rgb(0, 0, 0);
    public static final Color DARK_THEME_TEXT_COLOR = Color.rgb(255, 255, 255);
    public static final Color NODE_COLOR = Color.rgb(30, 144, 255);
    public static final Color EDGE_COLOR = Color.rgb(46, 139, 87);
    public static final Color EDGE_ARROW_TIP_COLOR = Color.rgb(64,128,0);
    public static final Paint CANVAS_BORDER_COLOR = Color.rgb(112, 128, 144);
    public static final Paint LABEL_BORDER_COLOR = Color.rgb(0,0,128);
    public static final BorderWidths CANVAS_BORDER_WIDTH = new BorderWidths(2);
    public static final BorderWidths LABEL_BORDER_WIDTH = new BorderWidths(1);
    public static final Paint CANVAS_BACKGROUND_COLOR = Color.TRANSPARENT;
    //Graph drawer options
    public static final int NODE_RADIUS = 10; //px
    public static final int NODE_AMOUNT_IN_ROW = 7;
    public static final int NODE_AMOUNT_IN_COLUMN = 7;
    public static final int SPACING_AMOUNT_IN_ROW = 8;
    public static final int SPACING_AMOUNT_IN_COLUMN = 8;
    public static final double EDGE_WIDTH_ON_CANVAS = 1; //px
    public static final double NODE_NUMBER_OFFSET_ON_CANVAS = 15;//px
    public static final double ONE_NODE_RADIUS_CIRCLE_EDGE_ON_CANVAS = 30;//px
    public static final double ONE_NODE_CIRCLE_EDGE_STRANGE_OFFSET = 10;//px
    public static final double DEGREES_0   = 0;
    public static final double DEGREES_90  = 90;
    public static final double DEGREES_180 = 180;
    public static final double DEGREES_270 = 270;
    public static final double DEGREES_360 = 360;
    public static final double DEGREES_45 = 45;
    public static final double DEGREES_45_ARROW_TIP_ANGLE = DEGREES_45;
    public static final double CURVE_EDGE_OFFSET_ON_CANVAS = 20;//px
    public static final double DISTANCE_FROM_LINE_ARROW_TIP = 15;//px
    public static final double SIDE_DISTANCE_FROM_LINE = 7;//px
}
