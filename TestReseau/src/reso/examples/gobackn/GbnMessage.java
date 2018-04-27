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
public class GbnMessage implements Message{

    
    String data;
    public final int seqNum;
    
    public GbnMessage(String data, int num) {
		this.data=data;
                this.seqNum=num;
    }
	
    public String toString() {
		return "Message[data="+data+"][seqNum="+seqNum+"]";
    }

    public String getData() {
        return data;
    }

    public int getSeqNum() {
        return seqNum;
    }

    
    @Override
    public int getByteLength() {
        return (data.getBytes().length)+(Integer.SIZE / 8);
    }
    
}

