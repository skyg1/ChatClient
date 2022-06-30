package Tool;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    Socket socket = null;
    BufferedReader br = null;
    BufferedWriter bw = null;
    String hostname;
    String ip;
    int port;

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        br =new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void SendMsg(String msg) throws IOException {
        bw.write(msg);
        bw.newLine();//换行刷新
        bw.flush();
    }

    public String RecMsg() throws IOException {
        String line;
        line = br.readLine();
        return line;
    }

    public void Close() throws IOException {
        if(socket != null){
            socket.close();
        }
        if(br != null){
            br.close();
        }
        if(bw != null){
            bw.close();
        }
    }
}
