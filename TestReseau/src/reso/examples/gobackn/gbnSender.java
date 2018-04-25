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
public class gbnSender extends AbstractApplication{
    private final IPLayer ip;
    private final IPAddress dst;
    private final int num;

    public gbnSender(IPHost host, IPAddress dst, boolean makeLose) {	
    	super(host, "sender");
    	this.dst= dst;
    	this.num= num;
    	ip= host.getIPLayer();
    }
    
    
    @Override
    public void start() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
