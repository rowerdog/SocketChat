package com.rower.chat;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * textArea: 显示用户登录信息及全部在线用户所发送的信息
 * socket:客户端建立连接的socket
 * socket1:取出HashTable()后转化的socket
 * hashtable:<String,Socket>存入名字与socket的对应关系
 *
 */
public class CServer extends Frame{
    TextArea textArea = new TextArea(20, 50);
    Socket socket = null;
    Socket socket1 = null;
    //存取<String,Socket>
    //即<name,socket>的对应关系
    Hashtable hashtable = new Hashtable();
    public CServer() throws HeadlessException {
        super();
        init();
    }
    public CServer(String string) throws HeadlessException {
        super(string);
        init();
    }

    /**
     *
     * @param str：客户端发送的消息
     * @param self：本客户端
     */
    public void boradCast(String str, Socket self) {
        // TODO Auto-generated method stub
        Enumeration enumeration = hashtable.keys();
        System.out.println("本聊天室共有"+hashtable.size()+"人");
        PrintStream printStream = null;
        textArea.append(str);
        while (enumeration.hasMoreElements()) {
            String s = (String)enumeration.nextElement();
            socket1 = (Socket)hashtable.get(s);
            if (socket1 != self) {
                try {
                    printStream = new PrintStream(socket1.getOutputStream());
                    printStream.println(str);
                } catch (IOException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 私聊
     * @param name：私聊你的客户端的名字
     * @param userMsg：私聊的信息
     * @throws IOException
     */
    public void privateChat(String name,String userMsg) throws IOException {

        String user = userMsg.split(SocketProtocol.SPLIT_SIGN) [0];
        String msg = userMsg.split(SocketProtocol.SPLIT_SIGN) [1];
        Socket s = (Socket) hashtable.get(user);
        PrintStream ps = new PrintStream(s.getOutputStream());
        ps.println(name + "对你发送消息：" + msg);
    }
    /**
     * 获得真实信息
     * @param line：客户端发来的消息
     * @return
     */
    private String getRealMsg(String line) {
        return line.substring(SocketProtocol.PROTOCOL_LEN,line.length()-SocketProtocol.PROTOCOL_LEN);
    }
    //窗口初始化方法
    public void init() {
        //textArea.setEditable(false);
        this.add(textArea);
        this.pack();
        this.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(WindowEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(WindowEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosing(WindowEvent arg0) {
                // TODO Auto-generated method stub
                System.exit(-1);
            }

            @Override
            public void windowClosed(WindowEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowActivated(WindowEvent arg0) {
                // TODO Auto-generated method stub

            }
        });
        this.setVisible(true);
        startServer();
    }

    //启动服务
    public void startServer() {
        // TODO Auto-generated method stub
        try {
            ServerSocket serverSocket = new ServerSocket(30000);
            //启动服务，一直等待客户端请求
            while (true) {
                socket = serverSocket.accept();
                //接受客户端请求之后，产生socket对象，交给多线程聊天服务处理
                Service ser = new Service(socket);
                new Thread(ser).start();
            }
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    //定义内部类
    //Thread的运行类
    class Service implements Runnable {
        Socket socket = null;
        String name;
        public Service(Socket socket) {
            // TODO Auto-generated constructor stub
            this.socket = socket;
            try {
                BufferedReader b1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                name = b1.readLine();
                hashtable.put(name, socket);
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        public Service() {
            // TODO Auto-generated constructor stub
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                boradCast(name+"进入聊天室\n", socket);
                while (true) {
                    String line = "";
                    System.out.println("服务器"+socket.isClosed());
                    if ((line = br.readLine()) != null) {
                        //如果消息中有私聊标志
                        if(line.startsWith(SocketProtocol.PRIVATE_ROUND)
                                && line.endsWith(SocketProtocol.PRIVATE_ROUND))
                        {
                            String userMsg = getRealMsg(line);
                            privateChat(name,userMsg);
                        }
                        //否则就是公聊
                        else{
                            String msg = getRealMsg(line);
                            boradCast(name+"说:"+msg+"\n", socket);
                        }

                    }
                    //quit退出
                    if ("quit".equals(getRealMsg(line))) {
                        hashtable.remove(name);
                        break;
                    }
                }
                br.close();
                socket.close();
                textArea.append("关闭连接"+name);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        CServer cServer = new CServer("服务器");
    }
}


