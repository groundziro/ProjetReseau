/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import reso.common.AbstractTimer;
import reso.scheduler.AbstractScheduler;
import reso.scheduler.Scheduler;

/**
 *
 * @author Alfatta
 */
public class GbnTimer{
    
    GbnSendingProtocol prot;
    Scheduler sch;
    
    boolean inProgress;
    
    InnerTimer tmp;
    
    public GbnTimer(Scheduler scheduler, GbnSendingProtocol prot) {
        this.prot=prot;
        sch=scheduler;
        inProgress=false;
    }
    
    public GbnTimer(GbnSendingProtocol prot) {
        this.prot=prot;
        sch=(Scheduler)prot.getHost().getNetwork().getScheduler();
        inProgress=false;
    }
    
    /**
     * Create an event that will call timeOutReaction in the GbnSendingProtocol after interval ms
     * @param interval the time before the timeOutReaction call, in ms
     */
    public void ScheduleTimeOut(int interval){
        double interv=(double)interval/1000;
        
        if(tmp!=null)
            tmp.stop(); //We stop the InnerTimer and throw it (replace it with another)
        tmp=new InnerTimer(this,sch,interv);
        tmp.start();
        inProgress=true;
    }
    
    public void cancel(){
        if(tmp!=null)
            tmp.stop();
        inProgress=false;
    }

    
    //Needed because "delay" is final in AbstractTimer
    private class InnerTimer extends AbstractTimer {
        GbnTimer outer;
    	public InnerTimer(GbnTimer outer,AbstractScheduler scheduler, double interval) {
    		super(scheduler, interval, false);
                this.outer=outer;
    	}
    	protected void run() throws Exception {
                        outer.inProgress=false;
                        prot.timeOutReaction();	
		}
    }
}

