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
public class GbnReceiver extends GbnApplication{

    private final IPLayer ip;

    public GbnReceiver(IPHost host) {
        super(host, "receiver");
	ip= host.getIPLayer();
    }

    public void start() {
       ip.addListener(GbnProtocol.IP_PROTO_GBN, new GbnProtocol(this,(IPHost) host));
    }
	
    public void stop() {}
    
}
