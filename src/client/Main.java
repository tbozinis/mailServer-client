/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import mail.*;

/**
 *
 * @author admin
 */
public class Main {

    private Socket requestSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Scanner s;

    //the program's client
    private Client client;

    private Request request;
    
    public Main() {

        client = null;

        try {
            requestSocket = new Socket("localhost", 3000);
            System.out.println("Connected to localhost in port 2004");
            out = new ObjectOutputStream(requestSocket.getOutputStream());//,true
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendR(Request r) throws InterruptedException, IOException {
        out.flush();
        out.writeObject(r);
//        Thread.sleep(1000);
        out.flush();

        s = new Scanner(System.in);
    }

    private void printLoginMenu() {
        System.out.println("1. Login");
        System.out.println("\n2. Creat new account...");

        int r = 0;
        boolean f = true;
        while (f) {
            try {
                r = Integer.parseInt(s.nextLine());
                if (r == 1 || r == 2) {
                    f = false;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        switch (r) {
            case 1:
                loggin();
                break;
            case 2:
                creatAcc();
                break;
        }
    }

    private void loggin() {
        
    }

    private void creatAcc() {
    }

    private boolean isClientLogedIn()
    {
        return client != null;
    }
    
    public static void main(String[] args) throws InterruptedException, IOException {
        Main m = new Main();
//        while (!m.isClientLogedIn()) {            
//            m.printLoginMenu();
//        }
        
        Request r = new Request("teo", new Client("teo"));
        m.sendR(r);
    }
}
