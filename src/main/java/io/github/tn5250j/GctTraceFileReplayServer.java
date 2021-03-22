package io.github.tn5250j;

import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static picocli.CommandLine.Help.Visibility.ALWAYS;

public class GctTraceFileReplayServer implements Runnable {

    private static final AtomicInteger connectionCounter = new AtomicInteger(0);
    private final Logger logger;

    @CommandLine.Parameters(index = "0", description = "The GCT trace file to use.")
    private File file;

    @CommandLine.Option(names = {"-p", "--port"}, description = "the TCP port to listen to.", defaultValue = "23232", showDefaultValue = ALWAYS)
    private int port;

    public GctTraceFileReplayServer() {
        this.logger = Logger.getLogger(GctTraceFileReplayServer.class.getName());
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        int exitCode = new CommandLine(new GctTraceFileReplayServer()).execute(args);
        System.exit(exitCode);
    }

    private void run2() throws Exception {
    }

    @Override
    public void run() {
        logger.info("Using port=" + this.port);
        logger.info("Using file=" + this.file);
        logger.info("Waiting for client connections...");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                int newId = connectionCounter.incrementAndGet();
                ScreenResponseHandler screenResponseHandler = new ScreenResponseHandler(socket, newId, file);
                executor.submit(screenResponseHandler);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
