import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Temp {
    public static void main(String[] args) throws IOException {
        String a = "E:\\DR3Maker\\迫真更新公告\\qw.txt", b = "E:\\DR3Maker\\迫真更新公告\\Songlist.json";
        Map<String, SongInfo> map = new HashMap<>();
        List<Update> list = new LinkedList<>();
        try (FileReader fileReader = new FileReader(b)) {
            Base64.Decoder decoder = Base64.getDecoder();
            Gson gson = new Gson();
            JsonArray songs = gson.fromJson(fileReader, JsonObject.class).get("songs").getAsJsonArray();
            for (JsonElement je :
                    songs) {
                JsonObject song = je.getAsJsonObject();
                byte[] bytes = decoder.decode(song.get("name").getAsString()),
                        bytes1 = decoder.decode(song.get("artist").getAsString());
                map.put(song.get("keyword").getAsString(), new SongInfo(new String(bytes, 0, bytes.length, StandardCharsets.UTF_8), new String(bytes1, 0, bytes1.length, StandardCharsets.UTF_8)));
            }
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(a))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.equals("")) continue;
                String[] strings = line.split(" -> ");
                if (strings.length != 2) {
                    System.err.println("警告：" + line + "格式有误");
                    continue;
                }
                String[] strings1 = strings[0].split("\\.");
                if (strings1.length != 2) {
                    System.err.println("警告：" + line + "格式有误");
                    continue;
                }
                try {
                    list.add(new Update(strings1[0], Integer.parseInt(strings1[1]), Integer.parseInt(strings[1])));
                } catch (NumberFormatException e) {
                    System.err.println("警告：" + line + "格式有误");
                }
            }
        }
        for (Update update : list) {
            SongInfo songInfo = map.get(update.keyword);
            String str = String.format("『%s』 - 「%s」 Tier%d%sTier%d", songInfo.name, songInfo.artist, update.originalTier, update.upOrDown, update.newTier);
            System.out.println(str);
        }
    }
}
class Update {
    public String keyword;
    public int originalTier, newTier;
    public String upOrDown;
    public Update(String keyword, int originalTier, int newTier) {
        this.keyword = keyword;
        this.originalTier = originalTier;
        this.newTier = newTier;
        upOrDown = newTier > originalTier ? "升为" : "降为";
    }
}
class SongInfo {
    public String name, artist;
    public SongInfo(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }
}