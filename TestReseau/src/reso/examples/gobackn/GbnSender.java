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
    
    public ArrayList<String> toSend;

    public GbnSender(IPHost host, IPAddress dst, boolean makeLose) {	
    	super(host, "sender");
    	this.dst= dst;
    	ip= host.getIPLayer();
        toSend=new ArrayList<String>();
    }

    public IPAddress getDst() {
        return dst;
    }
    
    public void addToSend(String s){
        toSend.add(s);
    }
    
    
    @Override
    public void start() throws Exception {
        GbnSendingProtocol prot= GbnProtocol.makeProtocol(this, (IPHost)host);
        ip.addListener(GbnSendingProtocol.IP_PROTO_SENDING_GBN, prot);
        prot.basicSend(dst);
    }

    @Override
    public void stop() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
    
}
