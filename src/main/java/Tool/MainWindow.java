package Tool;

import com.alibaba.fastjson.JSON;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.locks.ReentrantLock;

public class MainWindow extends JFrame {

    /**
     * 定义一个静态成员变量id，用来记录创建出来的窗口的数目，从而使每个窗体标题与id关联
     */
    public User user = null;

    static int id = 0;
    public int PORT = 9000;
    JPanel panel = null;
    JTextField input = null,ip_input = null, port_input;
    JTextArea textArea = null;
    JButton submitButton = null,loginButton = null,exitButton = null;
    Client client = null;

    Thread recThread = null;
    boolean recFlag = true;
    private final ReentrantLock lock=new ReentrantLock();

    public MainWindow(){
        super();

        //Window属性配置
        this.setTitle("客户端" + (++id));
        this.setBackground(Color.GRAY);

        //计算屏幕大小
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();

        // 位置、大小及是否运行用户改变窗口大小
        this.setLocation(screenSize.width / 4, screenSize.height / 4);
        this.setSize(screenSize.width / 2, screenSize.height / 2);
        this.setResizable(true);

        //b布局
        //this.setLayout(new BorderLayout());
        // 设定窗口默认关闭方式为退出应用程序
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口可视（显示）
        this.setVisible(true);

        //添加监听关闭窗口事件：退出程序
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        InitWindow();
    }

    public MainWindow(String title, Color backgroundColor, int x, int y, int width, int height, LayoutManager layoutManager){
        super();

        //Window属性配置
        this.setTitle(title);
        this.setBackground(backgroundColor);

        // 位置、大小及是否运行用户改变窗口大小
        this.setBounds(x, y , width, height);
        this.setResizable(true);

        //b布局
        this.setLayout(layoutManager);
        // 设定窗口默认关闭方式为退出应用程序
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口可视（显示）
        this.setVisible(true);

        InitWindow();
    }

    public void InitWindow(){
        panel = new JPanel();
        panel.setBackground(Color.ORANGE);
        //panel.setLayout(null);
        this.add(panel);

        textArea = new JTextArea(5, 5);
        panel.add(textArea);

        JLabel label1 = new JLabel("ip:");
        ip_input = new JTextField(10);
        JLabel label2 = new JLabel("port:");
        port_input = new JTextField(5);
        panel.add(label1);
        panel.add(ip_input);
        panel.add(label2);
        panel.add(port_input);

        JLabel label3 = new JLabel("Message:");
        input = new JTextField(10);
        panel.add(label3);
        panel.add(input);

        ButtonActionListener buttonActionListener = new ButtonActionListener();
        submitButton = new JButton("确认");
        loginButton = new JButton("登录");
        exitButton = new JButton("退出");

        submitButton.addActionListener(buttonActionListener);
        loginButton.addActionListener(buttonActionListener);
        exitButton.addActionListener(buttonActionListener);
        panel.add(submitButton);
        panel.add(loginButton);
        panel.add(exitButton);
    }
    public void submitText(){
        if(client != null){
            try {
                String message = input.getText();
                client.SendMsg(message);
                input.setText("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void login(){
        try {
            String ip = ip_input.getText().equals("") ? InetAddress.getLocalHost().getHostAddress() : ip_input.getText();
            int port = port_input.getText().equals("") ? PORT : Integer.parseInt(port_input.getText());
            textArea.append("ip:" + ip + ",port:" + port + "\n");
            client = new Client(ip, port);
            if(client != null){
                textArea.append("客户端连接成功！\n");

                //发送登录信息给服务器验证
                user = new User();
                user.setId(1L);
                user.setName("FF");
                String userInfo = JSON.toJSONString(user);
                textArea.append(userInfo + "\n");
                client.SendMsg(userInfo);

                //在连接成功的同时，开启接收线程，一直接收服务器发过来的消息，使用标志位来退出线程，而非线程.stop()
                recThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(recFlag){
                            lock.lock();
                            try {
                                textArea.append(client.RecMsg() + "\n");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }finally {
                                lock.unlock();
                            }
                        }
                    }
                });
                recThread.start();
            }
        } catch (IOException ex) {
            textArea.append("客户端连接失败！\n");
            throw new RuntimeException(ex);
        }
    }

    public void exit(){
        if(client != null){
            try {
                //发送断开连接的消息
                client.SendMsg("bye");
                //设置此标志量结束线程
                recFlag = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class ButtonActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            String text = button.getText();
            if(text.equals("确认")){
                submitText();
            }else if(text.equals("登录")){
                login();
            }else if(text.equals("退出")) {
                exit();
            }
        }
    }
}
