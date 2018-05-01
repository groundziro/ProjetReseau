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
    
    public void ScheduleTimeOut(int interval){
        double interv=(double)interval/1000;
        
        if(tmp!=null)
            tmp.stop(); //On stop l'InnerTimer et on le jette (remplace par un autre)
        tmp=new InnerTimer(this,sch,interv);
        tmp.start();
        inProgress=true;
    }
    
    public void cancel(){
        if(tmp!=null)
            tmp.stop();
        inProgress=false;
       // System.out.println("CANCELLED");
    }

    
    //Needed because "delay" is final in AbstractTimer
    private class InnerTimer extends AbstractTimer {
        GbnTimer outer;
    	public InnerTimer(GbnTimer outer,AbstractScheduler scheduler, double interval) {
    		super(scheduler, interval, false);
                this.outer=outer;
    	}
    	protected void run() throws Exception {
			//System.out.println("OYOYOYOYOYOYOYO " + scheduler.getCurrentTime()*1000 + " OYOYOYOYOYOYOYO");
                        outer.inProgress=false;
                        prot.timeOutReaction();	
		}
    }
}

