package arq;

import java.util.ArrayList;
import static java.lang.Math.pow;
import arq.Frame;
import arq.SandWreceiver;
import arq.SandWsender;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mahshid
 */
public class Manager {

    ArrayList<Frame> frames1;
    ArrayList<Frame> frames2;
    SandWsender sender1;
    SandWreceiver receiver1;
    GBNsender sender2;
    GBNreceiver receiver2;
    SRsender sender3;
    SRreceiver receiver3;
    String protocol;
    int ws;
    int wr;
    float p;
    int r;
    int nf;
    float v;
    float d;
    int n;
    float tprop;
    float ttrans;
    float pfail;
    float timeout;

    public Manager(String protocol, int ws, int wr, float p, int r, int nf, float v, float d, int n) {

        this.frames1 = new ArrayList();
        this.frames2 = new ArrayList();
        this.protocol = protocol;
        this.ws = ws;
        this.wr = wr;
        this.p = p;
        this.r = r;
        this.nf = nf;
        this.v = v;
        this.d = d;
        this.n = n;
        this.tprop = (float) d / (float) v;
//        System.err.println("tprop" + tprop);
        this.ttrans = (float) (nf * 8) / (float) (r);
//        System.err.println("ttrans" + ttrans);
        this.pfail = (float) (1 - (pow((double) (1 - p), (double) nf * 8)));
        System.err.println("pfail" + pfail);
        this.timeout = (float) (1000 * (2 * tprop + 2 * ttrans + 3));//in seconds
        infogenrator(nf);
        decider();

    }

    void decider() {
        if (protocol.equals("SW")) {
            System.out.println("stop and wait starts");
            this.sender1 = new SandWsender(frames1, n, nf, r, tprop, ttrans, pfail, timeout);
            this.receiver1 = new SandWreceiver(frames2, n, tprop, ttrans, pfail);
            sender1.setReceiver(receiver1);
            receiver1.setSender(sender1);
            sender1.start();
            receiver1.start();
        } else if (protocol.equals("GBN")) {
            System.out.println("GBN starts");
            this.sender2 = new GBNsender(frames1, ws, wr, n, nf, r, tprop, ttrans, pfail, timeout);
            this.receiver2 = new GBNreceiver(frames2, ws, wr, n, tprop, ttrans, pfail);
            sender2.setReceiver(receiver2);
            receiver2.setSender(sender2);
            sender2.start();
            receiver2.start();
        } else if (protocol.equals("SR")) {
            System.out.println("SR starts");
            this.sender3 = new SRsender(frames1, ws, wr, n,nf,r, tprop, ttrans, pfail, timeout);
            this.receiver3 = new SRreceiver(frames2, ws, wr, n, tprop, ttrans, pfail);
            sender3.setReceiver(receiver3);
            receiver3.setSender(sender3);
            sender3.start();
            receiver3.start();
        }
    }

    public void infogenrator(int nf) {
        int framenum = 100;
        for (int i = 0; i < framenum; i++) {
            String info1 = "";
            String info2 = "";
            for (int j = 0; j < 8 * (5); j++) {
                if (i * j % 5 == 0) {
                    info1 += "0";
                    info2 += "1";
                } else if (i * j % 2 == 1) {
                    info1 += "1";
                    info2 += "0";
                } else {
                    info1 += "0";
                    info2 += "1";
                }
            }
            Frame f1 = new Frame();
            Frame f2 = new Frame();
            f1.setInfo(info1);
            f2.setInfo(info2);
            if (i == framenum - 1) {
                f1.setFinall(1);
                f2.setFinall(1);
            } else {
                f1.setFinall(0);
                f1.setFinall(1);
            }
            this.frames1.add(f1);
            this.frames2.add(f2);

        }
    }
}
