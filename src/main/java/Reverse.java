import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Reverse {
    public static void main(String[] args) throws IOException {
        File songlist = new File(args[0]);
        String inputFolderPath = args[1].replace('\\', '/');
        if (!inputFolderPath.endsWith("/")) inputFolderPath += "/";
        File folder = new File(inputFolderPath);
        if (!songlist.exists() || !songlist.isFile() || !folder.exists() || !folder.isDirectory()) return;
        File[] files = folder.listFiles(File::isDirectory);
        Set<String> fileList = new HashSet<>(files.length);
        for (File file : files) {
            fileList.add(file.getName());
        }
        Gson gson = new Gson();
        JsonObject songlistObject = gson.fromJson(new FileReader(songlist), JsonObject.class);
        JsonArray songs = songlistObject.get("songs").getAsJsonArray();
        Base64.Decoder decoder = Base64.getDecoder();
        String outputPath = "./reverse-output/";
        String outputSongPath = outputPath + "SONGS/";
        File outputFile = new File(outputPath);
        File outputSongsFile = new File(outputSongPath);
        if (!outputFile.exists()) {
            outputFile.mkdirs();
        }
        if (!outputSongsFile.exists()) {
            outputSongsFile.mkdirs();
        }
        try (FileWriter fileWriter = new FileWriter(outputPath + "songlist.txt", false)) {
            for (JsonElement jsonElement : songs) {
                try {
                    JsonObject song = jsonElement.getAsJsonObject();
                    String keyword = song.get("keyword").getAsString();
                    String name = new String(decoder.decode(song.get("name").getAsString()), StandardCharsets.UTF_8).replace('>', '＞');
                    String artist = new String(decoder.decode(song.get("artist").getAsString()), StandardCharsets.UTF_8).replace('>', '＞');
                    String bpm = song.get("bpm").getAsString().trim();
                    if (name.contains(",")) {
                        name = "\"" + name.replace("\"", "\"\"") + "\"";
                    }
                    if (artist.contains(",")) {
                        artist = "\"" + artist.replace("\"", "\"\"") + "\"";
                    }
                    if (fileList.contains(keyword)) {
                        File[] files1 = new File(inputFolderPath + keyword).listFiles(File::isFile);
                        if (files1 == null) continue;
                        int minBpm = 0, maxBpm = 0;
                        try {
                            minBpm = maxBpm = Integer.parseInt(bpm);
                        } catch (NumberFormatException e) {
                            String[] split = bpm.split("~");
                            if (split.length == 2) {
                                try {
                                    minBpm = Integer.parseInt(split[0]);
                                    maxBpm = Integer.parseInt(split[1]);
                                } catch (NumberFormatException ex) {
                                    System.out.println("Warning：" + keyword + "的bpm不是预期的格式，请手动输入bpm");
                                }
                            } else {
                                System.out.println("Warning：" + keyword + "的bpm不是预期的格式，请手动输入bpm");
                            }
                        }
//                        for (File file : files1) {
//                            String awa = file.getName();
//                            String qwq = awa.substring(0, awa.lastIndexOf('.'));
//                            String ext = awa.substring(awa.lastIndexOf('.'));
//                            switch (qwq) {
//                                case "base":
//                                    if (ext.equals(".jpg") || ext.equals(".png") || ext.equals(".ogg") || ext.equals(".wav")) copyFileUsingStream(file, new File(outputSongPath + keyword + ext));
//                                    break;
//                                case "preview":
//                                    if (ext.equals(".ogg") || ext.equals(".wav")) copyFileUsingStream(file, new File(outputSongPath + "pre." + keyword + ext));
//                                    break;
//                            }
//                            if (ext.equals(".txt")) {
//                                try {
//                                    int hard = Integer.parseInt(qwq);
//                                    copyFileUsingStream(file, new File(outputSongPath + keyword + "." + hard + ".txt"));
//                                } catch (NumberFormatException ignored) {
//
//                                }
//                            }
//                        }
                        String output = "20000," + name + "," + artist + "," + keyword + "," + minBpm + "," + maxBpm + ",0,0," + artist;
                        fileWriter.write(output + System.lineSeparator());
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        if (!source.exists()) throw new IOException();
        try (InputStream is = Files.newInputStream(source.toPath()); OutputStream os = Files.newOutputStream(dest.toPath())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}
