/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import reso.common.AbstractApplication;
import reso.common.Host;

/**
 *
 * @author Alfatta
 */
public abstract class GbnApplication extends AbstractApplication{
    
    protected int seqNum;          //For receiver: num of the next EXPECTED message
    public String dudename;      //For debugging purpose
    
    public GbnApplication(Host host, String name) {
        super(host, name);
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
    
}
