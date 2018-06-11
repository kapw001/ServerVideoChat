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
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by yasar on 10/1/18.
 */

public class WifiConnectToPassMessages {

    private static final String TAG = "WifiConnectToPassMessag";
    private final int portNum = 3238;
    private InetAddress ip = null;
    private NetworkInterface networkInterface = null;
    private ServerSocket serverSocket;
    private MulticastSocket socket;
    private InetAddress group;
    private String username;
    private ReceiveSocketMessage receiveSocketMessage;


    public WifiConnectToPassMessages(Context context) {

        Context context1 = context;
        this.receiveSocketMessage = (ReceiveSocketMessage) context;

        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            String networkSSID = "TeacherStudent";
            WifiManager.MulticastLock lock =
                    wifi.createMulticastLock(networkSSID);
            lock.setReferenceCounted(true);
            lock.acquire();
        } else {
            Log.e("cSharing", "Unable to acquire multicast lock");
            Toast.makeText(context, "Unable to acquire multicast lock", Toast.LENGTH_SHORT).show();

        }

        connectSocket();

    }


    public void connectSocket() {
        try {
            if (socket == null) {


                Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
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
                socket = new MulticastSocket(portNum);
                Log.e(TAG, "onCreate: port " + socket.getPort() + "   local post " + socket.getLocalPort());
                socket.setInterface(ip);
                socket.setBroadcast(true);

                group = InetAddress.getByName("224.0.0.1");
                socket.joinGroup(new InetSocketAddress(group, portNum), networkInterface);
            }
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: " + e.getMessage());
        }
    }


    public void sendMessages(String msg) {
        new SendMessage(msg).execute();
    }


    public void receiveMessages() {
        new ReceiverMessage().execute();
    }

    public void onDestroy() {

        if (serverSocket != null) {
            try {
                serverSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void onResume() {

        try {
            socket = new MulticastSocket(portNum);
            socket.setInterface(ip);
            socket.setBroadcast(true);

            group = InetAddress.getByName("224.0.0.1");
            socket.joinGroup(new InetSocketAddress(group, portNum), networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public class ReceiverMessage extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {

            Thread newThread = new Thread() {

                public void run() {
                    while (true) {
                        byte[] recvPkt = new byte[1024];
                        DatagramPacket recv = new DatagramPacket(recvPkt, recvPkt.length);
                        try {
                            if (socket != null)
                                socket.receive(recv);
                            else Log.e(TAG, "run: socket not initialized ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final String medd = new String(recvPkt, 0, recv.getLength());

                        receiveSocketMessage.onMessages(medd);
                    }
                }
            };
            newThread.start();
            return null;
        }
    }


    public class SendMessage extends AsyncTask<Void, Void, Boolean> {

        String textMsg;

        SendMessage(String message) {

            textMsg = username + " : " + message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            byte[] data = textMsg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, portNum);

            try {
                if (socket != null)
                    socket.send(packet);
                else Log.e(TAG, "doInBackground: socket not initialized ");
                return true;
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: " + e.getMessage());
                return false;
            }

        }
    }


    public interface ReceiveSocketMessage {
        void onMessages(String msg);
    }

}
