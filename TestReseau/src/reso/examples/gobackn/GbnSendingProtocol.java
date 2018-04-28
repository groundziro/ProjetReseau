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
import static reso.examples.gobackn.GbnReceivingProtocol.IP_PROTO_RECEIVING_GBN;
import reso.ip.Datagram;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPInterfaceAdapter;

/**
 *
 * @author Alfatta
 */
public class GbnSendingProtocol extends GbnProtocol {
    public static final int IP_PROTO_SENDING_GBN= Datagram.allocateProtocolNumber("SENDING-GO-BACK-N");
    private int seqNum;
    
    private int nsq;   //next sequence number. the next send message will have this sequence number (except a loss is detected)
    private int base;  //the sequence number of the first pack in the window (= the seq number of the oldest send bu not ACKed message)
    private int N;     //the size of the window. we can't send a message with a seqNum >= base+N
    
    public GbnSendingProtocol(GbnSender sender, IPHost host) {
        super(sender, host);
        seqNum=0;
        nsq=0;
        base=0;
        N=8;
    }

    public GbnSendingProtocol(GbnSender sender) {
        super(sender);
        seqNum=0;
        nsq=0;
        base=0;
        N=8;
    }
    
    public void basicSend(IPAddress dst) throws Exception{
        DataMessage msg=new DataMessage("salut",0);
        System.out.println(""+applic.dudename+"  ->sending "+msg);
        FileOutputStream fos = null;
        try{
            File file = new File("Status.log");
            if(file.length()==0){
                fos = new FileOutputStream(file,false);
            }else{
                fos = new FileOutputStream(file,true);
            }
            String newLine = System.getProperty("line.separator");
            String s = "["+new Date(System.currentTimeMillis())+"]"+""+applic.dudename+"  ->sending "+msg+newLine;
            fos.write(s.getBytes());
        }catch(IOException e){
            System.err.println(e.getMessage());
        }finally{
            fos.close();
        }
        potentiallySend();
    }
    
    /**
     * Receive an ACK and manage related operations (adapt the window, send a new message)
     * @param src
     * @param datagram
     * @throws Exception
     */
    @Override
    public void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception{
        GbnMessage ms=(GbnMessage)datagram.getPayload();
        if(ms.getGbnMessType()=='a'){       //Sinon il y a eu une erreur et le message ne concerne pas ce protocol. Cela ne devrait cependant jamais arriver grace au IP_PROTO_...
            ACK ack = (ACK) ms;
            System.out.println(""+applic.dudename+"  ACK n°"+ack.getSeqNum()+" received");
            if(ack.getSeqNum()>=base){
                base=ack.getSeqNum()+1;
                potentiallySend();
            }

        }
    }
    
    public void potentiallySend() throws Exception{
        if(nsq<(base+N)){
            DataMessage nextMsg=new DataMessage("coucou",nsq);
            System.out.println(""+applic.dudename+"  ->sending "+nextMsg);
            nsq++;
            potentiallySend();
            host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg);            
        }
    }
}
