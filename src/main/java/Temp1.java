import java.io.*;
import java.nio.file.Files;

public class Temp1 {
    public static void main(String[] args) throws IOException {
        File a = new File("H:\\DR3 1.65\\songs"), b = new File("H:\\DR3 1.65");
        System.out.println(b.getAbsolutePath());
        File[] files = a.listFiles(File::isDirectory);
        for (File file : files) {
            File[] oggs = file.listFiles(pathname -> pathname.getName().endsWith(".ogg"));
            for (File ogg :
                    oggs) {
                switch (ogg.getName()) {
                    case "preview.ogg":
                        break;
                    case "base.ogg":
                        copyFileUsingStream(ogg, new File(b.getAbsolutePath() + "/" + file.getName() + ".ogg"));
                        break;
                    default:
                        copyFileUsingStream(ogg, new File(b.getAbsolutePath() + "/" + file.getName() + "." + ogg.getName()));
                        break;
                }
            }
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
