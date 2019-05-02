package it.polimi.ingsw.view;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

public class Gui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        double width, height;
        width = 800;
        height = 600;

        primaryStage.setTitle("Adrenalina");
/*
        StackPane layout = new StackPane();

        BackgroundImage myBI= new BackgroundImage(new Image("file:images/map1.png",width,height,true,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

//then you set to your node
        layout.setBackground(new Background(myBI));

        Scene sc = new Scene(layout, width, height);

        // primaryStage.setScene(s);
        //primaryStage.show();
*/

        Group root = new Group();
        Scene theScene = new Scene( root, width, height );
        primaryStage.setScene( theScene );
        primaryStage.setResizable(false);
        //primaryStage.initStyle(StageStyle.UNDECORATED);

        Canvas canvas = new Canvas( width, height);
        root.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        //gc.setFill( Color.RED );
        //gc.setStroke( Color.BLACK );
        //gc.setLineWidth(2);
        //Font theFont = Font.font( "Times New Roman", FontWeight.BOLD, 48 );
        //gc.setFont( theFont );
        //gc.fillText( "Hello, World!", 60, 50 );
        //gc.strokeText( "Hello, World!", 60, 50 );

        Image earth = new Image( "file:images/map/map1.png" );
        gc.drawImage( earth, 0, 0, width,height );

        primaryStage.show();
    }
}
