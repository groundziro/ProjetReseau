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
public class ACKmessage implements Message{
    	public final int num; 
	
	public ACKmessage(int num) {
		this.num= num;
	}
	
	public String toString() {
		return "ACK [num="+num+"]";
	}

	@Override
	public int getByteLength() {
		// The ping-pong message carries a single 'int'
		return Integer.SIZE / 8;
	}
}
