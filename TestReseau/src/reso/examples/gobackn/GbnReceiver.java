/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import reso.common.AbstractApplication;
import reso.common.Host;
import reso.ip.IPHost;
import reso.ip.IPLayer;

/**
 *
 * @author Alfatta
 */
public class GbnReceiver extends AbstractApplication{

    private final IPLayer ip;
    private int seqNum;          //num of the next EXPECTED message
    public String dudename;

    public GbnReceiver(IPHost host) {
        super(host, "receiver");
	ip= host.getIPLayer();
        seqNum=0;
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

	
    public void start() {
       ip.addListener(GbnProtocol.IP_PROTO_GBN, new GbnProtocol(this,(IPHost) host));
    }
	
    public void stop() {}
    
}
