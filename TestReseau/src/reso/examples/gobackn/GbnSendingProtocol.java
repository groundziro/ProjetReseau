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
import java.util.logging.Level;
import java.util.logging.Logger;
import reso.common.AbstractTimer;
import static reso.examples.gobackn.GbnReceivingProtocol.IP_PROTO_RECEIVING_GBN;
import reso.ip.Datagram;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPInterfaceAdapter;
import reso.scheduler.AbstractScheduler;
import reso.scheduler.Scheduler;

/**
 * The GbnProtocol managing the sending process of go-back-n, including the pipelining and potentially the congestion control (if useConqestion is true)
 * The send datas are the one of the corresponding host
 */
public class GbnSendingProtocol extends GbnProtocol {
    public static final int IP_PROTO_SENDING_GBN= Datagram.allocateProtocolNumber("SENDING-GO-BACK-N");
    
    public final String newLine = System.getProperty("line.separator"); //Character for '\n'
    
    private int duplicate;
    private int seqNumDuplicate = -2;
    private GbnTimer tim;
    
    private final boolean useCongestion; //Determines if we're using the Congestion Avoidance or not.
    private int w;                   //the seqNum focused by the timer
    private int new0;                //time, according to the scheduler, of the last ACK received that concerns the update of tDeadLine
    
    private int nsq;         //next sequence number. the next send message will have this sequence number (except a loss is detected). nsq=-1 when collection not established
    private int base;        //the sequence number of the first pack in the window (= the seq number of the oldest send bu not ACKed message). base=-1 reserver to start
    private int N;           //the size of the window. we can't send a message with a seqNum >= base+N
    
    private int tDeadLine;   //how much time (in ms) the timer will wait an ACK befaure considering the corresponding message as a loss
    
    private double current; //current size of the window for additive increase. N will be modified only if (int) current > N.
    private int ssthresh = Integer.MAX_VALUE; //threshold of the slow start.
    

    
    public GbnSendingProtocol(GbnSender sender, IPHost host, boolean congestion) {
        super(sender, host);
        useCongestion = congestion;
        nsq=-1;
        base=-1;
        if(congestion)
            N=1;
        else
            N=8;
        tDeadLine=300;
        tim=new GbnTimer((Scheduler)host.getNetwork().getScheduler(),this);
        w=-1; new0=0;
        losePorcent=0;
    }
    
    public GbnSendingProtocol(GbnSender sender, IPHost host, int losePorcent, boolean congestion) {
        super(sender, host);
        useCongestion = congestion;
        nsq=-1;
        base=-1;
        if(congestion)
            N=1;
        else 
            N=8;
        tDeadLine=300;
        tim=new GbnTimer((Scheduler)host.getNetwork().getScheduler(),this);
        w=-1; new0=0;
        this.losePorcent=losePorcent;
    }

    public GbnSendingProtocol(GbnSender sender, boolean congestion) {
        super(sender);
        useCongestion = congestion;
        nsq=-1;
        base=-1;
        if(congestion)
            N=1;
        else 
            N=8;
        tDeadLine=500;
        tim=new GbnTimer((Scheduler)host.getNetwork().getScheduler(),this);
        w=-1; new0=0;
        losePorcent=0;
    }
    
    public GbnSendingProtocol(GbnSender sender,int losePorcent,boolean congestion) {
        super(sender);
        useCongestion = congestion;
        nsq=-1;
        base=-1;
        if(congestion)
            N=1;
        else 
            N=8;
        tDeadLine=500;
        tim=new GbnTimer((Scheduler)host.getNetwork().getScheduler(),this);
        w=-1; new0=0;
        this.losePorcent=losePorcent;
    }
    

    
    /**
     * Send message with seq num = -1 to establish connection
     * @param dst
     * @throws Exception
     */
    public void basicSend(IPAddress dst) throws Exception{
        tim.ScheduleTimeOut(tDeadLine);
        DataMessage nextMsg=new DataMessage("coucou",-1);
        String s = ""+applic.dudename+"  ->sending BASIC "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)"+newLine;
        System.out.println(""+applic.dudename+"  ->sending BASIC "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
        if(!RandomSimulator.shouldI(losePorcent))
            host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg); 
        else{
            System.out.println("!!<-- PACKAGE LOSE SIMULATED -->!!");
            s+="!!<-- PACKAGE LOSE SIMULATED -->!!"+newLine;
        }
        nsq=0;
        log(s);
        
    }
    
    /**
     * Receive an ACK and manage related operations (adapt the window, send a new message,...)
     * @param src
     * @param datagram
     * @throws Exception
     */
    @Override
    public void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception{
        GbnMessage ms=(GbnMessage)datagram.getPayload();
        if(ms.getGbnMessType()=='a'){       //Sinon il y a eu une erreur et le message ne concerne pas ce protocol. Cela ne devrait cependant jamais arriver grace au IP_PROTO_...
            ACK ack = (ACK) ms;
            String s = ""+applic.dudename+"  ACK n°"+ack.getSeqNum()+" received"+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)"+newLine;
            System.out.println(""+applic.dudename+"  ACK n°"+ack.getSeqNum()+" received"+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
            if(ack.getSeqNum()>=base){
                base=ack.getSeqNum()+1;
                if(useCongestion)
                    uppgradeWindow();
                if(ack.getSeqNum()==w){ //Actualising the value of tDeadLine
                    tim.cancel();
                    int timeTaken=(int)(host.getNetwork().getScheduler().getCurrentTime()*1000) - new0;
                    timeTaken=(int)(timeTaken*2.5);
                    tDeadLine=(tDeadLine+timeTaken)/2;
                    System.out.println("[tDeadLine actualised to "+tDeadLine+"ms]");
                    s+="[tDeadLine actualised to "+tDeadLine+"ms]"+newLine;
                    new0=(int) (host.getNetwork().getScheduler().getCurrentTime()*1000);
                }
                log(s);
                potentiallySend();
            }else{
                if(useCongestion){
                    if(seqNumDuplicate==-2){
                        seqNumDuplicate=ack.getSeqNum();
                        System.out.println("Duplicate : "+ack.getSeqNum());
                        duplicate++;
                        
                    }else{
                        if(ack.getSeqNum()==seqNumDuplicate){
                            duplicate++;
                            if(duplicate == 3){
                                multiplicative();
                                duplicate = 0;
                                nsq=base;
                                System.out.println("Duplicated Ack :"+ack.getSeqNum()+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
                                log("Duplicated Ack :"+ack.getSeqNum()+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)"+newLine);
                                potentiallySend();
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * Check if we can send an another message (without exceeding of the window size).
     * If yes then actually send the message, and manage the corresponding information concerning the Timer
     * @throws Exception
     */
    public void potentiallySend() throws Exception{
        if(nsq < ((GbnSender)applic).sendingQueue.size()){
            if(nsq<(base+N)){
                
                String dataToSend=((GbnSender)applic).getDataToSend(nsq);
                DataMessage nextMsg=new DataMessage(dataToSend,nsq);
                System.out.println(""+applic.dudename+"  ->sending "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
                String s = ""+applic.dudename+"  ->sending "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)"+newLine;
                if(!RandomSimulator.shouldI(losePorcent))
                    host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg);
                else{
                    System.out.println("!!<-- PACKAGE LOSE SIMULATED -->!!");
                    s+="!!<-- PACKAGE LOSE SIMULATED -->!!"+newLine;
                }
                log(s);
                
                if(!tim.inProgress){
                    tim.ScheduleTimeOut(tDeadLine);
                    w=nsq;
                }
       
                nsq++;
                potentiallySend();
            }
        }
        else{            //Work is done
            tim.ScheduleTimeOut(tDeadLine);  //Last timer to check if everything is done
        }
    }

    /**
     * Simply send the package n° i
     * @param i
     * @throws Exception
     */
    public void sendOneMessage(int i) throws Exception{
        String dataToSend=((GbnSender)applic).getDataToSend(i);
        DataMessage nextMsg=new DataMessage(dataToSend,i);
        String s = ""+applic.dudename+"  ->sending "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)";
        System.out.println(""+applic.dudename+"  ->sending "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
        if(!RandomSimulator.shouldI(losePorcent)){
            host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg);
            s+=newLine;
        }
        else{
            System.out.println("!!<-- PACKAGE LOSE SIMULATED -->!!");
            s+="------> !!<-- PACKAGE LOSE SIMULATED -->!!"+newLine;
        }
        log(s);
    }
    
    /**
     * Send the message n°i, but unlike sendOneMessage(int i), it CAN'T lose the message
     * Note that this method is never needed, go-back-n could work without it.
     * We just use it in the timeOut to get clear results
     * @param i
     * @throws Exception
     */
    public void sendWithNoLoss(int i) throws Exception{
        String dataToSend=((GbnSender)applic).getDataToSend(i);
        DataMessage nextMsg=new DataMessage(dataToSend,i);
        System.out.println(""+applic.dudename+"  ->sending "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)");
        String s = ""+applic.dudename+"  ->sending "+nextMsg+ " (" + (int) (host.getNetwork().getScheduler().getCurrentTime()*1000) + "ms)"+newLine;
        log(s);
        host.getIPLayer().send(IPAddress.ANY, ((GbnSender)applic).getDst(), IP_PROTO_RECEIVING_GBN, nextMsg);
        if(useCongestion){
            System.out.println("End Timeout");
            nsq++;
        }
    }
    
    /**
    * Called after a timeout. 
    * Resend potentially lost messages and, if the congestion control is activated, manage the related informations
    */
    public void timeOutReaction() throws Exception{
        if(base==((GbnSender)applic).sendingQueue.size()){
            System.out.println("WORK IS DONE");
            log("WORK IS DONE");
            return;
        }
        if(useCongestion){
            if(N>1)
                ssthresh=N/2;
            else
                ssthresh=1;
            N=1;
            current=1;
            duplicate=0;
            seqNumDuplicate=-2;
            nsq=base;
            plot();
        }
        System.out.println("<><><><><><> TIMEOUT <><><><><><>");
        String s = "<><><><><><> TIMEOUT <><><><><><>"+newLine;
        log(s);
        if(nsq==0){
            basicSend(((GbnSender)applic).getDst());
        }
        else{
            if(useCongestion){
                sendWithNoLoss(nsq);  //We could also use sendOneMessage(nsq)
            }else{
                for(int j=base;j<nsq;j++){
                    sendWithNoLoss(j); //We could also use sendOneMessage(j)
                }
            }
        }

        tim.cancel();
        
        tDeadLine=(int)(tDeadLine*1.1);
        System.out.println("[tDeadLine actualised to "+tDeadLine+"ms]");
        s="[tDeadLine actualised to "+tDeadLine+"ms]"+newLine;
        if(tDeadLine>5000){            
            s+="Current value of tDeadLine:"+tDeadLine+".  We can assume that the does not works anymore"+newLine+"Network appears to be dead"+newLine+"---------------------------------------";            
            System.out.println("Current value of tDeadLine:"+tDeadLine+".  We can assume that the does not works anymore");
            log(s);
            throw new Exception("Network appears to be dead");
        }        
        
        tim.ScheduleTimeOut(tDeadLine);       
        log(s);
    }
    
    /**
     * Method to write our log in "Status.log"
     * @param s String to write
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
                 Logger.getLogger(GbnSendingProtocol.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Will write the size of the window at a time t in the file "Plot.log"
     * Use of a timer or write only when there's modification?
     */
    public void plot(){
        String s = "("+(int) (host.getNetwork().getScheduler().getCurrentTime()*1000)+") Size window = "+N;
        s+=" ssthresh = "+ssthresh+newLine;            
        FileOutputStream fos = null;
        try{
            File file = new File("Plot.log");
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
                 Logger.getLogger(GbnSendingProtocol.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Additive increase
     */
    public void additive(){
        current+=1/(double)N;
        if((int) current > N){
            N=(int) current;
            plot();
        }
    }
    
    /**
     * Multiplicative decrease.
     */
    public void multiplicative(){
        if(N>1)
            N/=2;
        else
            N=1;
        ssthresh = N;
        current = N;
        plot();
    }
    
    
    /**
     * Check if the window should grow the 'slow start way' or the 'additive increase way'
     * And make the window actually grow the corresponding way
     */
    public void uppgradeWindow(){
        if(N < ssthresh){   //SLOW START CASE
            N++;
            current++;
            plot();
        }
        else                //ADDITIVE INCREASE CASE
            additive();
    }
}
