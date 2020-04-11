package launcher;

import constants.AlertCommands;
import constants.Constants;
import files.FileExecutorForMatrixAdjacencyFactory;
import files.IFileExecutor;
import commonUsefulFunctions.UsefulFunction;
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
import objects.MyCircleNode;
import objects.MySpinner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Launcher extends Application
{
    private static Graph graph = new Graph();

    //UI variables
    private GridPane grid;
    private Pane canvas;
    private static MenuBar menuBar;
    private static List<MyCircleNode> circlesNodesOnCanvas = new ArrayList<>();
    private static List<Text> nodeNumbersOnCanvas = new ArrayList<>();
    private static List<Label> rootAndTargetNode_labels_forRunStage = new ArrayList<>();
    private static ObservableList<MySpinner<Integer>> rootAndTargetNode_spinners_forRunStage = FXCollections.observableArrayList();
    private static Boolean returnPreviousStageMI_DisableFlag = true;
    private static Boolean uploadFileFlag = false;
    private static Boolean setUpManuallyFlag = false;
    private static Boolean runButtonAndSpinnersDisableFlag = true;
    private static File uploadFile;

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
        grid.setPadding(new Insets(0, 0, 0, 0));
        //endregion

        //region (menuBar)
        menuBar = new MenuBar();
        menuBar.setCursor(Cursor.HAND);
        menuBar.setMinWidth(Constants.SCREEN_WEIGHT);
        menuBar.setId(Constants.MENU_ID);

        Menu main_menu = new Menu("Main");
        Menu prepare_menu = new Menu("Preparation");

        //region Upload (menuItem)
        MenuItem uploadFromFile_mi = new MenuItem("Upload a graph from file");
        uploadFromFile_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/uploadButton_mi.png"))));
        uploadFromFile_mi.setOnAction(((e) -> {
            uploadFileFlag = true;
            setUpManuallyFlag = false;
            runButtonAndSpinnersDisableFlag = true;

            //region FileChooser
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"),
                                                     new FileChooser.ExtensionFilter("XML", "*.xml"));
            //endregion

            uploadFile = fileChooser.showOpenDialog(primaryStage);
            if (uploadFile != null) {
                getCanvasObjectsWithBeforeRunStageIDs();
            }
        }));
        //endregion

        //region SetUp graph parameters (menuItem)
        MenuItem setUpParameters_mi = new MenuItem("Set up a random graph");
        setUpParameters_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/setUpParams_mi.png"))));
        setUpParameters_mi.setOnAction((e) -> {
            setUpManuallyFlag = true;
            uploadFileFlag = false;
            runButtonAndSpinnersDisableFlag = true;
            getCanvasObjectsWithBeforeRunStageIDs();
        });
        //endregion

        //region Exit (menuItem)
        MenuItem exit_mi = new MenuItem("Exit");
        exit_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/exit_mi.png"))));
        exit_mi.setOnAction((e) -> {
            System.exit(0);
        });
        //endregion

        //region Return a previous stage (menuItem)
        MenuItem return_previous_stage = new MenuItem("Return previous stage");
        return_previous_stage.setId(Constants.RETURN_PREVIOUS_STAGE);
        return_previous_stage.setDisable(returnPreviousStageMI_DisableFlag);
        return_previous_stage.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/return_previous_stage.png"))));
        return_previous_stage.setOnAction((e) -> {
            runButtonAndSpinnersDisableFlag = false;
            getCanvasObjectsWithBeforeRunStageIDs();
        });
        //endregion

        //region Clear working area (menuItem)
        MenuItem clear_working_area_mi = new MenuItem("Clear working area");
        clear_working_area_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/clear_working_area_mi.png"))));
        clear_working_area_mi.setOnAction((e) -> {
            clearWorkingAreaExceptIDs(Constants.MENU_ID);
        });
        //endregion

        //region Clear canvas objects (menuItem)
        MenuItem clear_canvas_objects_mi = new MenuItem("Clear canvas objects");
        clear_canvas_objects_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/clear_canvas_objects_mi.png"))));
        clear_canvas_objects_mi.setOnAction((e) -> {
            clearCanvasObjects();
            clearCircles();
        });
        //endregion

        main_menu.getItems().add(uploadFromFile_mi);
        main_menu.getItems().add(setUpParameters_mi);
        main_menu.getItems().add(exit_mi);

        prepare_menu.getItems().add(return_previous_stage);
        prepare_menu.getItems().add(clear_working_area_mi);
        prepare_menu.getItems().add(clear_canvas_objects_mi);

        menuBar.getMenus().addAll(main_menu, prepare_menu);
        grid.add(menuBar, 0, 0, 10, 1);
        //endregion

        return grid;
    }

    private void getCanvasObjectsWithBeforeRunStageIDs(){
        //region prohibit user to return to stage when he can choose between operations such as "upload file" and "set up manually"
        returnPreviousStageMI_DisableFlag = true;
        MenuItem reset_find_short_path_operation = (MenuItem) getObjectFromUIListByID(menuBar, Constants.RETURN_PREVIOUS_STAGE);
        reset_find_short_path_operation.setDisable(returnPreviousStageMI_DisableFlag);
        //endregion

        if(uploadFileFlag) {
            //region Upload a graph from file

            //region Before upload execution Cleaning
            clearWorkingAreaExceptIDs(Constants.MENU_ID, Constants.CANVAS_ID);
            clearRootAndTarget_labels_spinners();
            clearGraphNodes();
            clearCanvasObjects();
            clearCircles();

            //endregion

            //filling up graph from uploadFile
            FileExecutorForMatrixAdjacencyFactory factory = new FileExecutorForMatrixAdjacencyFactory();
            IFileExecutor fileExecutor = factory.getFileExecutor(uploadFile);

            AlertCommands alertCommand = null;
            alertCommand = fileExecutor.fillUp(graph);

            //region alertMap for File reading
            Map<AlertCommands, String> alertMap = new HashMap<>();
            alertMap.put(alertCommand.RIGHTS_RESULT, "File was successfully read.");
            alertMap.put(alertCommand.WARNING_RESULT, "That is not good execution. Check for it.");
            alertMap.put(alertCommand.ERROR_RESULT, "You uploaded an empty or invalid file. Check out for it.");
            //endregion
            boolean continueExecution = checkCommandResultForWarningAndError(alertCommand, alertMap, grid);
            if(!continueExecution) {
                clearWorkingAreaExceptIDs(Constants.MENU_ID);
                return;
            }

            getAndAddToGridRootAndTargetNodesNumbers_labels_spinners(grid, graph.Nodes().size(), Constants.LEFTSIDE_OBJECT_FOR_BEFORE_RUN_STAGE_ID);
            Node runButton = addRunButton(grid, rootAndTargetNode_spinners_forRunStage);

            //region Visibility setup for run stage objects
            runButton.setVisible(true);
//            for (MySpinner<Integer> spinner : rootAndTargetNode_spinners_forRunStage){
//                spinner.setVisible(true);
//            }
//            for (Label label : rootAndTargetNode_labels_forRunStage){
//                label.setVisible(true);
//            }
            //endregion

            grid.add(runButton, 1, 3, 1, 1);

            canvas = addCanvas(runButton);
            grid.add(canvas, 2, 1, 8, 9);

            //inform user to start landing nodes
            createAlert(Alert.AlertType.INFORMATION, "Information", "Please, land all nodes on the area with grey borders.");
            //endregion
        }
        else if (setUpManuallyFlag) {
            //region Get up a graph manually

            //region Cleaning working area except menuBar
            clearWorkingAreaExceptIDs(Constants.MENU_ID, Constants.CANVAS_ID);
            clearRootAndTarget_labels_spinners();
            clearGraphNodes();
            clearCanvasObjects();
            clearCircles();
            //endregion
            //region Spinners & Labels creation
            Label nodesAmount_lbl = createNewLabel("Nodes amount", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER, false);
            nodesAmount_lbl.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);
            MySpinner<Integer> nodesAmount_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER, Constants.MAX_GENERATED_NUMBER, Constants.defaultInitialValueForSpinner, HPos.CENTER);
            nodesAmount_spinner.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);
            Label edgesAmount_lbl = createNewLabel("Edges amount", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER, false);
            edgesAmount_lbl.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);
            MySpinner<Integer> edgesAmount_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER, Constants.MAX_GENERATED_NUMBER, Constants.defaultInitialValueForSpinner, HPos.CENTER);
            edgesAmount_spinner.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);

            Label weightsValuesRange_lbl = createNewLabel("Weights values range [1-50]", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER, false);
            weightsValuesRange_lbl.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);
            Label weightsValuesRange_FROM_lbl = createNewLabel("from", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT, false);
            weightsValuesRange_FROM_lbl.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);
            MySpinner<Integer> weightsValuesRange_FROM_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER,
                                                                                  Constants.MAX_GENERATED_NUMBER,
                                                                                  Constants.defaultInitialValueForSpinner,
                                                                                  HPos.CENTER);
            weightsValuesRange_FROM_spinner.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);
            Label weightsValuesRange_TO_lbl = createNewLabel("to", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT, false);
            weightsValuesRange_TO_lbl.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);
            MySpinner<Integer> weightsValuesRange_TO_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER,
                                                                                Constants.MAX_GENERATED_NUMBER,
                                                                                Constants.defaultInitialValueForSpinner,
                                                                                HPos.CENTER);
            weightsValuesRange_TO_spinner.setId(Constants.LEFTSIDE_OBJECT_SETUP_MANUALLY_ID);
            //endregion
            //region generateRandomGraph_btn settings
            Button generateRandomGraph_btn = new Button("Generate");
            generateRandomGraph_btn.setId(Constants.GENERATE_RANDOM_GRAPH_BUTTON_ID);
            generateRandomGraph_btn.setMaxSize(80, 30);
            generateRandomGraph_btn.setOnAction((e_2) -> {
                //region allow user to return to stage when he can choose between operations such as "upload file" and "set up manually"
                returnPreviousStageMI_DisableFlag = false;
                //reset_find_short_path_operation (MenuItem)
                ((MenuItem) getObjectFromUIListByID(menuBar, Constants.RETURN_PREVIOUS_STAGE)).setDisable(returnPreviousStageMI_DisableFlag);
                //endregion

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

                boolean continueExecution = checkCommandResultForWarningAndError(alertCommand, alertMap, grid);
                if(!continueExecution) {
                    runButtonAndSpinnersDisableFlag = false;
                    return;
                }
                //endregion

                clearWorkingAreaExceptIDs(Constants.MENU_ID, Constants.CANVAS_ID, Constants.LEFTSIDE_OBJECT_FOR_BEFORE_RUN_STAGE_ID, Constants.GENERATE_RANDOM_GRAPH_BUTTON_ID);

                generateRandomGraph_btn.setVisible(false);

                getAndAddToGridRootAndTargetNodesNumbers_labels_spinners(grid, nodesAmount_spinner.getCurrentValue(), Constants.LEFTSIDE_OBJECT_FOR_BEFORE_RUN_STAGE_ID);
                Button runButton = addRunButton(grid, rootAndTargetNode_spinners_forRunStage);

                //region Visibility setup for run stage objects
                //runButton.setVisible(true);
//                for (MySpinner<Integer> spinner : rootAndTargetNode_spinners_forRunStage){
//                    spinner.setVisible(true);
//                }
//                for (Label label : rootAndTargetNode_labels_forRunStage){
//                    label.setVisible(true);
//                }
                //endregion

                grid.add(runButton, 1, 3, 1, 1);

                canvas = addCanvas(runButton);
                grid.add(canvas, 2, 1, 8, 9);

                //inform user to start landing nodes
                createAlert(Alert.AlertType.INFORMATION, "Information", "Please, land all nodes on the area with grey borders.");
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
            //endregion
        }
        else{
            UsefulFunction.throwException("Error: Check for top conditions and flags for upload an d setup strategies. Program couldn't go through this way.");
        }
    }

    private boolean checkCommandResultForWarningAndError(AlertCommands resultCommand, Map<AlertCommands, String> alertMap, GridPane grid)
    {
        boolean continueExecution = true;

        switch (resultCommand) {
            case RIGHTS_RESULT: {
                createAlert(Alert.AlertType.INFORMATION, "Entered valid fields values!", resultCommand.getCommand() + System.lineSeparator() + alertMap.get(resultCommand));
                break;
            }
            case WARNING_RESULT: {
                createAlert(Alert.AlertType.WARNING, "Entered invalid values!", resultCommand.getCommand() + System.lineSeparator() + alertMap.get(resultCommand));
                continueExecution = false;
                break;
            }
            case ERROR_RESULT: {
                createAlert(Alert.AlertType.ERROR, "Total error!", resultCommand.getCommand() + System.lineSeparator() + alertMap.get(resultCommand));
                continueExecution = false;
                break;
            }
            default:
                clearWorkingAreaExceptIDs(Constants.MENU_ID);
                clearCircles();
                clearCanvasObjects();
                clearGraphNodes();
                UsefulFunction.throwException("There is no such a result. Fix code.");
                continueExecution = false;
        }

        return continueExecution;
    }

    private void createAlert(Alert.AlertType alertType, String title, String context)
    {
        Alert alert = new Alert(alertType, context);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private Pane addCanvas(Node runButton)
    {
        canvas = new Pane();
        canvas.setId(Constants.CANVAS_ID);
        canvas.setBackground(new Background(new BackgroundFill(Constants.CANVAS_BACKGROUND_COLOR, null, null)));
        canvas.setMinWidth(Constants.SCREEN_WEIGHT * Constants.CANVAS_WEIGHT_IN_PERCENT);
        canvas.setMinHeight(Constants.SCREEN_HEIGHT * Constants.CANVAS_HEIGHT_IN_PERCENT);
        canvas.setBorder(new Border(new BorderStroke(Constants.CANVAS_BORDER_COLOR, BorderStrokeStyle.SOLID, null, Constants.CANVAS_BORDER_WIDTH)));
        canvas.setOnMouseClicked( e->{
            //check for max nodes amount which can be set up on the canvas
            if(circlesNodesOnCanvas.size() >= graph.Nodes().size()) {
                //createAlert(Alert.AlertType.INFORMATION, "Stop spawn, please!", "You set up all available nodes.");
                return;
            }

            //if graph was created by random method then "generateRandomGraph_btn" remains in hidden state. Due to that we must remove this hidden button from the canvas
            clearWorkingAreaObjectsWithID(Constants.GENERATE_RANDOM_GRAPH_BUTTON_ID);

            MyCircleNode circleNode = new MyCircleNode(e.getX(), e.getY(), Constants.NODE_RADIUS, Constants.NODE_COLOR, graph, canvas, grid);
            Text nodeNumberText = circleNode.getUnusedNodeNumberAsText();
            circlesNodesOnCanvas.add(circleNode);
            nodeNumbersOnCanvas.add(nodeNumberText);
            canvas.getChildren().add(circleNode);
            canvas.getChildren().add(nodeNumberText);

            //check for last added node on canvas
            if(circlesNodesOnCanvas.size() == graph.Nodes().size()) {
                GraphDrawer.drawGraphEdges(graph, canvas, grid);

                //set up events handlers for every circles nodes on canvas
                for (MyCircleNode cNode : circlesNodesOnCanvas){
                    cNode.setUpSettings();
                }

                //set runButton, spinners for root and target nodes DISABLE as FALSE. In other words, activate them
                runButton.setDisable(false);

                for (MySpinner<Integer> spinner : rootAndTargetNode_spinners_forRunStage){
                    spinner.setDisable(false);
                }
            }
        });

        return canvas;
    }

    private Object getObjectFromUIListByID(Object listWithObjects, String objectIdToBeReturned)
    {
        String listName = listWithObjects.getClass().getTypeName();

        if(listName == menuBar.getClass().getTypeName()) {
            for (Menu menu : menuBar.getMenus()){
                for (MenuItem mi : menu.getItems()){
                    if(mi.getId() != null && !mi.getId().isEmpty()) {
                        if (mi.getId().equalsIgnoreCase(objectIdToBeReturned)) {
                            return mi;
                        }
                    }
                }
            }
        }
        else if(listName == grid.getClass().getTypeName()){
            for (Node node : grid.getChildren()){
                if(node.getId() != null && !node.getId().isEmpty()) {
                    if (node.getId().equalsIgnoreCase(objectIdToBeReturned)) {
                        return node;
                    }
                }
            }
        }
        else if(listName == canvas.getClass().getTypeName()){
            for (Node node : canvas.getChildren()){
                if(node.getId() != null && !node.getId().isEmpty()) {
                    if (node.getId().equalsIgnoreCase(objectIdToBeReturned)) {
                        return node;
                    }
                }
            }
        }

        UsefulFunction.throwException("Error: Conditions haven't worked. It may be because some another list of UI objects exists, but it isn't used in these above conditions. You should add this new list in condition.");
        return null;
    }

    private Button addRunButton(GridPane grid, ObservableList<MySpinner<Integer>> spinners)
    {
        Button runButton = new Button("Run");
        //runButton.setVisible(true);
        runButton.setDisable(runButtonAndSpinnersDisableFlag);
        runButton.setId(Constants.LEFTSIDE_OBJECT_FOR_BEFORE_RUN_STAGE_ID);
        GridPane.setHalignment(runButton, HPos.RIGHT);
        runButton.setCursor(Cursor.HAND);
        runButton.setOnAction((e_2) -> {
            //region allow user to return to previous stage. For "upload file" stage is root and target nodes choosing and for "set up manually" that stage is random graph generator form
            returnPreviousStageMI_DisableFlag = false;
            MenuItem return_previous_stage = (MenuItem) getObjectFromUIListByID(menuBar, Constants.RETURN_PREVIOUS_STAGE);
            return_previous_stage.setDisable(returnPreviousStageMI_DisableFlag);
            //endregion
            gainCrucialAlgorithmAndDraw(grid, spinners.get(0), spinners.get(1));
        });

        return runButton;
    }

    private void gainCrucialAlgorithmAndDraw(GridPane grid, MySpinner<Integer> spinnerForRootNode, MySpinner<Integer> spinnerForTargetNode)
    {
        try {
            //region Initiate Dekstra algorithm & DO algorithm
            DekstraAlgorithm algorithm = new DekstraAlgorithm(graph);
            AlertCommands alertCommand = algorithm.DO(spinnerForRootNode.getCurrentValue(), spinnerForTargetNode.getCurrentValue());
            //endregion

            //region AlertMap for algorithm execution result
            Map<AlertCommands, String> alertMap = new HashMap<>();
            alertMap.put(alertCommand.RIGHTS_RESULT, "Algorithm was successfully performed.");
            alertMap.put(alertCommand.WARNING_RESULT, "That is not good execution. There is no such a path. Check for it.");
            alertMap.put(alertCommand.ERROR_RESULT, "Error. There is no such a path.");
            boolean continueExecution = checkCommandResultForWarningAndError(alertCommand, alertMap, grid);
            if(!continueExecution) {
                setUpGraphNodesAsUnusedInForwardAlthm();
                return;
            }
            //endregion

            //store temporary canvas before it will be removed
            Pane tempCanvas = canvas;

            //region Cleaning working area except menuBar
            clearWorkingAreaExceptIDs(Constants.MENU_ID);
            //endregion

            //region Total path(s) amount label
            Label totalAmountPaths_lbl = createNewLabel("Total amount paths: " + algorithm.getAmountAllBackPaths(),
                                                        Constants.defaultFontFamily,
                                                        FontWeight.NORMAL,
                                                        Constants.bigFontSize,
                                                        5,
                                                        HPos.LEFT,
                                                        false);
            totalAmountPaths_lbl.setId(Constants.LEFTSIDE_OBJECT_FOR_AFTER_RUN_STAGE_ID);
            grid.add(totalAmountPaths_lbl, 0, 1, 1, 1);
            //endregion

            //region Best path(s) weight label
            Label bestPathsWeight_lbl = createNewLabel("Best path(s) weight: " + algorithm.getBestPathWeight(),
                                                       Constants.defaultFontFamily,
                                                       FontWeight.NORMAL,
                                                       Constants.bigFontSize,
                                                       5,
                                                       HPos.LEFT,
                                                       false);
            bestPathsWeight_lbl.setId(Constants.LEFTSIDE_OBJECT_FOR_AFTER_RUN_STAGE_ID);
            grid.add(bestPathsWeight_lbl, 0, 2, 1, 1);
            //endregion

            //region Algorithm spent time
            Label algorithmSpentTime_lbl = createNewLabel("Algorithm spent time: " + algorithm.getAlgorithmSpentTime() + " mcs",
                                                          Constants.defaultFontFamily,
                                                          FontWeight.NORMAL,
                                                          Constants.bigFontSize,
                                                          5,
                                                          HPos.LEFT,
                                                          false);
            bestPathsWeight_lbl.setId(Constants.LEFTSIDE_OBJECT_FOR_AFTER_RUN_STAGE_ID);
            grid.add(algorithmSpentTime_lbl, 0, 3, 1, 1);
            //endregion

            //region outputInfo_listView
//            TextArea outputInfo_textArea = new TextArea(UsefulFunction.getMapContent(algorithm.getMap()));
//            outputInfo_textArea.setMaxWidth(Constants.SCREEN_WEIGHT * Constants.LEFTSIDE_WEIGHT_IN_PERCENT / 2);
//            outputInfo_textArea.setMaxWidth(Constants.SCREEN_HEIGHT * Constants.LEFTSIDE_HEIGHT_IN_PERCENT);
//            outputInfo_textArea.setEditable(false);
//            outputInfo_textArea.setFont(Font.font(Constants.defaultFontFamily, FontWeight.BOLD, Constants.bigFontSize));
//
            ObservableList<String> mapOutputInfo = FXCollections.observableArrayList(UsefulFunction.getMapContent(algorithm.getMap()));
            ListView<String> outputInfo_listView = new ListView<>(mapOutputInfo);
            outputInfo_listView.setId(Constants.LEFTSIDE_OBJECT_FOR_AFTER_RUN_STAGE_ID);
            outputInfo_listView.setPadding(new Insets(0, 0, 0, 5));
            grid.add(outputInfo_listView, 0, 4, 1, 6);
            //endregion

            grid.add(tempCanvas, 2, 1, 8, 8);

            clearGraphNodes();
            clearRootAndTarget_labels_spinners();
        }
        catch (ExecutionException e1) {
            e1.printStackTrace();
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private void getAndAddToGridRootAndTargetNodesNumbers_labels_spinners(GridPane grid, Integer nodesAmount, String leftsideObjectId)
    {
        //region Root node label
        Label rootNode_lbl = createNewLabel("Root node: ", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.bigFontSize, 5, HPos.LEFT, false);
        rootNode_lbl.setId(leftsideObjectId);
        //rootNode_lbl.setVisible(false);
        grid.add(rootNode_lbl, 0, 1, 1, 1);
        //endregion

        //region Root node spinner
        MySpinner<Integer> spinnerForRootNode = createNewSpinner(Constants.MIN_GENERATED_NUMBER, nodesAmount, Constants.defaultInitialValueForSpinner, HPos.RIGHT);
        spinnerForRootNode.setId(leftsideObjectId);
        //spinnerForRootNode.setVisible(false);
        spinnerForRootNode.setDisable(runButtonAndSpinnersDisableFlag);
        grid.add(spinnerForRootNode, 1, 1, 1, 1);
        //endregion

        //region Target node label
        Label targetNode_lbl = createNewLabel("Target node: ", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.bigFontSize, 5, HPos.LEFT, false);
        targetNode_lbl.setId(leftsideObjectId);
        //targetNode_lbl.setVisible(false);
        grid.add(targetNode_lbl, 0, 2, 1, 1);
        //endregion

        //region Target node spinner
        MySpinner<Integer> spinnerForTargetNode = createNewSpinner(Constants.MIN_GENERATED_NUMBER, nodesAmount, Constants.defaultInitialValueForSpinner, HPos.RIGHT);
        spinnerForTargetNode.setId(leftsideObjectId);
        //spinnerForTargetNode.setVisible(false);
        spinnerForTargetNode.setDisable(runButtonAndSpinnersDisableFlag);
        grid.add(spinnerForTargetNode, 1, 2, 1, 1);
        //endregion

        //MySpinners list
        rootAndTargetNode_spinners_forRunStage.addAll(spinnerForRootNode, spinnerForTargetNode);

        //Labels list
        rootAndTargetNode_labels_forRunStage.add(rootNode_lbl);
        rootAndTargetNode_labels_forRunStage.add(targetNode_lbl);
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

    private void clearWorkingAreaExceptIDs(String... exceptIdsInArray)
    {
        ObservableList<Node> gridNodesList = grid.getChildren();

        List<String> exceptIds = Arrays.asList(exceptIdsInArray);

        if(gridNodesList == null || gridNodesList.isEmpty() || exceptIdsInArray == null || exceptIdsInArray.length == 0) return;

        //region Clear UI objects
        ObservableList<Node> newGridNodesList = FXCollections.observableArrayList();
        for (Node node : gridNodesList) {
            String node_ID = node.getId();

            if (node_ID == null || node_ID.isEmpty()) continue;

            for (String exceptId : exceptIds){
                if (exceptId.equalsIgnoreCase(node_ID)) {
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
    }

    private void clearWorkingAreaObjectsWithID(String... IDsToRemoveInArray)
    {
        ObservableList<Node> gridNodesList = grid.getChildren();
        //convert array to list
        List<String> IDsToRemove = Arrays.asList(IDsToRemoveInArray);

        if(gridNodesList == null || gridNodesList.isEmpty() || IDsToRemove == null || IDsToRemove.isEmpty()) return;

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

    private void clearCanvasObjects()
    {
        if(canvas != null) {
            if(canvas.getChildren() != null) {
                if(!canvas.getChildren().isEmpty()) {
                    canvas.getChildren().clear();
                }
            }
        }

        if(graph != null){
            if(graph.Nodes() != null) {
                for (DekstraNode node : graph.Nodes()){
                    node.setUpOnCanvas(false);
                }
            }
        }
    }

    private void clearGraphNodes()
    {
        if(graph != null) {
            if(graph.Nodes() != null) {
                if(!graph.Nodes().isEmpty()) {
                    graph.Nodes().clear();
                }
            }
        }
    }

    private void setUpGraphNodesAsUnusedInForwardAlthm()
    {
        if(graph != null) {
            if(graph.Nodes() != null) {
                if(!graph.Nodes().isEmpty()) {
                    for (DekstraNode node : graph.Nodes()){
                        node.setWasUsedInForwardAlthm(false);
                    }
                }
            }
        }
    }

    private void clearCircles()
    {
        if(circlesNodesOnCanvas != null) {
            if(!circlesNodesOnCanvas.isEmpty()) {
                circlesNodesOnCanvas.clear();
            }
        }
    }

    private void clearRootAndTarget_labels_spinners(){
        //labels
        if(rootAndTargetNode_labels_forRunStage != null) {
            if(!rootAndTargetNode_labels_forRunStage.isEmpty()) {
                rootAndTargetNode_labels_forRunStage.clear();
            }
        }

        //spinners
        if(rootAndTargetNode_spinners_forRunStage != null) {
            if(!rootAndTargetNode_spinners_forRunStage.isEmpty()) {
                rootAndTargetNode_spinners_forRunStage.clear();
            }
        }
    }
}
