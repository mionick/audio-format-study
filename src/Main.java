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

    static final int MAX_BYTES_TO_READ = 4096;
    static final int BUFFER_SIZE = 16; // must be greater than 10

    public static void main(String[] args) {
        Path path = Paths.get("C:\\tmp\\test.mp3");

        try (InputStream input = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ))) {


            int i; // num bytes read in loop
            int numRead = 0; // total read
            byte[] buffer = new byte[10];

            // Check if there is an ID3 header at the beginning of the file.
            // If there is, we need to process it and strip it
            // ID3 Tag would have 10 bytes.
            input.read(buffer, 0, 10);
            numRead += 10;
            System.out.println(new String(buffer, StandardCharsets.US_ASCII));
            System.out.println(printHexBinary(buffer));


            // hex for characters "ID3"
            if (
                    buffer[0] == 0x49 && buffer[1] == 0x44 && buffer[2] == 0x33
            ) {
                byte majorVersion = buffer[3];
                byte minorVersion = buffer[4];
                byte flags = buffer[5];
                /*
                http://id3lib.sourceforge.net/id3/id3v2com-00.html#sec2
                "An easy way of calculating the tag size is A*2^21+B*2^14+C*2^7+D =
                A*2097152+B*16384+C*128+D,
                where A is the first byte, B the second, C the third and D the fourth byte."
                 */
                int size = getId3Size(buffer, 6);
                System.out.println(String.format("ID3 version: %d.%d flags: %s size: %d bytes", majorVersion, minorVersion, getBinaryString(flags), size) );

                byte[] fullTag = new byte[size];
                i = input.read(fullTag, 0, size);
                numRead += i;
                System.out.println(new String(fullTag, StandardCharsets.UTF_8));
                System.out.println(printHexBinary(fullTag));

                /*
                Interpreting the frames of the ID3 Tag.
                "The headers of the frames are similar in their construction.
                They consist of one three character identifier (capital A-Z and 0-9) and one three byte size field, making a total of six bytes. The header is excluded from the size."
                 */
                i = 0;
                while(i < size) {
                    System.out.println("i: " + i);

                    // It's possible for the ID3 tag to end with padding, which must just be all 0's. In that case we are done.
                    if (fullTag[i] == 0 && fullTag[i + 1] == 0 && fullTag[i+2] == 0) {
                        System.out.println("Padding at end of header.");
                        System.out.println("Padding (Hex): " + printHexBinary(Arrays.copyOfRange(fullTag, i, size - 1)));
                        i= size;
                        continue;
                    }

                    // If not padding, then print out what this tag frame has:
                    String tag = new String(fullTag, i, 4);
                    int tagSize =  getId3Size(fullTag, 4 + i);
                    System.out.println(String.format("Tag: %s size: %d flags: %s", tag, tagSize, getBinaryString(fullTag[i+8]) + getBinaryString(fullTag[i+9])));
                    System.out.println("TagData (Hex): " + printHexBinary(Arrays.copyOfRange(fullTag, 10 + i, 10 + tagSize + i)));
                    // the first byte indicates which text encoding is used, so we strip it off. (0x03 indicates UTF8)
                    // Strings are also null terminated, I convert that to spaces to be more readable.
                    System.out.println("TagData (UTF): " + new String(Arrays.copyOfRange(fullTag, 10 + i + 1, 10 + tagSize + i), StandardCharsets.UTF_8).replace((char) 0, ' '));

                    i += 10 + tagSize;
                }


            }


            System.out.println();
            System.out.println("===================================");
            System.out.println("Actual song Data");
            System.out.println("===================================");


            buffer = new byte[BUFFER_SIZE];
            while ((numRead < MAX_BYTES_TO_READ && (i = input.read(buffer, 0, BUFFER_SIZE)) != -1)) {
                numRead += i;
                System.out.print(printHexBinary(buffer));
            }
        } catch (IOException e) {
            // code
            e.printStackTrace();
        }
    }


    static int getId3Size(byte[] buffer, int offset) {
        return buffer[offset]*2097152+buffer[offset+1]*16384+buffer[offset+2]*128+buffer[offset+3];
    }

    static String getBinaryString(byte flags) {
        return String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');
    }
}
