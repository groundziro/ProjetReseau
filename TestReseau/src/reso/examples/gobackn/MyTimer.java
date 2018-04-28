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
    TimerTask tim;
    private final GbnSendingProtocol prot;
    /**
     * Constructor classic.
     * @param protocol 
     */
   public MyTimer(GbnSendingProtocol protocol){
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
        if(tim!=null)
            cancel(tim);
        TimerTask tt = new TimerTask(){
            @Override 
            public void run(){
               WorkToDo(this);
            }
        };
        tim=tt;
        super.schedule(tt, time);
    }
    /**
     * Used to cancel the current to  when we don't want the timer to end.
     */
    public void cancel(TimerTask tt){
        tt.cancel();
    }
    /**
     * Method to terminate the timer when we don't need it anymore.
     */
    public void terminate(){
        super.cancel();
    }
    public void cancel(){
        cancel(tim);
    }
    /**
     * Method that will launch what we need for the timeout event.
     */
    public void WorkToDo(TimerTask tt){
        //prot.GererTimeOut();
        prot.timeOutReaction();
        System.out.println("<><><><><><> TIMEOUT <><><><><><>");
        cancel(tt); // A LAISSER ABSOLUMENT A LA FIN
    }
}


