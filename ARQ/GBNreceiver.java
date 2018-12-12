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
public class GBNreceiver extends Thread {

    GBNsender sender;
    ArrayList<Frame> datatosend;
    int ws;
    int wr;
    int n;
    float tprop;
    float ttrans;
    float pfail;
    long wait;
    int ns;
    int nr;

    public GBNreceiver(ArrayList<Frame> frames, int ws, int wr, int n, float tprop, float ttrans, float pfail) {
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
    }

    public void setSender(GBNsender sender) {
        this.sender = sender;
    }

    void tosend() {
        ArrayList<Frame> sendw = new ArrayList<>();
        for (int count = 0; count < wr; count++) {
            Frame sendf = new Frame();
            sendf = datatosend.get(ns);
            sendf.setNs(ns + 1);
            sendf.setNr(nr);
            sendw.add(sendf);
//            System.err.println("frame " + ns + "' is sent!");
            ns++;
            try {
                sleep(wait);
            } catch (InterruptedException ex) {
                Logger.getLogger(SandWsender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sender.toreceive(sendw);
    }

    void toreceive(ArrayList<Frame> receivedw) {
        // int nsminus = ns - 1;
        if (receivedw.get(0).nr == this.ns) {
            if (ns != 0) {
//                System.err.println("window' acked!");
            }
        } else {
            ns = receivedw.get(0).nr;
//            System.err.println("window' acked until frame " + receivedw.get(0).nr + "'");
        }
        for (int count = 0; count < receivedw.size(); count++) {
            if (ThreadLocalRandom.current().nextDouble(0, 1) > pfail) {
                nr++;
            } else {
                // int nrplus = nr + 1;
//                System.err.println("error in frame " + nr);
                break;
            }
        }
        this.tosend();
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
