package valery.kovshov;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

    private static void GetFilesPaths(String path, FileOutputStream FOS) throws IOException {
        File file = new File(path);
        if (file.isFile()) {
            try {
                StringBuilder HexString = new StringBuilder(Integer.toHexString(hashForNorm(Files.readAllBytes(Paths.get(path)))));
                while (HexString.length() < 8)
                    HexString.insert(0, "0");
                FOS.write((HexString + " " + path + "\n").getBytes());
            } catch (OutOfMemoryError e) {
                StringBuilder HexString = new StringBuilder(Integer.toHexString(hashForBig(path)));
                while (HexString.length() < 8)
                    HexString.insert(0, "0");
                FOS.write((HexString + " " + path + "\n").getBytes());
            }
        } else if (file.isDirectory()) {
            String[] list = file.list();
            assert list != null;
            for (String paths : list) {
                paths = file.getPath() + "\\" + paths;
                GetFilesPaths(paths, FOS);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File input;
        File output;
        if (args.length > 2 ){
            StringBuilder path1 = new StringBuilder(args[0]);
            StringBuilder path2 = new StringBuilder();
            for (int i = 0; i < args.length - 1; i++){
                if (args[i + 1].charAt(1) != ':'){
                    path1.append(" ").append(args[i + 1]);
                }
                else {
                    path2 = new StringBuilder(args[i + 1]);
                    for (int j = i + 2; j < args.length; j ++){
                        path2.append(" ").append(args[j]);
                    }
                    break;
                }
            }
            input = new File(path1.toString());
            output = new File(path2.toString());
        }else {
            input = new File(args[0]);
            output = new File(args[1]);
        }
        List<String> strings = Files.readAllLines(Paths.get(input.getPath()));
        FileOutputStream FOS = new FileOutputStream(output);
        assert strings != null;
        for (String paths : strings) {
            GetFilesPaths(paths, FOS);
        }
        FOS.close();
    }
}