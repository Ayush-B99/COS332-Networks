import java.io.*;
import java.net.*;

public class Prac2Server {
    public static void main(String[] args) throws IOException {
        int port = 5001;
        Prac2DB db = new Prac2DB();
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("✓ Server started on port " + port);
        System.out.println("✓ Database loaded");
        System.out.println("Waiting for clients...\n");

        while (true) 
        {
            Socket clientSocket = serverSocket.accept();
            System.out.println("✓ Client connected!");

            ClientHandler handler = new ClientHandler(clientSocket, db);
            handler.start();
        }
    }
}
