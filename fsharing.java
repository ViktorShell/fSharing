/*
*   Author: ViktorShell
*   GitHub: github.com/ViktorShell
*   Version: 1.0
*   Name: fSharing
*   Description: A simple program to share File in a LAN
*/

// Creazione, Scrittura/Lettura del File
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

// Creazione Socket, Trasferimento/Ricezione del File
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

// Strumenti per Input e controllo 
import java.util.Scanner;
import java.util.StringTokenizer;


// Gestione Errori
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;


public final class fsharing
{
    // Utilita
    Scanner keyboard;

    // Variabili relative al file
    private File file;
    private FileInputStream file_input;
    private FileOutputStream file_output;

    // Variabili relative al Socket
    private Socket socket;
    private ServerSocket server_socket;
    private DataInputStream data_input;
    private DataOutputStream data_output;

    // Istanzia tutte le utilita
    public fsharing()
    {
        keyboard = new Scanner(System.in);
    }

    public fsharing(int PORT, String FILENAME) throws IOException
    {
        super();

        // Crea il file
        file = new File(FILENAME);
        if(file.exists())
        {
            System.out.println(FILENAME + " already exist, override [Y/N]: ");
            String tmp = keyboard.nextLine();
            if(!tmp.equals("Y")) System.exit(1); // Terminato, il file non deve essere sovrascritto
        }
        else file.createNewFile();

        // Collego F_Input al file, per poter scrivere i byte al suo interno
        try
        {
            file_output = new FileOutputStream(file);
        }catch(FileNotFoundException e){e.printStackTrace();}

        // Instaura la connessione
        server_socket = new ServerSocket(PORT);
        System.out.println("Connection open on port " + PORT);
        socket = server_socket.accept();
        data_input = new DataInputStream(socket.getInputStream());

        //scrivo sul file
        System.out.println("Transfer in progress...");
        file_output.write(data_input.readAllBytes());
        System.out.println("Transfer complited!");
    }

    public fsharing(int PORT, String FILENAME, String IP) throws IOException, UnknownHostException
    {
        super();
        
        // Creo il file
        file = new File(FILENAME);
        if(!file.exists())
        {
            System.out.println("File not found!");
            System.exit(1);
        }

        // Collego F_Output cosi da poter leggere e trasferire i byte
        try
        {
            file_input = new FileInputStream(file);
        }catch(FileNotFoundException e){e.printStackTrace();}
        
        // Instauro la connessione
        socket = new Socket(InetAddress.getByName(IP), PORT);
        data_output = new DataOutputStream(socket.getOutputStream());
        
        // invio la sequenza di byte
        System.out.println("Transfer in progress...");
        data_output.write(file_input.readAllBytes());
        System.out.println("Transfer complited!");
    }

    public static void main(String... args)
    {
        String IP = null;
        int PORT = 0;
        String FILENAME = null;

        // Controllo delle opzioni scelte...
        if(args.length > 0)
            switch(args[0])
            {
                case "-send":
                    if(args.length > 2)
                    {
                        StringTokenizer st = new StringTokenizer(args[1], ":");
                        IP = st.nextToken();
                        PORT = Integer.valueOf(st.nextToken());
                        FILENAME = args[2];
                        //Richiamo a fsharing
                        try
                        {
                            new fsharing(PORT, FILENAME, IP);
                        }
                        catch(UnknownHostException uhe)
                        {
                            System.out.println(IP + ":" + PORT + " host not found...");
                            System.exit(1);
                        }
                        catch(IOException e){e.printStackTrace();}
                    }
                    else
                    {
                        System.out.println("Something is missing, try -help");
                    }
                break;
                case "-get":
                    if(args.length > 2)
                    {
                        PORT = Integer.valueOf(args[1]);
                        FILENAME = args[2];
                        //Richiamo a fsharing
                        try
                        {
                            new fsharing(PORT, FILENAME);
                        }catch(IOException e){e.printStackTrace();}
                    }
                    else
                    {
                        System.out.println("Something is missing, try -help");
                    }
                break;
                case "-help":
                    System.out.println("fsharing -send [IP:PORT] [File Name]\nfsharing -get [PORT] [File Name]\nwhen using -send there must be an active server on the target machine");
                    break;
                default:
                    System.out.println("[Exception] No arguments selected, type -help");
                    break;
            }
    }

    // TEST GET/SEND
    /*
    private static void showGET(int PORT, String FILENAME)
    {
        System.out.println("GET: " + PORT + " - " + FILENAME);
    }

    private static void showSEND(String IP, int PORT, String FILENAME)
    {
        System.out.println("SEND: " + IP + ":" + PORT + " - " + FILENAME);
    }
    */
}