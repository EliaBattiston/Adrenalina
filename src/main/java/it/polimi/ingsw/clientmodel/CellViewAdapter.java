package it.polimi.ingsw.clientmodel;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is needed for Gson to understand when serializing/deserializing a Cell data if it is an instance of a
 * SpawnCell or a RegularCell.
 */
public class CellViewAdapter implements JsonSerializer<CellView>, JsonDeserializer<CellView> {
    @Override
    public JsonElement serialize(CellView src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public CellView deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) /*throws JsonParseException*/ {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName("it.polimi.ingsw.clientmodel." + type));
        } catch (ClassNotFoundException cnfe) {
            Logger.getGlobal().log( Level.SEVERE, cnfe.toString());
            return null;
        }
    }
}