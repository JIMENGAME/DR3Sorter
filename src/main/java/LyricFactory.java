import com.google.gson.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.FileReader;
import java.io.IOException;

public class LyricFactory {
    public static void main(String[] args) {
        try (FileReader fileReader = new FileReader("E:\\Users\\Administrator\\WebstormProjects\\DanceRail3Viewer\\songs\\pm0\\lyric.json")) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);
            JsonArray lyrics = jsonObject.get("lyrics").getAsJsonArray();
            JsonArray newLyrics = new JsonArray();
            for (JsonElement qwq: lyrics) {
                JsonObject lyric = qwq.getAsJsonObject();
                JsonObject newLyric = new JsonObject();
                newLyric.add("time", new JsonPrimitive(lyric.get("time").getAsFloat() / 2f));
                newLyric.add("text", new JsonPrimitive(lyric.get("text").getAsString()));
                newLyrics.add(newLyric);
            }
            Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
            JsonObject output = new JsonObject();
            output.add("lyrics", newLyrics);
            String qwq = gson1.toJson(output);
            System.out.println(qwq);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable trans = new StringSelection(qwq);
            clipboard.setContents(trans, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
