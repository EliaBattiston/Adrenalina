package it.polimi.ingsw.model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is needed for Gson to understand when serializing/deserializing a Cell data if it is an instance of a
 * SpawnCell or a RegularCell.
 */
public class CellAdapter implements JsonSerializer<Cell>, JsonDeserializer<Cell> {
    @Override
    public JsonElement serialize(Cell src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public Cell deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) /*throws JsonParseException*/ {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName("it.polimi.ingsw.model." + type));
        } catch (ClassNotFoundException cnfe) {
            Logger.getGlobal().log( Level.SEVERE, cnfe.toString());
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }
}