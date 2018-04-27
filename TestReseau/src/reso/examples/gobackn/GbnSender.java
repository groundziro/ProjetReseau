/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import reso.common.AbstractApplication;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPLayer;

/**
 *
 * @author Alfatta
 */
public class GbnSender extends GbnApplication{
    private final IPLayer ip;
    private final IPAddress dst;  //Supposant ici qu'on communiquera tout le temps avec le même Node

    public GbnSender(IPHost host, IPAddress dst, boolean makeLose) {	
    	super(host, "sender");
    	this.dst= dst;
    	ip= host.getIPLayer();
    }
    
    
    @Override
    public void start() throws Exception {
        ip.addListener(GbnProtocol.IP_PROTO_GBN, new GbnProtocol(this,(IPHost) host));
        DataMessage msg=new DataMessage("salut",seqNum);
        System.out.println(""+dudename+"  ->sending "+msg);
        FileOutputStream fos = null;
        try{
            File file = new File("Status.log");
            if(file.length()==0){
                fos = new FileOutputStream(file,false);
            }else{
                fos = new FileOutputStream(file,true);
            }
            String s = ""+dudename+"  ->sending "+msg+"\n";
            fos.write(s.getBytes());
        }catch(IOException e){
            System.err.println(e.getMessage());
        }finally{
            fos.close();
        }
        
    	ip.send(IPAddress.ANY, dst, GbnProtocol.IP_PROTO_GBN,msg);
    }

    @Override
    public void stop() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
    
}