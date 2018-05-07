/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import reso.ip.Datagram;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPInterfaceAdapter;
import reso.ip.IPInterfaceListener;

/**
 * InterfaceListeners used with go-back-n
 * GbnSendingProtocols and GbnReceivingProtocols are GbnProtocols
 * Go-back-n need a Node using a GbnSendingProtocol and a node using a GbnReceivingProtocol in order to function
 */
public abstract class GbnProtocol implements IPInterfaceListener {
    
    public int losePorcent;   //% of messages (ACK) lost during the sending/receiving process (simulated). if set to 0, the sender will never lose any package
    protected final IPHost host; 
    protected GbnApplication applic;
    
    static GbnReceivingProtocol makeProtocol(GbnReceiver receiver, IPHost host){
        return new GbnReceivingProtocol(receiver,host);
    }
    static GbnReceivingProtocol makeProtocol(GbnReceiver receiver, IPHost host, int losP){
        return new GbnReceivingProtocol(receiver,host,losP);
    }
    static GbnReceivingProtocol makeProtocol(GbnReceiver receiver){
        return new GbnReceivingProtocol(receiver);
    }   
    static GbnReceivingProtocol makeProtocol(GbnReceiver receiver,int losP){
        return new GbnReceivingProtocol(receiver,losP);
    }   
    static GbnSendingProtocol makeProtocol(GbnSender sender, IPHost host, boolean congestion){
        return new GbnSendingProtocol(sender,host,congestion);
    }
    static GbnSendingProtocol makeProtocol(GbnSender sender, IPHost host, int losP, boolean congestion){
        return new GbnSendingProtocol(sender,host,losP, congestion);
    }
    static GbnSendingProtocol makeProtocol(GbnSender sender, boolean congestion){
        return new GbnSendingProtocol(sender,congestion);
    }
    static GbnSendingProtocol makeProtocol(GbnSender sender, int losP, boolean congestion){
        return new GbnSendingProtocol(sender,losP,congestion);
    }
    
    public GbnProtocol(GbnApplication applic, IPHost host) {
	this.host= host;
        this.applic=applic;
    }
    public GbnProtocol(GbnApplication applic, IPHost host, int lP) {
	this.host= host;
        this.applic=applic;
        losePorcent=lP;
    }
    public GbnProtocol(GbnApplication applic){
        this.host=(IPHost) applic.getHost();
        this.applic=applic;
    }
    public GbnProtocol(GbnApplication applic, int lP){
        this.host=(IPHost) applic.getHost();
        this.applic=applic;
        losePorcent=lP;
    }

    public void setLosePorcent(int losePorcent) {
        this.losePorcent = losePorcent;
    }

    public int getLosePorcent() {
        return losePorcent;
    }

    public IPHost getHost() {
        return host;
    }
    
    
    
    @Override
    public abstract void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception;
    
}
