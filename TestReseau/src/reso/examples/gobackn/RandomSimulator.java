/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.util.Random;

/**
 * Simulate a dice with 100 faces. Here, in the Go-Back-N implementation, it is only used know if we should simulate a package loss or not
 * @author Alfatta
 */
public class RandomSimulator {
    public static final Random rn = new Random();
    
    public static int sendingLosP=0;    //Will be called used at the start of GbnSender / GbnReceiver to make associated the protocol
    public static int receivingLosP=0;
    
    /**
     * Have porc% of chance to return true, and (100-porc)% of chance to return false
     * @param porc
     * @return true if we should lose the message, false if the sending process should be OK
     */
    public static boolean shouldI(int porc){
        if(porc==0)
            return false;
        int rolled=rn.nextInt(100)+1;
        //System.out.println(" COMPARING "+rolled+" WITH "+ porc);
        if(rolled<=porc)
            return true;
        else
            return false;
    }
}
