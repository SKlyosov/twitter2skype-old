package com.epam.jm;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewsService {

	private final static Logger logger = LogManager.getLogger(NewsService.class);
	
	private IPublisher[] publishers; 
	private ISubscriber[] subscribers; 
	
	public NewsService(IPublisher[] subscribers, ISubscriber[] senders) throws IOException {
		this.publishers = subscribers;
		this.subscribers = senders;
		PropertyLoader.load();
	}
	
	public void run() throws Exception {

		try {
			
			for (IPublisher publisher : publishers) {
				
				String msg;
				if ((msg = publisher.receive()) != null) {
					for (ISubscriber subscriber : subscribers) {
						subscriber.send(msg);
					}
				}
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
	}

}
