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

    public Main() {
    }
    
    private void sendR(Request r) throws InterruptedException
    {
        try {
            requestSocket = new Socket("localhost", 3000);
            System.out.println("Connected to localhost in port 2004");
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            
            out.writeObject(r);
            Thread.sleep(1000);
            out.flush();
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        Request r = new Request("teo", new Client("teo"));
        
        Main m = new Main();
        m.sendR(r);
    }
}
