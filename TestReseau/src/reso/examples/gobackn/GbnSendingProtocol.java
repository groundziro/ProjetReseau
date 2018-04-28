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
    
 
    
    private MyTimer tim;
    
    private int w;                   //the seqNum focused by the timer
    private int new0;                //time, according to the scheduler, of the last ACK received that concerns the update of tDeadLine
    
    private int nsq;         //next sequence number. the next send message will have this sequence number (except a loss is detected). nsq=-1 when collection not established
    private int base;        //the sequence number of the first pack in the window (= the seq number of the oldest send bu not ACKed message). base=-1 reserver to start
    private int N;           //the size of the window. we can't send a message with a seqNum >= base+N
    
    private int tDeadLine;   //how much time (in ms) the timer will wait an ACK befaure considering the corresponding message as a loss
    
    public GbnSendingProtocol(GbnSender sender, IPHost host) {
        super(sender, host);
        nsq=-1;
        base=-1;
        N=8;
        tDeadLine=300;
        tim=new MyTimer(this);
        w=-1; new0=0;
        losePorcent=0;
    }
    
    public GbnSendingProtocol(GbnSender sender, IPHost host, int losePorcent) {
        super(sender, host);
        nsq=-1;
        base=-1;
        N=8;
        tDeadLine=300;
        tim=new MyTimer(this);
        w=-1; new0=0;
        this.losePorcent=losePorcent;
    }

    public GbnSendingProtocol(GbnSender sender) {
        super(sender);
        nsq=-1;
        base=-1;
        N=8;
        tDeadLine=500;
        tim=new MyTimer(this);
        w=-1; new0=0;
        losePorcent=0;
    }
    
    public GbnSendingProtocol(GbnSender sender,int losePorcent) {
        super(sender);
        nsq=-1;
        base=-1;
        N=8;
        tDeadLine=500;
        tim=new MyTimer(this);
        w=-1; new0=0;
        this.losePorcent=losePorcent;
    }
    

    
    /**
     * Send message with seq num = -1 to establish connection
     * @param dst
     * @throws Exception
     */
    public void basicSend(IPAddress dst) throws Exception{
        tim.schedule(tDeadLine);
        DataMessage nextMsg=new DataMessage("coucou",-1);
        System.out.println(""+applic.dudename+"  ->sending BASIC "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
        if(!RandomSimulator.shouldI(losePorcent))
            host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg); 
        else
            System.out.println("!!<-- PACKAGE LOSE SIMULATED -->!!");
        nsq=0;
        
        /*
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
        */
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
            System.out.println(""+applic.dudename+"  ACK n°"+ack.getSeqNum()+" received"+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
            if(ack.getSeqNum()>=base){

                base=ack.getSeqNum()+1;

                if(ack.getSeqNum()==w){ //Actualising the value of tDeadLine
                    tim.cancel();
                    int timeTaken=(int)(host.getNetwork().getScheduler().getCurrentTime()*1000) - new0;
                    timeTaken=(int)(timeTaken*2.5);
                    tDeadLine=(tDeadLine+timeTaken)/2;
                    System.out.println("[tDeadLine actualised to "+tDeadLine+"ms]");
                    new0=(int) (host.getNetwork().getScheduler().getCurrentTime()*1000);
                }
                potentiallySend();
            }

        }
    }

    public void potentiallySend() throws Exception{
        if(nsq < ((GbnSender)applic).sendingQueue.size()){
            if(nsq<(base+N)){
                String dataToSend=((GbnSender)applic).getDataToSend(nsq);
                DataMessage nextMsg=new DataMessage(dataToSend,nsq);
                System.out.println(""+applic.dudename+"  ->sending "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
                if(!RandomSimulator.shouldI(losePorcent))
                    host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg);
                else
                    System.out.println("!!<-- PACKAGE LOSE SIMULATED -->!!");
                //if(nsq==base)
                if(!tim.inProgress){
                    //System.out.println("yoyo");
                    tim.schedule(tDeadLine);
                    w=nsq;
                }
                nsq++;
                potentiallySend();
            }
        }
        else{
            tim.cancel();
            tim.terminate();   //Work is done. We can shut down the timer (optionnal)
        }
    }
    
    public void sendOneMessage(int i){
        
    }
    
    /**
    * Called after a timeout. Resent potentially lost messages
    */
    public void timeOutReaction() throws Exception{
        System.out.println("<><><><><><> TIMEOUT <><><><><><>");
        
        
        if(nsq==0){
            basicSend(((GbnSender)applic).getDst());
        }
        else{
            /*
            String dataToSend=((GbnSender)applic).getDataToSend(base);
            DataMessage nextMsg=new DataMessage(dataToSend,base);
            System.out.println(""+applic.dudename+"  ->sending "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
            host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg);
            */
            
            
            for(int j=base;j<nsq;j++){
                //System.out.println("BASE = "+base+"    NSQ = "+nsq);
                String dataToSend=((GbnSender)applic).getDataToSend(j);
                DataMessage nextMsg=new DataMessage(dataToSend,j);
                System.out.println(""+applic.dudename+"  ->sending BACK "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
                //nsq++;
                host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg);
            }
            
        }
        
        
        
        
        tim.cancel();
        
        tDeadLine=(int)(tDeadLine*1.5);
        System.out.println("[tDeadLine actualised to "+tDeadLine+"ms]");
        if(tDeadLine>5000){
            System.out.println("Current value of tDeadLine:"+tDeadLine+".  We can assume that the network is dead");
            throw new Exception("Network appears to be dead");
        }
        //tim.schedule(tDeadLine);
        
        
    }
}
