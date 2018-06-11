package nsdchat.android.example.com.finalwifichatapplication;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class MainActivityBackup extends AppCompatActivity implements View.OnClickListener {

//    vinoth.a@innoppl.com
//    9176675444
private static final String TAG = "MainActivity";
    private String networkPass = "pass";
    static final int SocketServerPORT = 8080;

    final int portNum = 3238;
    InetAddress ip = null;
    NetworkInterface networkInterface = null;
    ServerSocket serverSocket;

    EditText edittext;
    private ArrayList<String> recQue;
    private String[] values;
    private ListView listView;
    private ArrayAdapter adapter;
    private MulticastSocket socket;
    private InetAddress group;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        username = "Testasdas";
        TextView userNm = (TextView) findViewById(R.id.usrName);
        userNm.setText(username);

        listView = (ListView) findViewById(R.id.listView);
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            String networkSSID = "TeacherStudent";
            WifiManager.MulticastLock lock =
                    wifi.createMulticastLock(networkSSID);
            lock.setReferenceCounted(true);
            lock.acquire();
        } else {
            Log.e("cSharing", "Unable to acquire multicast lock");
            Toast.makeText(getApplicationContext(), "Unable to acquire multicast lock", Toast.LENGTH_SHORT).show();

            finish();
        }
        recQue = new ArrayList<>();

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
            e.printStackTrace();
        }


        receiverMessage recvMsgThread = new receiverMessage(recQue);
        recvMsgThread.execute((Void) null);
        Button send = (Button) findViewById(R.id.buttonSend);
        send.setOnClickListener(this);

//        JoinWifiNetwork joinOne = new JoinWifiNetwork();
//        joinOne.execute((Void) null);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonSend:
                EditText text = (EditText) findViewById(R.id.editText2);
                String textMsg = text.getText().toString();
                text.setText("");
                sendMessage sendMessage = new sendMessage(textMsg);
                sendMessage.execute((Void) null);
                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }


    private class receiverMessage extends AsyncTask<Void, Void, Boolean> {
        ArrayList<String> msgList;

        receiverMessage(ArrayList<String> msgList) {
            recQue = msgList;
            this.msgList = msgList;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            Thread newThread = new Thread() {

                public void run() {
                    while (true) {
                        byte[] recvPkt = new byte[1024];
                        DatagramPacket recv = new DatagramPacket(recvPkt, recvPkt.length);
                        try {
                            socket.receive(recv);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final String medd = new String(recvPkt, 0, recv.getLength());
                        recQue.add(medd);
                        updateListView(medd);
                    }
                }
            };
            newThread.start();
            return null;
        }
    }

    private class sendMessage extends AsyncTask<Void, Void, Boolean> {

        String textMsg;

        sendMessage(String message) {

            textMsg = username + " : " + message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            byte[] data = textMsg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, portNum);

            try {
                socket.send(packet);
                return true;
            } catch (IOException e) {
                return false;
            }

        }
    }

    private void updateListView(final String message) {

        values = new String[recQue.size()];
        for (int x = 0; x < recQue.size(); x++) {
            values[x] = recQue.get(x);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message + " : " + recQue.size(), Toast.LENGTH_SHORT).show();
                adapter = new ArrayAdapter<>(MainActivityBackup.this, R.layout.list_white_text, R.id.list_content, values);
                listView.setAdapter(adapter);
                listView.setSelection(adapter.getCount() - 1);
            }
        });
        Log.d("cSharing", "Send : " + message);

    }


//    private class JoinWifiNetwork extends AsyncTask<Void, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            WifiConfiguration conf = new WifiConfiguration();
//            conf.SSID = "\"" + networkSSID + "\"";
//            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//
//            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            wifiManager.addNetwork(conf);
//            if (!wifiManager.isWifiEnabled()) {
//                wifiManager.setWifiEnabled(true);
//                wifiManager.startScan();
//            }
//
//            int netId = wifiManager.addNetwork(conf);
//            wifiManager.disconnect();
//            wifiManager.enableNetwork(netId, true);
//            wifiManager.reconnect();
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "Joined to " + networkSSID, Toast.LENGTH_SHORT).show();
//                    System.out.println("SUCCESS ");
//                }
//            });
//            return null;
//        }
//    }
}
