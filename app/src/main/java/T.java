import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class T extends Thread{
    public static void main(String[] args) {
        T t=new T();
        t.start();
    }
    String[] s={"ddd","aaaa","gggg"};
    String st="djjd";
    int[] i={1,2,3,4,5};
    ArrayList<String> al=new ArrayList<>(Arrays.asList(s));
    int in=343;

    void compareFiles(){
        File f1=new File("E:\\Music\\Inception Indian Version (TIME) Tushar Lall (TIJP).mp3");
        File f2=new File("E:\\Documents\\AndroidStudio\\DeviceExplorer\\samsung-sm_t705-192.168.29.77_5555\\storage\\9016-4EF8\\Android\\data\\m.u.sick\\files\\Music\\Inception Indian Version (TIME) Tushar Lall (TIJP).mp3");
        long f1Size=f1.length();
        long f2Size=f2.length();
        System.out.println("f1Size = "+f1Size);
        System.out.println("f2Size = "+f2Size);
        System.out.println("SizeDiff = "+(f1Size-f2Size));
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String s= reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        run();
    }
}
