/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arq;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mahshid
 */
public class SandWsender extends Thread {

    SandWreceiver receiver;
    ArrayList<Frame> datatosend;
    int n;
    int nf;
    int r;
    float tprop;
    float ttrans;
    float pfail;
    long timeout;
    long wait;
    int ns;
    int nr;
    int stop;
    double to;
    long tsend;
    long tack;
    int ackedframe;
    double T;
    double Reff;
    double util;
    double wait2;

    public SandWsender(ArrayList<Frame> frames, int n, int nf, int r, float tprop, float ttrans, float pfail, float timeout) {
        this.datatosend = frames;
        this.tprop = tprop;
        this.ttrans = ttrans;
        this.pfail = pfail;
        this.timeout = (long) (timeout);
        this.n = n;
        this.nf = nf;
        this.r = r;
        this.ns = 0;
        this.nr = 0;
        this.wait = (long) ((Math.ceil(ttrans + tprop)));
        this.wait2 = (double) (ttrans + tprop);
        this.stop = (int) ThreadLocalRandom.current().nextDouble(10, 20);
        System.out.println("stop" + stop);
        this.to = (double) (2 * (tprop) + ttrans);
        this.tsend = 0;
        this.tack = 0;
        this.ackedframe = 0;
        this.T = 0;
        this.Reff = 0;
        this.util = 0;
    }

    public void setReceiver(SandWreceiver receiver) {
        this.receiver = receiver;
    }

    void tosend() {
        int checkns = -1;
        Frame sendf = new Frame();
        sendf = datatosend.get(ns);
        sendf.setNs(ns + 1);
        sendf.setNr(nr);
        if (checkns == ns - 1) {
            tsend = System.currentTimeMillis();
        }
        checkns = ns;
        int nsprint = ns % 2;
        System.out.println("frame " + nsprint + " is sent!");
        ns++;
        try {
            sleep(wait);
        } catch (InterruptedException ex) {
            Logger.getLogger(SandWsender.class.getName()).log(Level.SEVERE, null, ex);
        }
        receiver.toreceive(sendf);
    }

    void toreceive(Frame f) {
        int nsminus = ns - 1;
        int nsprint = nsminus % 2;
        int nrprint;
        T += wait2;
        if (f.nr == this.ns) {
            System.out.println("frame " + nsprint + " acked!");
        } else {
            System.out.println("frame " + nsprint + " not acked!");
            ns--;
        }
        System.out.println("-------------------------");
        if (ThreadLocalRandom.current().nextDouble(0, 1) > pfail) {
            nrprint = nr % 2;
            System.out.println("frame " + nrprint + "' received correctly!");
            nr++;
            ackedframe += nf * 8;
        } else {
            nrprint = nr % 2;
            System.out.println("error in frame " + nrprint);
        }
        System.out.println("-------------------------");
        if (nr < stop) {
            this.tosend();
        }
    }

    @Override
    public void run() {

        for (int round = 0; round < n; round++) {
            System.err.println("--------- round " + round + " ----------");
            this.ns = 0;
            this.nr = 0;
            receiver.ns = 0;
            receiver.nr = 0;
            this.tosend();
            Reff += ackedframe / T;

        }
        Reff /= n;
        util = Reff / (double) r;
        System.out.println("reff: " + Reff);
        System.out.println("util: " + util);
    }
}
