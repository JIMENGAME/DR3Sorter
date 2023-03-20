import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 3 || !new File(args[0]).exists() || !new File(args[0]).isFile() || !new File(args[1]).exists() || !new File(args[1]).isDirectory())
            return;
        if (!new File(args[2]).exists()) new File(args[2]).mkdirs();
        if (!args[2].endsWith("/") || !args[2].endsWith("\\")) args[2] += "/";
        if (!args[1].endsWith("/") || !args[1].endsWith("\\")) args[1] += "/";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0])); Qwq qwq = new Qwq(args[2] + "/convert.bat", new File(args[2] + "songs/"))) {
            String line;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonArray jsonArray = new JsonArray();
            File[] files = new File(args[1] + "TextAsset").listFiles();
            if (new File(args[2] + "convert.bat").exists()) new File(args[2] + "convert.bat").delete();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().equals("")) continue;
                String[] lines = line.split(">");
                JsonObject jsonObject = new JsonObject();
                String title = lines[1];
                String artist = lines[2];
                String keyword = lines[3];
                float minBPM = Float.parseFloat(lines[4].trim());
                float maxBPM = Float.parseFloat(lines[5].trim());
                Base64.Encoder encoder = Base64.getEncoder();
                jsonObject.add("keyword", new JsonPrimitive(keyword));
                jsonObject.add("name", new JsonPrimitive(encoder.encodeToString(title.getBytes(StandardCharsets.UTF_8))));
                jsonObject.add("artist", new JsonPrimitive(encoder.encodeToString(artist.getBytes(StandardCharsets.UTF_8))));
                String bpm = (minBPM == maxBPM) ? (minBPM + "") : (minBPM + "~" + maxBPM);
                if (bpm.endsWith(".0")) bpm = bpm.substring(0, bpm.length() - 2);
                jsonObject.add("bpm", new JsonPrimitive(bpm));
                String folder = args[2] + "songs/" + keyword + "/";
                File file1 = new File(args[1] + "AudioClip/");
                if (!new File(folder).exists()) new File(folder).mkdirs();
                try (OutputStream outputStream = Files.newOutputStream(new File(folder + "info.txt").toPath())) {
                    outputStream.write((title + System.lineSeparator() + artist + System.lineSeparator() + bpm + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                    if (files != null) {
                        for (File file : files) {
                            String name = file.getName();
                            if (name.startsWith(keyword + ".")) {
                                String s = folder + name.substring(name.indexOf(".") + 1, name.lastIndexOf(".")) + ".txt";
                                if (!new File(s).exists())
                                    copyFileUsingStream(file, new File(s));
                            }
                        }
                    }
                    if (!new File(folder + "base.ogg").exists()) {
                        copyFileUsingStream(new File(file1.getAbsolutePath() + "/" + keyword + ".wav"), new File(folder + "base.wav"));
                        qwq.Write(keyword, "base");
                    }
                    if (!new File(folder + "preview.ogg").exists()) {
                        copyFileUsingStream(new File(file1.getAbsolutePath() + "/pre." + keyword + ".wav"), new File(folder + "preview.wav"));
                        qwq.Write(keyword, "preview");
                    }
                    if (!new File(folder + "base.png").exists()) {
                        copyFileUsingStream(new File(args[1] + "Sprite/" + keyword + ".png"), new File(folder + "base.png"));
                    }
                    File[] oggOverrides = file1.listFiles(pathname -> Pattern.matches("^" + keyword + "\\.(\\d+)\\.wav?", pathname.getName()));
                    if (oggOverrides.length > 0) {
                        for (File oggOverride : oggOverrides) {
                            String tier = oggOverride.getName();
                            tier = tier.substring(tier.indexOf(".") + 1, tier.lastIndexOf("."));
                            try {
                                int i = Integer.parseInt(tier);
                                if (!new File(folder + tier + ".ogg").exists()) {
                                    // copyFileUsingStream(new File(file1.getAbsolutePath() + "/" + keyword + "." + tier + ".wav"), new File(folder + tier + ".wav"));
                                    qwq.Write(keyword, i);
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                    jsonArray.add(jsonObject);
                } catch (NoSuchFileException ignored) {
                    new File(folder).delete();
                }
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("songs", jsonArray);
            String output = gson.toJson(jsonObject).replace("\\u003d", "=");
            try (OutputStream os = Files.newOutputStream(new File(args[2] + "Songlist.json").toPath())) {
                os.write(output.getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("end");
        }
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        if (!source.exists()) return;
        try (InputStream is = Files.newInputStream(source.toPath()); OutputStream os = Files.newOutputStream(dest.toPath())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}

class Qwq implements AutoCloseable {
    private final PrintWriter bufferedWriter;

    private final File src;

    public Qwq(String batPath, File src) throws IOException {
        bufferedWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(batPath, false), StandardCharsets.UTF_8));
        this.src = src;
        bufferedWriter.println("@echo off");
        bufferedWriter.println("chcp 65001");
        bufferedWriter.flush();
    }

    public void Write(String keyword, String name) {
        bufferedWriter.println("ffmpeg -i \"" + src.getAbsolutePath() + "/" + keyword + "/" + name + ".wav" + "\" -f ogg \"" + src.getAbsolutePath() + "/" + keyword + "/" + name + ".ogg\"");
        bufferedWriter.println("del \"" + src.getAbsolutePath() + "/" + keyword + "/" + name + ".wav\"");
    }

    public void Write(String keyword, int tier) {
        bufferedWriter.println("ffmpeg -i \"" + src.getAbsolutePath() + "/" + keyword + "/" + tier + ".wav" + "\" -f ogg \"" + src.getAbsolutePath() + "/" + keyword + "/" + tier + ".ogg\"");
        bufferedWriter.println("del \"" + src.getAbsolutePath() + "/" + keyword + "/" + tier + ".wav\"");
    }

    @Override
    public void close() {
        bufferedWriter.println("pause");
        bufferedWriter.close();
    }
}