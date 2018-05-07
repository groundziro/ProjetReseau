/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPLayer;

/**
 *
 * @author Alfatta
 */
public class GbnSender extends GbnApplication{
    public final String newLine = System.getProperty("line.separator"); //Character for '\n'
    private final IPLayer ip;
    private final IPAddress dst;  //Supposant ici qu'on communiquera tout le temps avec le même Node
    public ArrayList<String> sendingQueue;
    public boolean manageCongestion;

    public GbnSender(IPHost host, IPAddress dst) {	
    	super(host, "sender");
    	this.dst= dst;
    	ip= host.getIPLayer();
        sendingQueue=new ArrayList<String>();
        manageCongestion=false;
    }
    
    public GbnSender(IPHost host, IPAddress dst, boolean manageCongestion) {	
    	super(host, "sender");
    	this.dst= dst;
    	ip= host.getIPLayer();
        sendingQueue=new ArrayList<String>();
        this.manageCongestion=manageCongestion;
    }

    public IPAddress getDst() {
        return dst;
    }
    
    public void addToSendingQueue(String s){
        sendingQueue.add(s);
    }
    
    public String getDataToSend(int i){
        return sendingQueue.get(i);
    }
    
    @Override
    public void start() throws Exception {
        File file = new File("Status.log");
        if(file.exists())
            file.delete();
        file.createNewFile();
        String s = "-------------------------------------"+newLine+"["+new Date(System.currentTimeMillis())+"]"+newLine+"-------------------------------------"+newLine;
        //GbnSendingProtocol prot= GbnProtocol.makeProtocol(this, (IPHost)host, RandomSimulator.sendingLosP,true);
        //GbnSendingProtocol prot= GbnProtocol.makeProtocol(this, (IPHost)host, RandomSimulator.sendingLosP,false);
        GbnSendingProtocol prot= GbnProtocol.makeProtocol(this, (IPHost)host, RandomSimulator.sendingLosP,manageCongestion);
        log(s);
        File file1 = new File("Plot.log");
        if(file1.exists())
            file1.delete();
        file1.createNewFile();
        ip.addListener(GbnSendingProtocol.IP_PROTO_SENDING_GBN, prot);
        prot.basicSend(dst);
    }

    @Override
    public void stop() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    
    
}
