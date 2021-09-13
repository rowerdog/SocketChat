package com.rower.chat;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * textArea：实现客户输入消息、私聊消息的显示以及服务器的广播消息
 * textField：客户端的输入框
 * nickname：客户的昵称
 * address：服务器IP地址
 * port：端口号
 * connBtn：连接按钮
 * sendBtn：发送按钮
 *
 */
public class CClient extends JFrame{
    TextArea textArea = new TextArea(18, 50);
    JTextField textField = new JTextField(40);
    JTextField nickname = new JTextField(20);
    JTextField address = new JTextField("localhost", 10);
    JTextField port = new JTextField("30000", 5);
    JButton sendBtn = new JButton("发送");
    JButton connBtn = new JButton("连接");
    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    Socket socket = null;
    PrintStream printStream = null;
    public CClient() throws HeadlessException{
        super();
        init();
    }
    public CClient(String string) throws HeadlessException{
        super(string);
        init();
    }

    /**
     * 实现对客户端窗口的初始化
     */
    public void init() {
        // TODO Auto-generated method stub
        this.add(textArea);
        panel1.add(new JLabel("地址"));
        panel1.add(address);
        panel1.add(new JLabel("端口号"));
        panel1.add(port);
        panel1.add(new JLabel("昵称"));
        panel1.add(nickname);
        panel2.add(connBtn);
        panel2.add(textField);
        panel2.add(sendBtn);
        sendBtn.addActionListener(new MyListener());
        connBtn.addActionListener(new connBtnListener());
        this.add(panel1, BorderLayout.NORTH);
        this.add(panel2, BorderLayout.SOUTH);
        System.out.println(this.getTitle());
        this.pack();
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                PrintStream printStream;
                try {
                    printStream = new PrintStream(socket.getOutputStream());
                    printStream.println("quit");
                } catch (IOException e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
                close();
                System.exit(-1);
            }
        });
        this.setVisible(true);
        //connect();  //连接服务器
    }

    /**
     * 关闭输出流和socket
     */
    public void close() {
        if (printStream != null) {
            printStream.close();
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    /**
     * 建立scoket实现对服务器的连接
     */
    public void connect() {
        try {
            socket = new Socket(address.getText(), Integer.parseInt(port.getText()));
            this.setTitle(nickname.getText());
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    public void recive() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String string = "";
            while ((string = br.readLine()) != null) {
                textArea.append(string + "\n");
            }
        } catch (IOException e) {
            // TODO: handle exception
            System.exit(-1);
        }
    }

    /**
     * 发送昵称到服务器
     */
    public void sendName() {
        String name = nickname.getText();
        PrintStream printStream;
        try {
            printStream = new PrintStream(socket.getOutputStream());
            printStream.println(name);
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 内部类实现对发送按钮事件的监听
     */
    class MyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            try {
                PrintStream printStream =new PrintStream(socket.getOutputStream());
                /**
                 * 判断是否是私聊的标志
                 * 如果以//开头且中间有:的标志代表私聊
                 * 如小明私聊小红:hello
                 * 发送：
                 * //小红:hello
                 * 如果是私聊，添加私聊标志
                 */
                if(textField.getText().indexOf(":") > 0 && textField.getText().startsWith("//"))
                {
                    String s = textField.getText();
                    s = s.substring(2);
                    printStream.println(SocketProtocol.PRIVATE_ROUND + s.split(":")[0]
                            + SocketProtocol.SPLIT_SIGN + s.split(":")[1] + SocketProtocol.PRIVATE_ROUND);

                }

                /**
                 * 否则添加普通信息标志
                 */
                else
                {
                    printStream.println(SocketProtocol.MSG_ROUND + textField.getText() + SocketProtocol.MSG_ROUND);
                }
                textArea.append("我说:"+textField.getText()+"\n");
                textField.setText("");
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    //连接按钮的监听事件
    class connBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            connect();
            sendName();
            Recive r = new Recive();  //监听服务器消息
            new Thread(r).start();
        }
    }

    class Recive implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            recive();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        CClient cClient = new CClient("客户端");
    }
}
