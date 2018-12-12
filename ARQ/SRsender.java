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
public class SRsender extends Thread {

    SRreceiver receiver;
    ArrayList<Frame> datatosend;
    ArrayList<Integer> buffer;
    ArrayList<Integer> fakenr;
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
    int allr;
    int stop;
    double to;
    double T;
    double Reff;
    double util;
    int ackedframe;

    public SRsender(ArrayList<Frame> frames, int ws, int wr, int n, int nf, int r, float tprop, float ttrans, float pfail, float timeout) {
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
        allr = 0;
        stop = (int) ThreadLocalRandom.current().nextDouble(10, 20);
        System.out.println("stop" + stop);
        wait =  (long) (Math.ceil(ttrans + tprop) + 2);
        this.wait2 = (double) (ttrans + tprop);
        buffer = new ArrayList<>();
        fakenr = new ArrayList<>();
        this.to = (double) (2 * (tprop) + ttrans);
        this.T = 0;
        this.Reff = 0;
        this.util = 0;
        ackedframe = 0;
    }

    public void setReceiver(SRreceiver receiver) {
        this.receiver = receiver;
    }

    void tosend(int wanted) {
        int retrans = 0;
        int thiswants = -1;
        if (buffer.size() != 0) {
            thiswants = buffer.remove(0);
        }
        ArrayList<Frame> sendw = new ArrayList<>();
        if (wanted > -1) {
            Frame sendf = new Frame();
            sendf = datatosend.get(wanted);
            sendf.setNs(wanted + 1);
            sendf.setNr(nr);
            sendf.setWanted(thiswants);
            sendw.add(sendf);
            int wantedprint = wanted % ws;
            System.out.println("frame " + wantedprint + " is retransmitted!");
            System.out.println("---------------------------");
            retrans = 1;
            //ns++;
            try {
                sleep(wait);
            } catch (InterruptedException ex) {
                Logger.getLogger(SandWsender.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            for (int count = 0; count < ws; count++) {
                Frame sendf = new Frame();
                sendf = datatosend.get(ns);
                sendf.setNs(ns + 1);
                sendf.setNr(nr);
                sendf.setWanted(thiswants);
                sendw.add(sendf);
                int nsprint = ns % ws;
                System.out.println("frame " + nsprint + " is sent!");
                retrans = 0;
                ns++;
                try {
                    sleep(wait);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SandWsender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("---------------------------");
        }
        receiver.toreceive(sendw, retrans);
    }

    void toreceive(ArrayList<Frame> receivedw, int retrans) {
        int wanted = -1;
        T += wait2*(double)wr;
        if (receivedw.get(0).wanted == -1) {
            System.out.println("window acked");

        } else {
            wanted = receivedw.get(0).wanted;
            int wantedprint = wanted % ws;
            System.out.println("I should retrans frame " + wantedprint);
        }
        System.out.println("---------------------------");
        if (retrans == 1) {
            if (ThreadLocalRandom.current().nextDouble(0, 1) > pfail) {
                int fakenrprint = fakenr.remove(0) % wr;
                System.out.println("retrans of frame " + fakenrprint + "' successful");
                ackedframe += nf*8;
                nr++;
            } else {
                buffer.add(nr);
                int nrprint = nr % wr;
                System.out.println("you should retrans frame " + nrprint + "'");
                fakenr.add(nr);
            }
        } else {
            for (int count = 0; count < receivedw.size(); count++) {
                if (ThreadLocalRandom.current().nextDouble(0, 1) > pfail) {
                    int allrprint = allr % wr;
                    System.out.println("frame " + allrprint + "' received correctly!");
                    ackedframe += nf*8;
                    nr++;
                } else {
                    int nrprint = nr % wr;
                    System.out.println("you should retrans frame " + nrprint + "'");
                    buffer.add(nr);
                    fakenr.add(nr);
                }
                allr++;
            }
        }
        System.out.println("---------------------------");
        if (nr < stop) {
            this.tosend(wanted);
        }
    }

    @Override
    public void run() {
        for (int round = 1; round <= n; round++) {
            System.err.println("--------- round " + round + " ----------");
            this.ns = 0;
            this.nr = 0;
            receiver.ns = 0;
            receiver.nr = 0;
            this.tosend(-1);
            Reff += ackedframe / T;
        }
        Reff /= n;
        util = Reff / (double) r;
        System.out.println("reff: " + Reff);
        System.out.println("util: " + util);

    }

}
