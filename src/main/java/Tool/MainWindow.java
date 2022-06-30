package Tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainWindow extends JFrame {

    /**
     * 定义一个静态成员变量id，用来记录创建出来的窗口的数目，从而使每个窗体标题与id关联
     */
    static int id = 0;
    JPanel panel = null;
    JTextField input = null;
    JButton submitButton = null;
    Client client = null;

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

        input = new JTextField(10);
        panel.add(input);

        submitButton = new JButton("确认");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(client != null){
                    try {
                        client.SendMsg(input.getText());
                        input.setText(client.RecMsg());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        panel.add(submitButton);
    }

    public void setClient(Client client){
        this.client = client;
    }
}
