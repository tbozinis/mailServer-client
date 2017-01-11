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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import mail.*;

/**
 *
 * @author admin
 */
public class Main {

    private boolean clientLogedIn;

    private int port;
    private Socket requestSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Scanner s;

    //the program's client
    private String username;

    /**
     * Establish a connection with the server to localhost at the given port.
     */
    public void connect() {
        try {
            requestSocket = new Socket("localhost", port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());//true
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Flush the output stream and sends the request at the server.
     *
     * @param Request
     * @throws InterruptedException
     * @throws IOException
     */
    private void sendR(Request r) throws InterruptedException, IOException {
        out.flush();
        out.writeObject(r);
    }

    /**
     * Gets the answer from the server.
     *
     * @return Answer
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Answer getAnswer() throws IOException, ClassNotFoundException {
        return (Answer) in.readObject();
    }

    /**
     * Connects to server and attempts a login or a new account.
     *
     * @throws IOException
     */
    private void printLoginMenu() throws IOException {
        System.out.println("\n> login");
        System.out.println("> new account");

        String r = s.nextLine();

        switch (r) {
            case "login":
                connect();
                loggin();
                requestSocket.close();
                break;
            case "new account":
                connect();
                creatAcc();
                requestSocket.close();
                break;
            default:
                System.out.println("unidentifed command, try again...");
        }
    }

    private void loggin() {
        System.out.print("\nusername: ");
        String sender = s.nextLine();
        System.out.print("password: ");
        String password = s.nextLine();
        Request r = new Request(sender, password, Request.Type.LOGIN);

        try {
            sendR(r);
            Answer a = getAnswer();
            if (null != a.getType()) {
                switch (a.getType()) {
                    case WRONG_USERNAME:
                        System.out.println(">> wrong username...");
                        break;
                    case WRONG_PASSWORD:
                        System.out.println(">> wrong password...");
                        break;
                    default:
                        this.username = sender;
                        clientLogedIn = true;
                        break;
                }
            }

        } catch (InterruptedException | IOException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void creatAcc() {
        System.out.print("\nUsername: ");
        String tempUsername = s.nextLine();
        System.out.print("Password: ");
        String password = s.nextLine();

        Request r = new Request(tempUsername, new Client(tempUsername, password), Request.Type.NEW_CLIENT);
        try {
            sendR(r);
            Answer a = getAnswer();
            if (a.getType() == Answer.Type.WRONG_USERNAME) {
                System.out.println(">> username already exists...");
            } else {
                this.username = tempUsername;
                clientLogedIn = true;
            }
        } catch (InterruptedException | IOException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isClientLogedIn() {
        return clientLogedIn;
    }

    private int mainMenu() throws IOException {
        int i = 0;

        System.out.println("\n> new mail");
        System.out.println("> show mails");
        System.out.println("> read mail");
        System.out.println("> delete mail");
        System.out.println("> log out");
        System.out.println("> exit");

        String r = s.nextLine();

        switch (r) {
            case "new mail":
                connect();
                newMail();
                requestSocket.close();
                break;
            case "show mails":
                connect();
                showMails();
                requestSocket.close();
                break;
            case "read mail":
                connect();
                readMail();
                requestSocket.close();
                break;
            case "delete mail":
                connect();
                deleteMail();
                requestSocket.close();
                break;
            case "exit":
                i = 1;
                break;
            case "log out":
                System.out.println("desconected...");
                username = null;
                do {
                    printLoginMenu();
                } while (username == null);
                break;
            default:
                System.out.println("unidentifed command, try again...");
        }
        return i;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        try {
            int p = Integer.parseInt(args[0]);

            Main m = new Main(p);
            System.out.println("Wellcome to Thlia!");
            while (!m.isClientLogedIn()) {
                m.printLoginMenu();
            }

            System.out.println("client logged in: " + m.username);

            int flag = 0;
            while (flag == 0) {
                flag = m.mainMenu();
            }
        } catch (NumberFormatException | IOException ex) {
            System.out.println(ex.toString());
        }

    }

    public Main(int port) {
        this.port = port;
        s = new Scanner(System.in);
    }

    private void newMail() {
        Mail m = Mail.creatMail(username, s);
        Request req = new Request(Request.Type.NEW_MAIL, username, m);
        {
            try {
                sendR(req);
                Answer a = getAnswer();
                if (a.type.equals(Answer.Type.WRONG_USERNAME)) {
                    System.out.println(">>> error: wrong reciver username...");
                } else if (a.type.equals(Answer.Type.NEW_MAIL_SEND)) {
                    System.out.println(">>> mail sended...");
                }
            } catch (InterruptedException | IOException | ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void showMails() throws IOException {
        Request req = new Request(Request.Type.GET_MAILS, username);
        try {
            sendR(req);
            Answer a = getAnswer();

            if (a.type.equals(Answer.Type.GET_MAILS_RECEIVING)) {
                for (String str : (ArrayList<String>) a.getObj()) {
                    System.out.println(str);
                }
            }
        } catch (InterruptedException | IOException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readMail() {
        System.out.println(">> Type the id of the mail you want to read.");

        String str = s.nextLine();
        try {
            int i = Integer.parseInt(str);
            if (i < 0) {
                System.out.println(">> Type only numbers > 0...");
                return;
            }
            Request req = new Request(Request.Type.GET_MAIL, username, i);
            sendR(req);
            Answer a = getAnswer();
            if (a.getType().equals(Answer.Type.WRONG_ID)) {
                System.out.println(">> Id not found...");
            } else if (a.getType().equals(Answer.Type.MAIL)) {
                Mail m = (Mail) a.getObj();
                System.out.println(m.toString());
            }

        } catch (NumberFormatException | InterruptedException | IOException | ClassNotFoundException e) {
            System.out.println(">> Type numbers only...");
        }
    }

    private void deleteMail() {
        System.out.println(">> Type the id of the mail you want to delete.");

        String str = s.nextLine();
        try {
            int i = Integer.parseInt(str);
            if (i < 0) {
                System.out.println(">> Type only numbers > 0...");
                return;
            }
            Request req = new Request(Request.Type.DELETE_MAIL, username, i);
            sendR(req);
            Answer a = getAnswer();
            if (a.getType().equals(Answer.Type.WRONG_ID)) {
                System.out.println(">> Id not found...");
            } else if (a.getType().equals(Answer.Type.MAIL_DELETED)) {
                System.out.println(">> mail deleted...");
            }

        } catch (NumberFormatException | InterruptedException | IOException | ClassNotFoundException e) {
            System.out.println(">> Type numbers only...");
        }
    }
}
