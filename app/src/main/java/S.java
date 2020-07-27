import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class S {
    static int port=5000;
    public static void main(String[] args) throws IOException {
        String dr="E:/Music/";
        System.out.println("Starting Server");
        System.out.println(InetAddress.getLocalHost().getHostAddress());
        ServerSocket s=new ServerSocket(port);
        System.out.println("Waiting for Client");
        Socket c=s.accept();
        System.out.println("Connected");
        final DataInputStream dis = new DataInputStream(c.getInputStream());
        DataOutputStream dos = new DataOutputStream(c.getOutputStream());
        String nms=dis.readUTF();
//        System.out.println(nms);
        String pcnms="";
        File f=new File(dr);
        File[] sn=f.listFiles();
        for (int i = 0; i < sn.length; i++) {
            if(sn[i].getName().contains(".mp3")) {
                pcnms += sn[i].getName() + "\n";
            }
        }
        pcnms=pcnms.trim();
        dos.writeUTF(pcnms);
        String inn=dis.readUTF();
        ArrayList<String> ad=new ArrayList<String>();
        while(!inn.equals("add")){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(inn.length()>1)ad=new ArrayList<String>(Arrays.asList(inn.split("\n")));
            else System.out.print(inn);
            inn=dis.readUTF();
        }
        dos.flush();
        System.out.println(ad);
//        System.out.println(ad.size());
        for(String x : ad){
            File m3=new File(dr+x);
            dos.writeLong(m3.length());
            System.out.println(m3.exists()+" "+x);
            InputStream is=new FileInputStream(m3);
            int bs=1024;
            int ct;
            byte[] bt=new byte[bs];
            ct=is.read(bt);
            while(ct!=-1) {
                dos.write(bt);
                ct=is.read(bt);
            }
            System.out.println("sent");
            is.close();
            break;
        }
    }
}
