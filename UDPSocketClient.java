package atmserver;

import java.io.ByteArrayInputStream;
import java.net.DatagramSocket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;

/* simplex-talk server, UDP version */
public class UDPSocketClient {

    /**
     * DatagramSocket: sending or receiving point for a packet delivery service.
     * Each packet sent or received on a datagram socket is individually
     * addressed and routed. Multiple packets sent from one machine to another
     * may be routed differently, and may arrive in any order.
     */
    DatagramSocket Socket;

    public UDPSocketClient() {
    }

    public void createAndListenSocket() throws IOException, ClassNotFoundException {

        try {
            Scanner input = new Scanner(System.in);
            int AccountNumber; // 9 digit integer
            int pin; // 4 numbers
            int Request; // used for telling server what action to take
            int amount; // amount of $ to deposit/ withdrawl
            int balance;

            Socket = new DatagramSocket();

            InetAddress IPAddress = InetAddress.getByName("10.100.2.1");
            // IP address of server destination. 

            System.out.println("welcome! what would you like to do? (Please enter in integer units only)"
                    + "\n 0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit");
            while (true) {

                Request = input.nextInt();
                //0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit

                Client client = null; // initializes client

                switch (Request) {
                    case 0: // login command
                        System.out.println("enter account number");
                        AccountNumber = input.nextInt();
                        System.out.println("enter account pin");
                        pin = input.nextInt();
                        client = new Client(AccountNumber, pin);
                        break;

                    case 1: // 1 = balance
                        System.out.println("asking server for balance");
                        client = new Client(1, -1, -1, -1);
                        break;

                    case 2: // 2 = withdrawl
                        System.out.println("How much would you like to withdrawl?");
                        amount = input.nextInt();
                        System.out.println("Withdrawing $" + amount + " dollars.");
                        client = new Client(2, -1, -1, amount);
                        break;

                    case 3: // 3 = deposit
                        System.out.println("How much would you like to deposit?");
                        amount = input.nextInt();
                        System.out.println("Depositing $" + amount + "dollars into your account.");
                        client = new Client(3, -1, -1, amount);
                        break;

                    case 4: // 4 = exit
                        System.out.println("would you like to exit? (Y/N)");
                        if (input.next().toLowerCase() == "y") {
                            client = new Client(4, -1, -1, -1);
                            System.out.println("Goodbye!");
                            System.exit(4);
                            break;
                        } else {
                            System.out.println("What would you like to do?"
                                    + "\n 0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit");
                        }
                        break;
                    default: // edge cases
                        System.out.println("invalid selection. Please try again.");
                        break;
                }
                /* below is to send the client object*/
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                // This class implements an output stream in which the data is written into a byte array. 
                //The buffer automatically grows as data is written to it. The data can be retrieved using toByteArray() and toString().

                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                // writes primitive data types and graphs of Java objects to an OutputStream. The objects can 
                //be read (reconstituted) using an ObjectInputStream. Persistent storage of objects can be 
                //accomplished by using a file for the stream. If the stream is a network socket stream, the 
                //objects can be reconstituted on another host or in another process.

                os.writeObject(client);
                // writes new client object to send message

                byte[] b = outputStream.toByteArray();
                // method creates a newly allocated buffer with the size as the current size of this output stream.

                DatagramPacket msg = new DatagramPacket(b, b.length, IPAddress, 4445);
                // creates new datagram to send with coordinates
                Socket.send(msg); // sends message

                // below is the actual receive operation
                Socket.receive(msg);
                System.err.println("message from <"
                        + msg.getAddress().getHostAddress() + "," + msg.getPort() + ">");

                byte[] data = msg.getData();
                // puts bytes in array through thr datagram packet to retrieve bytes ( b ) 

                ByteArrayInputStream in = new ByteArrayInputStream(data);
                // places bytes into datastream to re-constitute message

                ObjectInputStream is = new ObjectInputStream(in);
                // deserializes primitive data and objects previously written using an ObjectOutputStream.

                Client atmMessage = (Client) is.readObject(); // reads message
                System.out.println("Message received :: " + atmMessage); // prints message
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /* 
//    PSUDO:
//    if (atmMessage(0) == 5)
//    { print " balance/withdrawl/deposit request was a success." }
//    else if (atmMessage(0) == 6) 
//    { print ("error occured. please try again.")
//     */
//    public void ServerResponse() {
//        
//    }
}
