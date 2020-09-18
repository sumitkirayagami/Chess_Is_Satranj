
package com.chess.engine.pieces;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;


public class PromotionChoice extends JFrame implements MouseListener {
    
    int i=0;
    JButton jbq,jbr,jbk,jbb;
    
    public void fun(){
        setLayout(new GridLayout(1,4));                                            
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        
        //Queen
        jbq=new JButton("Queen");
        jbq.addMouseListener(this);
        add(jbq);
        //Rook
        jbr=new JButton("Rook");
        jbr.addMouseListener(this);
        add(jbr);
        //Knight
        jbk=new JButton("Knight");
        jbk.addMouseListener(this);
        add(jbk);
        //Bishop
        jbb=new JButton("Bishop");
        jbb.addMouseListener(this);
        add(jbb);
        setLocation(970,0);
        setSize(400,100);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setVisible(true);
    }
    public static void main(String[] args) {
        PromotionChoice pc=new PromotionChoice();
        pc.fun();
        try{
        Thread.sleep(3000);
        }catch(Exception e){}
        if(pc.i!=0)
            System.out.println(pc.i);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.setVisible(false);
        if(e.getSource()==jbq)
            this.i=1;
        if(e.getSource()==jbr)
            this.i=2;
        if(e.getSource()==jbk)
            this.i=3;
        if(e.getSource()==jbb)
            this.i=4;
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
