import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecursiveHash {

    private static final int FNV_32_INIT = 0x811c9dc5;
    private static final int FNV_32_PRIME = 0x01000193;

    private static int hashForNorm(final byte[] k) {
        int rv = FNV_32_INIT;
        for (byte aK : k) {
            rv ^= aK;
            rv *= FNV_32_PRIME;
        }
        return rv;
    }

    private static int hashForBig(String path) {
        int rv = FNV_32_INIT;
        try {
            FileInputStream FIS = new FileInputStream(path);
            int in;
            System.out.println("Следующий файл огромен, чтение может занять продолжительное время");
            while ((in = FIS.read()) != -1) {
                rv ^= in;
                rv *= FNV_32_PRIME;
            }
            FIS.close();
        } catch (IOException e) {
            System.out.println("00000000 " + path);
        }
        return rv;
    }

    private static void GetFilesPaths(String path, Set<String> set) throws IOException {
        File file = new File(path);
        if (file.isFile()) {
            try {
                StringBuilder HexString = new StringBuilder(Integer.toHexString(hashForNorm(Files.readAllBytes(Paths.get(path)))));
                while (HexString.length() < 8)
                    HexString.insert(0, "0");
                set.add(HexString + " " + path + "\r\n");
            } catch (OutOfMemoryError e) {
                StringBuilder HexString = new StringBuilder(Integer.toHexString(hashForBig(path)));
                while (HexString.length() < 8)
                    HexString.insert(0, "0");
              set.add(HexString + " " + path + "\n");
            }
        } else if (file.isDirectory()) {
            String[] list = file.list();
            assert list != null;
            for (String paths : list) {
                paths = file.getPath() + "\\" + paths;
                GetFilesPaths(paths, set);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File input = new File(args[0]);
        File output = new File(args[1]);

        List<String> strings = Files.readAllLines(Paths.get(input.getPath()));
        Set<String> set = new HashSet<>();
        assert strings != null;
        for (String paths : strings) {
            GetFilesPaths(paths, set);
        }
        FileOutputStream FOS = new FileOutputStream(output);
        for (String str:set
             ) {
            FOS.write(str.getBytes());
        }
        FOS.close();
    }
}