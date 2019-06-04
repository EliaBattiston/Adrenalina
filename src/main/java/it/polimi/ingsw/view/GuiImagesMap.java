package it.polimi.ingsw.view;

import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;

/**
 * A singleton of a map that contains all the requested images since that moment. It's fundamental to ask the images
 * through this class so that the class can load it only the first time and than reuse it at need.
 */
public class GuiImagesMap {
    private static HashMap<String, Image> map = null;

    /**
     * The constructor is private because it's a Singleton Class
     */
    private GuiImagesMap(){

    }

    /**
     * Return the image at the path requested. The root path where this method look for the image is '/images/' inside
     * the resources folder.
     * It caches the image too.
     * @param imgPath the path of the requested image
     * @return Image the image requested
     */
    public static Image getImage(String imgPath){
        String imgRoot = "/images/";

        if(map == null)
            map = new HashMap<>();

        imgPath = imgRoot + imgPath;

        if(map.get(imgPath) == null)
            map.put(imgPath, new Image(imgPath));

        return map.get(imgPath);
    }

    /**
     * Return the image at the path requested. The root path where this method look for the image is '/images/' inside
     * the resources folder.
     * It caches the image too.
     * @param imgPath the path of the requested image
     * @return Image the image requested
     */
    public static Image getImageWithShadow(String imgPath, double w, double h){
        String imgRoot = "/images/";

        if(map == null)
            map = new HashMap<>();

        imgPath = imgRoot + imgPath;

        if(map.get(imgPath) == null)
            map.put(imgPath, new Image(imgPath));

        ImageView iv = new ImageView(map.get(imgPath));
        iv.setFitHeight(h);
        iv.setFitWidth(w);

        iv.setEffect(new DropShadow(BlurType.GAUSSIAN, javafx.scene.paint.Color.BLACK, 1, 0.8, 1, 1));

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(javafx.scene.paint.Color.TRANSPARENT);
        return iv.snapshot(sp, null);
    }
}
