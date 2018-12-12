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
public class SRreceiver extends Thread {

    SRsender sender;
    ArrayList<Frame> datatosend;
    ArrayList<Integer> buffer;
    int ws;
    int wr;
    int n;
    float tprop;
    float ttrans;
    float pfail;
    long wait;
    int ns;
    int nr;

    public SRreceiver(ArrayList<Frame> frames, int ws, int wr, int n, float tprop, float ttrans, float pfail) {
        this.datatosend = frames;
        this.ws = ws;
        this.wr = wr;
        this.tprop = tprop;
        this.ttrans = ttrans;
        this.pfail = pfail;
        wait = (long) (Math.ceil(ttrans + tprop) + 2);
//        System.err.println("wait rrr" + wait);
        nr = 0;
        ns = 0;
        buffer = new ArrayList<>();
    }

    public void setSender(SRsender sender) {
        this.sender = sender;
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
//                System.out.println("frame " + wanted + " is retransmitted!");
            retrans = 1;
//            ns++;
            try {
                sleep(wait);
            } catch (InterruptedException ex) {
                Logger.getLogger(SandWsender.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            for (int count = 0; count < wr; count++) {
                Frame sendf = new Frame();
                sendf = datatosend.get(ns);
                sendf.setNs(ns + 1);
                sendf.setNr(nr);
                sendf.setWanted(thiswants);
                sendw.add(sendf);
//                        System.out.println("frame " + ns + " is sent!");
                retrans = 0;
                ns++;
                try {
                    sleep(wait);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SandWsender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        sender.toreceive(sendw, retrans);
    }

    void toreceive(ArrayList<Frame> receivedw, int retrans) {
        int wanted = -1;
        if (receivedw.get(0).wanted == -1) {
            if (ns != 0) {
//                        System.out.println("window acked");
            }
        } else {
            wanted = receivedw.get(0).wanted;
//              System.out.println("I should retrans frame " + wanted);
        }
        if (retrans == 1) {
            if (ThreadLocalRandom.current().nextDouble(0, 1) > pfail) {
                nr++;
//                         System.out.println("retrans of frame " + " successful");
            } else {
                buffer.add(nr);
//                       System.out.println("you should retrans frame " + nr + "'");
            }
        } else {
            for (int count = 0; count < receivedw.size(); count++) {
                if (ThreadLocalRandom.current().nextDouble(0, 1) > pfail) {
//                               System.out.println("frame " + nr + "' received correctly!");
                    nr++;
                } else {

//                             System.out.println("error in frame " + nr + "'");
                    buffer.add(nr);
                }
            }
        }

        this.tosend(wanted);

    }

    @Override
    public void run() {
        try {
            sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SandWreceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
