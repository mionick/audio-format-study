import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class Id3Processor {


    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Takes a BufferedInputStream and process any ID3 tag starting at <code>offset</code>.
     * Returns an offset into the stream which is the first byte after the ID3 tag.
     *
     * Note: technically ID3 tags can be appended instead of prepended.
     * This situation is not handled right now.
     * @param input
     * @param offset
     * @return
     * @throws IOException
     */
    public int ProcessID3Tags(BufferedInputStream input, int offset) throws IOException {
        int i; // num bytes read in loop
        int numRead = 0; // total read
        byte[] buffer = new byte[10];

        // Check if there is an ID3 header at the beginning of the file.
        // If there is, we need to process it and strip it
        // ID3 Tag would have 10 bytes.
        input.mark(20); // maximum we can read without ruining the stream is 4096.
        // We'll reset the stream if we do not see an ID3 tag.
        input.read(buffer, 0, 10);
        log.debug(new String(buffer, StandardCharsets.US_ASCII));
        log.debug(printHexBinary(buffer));


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
            log.info(String.format("ID3 version: %d.%d flags: %s size: %d bytes", majorVersion, minorVersion, getBinaryString(flags), size) );
            numRead += 10;

            byte[] fullTag = new byte[size];
            i = input.read(fullTag, 0, size);
            numRead += i;
            log.debug(new String(fullTag, StandardCharsets.UTF_8));
            log.debug(printHexBinary(fullTag));

                /*
                Interpreting the frames of the ID3 Tag.
                "The headers of the frames are similar in their construction.
                They consist of one three character identifier (capital A-Z and 0-9) and one three byte size field, making a total of six bytes. The header is excluded from the size."
                 */
            i = 0;
            while(i < size) {
                log.debug("offset i: " + i);

                // It's possible for the ID3 tag to end with padding, which must just be all 0's. In that case we are done.
                if (fullTag[i] == 0 && fullTag[i + 1] == 0 && fullTag[i+2] == 0) {
                    log.info("Padding at end of header.");
                    log.debug("Padding (Hex): " + printHexBinary(Arrays.copyOfRange(fullTag, i, size - 1)));
                    i= size;
                    continue;
                }

                // If not padding, then print out what this tag frame has:
                String tag = new String(fullTag, i, 4);
                int tagSize =  getId3Size(fullTag, 4 + i);
                log.info(String.format("Tag: %s size: %d flags: %s", tag, tagSize, getBinaryString(fullTag[i+8]) + getBinaryString(fullTag[i+9])));
                log.debug("TagData (Hex): " + printHexBinary(Arrays.copyOfRange(fullTag, 10 + i, 10 + tagSize + i)));
                // the first byte indicates which text encoding is used, so we strip it off. (0x03 indicates UTF8)
                // Strings are also null terminated, I convert that to spaces to be more readable.
                log.info("TagData (UTF): " + new String(Arrays.copyOfRange(fullTag, 10 + i + 1, 10 + tagSize + i), StandardCharsets.UTF_8).replace((char) 0, ' '));

                i += 10 + tagSize;
            }


        } else {
            // We did not see a tag. reset to where the stream was when it was passed in.
            input.reset();
        }

        return numRead;

    }

    int getId3Size(byte[] buffer, int offset) {
        return buffer[offset]*2097152+buffer[offset+1]*16384+buffer[offset+2]*128+buffer[offset+3];
    }

    String getBinaryString(byte flags) {
        return String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');
    }
}
