import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
	    String FILENAME = "database.txt";


        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(1);
        }

        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    clientSocket.getInputStream()));
            String inputLine = "";

            String temp;

            while((temp = in.readLine()) != null){
                System.out.println(temp);
                inputLine += temp;
            }

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
				bw.write(inputLine);
				bw.close();
				System.out.println("Done writing to file!");
			} catch (IOException e) {
				e.printStackTrace();
			}

            clientSocket.close();
            in.close();
        }
    }
}