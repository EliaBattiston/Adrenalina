package it.polimi.ingsw.view;

import javafx.scene.image.Image;

import java.util.HashMap;

public class GuiImagesMap {
    private static HashMap<String, Image> map = null;

    public static Image getImage(String imgPath){
        if(map == null)
            map = new HashMap<>();

        if(map.get(imgPath) == null)
            map.put(imgPath, new Image(imgPath));

        return map.get(imgPath);
    }
}
