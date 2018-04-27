/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
    //private GbnSender sender;
    //private GbnReceiver receiver;
    private GbnApplication applic;
    
    public GbnProtocol(GbnSender sender, IPHost host) {
	this.host= host;
        //this.sender=sender;
        this.applic=sender;
        isSender=true;
    }
    
    public GbnProtocol(GbnSender sender){
        this.host=(IPHost) sender.getHost();
        //this.sender=sender;
        this.applic=sender;
        isSender=true;
    }
    
    public GbnProtocol(GbnReceiver receiver, IPHost host) {
	this.host= host;
        //this.receiver=receiver;
        this.applic=receiver;
        isSender=false;
    }
    
    public GbnProtocol(GbnReceiver receiver){
        this.host=(IPHost) receiver.getHost();
        //this.receiver=receiver;
        this.applic=receiver;
        isSender=false;
    }

    @Override
    public void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception {
        GbnMessage msg = (GbnMessage) datagram.getPayload();
        
        if(msg.getGbnMessType()=='a' && isSender){   //If the message is an ACK and this protocol concerns a sender
            receiveACK(src,datagram);
        }
        
        else if(msg.getGbnMessType()=='m' && !isSender){  //If the message is an ACK and this protocol concerns a receiver
            receiveMsg(src,datagram);
        }        
    }

    
    public void receiveACK(IPInterfaceAdapter src, Datagram datagram) throws Exception{
        ACK ack = (ACK) datagram.getPayload();
        System.out.println(""+applic.dudename+"  ACK n°"+ack.getSeqNum()+" received");
        File file = new File("Status.log");
        if(!file.exists()){
            file.createNewFile();
            System.out.print("fichier créé");
        }
        FileWriter fos;
        if(file.length()==0){
            fos = new FileWriter(file,false);
        }else{
            fos = new FileWriter(file,true);
        }
        String s = ""+applic.dudename+"  ACK n°"+ack.getSeqNum()+" received";
        fos.write(s);        
        if(ack.getSeqNum()==applic.getSeqNum()){
            applic.incrmtSeqNum();
            DataMessage nextMsg=new DataMessage("coucou",applic.getSeqNum());
            host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_GBN, nextMsg);
            System.out.println(""+applic.dudename+"  ->sending "+nextMsg);
        }
    }
    
    public void receiveMsg(IPInterfaceAdapter src, Datagram datagram) throws Exception{
        DataMessage msg= (DataMessage) datagram.getPayload();
        if(msg.getSeqNum()==applic.getSeqNum()){
            System.out.println(""+applic.dudename+"  Message n°"+msg.getSeqNum()+" received. Data= "+msg.getData());
            File file = new File("Status.log");
            if(!file.exists()){
                file.createNewFile();
                System.out.print("fichier créé");
            }
            FileWriter fos;
            if(file.length()==0){
                fos = new FileWriter(file);
            }else{
                fos = new FileWriter(file,true);
            }
            String s = ""+applic.dudename+"  Message n°"+msg.getSeqNum()+" received. Data= "+msg.getData();
            fos.write(s);
            host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_GBN, new ACK(applic.getSeqNum()));
            applic.incrmtSeqNum();
        }
    }
    
}
