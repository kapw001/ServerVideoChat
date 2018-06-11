package nsdchat.android.example.com.finalwifichatapplication;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by yasar on 6/2/18.
 */

public class ScoketClass {

    private static final String TAG = "ScoketClass";

    private final int portNum = 3238;
    private InetAddress ip = null;
    private NetworkInterface networkInterface = null;
    private MulticastSocket socket;
    private InetAddress group;
    private Context mContext;

    public ScoketClass(Context mContext) {
        this.mContext = mContext;

        String networkSSID = "TeacherStudent";
        if (getWifiManager(mContext) != null) {

            WifiManager.MulticastLock lock =
                    getWifiManager(mContext).createMulticastLock(networkSSID);
            lock.setReferenceCounted(true);
            lock.acquire();

        } else {

            Toast.makeText(mContext, "There is no wifi ", Toast.LENGTH_SHORT).show();

        }


        if (socket == null) {

            Enumeration<NetworkInterface> enumNetworkInterfaces = null;
            try {
                enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
            while (enumNetworkInterfaces.hasMoreElements()) {

                networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress;
                        break;
                    }
                }
                if (ip != null) {
                    break;
                }


            }
        }

        init();
    }


    public void init() {

        try {

            socket = new MulticastSocket(portNum);
            Log.e(TAG, "onCreate: port " + socket.getPort() + "   local post " + socket.getLocalPort());
            socket.setInterface(ip);
            socket.setBroadcast(true);

            group = InetAddress.getByName("224.0.0.1");
            socket.joinGroup(new InetSocketAddress(group, portNum), networkInterface);


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public WifiManager getWifiManager(Context mContext) {
        return (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public int getPortNum() {
        return portNum;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }

    public InetAddress getGroup() {
        return group;
    }

    public void setGroup(InetAddress group) {
        this.group = group;
    }


    public void sendMessages(String msg) {
        new sendMessage(msg).execute();
    }


    private class sendMessage extends AsyncTask<Void, Void, Boolean> {

        String textMsg;

        sendMessage(String message) {

            textMsg = message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            byte[] data = textMsg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, portNum);

            try {
                if (socket != null) {

                    socket.send(packet);

                    return true;
                }
                return false;

            } catch (IOException e) {
                return false;
            } catch (RuntimeException e) {
                return false;
            }

        }
    }

}
