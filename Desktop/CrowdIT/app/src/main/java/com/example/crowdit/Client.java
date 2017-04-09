package com.example.crowdit;

/**
 * Created by oscar on 4/9/17.
 */

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by oscar on 4/9/17.
 */

public class Client extends AsyncTask<Void, Integer, Integer> {

    private String data;

    public Client(String data){
        this.data = data;
    }

    @Override
    protected Integer doInBackground(Void... params){
        String hostName = "10.0.2.2";
        int portNumber = 4444;

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ) {

            if (this.data != null) {
                out.println(this.data);
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }

        return 100;
    }
}