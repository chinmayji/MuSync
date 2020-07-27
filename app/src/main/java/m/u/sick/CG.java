package m.u.sick;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class CG {
    Activity activity;
    CG(Activity a){
        this.activity=a;
    }

    public void wto(final File f, final FileOutputStream o){
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
                    l(size-w);
                }
                catch (Exception e){
                    l(Log.getStackTraceString(e));
                }
            }
        }).start();
    }
    public void rfi(final File f, final FileInputStream i){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    int bufferSize=1024;
                    byte[] buffer=new byte[bufferSize];
                    int r;
                    long w=0;
                    FileOutputStream o=new FileOutputStream(f);
                    while((r=i.read(buffer))>0){
                        o.write(buffer,0, r);
                        w+=r;
                    }
                    o.close();
                    l("read = "+w+" bytes");
                }
                catch (Exception e){
                    l(Log.getStackTraceString(e));
                }
            }
        }).start();
    }

    public <D> void t(final D s){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(),String.valueOf(s),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public <D> void t(final D[] s){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(),Arrays.toString(s),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void t(final int[] s){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(),Arrays.toString(s),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void t(final double[] s){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(),Arrays.toString(s),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void t(final long[] s){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(),Arrays.toString(s),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void t(final List l){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(),Arrays.toString(l.toArray()),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void t(final Exception e){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(),Log.getStackTraceString(e),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void l(final Exception e){
        Log.d("CG",Log.getStackTraceString(e));
    }
    public <D> void l(final D s){
                Log.d("CG",String.valueOf(s));
    }
    public <D> void l(final D[] s){
                Log.d("CG",Arrays.toString(s));
    }
    public void l(final int[] s){
        Log.d("CG",Arrays.toString(s));
    }
    public void l(final double[] s){
        Log.d("CG",Arrays.toString(s));
    }
    public void l(final long[] s){
        Log.d("CG",Arrays.toString(s));
    }
    public void l(final List l){
        Log.d("CG",Arrays.toString(l.toArray()));
    }


}

