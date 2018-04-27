/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import reso.ip.Datagram;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPInterfaceAdapter;
import reso.ip.IPInterfaceListener;

/**
 *
 * @author Alfatta
 */
public class GbnProtocol implements IPInterfaceListener {
    
    public static final int IP_PROTO_GBN= Datagram.allocateProtocolNumber("GO-BACK-N");
    private final IPHost host; 
    private final boolean isSender;
    private GbnSender sender;
    private GbnReceiver receiver;
    
    public GbnProtocol(GbnSender sender, IPHost host) {
	this.host= host;
        this.sender=sender;
        isSender=true;
    }
    
    public GbnProtocol(GbnSender sender){
        this.host=(IPHost) sender.getHost();
        this.sender=sender;
        isSender=true;
    }
    
    public GbnProtocol(GbnReceiver receiver, IPHost host) {
	this.host= host;
        this.receiver=receiver;
        isSender=false;
    }
    
    public GbnProtocol(GbnReceiver receiver){
        this.host=(IPHost) receiver.getHost();
        this.receiver=receiver;
        isSender=false;
    }

    @Override
    public void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception {
        if(isSender)
            receiveACK(src,datagram);
        else
            receiveMsg(src,datagram);
    }

    
    public void receiveACK(IPInterfaceAdapter src, Datagram datagram) throws Exception{
        ACKmessage ack = (ACKmessage) datagram.getPayload();
        System.out.println(""+sender.dudename+"  ACK n°"+ack.getSeqNum()+" received");
        if(ack.getSeqNum()==sender.getSeqNum()){
            sender.incrmtSeqNum();
            host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_GBN, new GbnMessage("coucou",sender.getSeqNum()));
        }
    }
    
    public void receiveMsg(IPInterfaceAdapter src, Datagram datagram) throws Exception{
        GbnMessage msg= (GbnMessage) datagram.getPayload();
        if(msg.getSeqNum()==receiver.getSeqNum()){
            System.out.println(""+receiver.dudename+"  Message n°"+msg.getSeqNum()+" received. Data= "+msg.getData());
            host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_GBN, new ACKmessage(receiver.getSeqNum()));
            receiver.incrmtSeqNum();
        }
    }
    
}
