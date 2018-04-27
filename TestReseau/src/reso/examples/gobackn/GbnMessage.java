/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import reso.common.Message;

/**
 *
 * @author Alfatta
 */
public abstract class GbnMessage implements Message{
    public final int seqNum;
    
    public GbnMessage(int seqNum){
        this.seqNum=seqNum;
    }

    public int getSeqNum() {
        return seqNum;
    }
    
    abstract public char getGbnMessType();  //'a' or 'm'. This is important in case one node is both a sender and a receiver
    
    
}
