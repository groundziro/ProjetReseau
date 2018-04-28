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
        super.schedule(new TimerTask(){
            @Override 
            public void run(){
               WorkToDo();
            }
        }, time);
    }
    /**
     * Used to cancel the timer when we don't want the timer to end.
     */
    @Override
    public void cancel(){
        super.cancel();
    }
    /**
     * Method that will launch what we need for the timeout event.
     */
    public void WorkToDo(){
        //prot.GererTimeOut();
        System.out.println(this);
        cancel(); // A LAISSER ABSOLUMENT A LA FIN
    }
}


