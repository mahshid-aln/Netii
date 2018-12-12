/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arq;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mahshid
 */
public class GBNsender extends Thread {

    GBNreceiver receiver;
    ArrayList<Frame> datatosend;
    int ws;
    int wr;
    int n;
    int nf;
    int r;
    float tprop;
    float ttrans;
    float pfail;
    long timeout;
    long wait;
    double wait2;
    int ns;
    int nr;
    int stop;
    double to;
    double T;
    double Reff;
    double util;
    int ackedframe;

    public GBNsender(ArrayList<Frame> frames, int ws, int wr, int n, int nf, int r, float tprop, float ttrans, float pfail, float timeout) {
        this.datatosend = frames;
        this.ws = ws;
        this.wr = wr;
        this.tprop = tprop;
        this.ttrans = ttrans;
        this.pfail = pfail;
        this.timeout = (long) (timeout);
        this.n = n;
        this.nf = nf;
        this.r = r;
        ns = 0;
        nr = 0;
        stop = (int) ThreadLocalRandom.current().nextDouble(10, 20);
        System.out.println("stop" + stop);
        wait = (long) (Math.ceil(ttrans + tprop) + 2);
        this.wait2 = (double) (ttrans + tprop);
        this.to = (double) (2 * (tprop) + ttrans);
        this.T = 0;
        this.Reff = 0;
        this.util = 0;
            ackedframe = 0;
    }

    public void setReceiver(GBNreceiver receiver) {
        this.receiver = receiver;
    }

    void tosend() {
        ArrayList<Frame> sendw = new ArrayList<>();
        for (int count = 0; count < ws; count++) {
            Frame sendf = new Frame();
            sendf = datatosend.get(ns);
            sendf.setNs(ns + 1);
            sendf.setNr(nr);
            sendw.add(sendf);
            int nsprint = ns % ws;
            System.out.println("frame " + nsprint + " is sent!");
            ns++;
            try {
                sleep(wait);
            } catch (InterruptedException ex) {
                Logger.getLogger(SandWsender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("-------------------------");
        receiver.toreceive(sendw);
    }

    void toreceive(ArrayList<Frame> receivedw) {
        int nrprint;
        T += (double) wr * wait2;
        if (receivedw.get(0).nr == this.ns) {
            System.out.println("window acked!");
        } else {
            nrprint = receivedw.get(0).nr % ws;
            System.out.println("window acked until frame " + nrprint);
            ns = receivedw.get(0).nr;
        }
        System.out.println("-------------------------");
        for (int count = 0; count < receivedw.size(); count++) {
            if (ThreadLocalRandom.current().nextDouble(0, 1) > pfail) {
                nrprint = nr % wr;
                System.out.println("frame " + nrprint + "' received correctly!");
                ackedframe += nf * 8;
                nr++;
            } else {
                nrprint = nr % wr;
                System.out.println("error in frame " + nrprint + "'");
                break;
            }
        }
        System.out.println("-------------------------");
        if (nr < stop) {
            this.tosend();
        }
    }

    @Override
    public void run() {
        try {
            sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(GBNsender.class.getName()).log(Level.SEVERE, null, ex);
        }

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
