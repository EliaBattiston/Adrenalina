package it.polimi.ingsw.view;

import javafx.scene.image.Image;

import java.util.HashMap;

public class GuiImagesMap {
    private static String imgRoot = "/images/";//for the entire package only
    private static HashMap<String, Image> map = null;

    public static Image getImage(String imgPath){
        if(map == null)
            map = new HashMap<>();

        imgPath = imgRoot + imgPath;

        if(map.get(imgPath) == null)
            map.put(imgPath, new Image(imgPath));
        //map.put(imgPath, new Image(new FileInputStream(Game.class.getClassLoader().getResourceAsStream(imgPath))));

        return map.get(imgPath);
    }
}
