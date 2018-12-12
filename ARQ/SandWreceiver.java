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
public class SandWreceiver extends Thread {

    SandWsender sender;
    ArrayList<Frame> datatosend;

    int n;
    float tprop;
    float ttrans;
    float pfail;
    long wait;
    int ns;
    int nr;

    public SandWreceiver(ArrayList<Frame> frames, int n, float tprop, float ttrans, float pfail) {
        this.datatosend = frames;
        this.tprop = tprop;
        this.ttrans = ttrans;
        this.pfail = pfail;
        wait =  (long) (Math.ceil(ttrans + tprop));
//        System.err.println("wait rrr" + wait);
        nr = 0;
        ns = 0;
    }

    public void setSender(SandWsender sender) {
        this.sender = sender;
    }

    void tosend() {
        Frame sendf = new Frame();
        sendf = datatosend.get(ns);
        sendf.setNs(ns + 1);
        sendf.setNr(nr);
//        System.err.println("frame " + ns + "' is sent!");
        ns++;
        try {
            sleep(wait);
        } catch (InterruptedException ex) {
            Logger.getLogger(SandWsender.class.getName()).log(Level.SEVERE, null, ex);
        }
        sender.toreceive(sendf);
    }

    void toreceive(Frame f) {
        int nsminus = ns - 1;
        if (f.nr == this.ns) {
            if (ns != 0) {
//                System.err.println("frame " + nsminus + "' acked!");
            }
        } else {
            ns--;
//            System.err.println("frame " + nsminus + "' not acked!");
        }
        if (ThreadLocalRandom.current().nextDouble(0, 1) > pfail) {
            nr++;
        } else {
//               System.err.println(":D");
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
