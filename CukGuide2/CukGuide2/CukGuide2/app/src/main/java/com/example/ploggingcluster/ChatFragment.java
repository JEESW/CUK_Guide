package com.example.ploggingcluster;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ChatFragment extends Fragment {

    private Button startButton;
    private EditText inputMessage,editPeerIP;
    private InetAddress PeerIP2;
    private InetAddress PeerIP1;
    private boolean Callee = false;
    private TextView receive_message;
    private int SEND_PORT = 7777;
    private int RECV_PORT = 8888;
    private String MessageInput;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mConversationView;
    private DatagramSocket send_socket;
    TextView OwnIP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);
        initUI(rootView);
        return rootView;
    }

    public void initUI(ViewGroup rootView){
        OwnIP = (TextView) rootView.findViewById(R.id.OwnIP);
        OwnIP.setText(getIpAddress());
        editPeerIP = (EditText)rootView.findViewById(R.id.editPeerIP);
        startButton = (Button) rootView.findViewById (R.id.start_button);
        inputMessage = (EditText) rootView.findViewById (R.id.input_message);
        receive_message = (TextView) rootView.findViewById (R.id.receive_message);
        mConversationArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.message);
        mConversationView = (ListView) rootView.findViewById(R.id.listView1);
        mConversationView.setAdapter(mConversationArrayAdapter);


        startButton.setOnClickListener(startP2PSend);



        Thread startReceiveThread = new Thread(new StartReceiveThread());
        startReceiveThread.start();

        try {
            send_socket = new DatagramSocket(SEND_PORT);
        } catch (SocketException e) {
            Log.e("VR", "Sender SocketException");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    private final View.OnClickListener startP2PSend = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Log.d("VR", "Click OK");
            startP2PSending();

        }

    };

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


    public void startP2PSending() {

        Thread startP2PSendingThread = new Thread (new Runnable() {

            @Override
            public void run() {

                try {
                    MessageInput = inputMessage.getText().toString();
                    if(Callee == true){
                        PeerIP1 = PeerIP2;
                    }
                    else
                    {
                        PeerIP1 =  InetAddress.getByName(editPeerIP.getText().toString());
                    }

                    final InetAddress peerIP = InetAddress.getByName(editPeerIP.getText().toString());

                    DatagramPacket send_packet = new DatagramPacket(MessageInput.getBytes(), MessageInput.length(),PeerIP1,RECV_PORT);

                    send_socket.send(send_packet);
                    Log.d("VR", "Packet Send");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            mConversationArrayAdapter.add("Sending from " + getIpAddress().trim() + " : " + inputMessage.getText().toString());
                        }
                    });

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            inputMessage.setText("");

                        }
                    });
                    //}

                } catch (SocketException e) {
                    Log.e("VR", "Sender SocketException");

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

        });
        startP2PSendingThread.start();
    }
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    private class StartReceiveThread extends Thread {

        DatagramSocket recv_socket;
        byte[] receiveData =new byte[1024];

        public void run() {

            try {

                recv_socket = new DatagramSocket(RECV_PORT);

                Log.d("VR", "Receiver Socket Created");

                while (true) {
                    DatagramPacket recv_packet = new DatagramPacket(receiveData, receiveData.length);
                    recv_socket.receive(recv_packet);
                    Log.d("VR", "Packet Received");
                    final String  receive_data = new String(recv_packet.getData(), 0 , recv_packet.getLength());

                    InetAddress sourceHost = recv_packet.getAddress() ;
                    PeerIP2 = sourceHost;
                    Callee = true;
                    final String sourceIP = sourceHost.getHostName();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mConversationArrayAdapter.add("Message from " + sourceIP + " : " + receive_data);

                        }
                    });
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

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
                        ip += inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

}
