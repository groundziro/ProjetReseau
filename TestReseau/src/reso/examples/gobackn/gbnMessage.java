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
public class gbnMessage implements Message{

    
    String data;
    int num;
    
    public gbnMessage(String data, int num) {
		this.data=data;
    }
	
    public String toString() {
		return "Message[data="+data+"][num="+num+"]";
    }

    
    @Override
    public int getByteLength() {
        return (data.getBytes().length)+(Integer.SIZE / 8);
    }
    
}

