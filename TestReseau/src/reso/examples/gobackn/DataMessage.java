/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import reso.common.Message;

/**
 * Message containing datas (and a seqNum), send by the sender to the receiver. 
 * 
 */
public class DataMessage extends GbnMessage{

    
    String data;
    
    public DataMessage(String data, int num) {
        super(num);
        this.data=data;
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

    @Override
    public char getGbnMessType() {
        return 'm';
    }
    
}

