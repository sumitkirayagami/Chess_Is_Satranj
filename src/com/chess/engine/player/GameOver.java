
package com.chess.engine.player;

import com.chess.gui.Table;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class GameOver extends JFrame {

    public GameOver(String str,int i) throws Exception{
        setLayout(null);                                           
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("Chess");
        setSize(600, 600);
        setLocation(100,100);
        setResizable(false);
        
        
        //background image
        ImageIcon ic1=new ImageIcon("src/art/background.jpg");
        Image ic2 = ic1.getImage().getScaledInstance(600,600,Image.SCALE_DEFAULT);
        ImageIcon ic3=new ImageIcon(ic2);
        JLabel backImage = new  JLabel("",ic3,JLabel.CENTER);
        backImage.setBounds(0,0,600,600);
        this.add(backImage);
        backImage.setLayout(new GridLayout(5,1));
        repaint();
        
        Color clr=new Color(0,0,0,30);
        //Result
        JPanel jp1=new JPanel();
        jp1.setBackground(clr);
        String str1;
        if(i==0) str1="Game Drawn";
        else if(i==1)  str1="Game Over";
        else str1="Welcome to world of traps -> Chess";
        JLabel lb1=new JLabel(str1);
        Font f1;
        if(i==2)
            f1=new Font("SENS-SERIF",Font.BOLD,33);
        else
            f1=new Font("SENS-SERIF",Font.BOLD,40);
        
        lb1.setFont(f1);
        if(i==2)
            lb1.setForeground(new Color(168,6,130));
        else
            lb1.setForeground(Color.RED);
        jp1.add(lb1);
        backImage.add(jp1);
        validate();
        repaint();
        
        //Winner
        JPanel jp2=new JPanel();
        jp2.setBackground(clr);
        int width,height;
        if(i==1){
            if(str.equals("WHITE")) width = 60;
            else width = 100;
            height=100;
        }else if(i==2){
            width=200;
            height=200;
        }else{
            width = 100;
            height=100;
        }
        if(i==0)
            ic1=new ImageIcon("src/art/handshake.png");
        else if(i==2)
            ic1=new ImageIcon("src/art/welcome.png");
        else
            ic1=new ImageIcon("src/art/"+str+".png");
        ic2 = ic1.getImage().getScaledInstance(width,height,Image.SCALE_DEFAULT);
        ic3=new ImageIcon(ic2);
        JLabel lbImage = new JLabel(ic3);
        lbImage.setBackground(Color.WHITE);
        lb1.setBounds(100,200,100,100);
        jp2.add(lbImage);
        backImage.add(jp2);
        validate();
        repaint();
        
        //Message
        JPanel jp3=new JPanel();
        jp3.setBackground(clr);
        if(i==1) str+= " won";
        else if(i==0) str = "In the end, it settled peacefully!";
        else str="";
        JLabel lb2=new JLabel(str);
        Font f2=new Font("Arial",Font.ROMAN_BASELINE,30);
        lb2.setFont(f2);
        lb2.setForeground(Color.GREEN);
        jp3.add(lb2);
        backImage.add(jp3);
        lb2.setBounds(100,200,50,50);
        validate();
        repaint();
        
        //New Game
        JPanel jp4=new JPanel();
        jp4.setBackground(clr);
        JButton bt1 = new JButton("New Game");
        Font f3=new Font("Arial",Font.ITALIC,30);
        bt1.setFont(f3);
        bt1.setBackground(Color.DARK_GRAY);
        bt1.setForeground(Color.WHITE);
        bt1.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                Table.get().show();
            }
            
        });
        jp4.add(bt1);
        backImage.add(jp4);
        validate();
        repaint();
        
        //Quit
        JPanel jp5 = new JPanel();
        jp5.setBackground(clr);
        JButton bt2 = new JButton("Quit");
        Font f4=new Font("Arial",Font.ITALIC,20);
        bt2.setFont(f4);
        bt2.setBackground(Color.GRAY);
        bt2.setForeground(Color.WHITE);
        bt2.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
            
        });
        jp5.add(bt2);
        backImage.add(jp5);
        validate();
        repaint();
    }
    
    public static void main(String[] args) throws Exception {
        GameOver gm =new GameOver("BLACK",0);
    }
    
}
