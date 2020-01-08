package launcher;

import constants.Constants;
import constants.AlertCommands;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logics.DekstraAlgorithm;
import logics.GraphDrawer;
import logics.RandomGraphGenerator;
import objects.Graph;
import objects.MySpinner;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class Launcher extends Application
{
    private static Graph graph = new Graph();

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
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWeight = gd.getDisplayMode().getWidth() + 20;
        int screenHeight = gd.getDisplayMode().getHeight() - 20;
        primaryStage.setWidth(screenWeight);
        primaryStage.setHeight(screenHeight);
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
        root.getChildren().addAll(addGridPane(primaryStage, screenHeight, screenWeight));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public GridPane addGridPane(Stage primaryStage, int screenHeight, int screenWeight)
    {
        //region grid settings
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(0, 10, 0, 10));
        //endregion

        //region menuBar
        MenuBar menuBar = new MenuBar();
        menuBar.setCursor(Cursor.HAND);
        menuBar.setMinWidth(screenWeight);
        menuBar.setId("MenuBar_ID");

        Menu main_menu = new Menu("Main");
        Menu prepare_menu = new Menu("Preparation");

        //region Upload menuItem
        MenuItem uploadFromFile_mi = new MenuItem("Upload");
        uploadFromFile_mi.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/uploadButton.png"))));
        uploadFromFile_mi.setOnAction(((e) -> {
            refreshWorkingAreaExceptMenuBar(grid);

            //region FileChooser
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
            //endregion

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    //filling up graph from file
                    FileExecutorForMatrixAdjacency fileExecutor = new FileExecutorForMatrixAdjacency(file);
                    fileExecutor.fillUp(graph);

                    ObservableList<MySpinner<Integer>> spinners = getAndAddToGridRootAndTargetNodes_lbl_spinners(grid, graph.Nodes().size());

                    grid.add(addRunButton(grid, spinners), 0, 3, 2, 1);
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }));
        //endregion
        //region setUp parameters menuItem
        MenuItem setUpParameters_mi = new MenuItem("Set up parameters");
        setUpParameters_mi.setOnAction((e) -> {
            //region Cleaning working area except menuBar
            refreshWorkingAreaExceptMenuBar(grid);
            //endregion
            //region Spinners & Labels creation
            Label nodesAmount_lbl = createNewLabel("Nodes amount", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER);
            MySpinner<Integer> nodesAmount_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER, Constants.MAX_GENERATED_NUMBER, Constants.defaultInitialValueForSpinner, HPos.CENTER);
            Label edgesAmount_lbl = createNewLabel("Edges amount", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER);
            MySpinner<Integer> edgesAmount_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER, Constants.MAX_GENERATED_NUMBER, Constants.defaultInitialValueForSpinner, HPos.CENTER);

            Label weightsValuesRange_lbl = createNewLabel("Weights values range [1-50]", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.CENTER);
            Label weightsValuesRange_FROM_lbl = createNewLabel("from", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT);
            MySpinner<Integer> weightsValuesRange_FROM_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER,
                                                                                  Constants.MAX_GENERATED_NUMBER,
                                                                                  Constants.defaultInitialValueForSpinner,
                                                                                  HPos.CENTER);
            Label weightsValuesRange_TO_lbl = createNewLabel("to", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT);
            MySpinner<Integer> weightsValuesRange_TO_spinner = createNewSpinner(Constants.MIN_GENERATED_NUMBER,
                                                                                Constants.MAX_GENERATED_NUMBER,
                                                                                Constants.defaultInitialValueForSpinner,
                                                                                HPos.CENTER);
            //endregion
            //region generateRandomGraph_btn settings
            Button generateRandomGraph_btn = new Button("Generate");
            generateRandomGraph_btn.setMaxSize(80, 30);
            generateRandomGraph_btn.setOnAction((e_2) -> {
                refreshWorkingAreaExceptMenuBar(grid);

                ObservableList<MySpinner<Integer>> spinners = getAndAddToGridRootAndTargetNodes_lbl_spinners(grid, nodesAmount_spinner.getCurrentValue());

                grid.add(addRunButton(grid, spinners), 0, 3, 2, 1);

                RandomGraphGenerator randomGraphGenerator = new RandomGraphGenerator(graph,
                                                                                     nodesAmount_spinner.getCurrentValue(),
                                                                                     edgesAmount_spinner.getCurrentValue(),
                                                                                     weightsValuesRange_FROM_spinner.getCurrentValue(),
                                                                                     weightsValuesRange_TO_spinner.getCurrentValue());
                AlertCommands alertCommand = randomGraphGenerator.generate();
                checkResultCommandForWarningAndError(alertCommand, "There is no such a path",  grid);
            });
            GridPane.setHalignment(generateRandomGraph_btn, HPos.CENTER);
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
        exit_mi.setOnAction((e) -> {
            System.exit(0);
        });
        //endregion
        //region Refresh menuItem
        MenuItem refresh_mi = new MenuItem("Refresh");
        refresh_mi.setOnAction((e) -> {
            refreshWorkingAreaExceptMenuBar(grid);
        });
        //endregion

        main_menu.getItems().add(uploadFromFile_mi);
        main_menu.getItems().add(setUpParameters_mi);
        main_menu.getItems().add(exit_mi);

        prepare_menu.getItems().add(refresh_mi);

        menuBar.getMenus().addAll(main_menu, prepare_menu);
        grid.add(menuBar, 0, 0, 10, 1);
        //endregion

        return grid;
    }

    private void checkResultCommandForWarningAndError(AlertCommands resultCommand, String concreteTextInfo_forErrorAlert, GridPane grid)
    {
        switch (resultCommand) {
            case RIGHTS_RESULT: {
                createAlert(AlertType.INFORMATION, "Entered valid fields values!", resultCommand.getCommand());
                break;
            }
            case WARNING_RESULT: {
                refreshWorkingAreaExceptMenuBar(grid);
                createAlert(AlertType.WARNING, "Entered invalid values!", resultCommand.getCommand());
                break;
            }
            case ERROR_RESULT: {
                refreshWorkingAreaExceptMenuBar(grid);
                createAlert(AlertType.ERROR, "Total error!", resultCommand.getCommand() + System.lineSeparator() + concreteTextInfo_forErrorAlert);
                break;
            }
            default:
                UsefulFunction.throwException("There is no such a result. Fix code.");
        }
    }

    private void createAlert(AlertType alertType, String title, String context)
    {
        Alert alert = new Alert(alertType, context);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private Node addRunButton(GridPane grid, ObservableList<MySpinner<Integer>> spinners)
    {
        Button runButton = new Button("Run");
        runButton.setCursor(Cursor.HAND);
        runButton.setOnAction((e_2) -> {
            gainCrucialAlgthmAndDraw(grid, spinners.get(0), spinners.get(1));
        });

        return runButton;
    }

    private void gainCrucialAlgthmAndDraw(GridPane grid, MySpinner<Integer> spinnerForRootNode, MySpinner<Integer> spinnerForTargetNode)
    {
        try {
            //initiate Dekstra algorithm
            DekstraAlgorithm algorithm = new DekstraAlgorithm(graph);
            //region DO algorithm
            AlertCommands alertCommand = algorithm.DO(spinnerForRootNode.getCurrentValue(), spinnerForTargetNode.getCurrentValue());
            //endregion
            //region Total path(s) amount label
            Label totalAmountPaths_lbl = createNewLabel("Total amount paths: " + algorithm.getAmountAllBackPaths(),
                                                        Constants.defaultFontFamily,
                                                        FontWeight.EXTRA_BOLD,
                                                        Constants.bigFontSize,
                                                        10,
                                                        HPos.CENTER);
            grid.add(totalAmountPaths_lbl, 0, 4, 2, 1);
            //endregion
            //region Best path(s) weight label
            Label bestPathsWeight_lbl = createNewLabel("Best path(s) weight: " + algorithm.getBestPathWeight(),
                                                       Constants.defaultFontFamily,
                                                       FontWeight.EXTRA_BOLD,
                                                       Constants.bigFontSize,
                                                       10,
                                                       HPos.CENTER);
            grid.add(bestPathsWeight_lbl, 0, 5, 2, 1);
            //endregion
            //region outputInfo textArea
            TextArea outputInfo_textArea = new TextArea(UsefulFunction.getMapContent(algorithm.getMap()));
            outputInfo_textArea.setPadding(new Insets(10));
            outputInfo_textArea.setMinHeight(600);
            outputInfo_textArea.setMinWidth(50);
            outputInfo_textArea.setMaxWidth(300);
            outputInfo_textArea.setFont(Font.font(Constants.defaultFontFamily, FontWeight.BOLD, Constants.defaultFontSize));
            grid.add(outputInfo_textArea, 0, 6, 2, 5);
            //endregion
            //region drawing graph
            Canvas canvas = new Canvas();
            GraphicsContext context = canvas.getGraphicsContext2D();
            GraphDrawer.drawGraph(context, graph);
            grid.add(canvas, 2, 1, 8, 9);
            //endregion
            checkResultCommandForWarningAndError(alertCommand, "There is no such a path", grid);
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

        Label rootNode_lbl = createNewLabel("Root node: ", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT);
        grid.add(rootNode_lbl, 0, 1);
        MySpinner<Integer> spinnerForRootNode = createNewSpinner(Constants.MIN_GENERATED_NUMBER, nodesAmount, Constants.defaultInitialValueForSpinner, HPos.LEFT);
        grid.add(spinnerForRootNode, 1, 1);

        Label targetNode_lbl = createNewLabel("Target node: ", Constants.defaultFontFamily, FontWeight.NORMAL, Constants.defaultFontSize, 5, HPos.LEFT);
        grid.add(targetNode_lbl, 0, 2);
        MySpinner<Integer> spinnerForTargetNode = createNewSpinner(Constants.MIN_GENERATED_NUMBER, nodesAmount, Constants.defaultInitialValueForSpinner, HPos.LEFT);
        grid.add(spinnerForTargetNode, 1, 2);

        spinners.addAll(spinnerForRootNode, spinnerForTargetNode);

        return spinners;
    }

    private Label createNewLabel(String text, String fontFamily, FontWeight fontWeight, Double fontSize, Integer padding, HPos hPos)
    {
        Label label = new Label(text);
        label.setFont(Font.font(fontFamily, fontWeight, fontSize));
        label.setPadding(new Insets(padding));
        GridPane.setHalignment(label, hPos);
        return label;
    }

    private MySpinner<Integer> createNewSpinner(int min, int max, int initialValue, HPos hPos)
    {
        final MySpinner<Integer> spinner = new MySpinner<>();
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue);
        spinner.setValueFactory(spinnerValueFactory);
        GridPane.setHalignment(spinner, hPos);

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            spinner.setCurrentValue(newValue);
        });

        return spinner;
    }

    private void refreshWorkingAreaExceptMenuBar(GridPane grid)
    {
        ObservableList<Node> gridNodesList = grid.getChildren();
        ObservableList<Node> newGridNodesList = FXCollections.observableArrayList();
        for (Node node : gridNodesList) {
            String node_ID = node.getId();

            if (node_ID == null) continue;

            if (!node_ID.isEmpty() || node_ID.equalsIgnoreCase("MenuBar_ID")) {
                newGridNodesList.add(node);
            }
        }
        gridNodesList.clear();
        for (Node savedNode : newGridNodesList) {
            gridNodesList.add(savedNode);
        }
    }
}
