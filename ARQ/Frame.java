package arq;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mahshid
 */
public class Frame {
    
    String flag;
    String addr ;
    int ns;
    int finall;
    int nr;
    String info;
    int wanted;
    

    public Frame() {
        flag = "01111110";
        addr = "";
        ns=0;
        finall=0;
        nr=0;
        info="";
        wanted=-1;
        
    }

    public void setWanted(int acknum) {
        this.wanted = acknum;
    }

    public void setFinall(int finall) {
        this.finall = finall;
    }

    public void setNs(int ns) {
        this.ns = ns;
    }

  
    public void setNr(int nr) {
        this.nr = nr;
    }

    public void setInfo(String info) {
        this.info = info;
    }

     
}
