package io.github.tn5250j;

import io.github.tn5250j.gct.GctFileReader;
import io.github.tn5250j.gct.ReaderState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import static io.github.tn5250j.gct.ReaderState.Direction.Recv;
import static java.lang.String.format;

class ScreenResponseHandler implements Runnable {

    private final Socket clientSocket;
    private final int id;
    private final File file;
    private final Logger logger;

    public ScreenResponseHandler(Socket clientSocket, int id, File file) {
        this.clientSocket = clientSocket;
        this.id = id;
        this.file = file;
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void run() {
        try {
            serve();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignore if already closed
            }
        }
    }

    private void serve() throws IOException {
        GctFileReader gctFileReader = new GctFileReader(file);
        logger.info(format("#%1$d new connection...", this.id));
        InputStream is = clientSocket.getInputStream();
        OutputStream out = clientSocket.getOutputStream();
        while (clientSocket.isConnected()) {
            // write
            ReaderState readerState = gctFileReader.readNextState(Recv);
            if (null == readerState) {
                logger.info(format("#%1$d End of packet data.", this.id));
                return;
            }
            logger.info(format("#%1$d Sending screen nr=%2$d", this.id, readerState.screenNumber));
            out.write(readerState.data);

            // read
            int bytesExpected = 256;
            byte[] buffer = new byte[bytesExpected];
            int readCount = is.read(buffer);
            if (readCount < 0) {
                logger.info(format("#%1$d No more bytes to read.", this.id));
                return;
            }
            logger.info(format("#%1$d Read %2$d bytes.", this.id, readCount));
        }
    }
}
