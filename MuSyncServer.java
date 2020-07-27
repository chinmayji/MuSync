
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.Toolkit;

public class MuSyncServer extends Thread {
    public static void main(String[] args) {
        new MuSyncServer().start();
    }
    ServerSocket server;
    DataOutputStream dos;
    DataInputStream dis;
    Socket client;
    int port =5000;
    String musicDirectory="/home/chinmay/Music/",onPC="";
    boolean deleteAlert=false,deleteDefault=false;

    void startServer(){
        try{
            System.out.println("Starting Server");
            System.out.println(InetAddress.getLocalHost().getHostAddress());

            server=new ServerSocket(port);
            System.out.println("Waiting for Client");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void waitForClient(){
        try {
            client=server.accept();
            dos=new DataOutputStream(client.getOutputStream());
            dis=new DataInputStream(client.getInputStream());
            System.out.println("Connected");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    void waitForClient2(){
        try {
            client=server.accept();
            dos=new DataOutputStream(client.getOutputStream());
            dis=new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    void getSongsOnPC(){
        File f=new File(musicDirectory);
        String[] m3a=f.list();
        for(String x : m3a){
            if(x.contains(".mp3")){
                onPC+=x+"\n";
            }
        }
        onPC=onPC.trim();
    }

    void sendSongList(){
        try {
            dos.writeUTF(onPC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendFile(String name){
        int bufferSize=1024;
        byte[] buffer=new byte[bufferSize];

        try {
            File song=new File(musicDirectory+name);
            dos.writeLong(song.length());
            InputStream i= new FileInputStream(song);
            int ct;
            while((ct=i.read(buffer))>0){
                dos.write(buffer,0,ct);
            }
            client.close();
            System.out.println("sent "+name);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void downloadFile(String name){
        int bufferSize=1024;
        byte[] buffer=new byte[bufferSize];

        try {
            File song=new File(musicDirectory+name);
            long fileSize=dis.readLong();
            OutputStream o=new FileOutputStream(song);
            int ct;
            while((ct=dis.read(buffer))>0) {
                o.write(buffer,0,ct);
            }
            o.close();
            if((song.length()-fileSize)!=0){
                System.out.println(String.valueOf(song.length()-fileSize));
            }
            System.out.println("downloaded "+name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteFile(String name){
        try {
            File song=new File(musicDirectory+name);
            if(deleteAlert) {
                Toolkit.getDefaultToolkit().beep();
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("To delete " + name + ", enter anything");
                String s = reader.readLine();
                if (!s.isEmpty()) {
                    song.delete();
                    System.out.println("deleted " + name);
                }
            }
            else{
                if(deleteDefault){
                    song.delete();
                    System.out.println("deleted " + name);
                }
            }
        }
        catch (Exception e){

        }
    }

    void receiveRequests(){
        try {
            waitForClient2();
            String req=dis.readUTF();
            if(req.equals("done"))return;
            if(req.equals("skip")){
                client.close();
                receiveRequests();
                return;
            }
            System.out.println(req);

            if(req.split("=")[0].trim().equals("send")){
                sendFile(req.split("=")[1].trim());
            }
            else if(req.split("=")[0].trim().equals("download")){
                downloadFile(req.split("=")[1].trim());
            }
            else if(req.split("=")[0].trim().equals("delete")){
                deleteFile(req.split("=")[1].trim());
            }
            client.close();
            receiveRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void setAlertForDelete(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter for delete default : y to delete, n to not, else ask every time");
            String s=reader.readLine();
            if(s.equals("y")){
                deleteAlert=false;
                deleteDefault=true;
                System.out.println("Delete");
            }
            else if(s.equals("n")){
                deleteAlert=false;
                deleteDefault=false;
                System.out.println("Don't Delete");
            }
            else{
                deleteAlert=true;
                deleteDefault=false;
                System.out.println("Ask Everytime");
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        setAlertForDelete();
        startServer();
        waitForClient();
        getSongsOnPC();
        sendSongList();
        receiveRequests();
    }
}
