package io.github.tn5250j.gct;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GctFileReader {

    public static final String REC_TYPE = "RecType=";
    public static final String LENGTH = "Length=";

    private final Pattern screenNumberPattern = Pattern.compile("\\[(\\d+)]");
    private final Pattern hexArrayPattern = Pattern.compile("\\d{6}= (.*)");

    private BufferedReader reader;
    private ReaderState readerState;

    public GctFileReader(File filename) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis);
        reader = new BufferedReader(isr);
        readerState = new ReaderState();
    }

    public ReaderState readNextState() throws IOException {
        if (null == reader) {
            return null;
        }
        String line;
        while (null != (line = reader.readLine())) {
            readerState.lineNumber++;
            if (line.trim().isEmpty()) {
                if (readerState.lineNumber > 1) break;
            }
            Matcher matcher = screenNumberPattern.matcher(line);
            if (matcher.matches()) {
                readerState.initNewScreenState(Integer.parseInt(matcher.group(1)));
            } else if (line.startsWith(REC_TYPE)) {
                readerState.direction = ReaderState.Direction.valueOf(line.substring(REC_TYPE.length()));
            } else if (line.startsWith(LENGTH)) {
                readerState.length = Integer.parseInt(line.substring(LENGTH.length()));
            } else {
                matcher = hexArrayPattern.matcher(line);
                if (matcher.matches()) {
                    String hexArrayString = matcher.group(1);
                    hexArrayString = hexArrayString.replace(" ", "");
                    hexArrayString = hexArrayString.replace("|", "");
                    byte[] bytes = DatatypeConverter.parseHexBinary(hexArrayString);
                    readerState.data = concat(readerState.data, bytes);
                }
            }
        }
        closeReaderIfNeeded(line);
        return readerState;
    }

    public ReaderState readNextState(ReaderState.Direction withDiretion) throws IOException {
        ReaderState readerState;
        do {
            readerState = readNextState();
        } while (readerState.direction != withDiretion);
        return readerState;
    }


    private void closeReaderIfNeeded(String line) throws IOException {
        if (line == null) {
            reader.close();
            reader = null;
        }
    }

    private static byte[] concat(byte[] first, byte[] second) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(first);
        outputStream.write(second);
        return outputStream.toByteArray();
    }

}
