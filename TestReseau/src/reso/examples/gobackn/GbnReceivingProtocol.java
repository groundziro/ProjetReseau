/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import static reso.examples.gobackn.GbnSendingProtocol.IP_PROTO_SENDING_GBN;
import reso.ip.Datagram;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPInterfaceAdapter;

/**
 * Receive data and send back ACK
 * @author Alfatta
 */
public class GbnReceivingProtocol extends GbnProtocol{
    
    public static final int IP_PROTO_RECEIVING_GBN= Datagram.allocateProtocolNumber("RECEIVING-GO-BACK-N");
    private int seqNum;
    
    
    public GbnReceivingProtocol(GbnReceiver receiver, IPHost host) {
        super(receiver, host);
        seqNum=0;
    }

    public GbnReceivingProtocol(GbnReceiver receiver) {
        super(receiver);
        seqNum=0;
    }
    
    /**
     * Receive a message and send back the corresponding ACK
     * @param src
     * @param datagram
     * @throws Exception
     */
    @Override
    public void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception{
        
        GbnMessage ms=(GbnMessage)datagram.getPayload();
        if(ms.getGbnMessType()=='m'){       //Sinon il y a eu une erreur et le message ne concerne pas ce protocol. Cela ne devrait cependant jamais arriver grace au IP_PROTO_...
            DataMessage msg= (DataMessage) ms;
            System.out.println(""+applic.dudename+"  Message n°"+msg.getSeqNum()+" received. Data= "+msg.getData());
            if(msg.getSeqNum()==seqNum){
                /*
                FileOutputStream fos = null;
                try{
                    File file = new File("Status.log");
                if(file.length()==0){
                    fos = new FileOutputStream(file,false);
                }else{
                    fos = new FileOutputStream(file,true);
                }
                String newLine = System.getProperty("line.separator");
                String s = "["+new Date(System.currentTimeMillis())+"]"+""+applic.dudename+"  Message n°"+msg.getSeqNum()+" received. Data= "+msg.getData()+newLine;
                fos.write(s.getBytes());
                }catch(IOException e){
                    System.err.println(e.getMessage());
                }finally{
                    fos.close();
                }
                */
                System.out.println(""+applic.dudename+"  ->sending ACK("+seqNum+")");
                host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_SENDING_GBN, new ACK(seqNum));
                seqNum++;
            }
            else{
                System.out.println(""+applic.dudename+"  ->sending ACK("+(seqNum-1)+")");
                host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_SENDING_GBN, new ACK(seqNum-1));
            }
        }
    }
}
