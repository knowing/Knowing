package de.lmu.ifi.dbs.knowing.core.test;

import akka.actor.UntypedActor;

public class MyActor extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		System.out.println("Message: " + msg);
	}

}
