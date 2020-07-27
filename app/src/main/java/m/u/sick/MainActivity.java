package m.u.sick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SharedMemory;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;


//commit3

public class MainActivity extends AppCompatActivity {
//declare
    CG cg;
    Socket client;
    DataInputStream dis;
    DataOutputStream dos;
    ContentResolver cr;
    Uri mufo;
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    TextView info,add_tv,del_tv;
    ListView addList,delList;
    AA addAdapter,delAdapter;
    Button btn_send;
    int port=5000;
    String serverip="",musicDirectory="",onDevice="",onPC="",mufoDirectory="";
    ArrayList<String> onDeviceArray,onPCArray,add,del;

    public void t(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }
    void bs(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_send.setText(s);
                if(s.equals("SYNCHRONIZE"))btn_send.setClickable(true);
            }
        });
    }
    void connectToServer(boolean updateIp){
        if(updateIp)updateIp();
        else tryToConnect();
    }
    void onlyConnect(){
        try {
            client=new Socket();
            client.connect(new InetSocketAddress(serverip,port),100000);
            dis=new DataInputStream(client.getInputStream());
            dos=new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            cg.l(Log.getStackTraceString(e));
        }
    }
    void tryToConnect() {
        serverip=pref.getString("ip",null);
        bs("Connecting : "+serverip);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client=new Socket();
                    client.connect(new InetSocketAddress(serverip,port),1000);
                    dis=new DataInputStream(client.getInputStream());
                    dos=new DataOutputStream(client.getOutputStream());
                    t("Connected");
                    bs("SYNCHRONIZE");
                    getSongsOnPC();
                    client.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillListViews();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    bs("Unreachable");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectToServer(true);
                        }
                    });

                }
            }
        }).start();
    }
    boolean has(ArrayList<String> a, String s){
        for(String x : a){
            if(x.equals(s))return true;
        }
        return false;
    }
    void newSongs(){
        add=new ArrayList<>();
        for(String x : onPCArray){
            if(!has(onDeviceArray,x)){
                add.add(x);
            }
        }
    }
    void  outdatedSongs(){
        del=new ArrayList<>();
        for(String x : onDeviceArray){
            if(!has(onPCArray,x)){
                del.add(x);
            }
        }
    }
    void fillListViews(){
        newSongs();
        outdatedSongs();

        addAdapter=new AA(this,android.R.layout.simple_list_item_1,add);
        addList.setAdapter(addAdapter);
        delAdapter=new AA(this,android.R.layout.simple_list_item_1,del);
        delList.setAdapter(delAdapter);
    }
    void getMusicDirectory(){
        File[] f = getExternalFilesDirs(null);
        try {
            musicDirectory = f[1].getAbsolutePath() + "/Music";
        }
        catch (Exception e){
            musicDirectory = f[0].getAbsolutePath() + "/Music";
        }
        File root=new File(musicDirectory);
        if(!root.exists())root.mkdir();
    }
    void initializeViews(){
        btn_send=(Button)findViewById(R.id.btn_send);
        info=(TextView)findViewById(R.id.info);
        add_tv=(TextView)findViewById(R.id.add_tv);
        del_tv=(TextView)findViewById(R.id.del_tv);
        addList=(ListView)findViewById(R.id.add_list);
        delList=(ListView)findViewById(R.id.del_list);
    }
    void setViewEvents(){
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iterateArrays();
                btn_send.setClickable(false);
                btn_send.setBackgroundColor(Color.argb(0,0,0,0));
                btn_send.setText("SYNCHRONIZING");
            }
        });
        add_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addAdapter.chk!=null&&addAdapter.chk.length>0) {
                    if (addAdapter.chk[0]) {
                        Arrays.fill(addAdapter.chk, false);
                        addAdapter.notifyDataSetChanged();
                    } else {
                        Arrays.fill(addAdapter.chk, true);
                        addAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        del_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(delAdapter.chk!=null&&delAdapter.chk.length>0) {
                    if (delAdapter.chk[0]) {
                        Arrays.fill(delAdapter.chk, false);
                        delAdapter.notifyDataSetChanged();
                    } else {
                        Arrays.fill(delAdapter.chk, true);
                        delAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }
    String ipfy(String s){
        for(int i=0;i<s.length();i++){
            if(!"1234567890".contains(String.valueOf(s.charAt(i)))){
                s=s.replace(s.charAt(i),'.');
            }
        }
        return s;
    }
    void updateIp(){
        AlertDialog.Builder ab=new AlertDialog.Builder(this);
        final EditText input=new EditText(this);
        input.setHint("Server IP address");
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setHeight(150);
        ab.setView(input);
        ab.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit.putString("ip",ipfy(input.getText().toString()));
                edit.commit();
                tryToConnect();
            }
        });
        ab.setCancelable(false);
        input.setText(serverip);
        input.setSelection(0,input.getText().length());
        ab.show();
    }
    void getSongsOnDevice(){
        File f=new File(mufoDirectory);
        String[] m3a=f.list();
        for(String x : m3a){
            if(x.contains(".mp3")) {
                onDeviceArray.add(x);
                onDevice+=x+"\n";
            }
        }
        onDevice=onDevice.trim();
    }
    void getSongsOnPC(){
        try {
            onPC=dis.readUTF();
            onPCArray=new ArrayList<String>(Arrays.asList(onPC.split("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void wto(final File f, final FileOutputStream o){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int bufferSize=1024;
                long size=f.length();
                long w=0;
                byte[] buffer=new byte[bufferSize];
                int r;
                try {
                    FileInputStream i = new FileInputStream(f);
                    while((r=i.read(buffer))>0){
                        o.write(buffer,0,r);
                        w+=r;
                    }
                    o.close();
                    f.delete();
                    cg.l(size-w);
                }
                catch (Exception e){
                    cg.l(Log.getStackTraceString(e));
                }
            }
        }).start();
    }
    void moveToPublicDirectory(File song){
        try {
            Uri songUri=DocumentsContract.createDocument(cr,mufo,"audio/mpeg",song.getName());
            ParcelFileDescriptor pf=getContentResolver().openFileDescriptor(songUri,"w");
            FileDescriptor fd=pf.getFileDescriptor();
            FileOutputStream o=new FileOutputStream(fd);
            wto(song,o);
        } catch (Exception e) {
            cg.l(e);
        }

    }
    void downloadFile(String name){
        int bufferSize=1024;
        byte[] buffer=new byte[bufferSize];

        try {
            File song=new File(musicDirectory+"/"+name);
            long fileSize=dis.readLong();
            OutputStream o=new FileOutputStream(song);
            int ct;
            while((ct=dis.read(buffer))>0) {
                o.write(buffer,0,ct);
            }
            o.close();
            if((song.length()-fileSize)!=0){
                cg.l(name + " : " + String.valueOf(song.length()-fileSize));
            }
            moveToPublicDirectory(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void sendFile(String name){
        int bufferSize=1024;
        byte[] buffer=new byte[bufferSize];

        try {
            File song=new File(mufoDirectory+"/"+name);
            dos.writeLong(song.length());
            InputStream i= new FileInputStream(song);
            int ct;
            while((ct=i.read(buffer))>0){
                dos.write(buffer,0,ct);
            }
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Uri getDocumentUri(String name){
        Uri childUri=DocumentsContract.buildChildDocumentsUriUsingTree(mufo,DocumentsContract.getDocumentId(mufo));
        Uri songUri=null;
        Cursor c=cr.query(childUri,new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME},OpenableColumns.DISPLAY_NAME+"="+name,null,null,null);
        cg.l(c.getColumnNames());
        if(c!=null && c.moveToFirst()){
            while(!c.isAfterLast()) {
                if(c.getString(c.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)).equals(name)){
                    cg.l(name);
                    songUri=DocumentsContract.buildDocumentUriUsingTree(mufo,c.getString(c.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)));
                }
                c.moveToNext();
            }
        }
        return songUri;
    }
    void deletFile(String name){
        Uri songUri=getDocumentUri(name);
        try {
            DocumentsContract.deleteDocument(getContentResolver(),songUri);
        } catch (FileNotFoundException e) {
            cg.l(e);
        }
    }
    void iterateAddArray(){
        try {
            for(int i=0;i<add.size();i++){
                onlyConnect();
                if(addAdapter.chk[i]){
                    dos.writeUTF("send="+add.get(i));
                    downloadFile(add.get(i));
                }
                else{
                    dos.writeUTF("delete="+add.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    void iterateDelArray(){
        try {
            for(int i=0;i<del.size();i++){
                onlyConnect();
                if(delAdapter.chk[i]){
                    dos.writeUTF("skip");
                    deletFile(del.get(i));
                }
                else{
                    dos.writeUTF("download="+del.get(i));
                    sendFile(del.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void iterateArrays(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                iterateAddArray();
                iterateDelArray();
                onlyConnect();
                try {
                    dos.writeUTF("done");
                    bs("DONE");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    String uri2string(String uri){
        return uri.substring(uri.lastIndexOf("/")+1).replace("%3A","/").replace("%2F","/");
    }
    void getMusicFolder(){
        if(pref.getString("mufo",null)==null){
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(i, 999);
        }
        else{
            mufo=Uri.parse(pref.getString("mufo",null));
            mufoDirectory=pref.getString("mufodir",null);
            getSongsOnDevice();
        }
    }
    void getMuFoDirectory(){
        String uri=mufo.toString();
        uri=uri2string(uri);
        File[] f=getExternalFilesDirs("");
        String[] volumes=new String[f.length];
        String[] volumeNames=new String[f.length];
        for(int i=0;i<f.length;i++){
            volumes[i]=f[i].getAbsolutePath().split("android|Android")[0];
            volumeNames[i]=volumes[i].split("/")[volumes[i].split("/").length-1];
        }
        String v=uri.split("/")[0];
        int i=0;
        for(;i<volumeNames.length;i++){
            if(uri.split("/")[0].equals(volumeNames[i])){
                mufoDirectory=(volumes[i]+uri.substring(uri.indexOf("/")+1));
                break;
            }
        }
        if(i==volumeNames.length)mufoDirectory=(volumes[0]+uri.substring(uri.indexOf("/")+1));
        cg.l(volumes);
        cg.l(volumeNames);
        cg.l(mufoDirectory);
        cg.l(new File(mufoDirectory).exists());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==999){
            mufo=data.getData();
            getMuFoDirectory();
            mufo=DocumentsContract.buildDocumentUriUsingTree(mufo,DocumentsContract.getTreeDocumentId(mufo));
            getSongsOnDevice();
            edit.putString("mufo",mufo.toString());
            edit.putString("mufodir",mufoDirectory);
            edit.commit();
        }
    }
    void checkPermissions(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},435);
        }
        else{
            launch();
        }
    }
    void launch(){
        getMusicDirectory();
        getMusicFolder();
        info.setText(mufoDirectory);
        getSupportActionBar().hide();
        connectToServer(false);
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==435) {
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                checkPermissions();
            }
            else {
                launch();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//initialize
        cg=new CG(this);
        cr=getContentResolver();
        onDeviceArray=new ArrayList<>();
        onPCArray=new ArrayList<>();

        initializeViews();
        setViewEvents();
        btn_send.setClickable(false);
        btn_send.setActivated(false);

        pref=getSharedPreferences("ip", Context.MODE_PRIVATE);
        edit=pref.edit();
        serverip=pref.getString("ip","Server IP address");
        checkPermissions();
    }
}

class AA extends ArrayAdapter<String>{
    Context context;
    ArrayList<String> list;
    boolean[] chk;
    public AA(@NonNull Context context, int resource, ArrayList<String> data) {
        super(context, resource, data);
        this.context=context;
        this.list=data;
        chk=new boolean[list.size()];
        Arrays.fill(chk,true);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            CheckBox cb=new CheckBox(context);
            cb.setText(list.get(position));
            cb.setChecked(chk[position]);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chk[position])chk[position]=false;
                    else chk[position]= true;

                }
            });
            return cb;
        }
        else{
            CheckBox cb=(CheckBox) convertView;
            cb.setText(list.get(position));
            cb.setChecked(chk[position]);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chk[position])chk[position]=false;
                    else chk[position]= true;

                }
            });
            return cb;
        }
    }
}


