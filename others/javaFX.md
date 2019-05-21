# JavaFX
The structure of a JavaFX app is:
* stage: each window of the app (main + dialogs + ...)
* scene: the main content (ex. not the menus) of a window
* scene graph: obj, controls, layouts inside the scene

![JavaFX app structure](http://tutorials.jenkov.com/images/java-javafx/javafx-overview-1.png)

There's one scene at a time in a stage but you can change it at runtime

* nodes: the components attached to the graphs are nodes and are subclasses of `javafx.scene.Node`. The node can be a branch one or a leaf one.

* controls: components with control functionalities (like buttons) here is the full list:
    + Accordion
    + __Button__
    + __CheckBox__
    + ChoiceBox
    + ColorPicker
    + ComboBox
    + DatePicker
    + __Label__
    + ListView
    + Menu
    + MenuBar
    + PasswordField
    + ProgressBar
    + __RadioButton__
    + Slider
    + Spinner
    + SplitMenuButton
    + SplitPane
    + TableView
    + TabPane
    + TextArea
    + __TextField__
    + TitledPane
    + __ToggleButton__
    + ToolBar
    + TreeTableView
    + TreeView

## Layouts
Components with nested components. The layout manage the layout of what's inside it. They inherit from `javafx.scene.Parent`.

The layouts are: Group, Region, Pane, __HBox__, __VBox__, FlowPane, BorderPane, BorderPane, StackPane, TilePane, GridPane, AnchorPane, TextFlow

## Application Class
The main gui class needs to extend `javafx.application.Application` and implement the start method. The main method has to call `launch(args);`

Usefull commands:
__stage__
* Stage stage = new Stage(); ---> stage.show(); OR stage.showAndWait();
* primaryStage.setTitle("Adrenalina");
* stage.setScene(scene);
* stage.setX(50); stage.setY(50);
* stage.setWidth(600); stage.setHeight(300);
* stage.initModality(Modality.APPLICATION_MODAL);  //stage.initModality(Modality.WINDOW_MODAL); //stage.initModality(Modality.NONE);
* stage.initOwner(primaryStage);
* primaryStage.setFullScreen(true);
__scene__
* Scene scene = new Scene(component, w, h); //in order to be visible you must add it to a stage



Rectangel2d

ract.contains(points)