/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas
 */
public class MyTimer extends Timer{
    public boolean inProgress;
    TimerTask theTask;
    int time;
    private final GbnSendingProtocol prot;
    /**
     * Constructor classic.
     * @param protocol 
     */
   public MyTimer(GbnSendingProtocol protocol){
        prot = protocol;
        inProgress=false;
    }
    /**
     * Constructor for debug essentially
     * @param time time for the scheduled task
     */
    public MyTimer(int time){
        prot = null;
        inProgress=false;
        schedule(time);
    }
    
    /**
     * The method for the task scheduling.
     * @param time Delay that we want to leave before the execution
     */
    public void schedule(int time){
        this.time = time;
        if(theTask!=null)
            cancel(theTask);
        System.out.println("<<<<<SCHEDULE>>>>>");
        TimerTask tt = new TimerTask(){
            @Override 
            public void run(){
                try {
                    WorkToDo(this);
                } catch (Exception ex) {
                    Logger.getLogger(MyTimer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        theTask=tt;
        inProgress=true;
        super.schedule(tt, time);
    }
    /**
     * Used to cancel the current to  when we don't want the timer to end.
     */
    public void cancel(TimerTask tt){
        System.out.println("<<<<<CANCEL>>>");
        tt.cancel();
        inProgress=false;
    }
    /**
     * Method to terminate the timer when we don't need it anymore.
     */
    public void terminate(){
        super.cancel();
    }
    
    public long getElapsedTime(){
        //System.out.println("<--->"+theTask.scheduledExecutionTime());
        return Math.abs(System.currentTimeMillis()- theTask.scheduledExecutionTime()+time);
    }
    public void cancel(){
        cancel(theTask);
    }
    /**
     * Method that will launch what we need for the timeout event.
     */
    public void WorkToDo(TimerTask tt) throws Exception{
        prot.timeOutReaction();
        cancel(tt); // A LAISSER ABSOLUMENT A LA FIN
    }
}


