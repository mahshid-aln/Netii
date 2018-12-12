package arq;

import java.util.Scanner;

/**
 *
 * @author Mahshid
 */
public class ARQ {

    public static void main(String[] args) {

        String protocol;
        int ws;
        int wr;
        float p;
        int r;
        int nf;
        float v;
        float d;
        int n;

        Scanner scan = new Scanner(System.in);
        System.out.println("choose the protocol! S&W or GBN or SR?");
        protocol = scan.next();
        System.out.println("specify the sender window size! at most 8!!!");
        ws = scan.nextInt();
        System.out.println("specify the receiver window size! at most 8!!!");
        wr = scan.nextInt();
        System.out.println("specify the bit error rate! between 0 and 1!!!");
        p = scan.nextFloat();
        System.out.println("specify link bitrate in bit per second!");
        r = scan.nextInt();
        System.out.println("specify the frame size in byte!");
        nf = scan.nextInt();
        System.out.println("specify the propagation speed in meter per second!");
        v = scan.nextFloat();
        System.out.println("specify the distance in meter!");
        d = scan.nextFloat();
        System.out.println("specify the number of simulations!");
        n = scan.nextInt();
        Manager m = new Manager(protocol, ws,wr, p, r, nf, v, d, n);
    }
}
