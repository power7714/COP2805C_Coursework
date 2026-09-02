package cop2805;

import java.io.*;
import java.net.*;

/**
 * SteveAgentServerd.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 *
 * This is a simple socket server that runs locally.
 * It waits for a client to connect, reads the encrypted text,
 * decrypts the text using the Caesar cipher, and sends the result back.
 *
 */
public class AgentServer {

    // The port this server listens on.
    // If 1236 is taken, try 1237, 1238, etc.
    private static final int PORT = 1236;

    // Command that tells the server to shut down.
    private static final String SHUTDOWN_COMMAND = "shutdown";

    // main()
    // Starts the server and keeps it running until shutdown.
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  Sterling Intelligence Service");
        System.out.println("  Decryption Server");
        System.out.println("=".repeat(50));
        System.out.println("  Listening on port " + PORT);
        System.out.println("  Send \"shutdown\" to stop the server.");
        System.out.println("-".repeat(50));

        startServer();
    }

    // startServer()
    // Opens the ServerSocket and loops waiting for clients.
    // Stops when the client sends the shutdown command.
    private static void startServer() {
        try (ServerSocket serverSocket = openServerSocket()) {

            boolean running = true;

            while (running) {
                System.out.println("  Waiting for client connection...");

                Socket clientSocket = acceptClient(serverSocket);
                if (clientSocket == null) continue;

                running = handleClient(clientSocket);
            }

            System.out.println("  Shutdown command received. Server is closing.");

        } catch (IOException e) {
            System.out.println("  Server error: " + e.getMessage());
        }

        System.out.println("=".repeat(50));
        System.out.println("  Server stopped.");
        System.out.println("=".repeat(50));
    }

    // openServerSocket()
    // Creates and returns a ServerSocket bound to PORT.
    // Throws IOException if the port is already in use.
    private static ServerSocket openServerSocket() throws IOException {
        try {
            return new ServerSocket(PORT);
        } catch (BindException e) {
            throw new IOException(
                "Port " + PORT + " is already in use. " +
                "Try changing PORT to a different number.", e
            );
        }
    }

    // acceptClient()
    // Waits for a client to connect and returns their socket.
    // Returns null if something goes wrong so the loop can continue.
    private static Socket acceptClient(ServerSocket serverSocket) {
        try {
            Socket client = serverSocket.accept();
            System.out.println("  Client connected: " + client.getInetAddress());
            return client;
        } catch (IOException e) {
            System.out.println("  Failed to accept client: " + e.getMessage());
            return null;
        }
    }

    // handleClient()
    // Reads the message from the client, decrypts it, and sends
    // the result back. Closes the client socket when done.
    //
    // Returns false if the client sent the shutdown command,
    // true if the server should keep running.
    private static boolean handleClient(Socket clientSocket) {
        try (
            clientSocket;
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(clientSocket.getOutputStream()), true)
        ) {
            // Read the message the client sent
            String received = reader.readLine();
            System.out.println("  Received: " + received);

            if (SHUTDOWN_COMMAND.equalsIgnoreCase(received)) {
                writer.println("SHUTDOWN_ACK");
                return false;
            }

            if (received == null || received.isEmpty()) {
                writer.println("ERROR: Empty message received.");
            } else {
                String decrypted = decryptMessage(received);
                System.out.println("  Decrypted: " + decrypted);
                writer.println(decrypted);
            }

        } catch (IOException e) {
            System.out.println("  Error handling client: " + e.getMessage());
        }

        return true; // keep the server running
    }

    // decryptMessage()
    // Takes an encrypted string and returns the decrypted version.
    // The encryption was done by subtracting 10 from each character.
    // To undo it, we add 10 back to each character.
    private static String decryptMessage(String encrypted) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < encrypted.length(); i++) {
            result.append((char)(encrypted.charAt(i) + 10));
        }

        return result.toString();
    }

    // INNER CLASS: DecryptServerTests
    // Unit tests for the server logic.
    // To run: temporarily change main() to call
    //         DecryptServerTests.runAll() instead of startServer().
    static class DecryptServerTests {

        private static int passed = 0;
        private static int failed = 0;

        public static void runAll() {
            System.out.println("=".repeat(50));
            System.out.println("  DecryptServer -- Unit Tests");
            System.out.println("=".repeat(50));

            testDecryption();
            testEdgeCases();
            testKnownValues();
            testShutdownCommand();

            System.out.println("-".repeat(50));
            System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
            System.out.println("=".repeat(50));
        }

        private static void testDecryption() {
            System.out.println("\n  [Decryption Tests]");

            check("j[ij'()* decrypts to test1234",
                decryptMessage("j[ij'()*"), "test1234");

            check("Empty string stays empty",
                decryptMessage(""), "");

            // 'A' = 65 + 10 = 75 = 'K'
            check("'A' decrypts to 'K'",
                decryptMessage("A"), "K");
        }

        private static void testEdgeCases() {
            System.out.println("\n  [Edge Case Tests]");

            String original = "Hello Agent";
            String encrypted = encrypt(original);
            check("Round-trip: Hello Agent",
                decryptMessage(encrypted), original);

            String longStr = "j[ij".repeat(500);
            check("Long string does not crash",
                decryptMessage(longStr).length() > 0 ? "ok" : "fail", "ok");

            check("Space decrypts to '*'",
                decryptMessage(" "), "*");
        }

        private static void testKnownValues() {
            System.out.println("\n  [Known Value Tests]");

            check("'j' decrypts to 't'", decryptMessage("j"), "t");
            check("'[' decrypts to 'e'", decryptMessage("["), "e");
            check("'i' decrypts to 's'", decryptMessage("i"), "s");
            check("'j[ij' decrypts to 'test'", decryptMessage("j[ij"), "test");
        }

        private static void testShutdownCommand() {
            System.out.println("\n  [Shutdown Command Tests]");

            check("Shutdown command is correct keyword",
                SHUTDOWN_COMMAND, "shutdown");

            check("Shutdown check is case-insensitive",
                SHUTDOWN_COMMAND.equalsIgnoreCase("SHUTDOWN") ? "ok" : "fail", "ok");
        }

        private static String encrypt(String text) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                sb.append((char)(text.charAt(i) - 10));
            }
            return sb.toString();
        }

        private static void check(String name, String actual, String expected) {
            if (expected.equals(actual)) {
                System.out.printf("  [PASS] %s%n", name);
                passed++;
            } else {
                System.out.printf("  [FAIL] %s%n", name);
                System.out.printf("         Expected : \"%s\"%n", expected);
                System.out.printf("         Actual   : \"%s\"%n", actual);
                failed++;
            }
        }
    }
}

/*
 * -------------------------------------------------------
 * HOW TO SWITCH AgentClient TO USE THIS SERVER
 * -------------------------------------------------------
 * To use this local server instead of the PHP
 * server on sixactualstudios.com, replace the sendToServer()
 * method in AgentClient's ServerConnection inner class with
 * this socket-based version:
 *
 *   private static final String HOST = "127.0.0.1";
 *   private static final int    PORT = 1236;
 *
 *   static String sendToServer(String text) throws IOException {
 *       try (
 *           Socket socket = new Socket(HOST, PORT);
 *           PrintWriter writer = new PrintWriter(
 *               new OutputStreamWriter(socket.getOutputStream()), true);
 *           BufferedReader reader = new BufferedReader(
 *               new InputStreamReader(socket.getInputStream()))
 *       ) {
 *           writer.println(text);
 *           return reader.readLine();
 *       }
 *   }
 *
 */