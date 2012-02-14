package com.meituan.tools.proxy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DemoStarter extends JFrame implements ActionListener,Runnable {

    public DemoStarter() throws HeadlessException {
        super("Java Http Proxy");
        this.args=new String[4];
        args[0]="null";
        args[1]="58088";
        args[2]="./files";
        args[3]="running";
        this.design();
    }

    JButton button;
    String[] args;
    
    protected void design() {
        Container panel = this.getContentPane();
        panel.setLayout(new BorderLayout());
        button=new JButton("已启动/退出");
        panel.add(button,BorderLayout.CENTER);
        button.addActionListener(this);
        Thread thread=new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        args[3]=null;
        this.dispose();
    }

    @Override
    public void run() {
        try {
            JavaHttpProxy.main(args);
        } catch (Throwable t) {
            t.printStackTrace();
            button.setText("错误/退出");
        }
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        DemoStarter starter=new DemoStarter();
        starter.pack();
        starter.setLocation(640,480);
        starter.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        starter.setVisible(true);
    }
    
}
