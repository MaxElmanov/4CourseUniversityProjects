package launcher;

import constants.AlertCommands;
import constants.Constants;
import files.FileExecutorForMatrixAdjacency;
import functions.UsefulFunction;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logics.DekstraAlgorithm;
import logics.GraphDrawer;
import logics.RandomGraphGenerator;
import objects.DekstraNode;
import objects.Graph;
import objects.MyCircle;
import objects.MySpinner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Launcher extends Application
{
    private static Graph graph = new Graph();
    private GridPane grid;
    private Pane canvas;

    private static List<MyCircle> circles = new ArrayList<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException
    {
//        graph.add(new DekstraNode(new Node(1,  Arrays.asList(2, 3, 4, 14, 15),   Arrays.asList(1, 1, 1, 3, 3))));
//        graph.add(new DekstraNode(new Node(2,  Arrays.asList(5, 10),   Arrays.asList(1, 4))));
//        graph.add(new DekstraNode(new Node(3,  Arrays.asList(5),   Arrays.asList(1))));
//        graph.add(new DekstraNode(new Node(4,  Arrays.asList(5),   Arrays.asList(1))));
//        graph.add(new DekstraNode(new Node(5,  Arrays.asList(6, 7, 8, 12),   Arrays.asList(1, 1, 1, 3))));
//        graph.add(new DekstraNode(new Node(6,  Arrays.asList(9),   Arrays.asList(1))));
//        graph.add(new DekstraNode(new Node(7,  Arrays.asList(9),   Arrays.asList(1))));
//        graph.add(new DekstraNode(new Node(8,  Arrays.asList(9),   Arrays.asList(1))));
//        graph.add(new DekstraNode(new Node(9,  Arrays.asList(10, 11, 12),   Arrays.asList(1, 1, 1))));
//        graph.add(new DekstraNode(new Node(10,  Arrays.asList(13),   Arrays.asList(1))));
//        graph.add(new DekstraNode(new Node(11,  Arrays.asList(13),   Arrays.asList(1))));
//        graph.add(new DekstraNode(new Node(12,  Arrays.asList(13),   Arrays.asList(1))));
//        graph.add(new DekstraNode(new Node(13,  null,   null)));
//        graph.add(new DekstraNode(new Node(14,  Arrays.asList(13),   Arrays.asList(3))));
//        graph.add(new DekstraNode(new Node(15,  Arrays.asList(13),   Arrays.asList(3))));
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //region screen settings
        primaryStage.setX(-10);
        primaryStage.setY(0);
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(300);
        primaryStage.setResizable(true);
        primaryStage.setTitle("Dekstra application");
        //endregion

        //region icon settings
        InputStream inputStream = getClass().getResourceAsStream("/icon.png");
        primaryStage.getIcons().add(new Image(inputStream));
        //endregion

        Group root = new Group();
        root.getChildren().addAll(addGridPane(primaryStage));
        Scene scene = new Scene(root, Constants.SCREEN_WEIGHT, Constants.SCREEN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public GridPane addGridPane(Stage primaryStage)
    {
        //region grid settings
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 5, 0, 5));
        //endregion

        //region menuBar
        MenuBar menuBar = new MenuBar();
        menuBar.setCursor(Cursor.HAND);
        menuBar.setMinWidth(Constants.SCREEN_WEIGHT);
        menuBar.setId("Menu_ID");

        Menu main_menu = new Menu("Main");
        Menu prepare_menu = new Menu("Preparation");

        //region Upload menuItem
        MenuItem uploadFromFile_mi = new MenuItem("Upload a graph from file");
        uploadFromFile_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/uploadButton_mi.png"))));
        uploadFromFile_mi.setOnAction(((e) -> {
            refreshWorkingAreaExceptMenuBarExcept(Arrays.asList("Menu_ID", "Canvas_ID"));

            //region FileChooser
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
            //endregion

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    //filling up graph from file
                    FileExecutorForMatrixAdjacency fileExecutor = new FileExecutorForMatrixAdjacency(file);
                    AlertCommands alertCommand = fileExecutor.fillUp(graph);
                    //region alertMap for File reading
                    Map<AlertCommands, String> alertMap = new HashMap<>();
                    alertMap.put(alertCommand.RIGHTS_RESULT, "File was successfully read.");
                    alertMap.put(alertCommand.WARNING_RESULT, "That is not good execution. Check for it.");
                    alertMap.put(alertCommand.ERROR_RESULT, "You uploaded an empty file. Check out for it.");
                    //endregion
                    boolean continueExecution = checkResultCommandForWarningAndError(alertCommand, alertMap, grid);
                    if(!continueExecution) {
                        return;
                    }

                    ObservableList<MySpinner<Integer>> spinners = getAndAddToGridRootAndTargetNodes_lbl_spinners(grid, graph.Nodes().size());

                    grid.add(addRunButton(grid, spinners), 0, 3, 1, 1);

                    grid.add(addCanvas(), 2, 1, 8, 9);
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }));
        //endregion
        //region setUp graph parameters menuItem
        MenuItem setUpParameters_mi = new MenuItem("Set up a random graph");
        setUpParameters_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/setUpParams_mi.png"))));
        setUpParameters_mi.setOnAction((e) -> {
            //region Cleaning working area except menuBar
            refreshWorkingAreaExceptMenuBarExcept(Arrays.asList("Menu_ID", "Canvas_ID"));
            clearGraphNodes();
            //endregion
            //region Spinners & Labels creation
            Label nodesAmount_lbl = createNewLabel("Nodes amount", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER, false);
            nodesAmount_lbl.setId("LeftSideObject_ID");
            MySpinner<Integer> nodesAmount_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER, Constants.MAX_GENERATED_NUMBER, Constants.defaultInitialValueForSpinner, HPos.CENTER);
            nodesAmount_spinner.setId("LeftSideObject_ID");
            Label edgesAmount_lbl = createNewLabel("Edges amount", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER, false);
            edgesAmount_lbl.setId("LeftSideObject_ID");
            MySpinner<Integer> edgesAmount_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER, Constants.MAX_GENERATED_NUMBER, Constants.defaultInitialValueForSpinner, HPos.CENTER);
            edgesAmount_spinner.setId("LeftSideObject_ID");

            Label weightsValuesRange_lbl = createNewLabel("Weights values range [1-50]", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER, false);
            weightsValuesRange_lbl.setId("LeftSideObject_ID");
            Label weightsValuesRange_FROM_lbl = createNewLabel("from", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT, false);
            weightsValuesRange_FROM_lbl.setId("LeftSideObject_ID");
            MySpinner<Integer> weightsValuesRange_FROM_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER,
                                                                                  Constants.MAX_GENERATED_NUMBER,
                                                                                  Constants.defaultInitialValueForSpinner,
                                                                                  HPos.CENTER);
            weightsValuesRange_FROM_spinner.setId("LeftSideObject_ID");
            Label weightsValuesRange_TO_lbl = createNewLabel("to", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT, false);
            MySpinner<Integer> weightsValuesRange_TO_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER,
                                                                                Constants.MAX_GENERATED_NUMBER,
                                                                                Constants.defaultInitialValueForSpinner,
                                                                                HPos.CENTER);
            weightsValuesRange_TO_spinner.setId("LeftSideObject_ID");
            //endregion
            //region generateRandomGraph_btn settings
            Button generateRandomGraph_btn = new Button("Generate");
            generateRandomGraph_btn.setId("LeftSideObject_ID");
            generateRandomGraph_btn.setMaxSize(80, 30);
            generateRandomGraph_btn.setOnAction((e_2) -> {
                refreshWorkingAreaExceptMenuBarExcept(Arrays.asList("Menu_ID", "Canvas_ID"));

                ObservableList<MySpinner<Integer>> spinners = getAndAddToGridRootAndTargetNodes_lbl_spinners(grid, nodesAmount_spinner.getCurrentValue());

                grid.add(addRunButton(grid, spinners), 0, 3, 2, 1);

                RandomGraphGenerator randomGraphGenerator = new RandomGraphGenerator(graph,
                                                                                     nodesAmount_spinner.getCurrentValue(),
                                                                                     edgesAmount_spinner.getCurrentValue(),
                                                                                     weightsValuesRange_FROM_spinner.getCurrentValue(),
                                                                                     weightsValuesRange_TO_spinner.getCurrentValue());
                AlertCommands alertCommand = randomGraphGenerator.generate();
                //region alertMap for random graph generation
                Map<AlertCommands, String> alertMap = new HashMap<>();
                alertMap.put(alertCommand.RIGHTS_RESULT, "Random graph was successfully built.");
                alertMap.put(alertCommand.WARNING_RESULT, "That is not good execution. Check for it.");
                alertMap.put(alertCommand.ERROR_RESULT, "Random graph can not be build. Check for entered parameters.");
                //endregion
                boolean continueExecution = checkResultCommandForWarningAndError(alertCommand, alertMap,  grid);
                if(!continueExecution) {
                    return;
                }

                grid.add(addCanvas(), 2, 1, 8, 9);
            });
            GridPane.setHalignment(generateRandomGraph_btn, HPos.RIGHT);
            //endregion
            //region Addition to grid
            grid.add(nodesAmount_lbl, 0, 1);
            grid.add(nodesAmount_spinner, 1, 1);
            grid.add(edgesAmount_lbl, 0, 2);
            grid.add(edgesAmount_spinner, 1, 2);

            grid.add(weightsValuesRange_lbl, 0, 3, 2, 1);
            grid.add(weightsValuesRange_FROM_lbl, 0, 4);
            grid.add(weightsValuesRange_FROM_spinner, 1, 4);
            grid.add(weightsValuesRange_TO_lbl, 0, 5);
            grid.add(weightsValuesRange_TO_spinner, 1, 5);

            grid.add(generateRandomGraph_btn, 0, 6, 2, 1);
            //endregion
        });
        //endregion
        //region exit menuItem
        MenuItem exit_mi = new MenuItem("Exit");
        exit_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/exit_mi.png"))));
        exit_mi.setOnAction((e) -> {
            System.exit(0);
        });
        //endregion
        //region Refresh working area menuItem
        MenuItem refresh_working_area_mi = new MenuItem("Refresh working area");
        refresh_working_area_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/refresh_working_area_mi.png"))));
        refresh_working_area_mi.setOnAction((e) -> {
            refreshWorkingAreaExceptMenuBarExcept(Arrays.asList("Menu_ID"));
        });
        //endregion
        //region Refresh canvas objects menuItem
        MenuItem refresh_canvas_objects_mi = new MenuItem("Refresh canvas objects");
        refresh_canvas_objects_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/refresh_canvas_objects_mi.png"))));
        refresh_canvas_objects_mi.setOnAction((e) -> {
            eraseCanvasObjects();
        });
        //endregion

        main_menu.getItems().add(uploadFromFile_mi);
        main_menu.getItems().add(setUpParameters_mi);
        main_menu.getItems().add(exit_mi);

        prepare_menu.getItems().add(refresh_working_area_mi);
        prepare_menu.getItems().add(refresh_canvas_objects_mi);

        menuBar.getMenus().addAll(main_menu, prepare_menu);
        grid.add(menuBar, 0, 0, 10, 1);
        //endregion

        return grid;
    }

    private boolean checkResultCommandForWarningAndError(AlertCommands resultCommand, Map<AlertCommands, String> alertMap, GridPane grid)
    {
        boolean continueExecution = true;

        switch (resultCommand) {
            case RIGHTS_RESULT: {
                createAlert(AlertType.INFORMATION, "Entered valid fields values!", resultCommand.getCommand() + System.lineSeparator() + alertMap.get(resultCommand));
                break;
            }
            case WARNING_RESULT: {
                refreshWorkingAreaExceptMenuBarExcept(Arrays.asList("Menu_ID", "Canvas_ID"));
                createAlert(AlertType.WARNING, "Entered invalid values!", resultCommand.getCommand() + System.lineSeparator() + alertMap.get(resultCommand));
                break;
            }
            case ERROR_RESULT: {
                refreshWorkingAreaExceptMenuBarExcept(Arrays.asList("Menu_ID", "Canvas_ID", "LeftSideObject_ID"));
                createAlert(AlertType.ERROR, "Total error!", resultCommand.getCommand() + System.lineSeparator() + alertMap.get(resultCommand));
                continueExecution = false;
                break;
            }
            default:
                UsefulFunction.throwException("There is no such a result. Fix code.");
                continueExecution = false;
        }

        return continueExecution;
    }

    private void createAlert(AlertType alertType, String title, String context)
    {
        Alert alert = new Alert(alertType, context);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private Node addCanvas()
    {
        canvas = new Pane();
        canvas.setId("Canvas_ID");
        canvas.setBackground(new Background(new BackgroundFill(Constants.CANVAS_BACKGROUND_COLOR, null, null)));
        canvas.setMinWidth(Constants.SCREEN_WEIGHT * Constants.CANVAS_WEIGHT_IN_PERCENT);
        canvas.setMinHeight(Constants.SCREEN_HEIGHT * Constants.CANVAS_HEIGHT_IN_PERCENT);
        canvas.setBorder(new Border(new BorderStroke(Constants.CANVAS_BORDER_COLOR, BorderStrokeStyle.SOLID, null, Constants.CANVAS_BORDER_WIDTH)));
        canvas.setOnMouseClicked(e->{
            //check for max nodes amount which can be set up on the canvas
            if(circles.size() >= graph.Nodes().size()) {
                createAlert(AlertType.INFORMATION, "Stop spawn, please!", "You set up all available nodes.");
                return;
            }

            MyCircle myCircle = new MyCircle(e.getX(), e.getY(), Constants.NODE_RADIUS, Constants.NODE_COLOR, graph);
            Text text = myCircle.getUnusedNodeNumberAsText(e.getX(), e.getY());
            canvas.getChildren().addAll(myCircle, text);
            circles.add(myCircle);

            //check for last added node on canvas
            if(circles.size() == graph.Nodes().size()) {
                GraphDrawer.drawGraphEdges(graph, canvas);
            }
        });

        return canvas;
    }

    private Node addRunButton(GridPane grid, ObservableList<MySpinner<Integer>> spinners)
    {
        Button runButton = new Button("Run");
        GridPane.setHalignment(runButton, HPos.RIGHT);
        runButton.setCursor(Cursor.HAND);
        runButton.setOnAction((e_2) -> {
            gainCrucialAlthmAndDraw(grid, spinners.get(0), spinners.get(1));
        });

        return runButton;
    }

    private void gainCrucialAlthmAndDraw(GridPane grid, MySpinner<Integer> spinnerForRootNode, MySpinner<Integer> spinnerForTargetNode)
    {
        try {
            //region Initiate Dekstra algorithm & DO algorithm
            DekstraAlgorithm algorithm = new DekstraAlgorithm(graph);
            AlertCommands alertCommand = algorithm.DO(spinnerForRootNode.getCurrentValue(), spinnerForTargetNode.getCurrentValue());
            //endregion
            //region AlertMap for algorithm execution result
            Map<AlertCommands, String> alertMap = new HashMap<>();
            alertMap.put(alertCommand.RIGHTS_RESULT, "Algorithm was successfully performed.");
            alertMap.put(alertCommand.WARNING_RESULT, "That is not good execution. Check for it.");
            alertMap.put(alertCommand.ERROR_RESULT, "There is no such a path.");
            boolean continueExecution = checkResultCommandForWarningAndError(alertCommand, alertMap, grid);
            if(!continueExecution) {
                return;
            }
            //endregion
            //region Cleaning working area except menuBar
            refreshWorkingAreaExceptMenuBarExcept(Arrays.asList("Menu_ID", "Canvas_ID"));
            //endregion
            //region Total path(s) amount label
            Label totalAmountPaths_lbl = createNewLabel("Total amount paths: " + algorithm.getAmountAllBackPaths(),
                                                        Constants.defaultFontFamily,
                                                        FontWeight.NORMAL,
                                                        Constants.bigFontSize,
                                                        5,
                                                        HPos.LEFT,
                                                        true);
            grid.add(totalAmountPaths_lbl, 0, 1, 1, 1);
            //endregion
            //region Best path(s) weight label
            Label bestPathsWeight_lbl = createNewLabel("Best path(s) weight: " + algorithm.getBestPathWeight(),
                                                       Constants.defaultFontFamily,
                                                       FontWeight.NORMAL,
                                                       Constants.bigFontSize,
                                                       5,
                                                       HPos.LEFT,
                                                       true);
            grid.add(bestPathsWeight_lbl, 0, 2, 1, 1);
            //endregion
            //region Algorithm spent time
            Label algorithmSpentTime_lbl = createNewLabel("Algorithm spent time: " + algorithm.getAlgorithmSpentTime() + " mcs",
                                                       Constants.defaultFontFamily,
                                                       FontWeight.NORMAL,
                                                       Constants.bigFontSize,
                                                       5,
                                                       HPos.LEFT,
                                                          true);
            grid.add(algorithmSpentTime_lbl, 0, 3, 1, 1);
            //endregion
            //region OutputInfo textArea
//            TextArea outputInfo_textArea = new TextArea(UsefulFunction.getMapContent(algorithm.getMap()));
//            outputInfo_textArea.setMaxWidth(Constants.SCREEN_WEIGHT * Constants.LEFTSIDE_WEIGHT_IN_PERCENT / 2);
//            outputInfo_textArea.setMaxWidth(Constants.SCREEN_HEIGHT * Constants.LEFTSIDE_HEIGHT_IN_PERCENT);
//            outputInfo_textArea.setEditable(false);
//            outputInfo_textArea.setFont(Font.font(Constants.defaultFontFamily, FontWeight.BOLD, Constants.bigFontSize));
//
            ObservableList<String> mapOutputInfo = FXCollections.observableArrayList(UsefulFunction.getMapContent(algorithm.getMap()));
            ListView<String> outputInfo_listView = new ListView<>(mapOutputInfo);
            grid.add(outputInfo_listView, 0, 4, 1, 6);
            //endregion
        }
        catch (ExecutionException e1) {
            e1.printStackTrace();
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private ObservableList<MySpinner<Integer>> getAndAddToGridRootAndTargetNodes_lbl_spinners(GridPane grid, Integer nodesAmount)
    {
        ObservableList<MySpinner<Integer>> spinners = FXCollections.observableArrayList();

        Label rootNode_lbl = createNewLabel("Root node: ", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT, false);
        grid.add(rootNode_lbl, 0, 1, 1, 1);
        MySpinner<Integer> spinnerForRootNode = createNewSpinner(Constants.MIN_GENERATED_NUMBER, nodesAmount, Constants.defaultInitialValueForSpinner, HPos.RIGHT);
        grid.add(spinnerForRootNode, 1, 1, 1, 1);

        Label targetNode_lbl = createNewLabel("Target node: ", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT, false);
        grid.add(targetNode_lbl, 0, 2, 1, 1);
        MySpinner<Integer> spinnerForTargetNode = createNewSpinner(Constants.MIN_GENERATED_NUMBER, nodesAmount, Constants.defaultInitialValueForSpinner, HPos.RIGHT);
        grid.add(spinnerForTargetNode, 1, 2, 1, 1);

        spinners.addAll(spinnerForRootNode, spinnerForTargetNode);

        return spinners;
    }

    private Label createNewLabel(String string, String fontFamily, FontWeight fontWeight, Double fontSize, Integer padding, HPos hPos, boolean setBorder)
    {
        Text text = new Text(string);
        //text.setFill(Constants.WHITE_THEME_TEXT_COLOR);
        Label label = new Label(text.getText());
        label.setFont(Font.font(fontFamily, fontWeight, fontSize));
        label.setPadding(new Insets(padding));
        if(setBorder) label.setBorder(new Border(new BorderStroke(Constants.LABEL_BORDER_COLOR, BorderStrokeStyle.SOLID, null, Constants.LABEL_BORDER_WIDTH)));
        //label.setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
        GridPane.setHalignment(label, hPos);
        return label;
    }

    private MySpinner<Integer> createNewSpinner(int min, int max, int initialValue, HPos hPos)
    {
        final MySpinner<Integer> spinner = new MySpinner<>();
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max);
        spinner.setValueFactory(spinnerValueFactory);
        GridPane.setHalignment(spinner, hPos);

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            spinner.setCurrentValue(newValue);
        });

        return spinner;
    }

    private void refreshWorkingAreaExceptMenuBarExcept(List<String> exceptIds)
    {
        //region Clear UI objects
        ObservableList<Node> gridNodesList = grid.getChildren();
        ObservableList<Node> newGridNodesList = FXCollections.observableArrayList();
        for (Node node : gridNodesList) {
            String node_ID = node.getId();

            if (node_ID == null || node_ID.isEmpty()) continue;

            for (String exceptId : exceptIds){
                if (node_ID.equalsIgnoreCase(exceptId)) {
                    newGridNodesList.add(node);
                    break;
                }
            }
        }

        gridNodesList.clear();

        for (Node savedNode : newGridNodesList) {
            gridNodesList.add(savedNode);
        }
        //endregion

        //region List with objects
        circles.clear();
        //endregion
    }

    private void eraseCanvasObjects()
    {
        circles.clear();

        canvas.getChildren().clear();

        for (DekstraNode node : graph.Nodes()){
            node.setUpOnCanvas(false);
        }
    }

    private void clearGraphNodes()
    {
        graph.Nodes().clear();
    }
}
