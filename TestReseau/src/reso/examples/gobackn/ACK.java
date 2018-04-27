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
public class ACK extends GbnMessage{
    	
	
	public ACK(int num) {
            super(num);
	}
	
	public String toString() {
		return "ACK [num="+seqNum+"]";
	}

	@Override
	public int getByteLength() {
		// The ping-pong message carries a single 'int'
		return Integer.SIZE / 8;
	}

    public int getSeqNum() {
        return seqNum;
    }

    @Override
    public char getGbnMessType() {
        return 'a';
    }
        
}
