/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import reso.common.AbstractApplication;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPLayer;

/**
 *
 * @author Alfatta
 */
public class GbnSender extends AbstractApplication{
    private final IPLayer ip;
    private final IPAddress dst;  //Supposant ici qu'on communiquera tout le temps avec le même Node
    private int seqNum;
    public String dudename;

    public GbnSender(IPHost host, IPAddress dst, boolean makeLose) {	
    	super(host, "sender");
    	this.dst= dst;
    	ip= host.getIPLayer();
        seqNum=0;
    }
    
    
    @Override
    public void start() throws Exception {
        ip.addListener(GbnProtocol.IP_PROTO_GBN, new GbnProtocol(this,(IPHost) host));
    	ip.send(IPAddress.ANY, dst, GbnProtocol.IP_PROTO_GBN, new GbnMessage("salut",seqNum));
    }

    @Override
    public void stop() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }
    
    public void incrmtSeqNum(){
        seqNum++;
    }
    
    
}
