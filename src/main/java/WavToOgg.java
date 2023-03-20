import java.io.*;
import java.nio.charset.StandardCharsets;

public class WavToOgg {
    public static void main(String[] args) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("H:\\DR3 1.66\\sb.bat", false), StandardCharsets.UTF_8))) {
            bufferedWriter.write("@echo off");
            bufferedWriter.newLine();
            bufferedWriter.write("chcp 65001");
            bufferedWriter.newLine();
            File[] files = new File("H:\\DR3 1.66\\raw\\AudioClip").listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".wav"));
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String fileName = file.getName();
                bufferedWriter.write("ffmpeg -i \"" + absolutePath + "\" -f ogg \"H:\\DR3 1.66\\raw\\AudioClip\\" + fileName.substring(0, fileName.lastIndexOf(".")) + ".ogg\"");
                bufferedWriter.newLine();
                bufferedWriter.write("del \"" + absolutePath + "\"");
                bufferedWriter.newLine();
            }
            bufferedWriter.write("pause");
            bufferedWriter.newLine();
        }
    }
}
