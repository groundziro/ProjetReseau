/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reso.examples.gobackn;

import java.util.Scanner;
import reso.common.*;
import reso.ethernet.*;
import reso.examples.static_routing.AppSniffer;
import reso.ip.*;
import reso.scheduler.AbstractScheduler;
import reso.scheduler.Scheduler;
import reso.utilities.NetworkBuilder;
/**
 *
 * @author Alfatta
 */
public class Demo {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Do you wanna use congestion ? [y/n]");
        String s = scan.next();
        boolean congestion;
        while(!"Y".equals(s.toUpperCase())&& !s.toUpperCase().equals("N")){
            System.out.println("Do you want to use congestion?");
            s = scan.next();
        }
        congestion = s.toUpperCase().equals("Y");
        	AbstractScheduler scheduler= new Scheduler();
		Network network= new Network(scheduler);
    	try {
    		final EthernetAddress MAC_ADDR1= EthernetAddress.getByAddress(0x00, 0x26, 0xbb, 0x4e, 0xfc, 0x28);
    		final EthernetAddress MAC_ADDR2= EthernetAddress.getByAddress(0x00, 0x26, 0xbb, 0x4e, 0xfc, 0x29);
    		final IPAddress IP_ADDR1= IPAddress.getByAddress(192, 168, 0, 1);
    		final IPAddress IP_ADDR2= IPAddress.getByAddress(192, 168, 0, 2);

    		IPHost host1= NetworkBuilder.createHost(network, "H1", IP_ADDR1, MAC_ADDR1);
    		host1.getIPLayer().addRoute(IP_ADDR2, "eth0");

                
                
                AbstractApplication sender = new GbnSender(host1, IP_ADDR2, congestion);
                
                ((GbnSender)sender).dudename="sen0";

                host1.addApplication(sender);
                int j = 0;
                System.out.println("How much packets do you wanna send ?");
                j=scan.nextInt();
                
                for(int i=0;i<j;i++){      
                    ((GbnSender)sender).addToSendingQueue("<data_n°"+String.valueOf(i)+">");
                }
                
    		IPHost host2= NetworkBuilder.createHost(network,"H2", IP_ADDR2, MAC_ADDR2);
    		host2.getIPLayer().addRoute(IP_ADDR1, "eth0");
                
                AbstractApplication receiver = new GbnReceiver(host2);
                ((GbnReceiver)receiver).dudename="rec1";
    		host2.addApplication(receiver);

    		EthernetInterface h1_eth0= (EthernetInterface) host1.getInterfaceByName("eth0");
    		EthernetInterface h2_eth0= (EthernetInterface) host2.getInterfaceByName("eth0");
    		
    		// Connect both interfaces with a 5000km long link
    		new Link<EthernetFrame>(h1_eth0, h2_eth0, 5000000, 100000);

                
                RandomSimulator.receivingLosP=0;
                RandomSimulator.sendingLosP=5;       //Each package send has 5% chance to be lost
                
    		host1.start();
    		host2.start();
    		
    		scheduler.run();
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		e.printStackTrace(System.err);
    	}
    }
    
}
