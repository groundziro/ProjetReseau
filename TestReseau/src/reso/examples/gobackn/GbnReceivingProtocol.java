/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static reso.examples.gobackn.GbnSendingProtocol.IP_PROTO_SENDING_GBN;
import reso.ip.Datagram;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPInterfaceAdapter;

/**
 * Receive data and send back ACK
 */
public class GbnReceivingProtocol extends GbnProtocol{
    
    public static final int IP_PROTO_RECEIVING_GBN= Datagram.allocateProtocolNumber("RECEIVING-GO-BACK-N");
    private int seqNum;
    
    public final String newLine = System.getProperty("line.separator"); //Character for '\n'
    
    
    public GbnReceivingProtocol(GbnReceiver receiver, IPHost host) {
        super(receiver, host);
        seqNum=-1;
    }
    public GbnReceivingProtocol(GbnReceiver receiver, IPHost host, int losP) {
        super(receiver, host);
        seqNum=-1;
        losePorcent=losP;
    }
    public GbnReceivingProtocol(GbnReceiver receiver) {
        super(receiver);
        seqNum=-1;
    }
    public GbnReceivingProtocol(GbnReceiver receiver, int losP) {
        super(receiver);
        seqNum=-1;
        losePorcent=losP;
    }
    
    /**
     * Receive a message and send back the corresponding ACK
     * @param src
     * @param datagram
     * @throws Exception
     */
    @Override
    public void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception{
        //System.out.println("AH, ON RECOIT QQCH");
        GbnMessage ms=(GbnMessage)datagram.getPayload();
        if(ms.getGbnMessType()=='m'){       //Sinon il y a eu une erreur et le message ne concerne pas ce protocol. Cela ne devrait cependant jamais arriver grace au IP_PROTO_...
            DataMessage msg= (DataMessage) ms;
            System.out.println(""+applic.dudename+"  Message n°"+msg.getSeqNum()+" received. Data= "+msg.getData()+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
            String s = ""+applic.dudename+"  Message n°"+msg.getSeqNum()+" received. Data= "+msg.getData();
            if(msg.getSeqNum()==seqNum){
                System.out.println(""+applic.dudename+"  ->sending ACK("+seqNum+")"+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
                s+="  ->sending ACK("+seqNum+")"+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)"+newLine;
                if(!RandomSimulator.shouldI(losePorcent))
                    host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_SENDING_GBN, new ACK(seqNum));
                else{
                    System.out.println("!!<-- PACKAGE LOSE SIMULATED -->!!");
                    s+= "!!<-- PACKAGE LOSE SIMULATED -->!!"+newLine;
                }
                seqNum++;
            }
            else{
                System.out.println(""+applic.dudename+"  ->sending ACK("+(seqNum-1)+")"+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
                s+="  ->sending ACK("+(seqNum-1)+")"+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)"+newLine;
                if(!RandomSimulator.shouldI(losePorcent))
                    host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_SENDING_GBN, new ACK(seqNum-1));
                else{
                    System.out.println("!!<-- PACKAGE LOSE SIMULATED -->!!");
                    s+="!!<-- PACKAGE LOSE SIMULATED -->!!"+newLine;
                }
            }
            log(s);
           
        }
    }
    /**
     * Method to write all the data in our file "Status.log"
     * @param s 
     */
    public void log(String s){
        FileOutputStream fos = null;
        try{
            File file = new File("Status.log");
        if(file.length()==0){
            fos = new FileOutputStream(file,false);
        }else{
            fos = new FileOutputStream(file,true);
        }
        fos.write(s.getBytes());
        }catch(IOException e){
            System.err.println(e.getMessage());
        }finally{
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(GbnReceivingProtocol.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
