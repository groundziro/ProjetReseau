/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Thomas
 */
public class MyTimer extends Timer{
    private final GbnProtocol prot;
    /**
     * Constructor classic.
     * @param protocol 
     */
   public MyTimer(GbnProtocol protocol){
        prot = protocol;
    }
    /**
     * Constructor for debug essentially
     * @param time time for the scheduled task
     */
    public MyTimer(int time){
        prot = null;
        schedule(time);
    }
    /**
     * The method for the task scheduling.
     * @param time Delay that we want to leave before the execution
     */
    public void schedule(int time){
        TimerTask tt = new TimerTask(){
            @Override 
            public void run(){
               WorkToDo(this);
            }
        };
        super.schedule(tt, time);
    }
    /**
     * Used to cancel the timer when we don't want the timer to end.
     */
    public void cancel(TimerTask tt){
        tt.cancel();
    }
    /**
     * Method that will launch what we need for the timeout event.
     */
    public void WorkToDo(TimerTask tt){
        //prot.GererTimeOut();
        System.out.println(this);
        cancel(tt); // A LAISSER ABSOLUMENT A LA FIN
    }
}


