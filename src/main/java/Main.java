import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.IntStream;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class Main {

    static final int MAX_BYTES_TO_READ = 2048;
    static final int BUFFER_SIZE = 16; // must be greater than 10

    public static void main(String[] args) throws IOException {

        Files.list(Paths.get("D:\\Nick\\Music\\2019-05-07"))
                .filter(Files::isRegularFile)
                .filter(x -> x.toString().toLowerCase().endsWith("mp3"))
                .peek(System.out::println)
                .forEach(Main::processMp3);
        //processMp3("C:\\tmp\\test.mp3");

    }

    public static void processMp3(Path path) {

        try (BufferedInputStream input = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ))) {

            int numRead = new Id3Processor().ProcessID3Tags(input, 0);
            System.out.println();
            System.out.println();

            int i; // num bytes read in loop

//            System.out.println();
//            System.out.println("===================================");
//            System.out.println("Actual song Data");
//            System.out.println("===================================");
//
//
//            byte[] buffer = new byte[BUFFER_SIZE];
//            while ((numRead < MAX_BYTES_TO_READ && (i = input.read(buffer, 0, BUFFER_SIZE)) != -1)) {
//                numRead += i;
//                System.out.print(printHexBinary(buffer));
//            }
        } catch (IOException e) {
            // code
            e.printStackTrace();
        }
    }

}
