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
	
	public static void main(String[] args) {
	
		try (FileIdsRepository vkIdsRepository = new FileIdsRepository(args[0]);
				FileIdsRepository twitterIdsRepository = new FileIdsRepository(args[1])) {
			
			NewsService newService = new NewsService(new IPublisher[] {new VkReceiver(vkIdsRepository),
					new TwitterReceiver(twitterIdsRepository)},
					new ISubscriber[] {new SkypeSender()});
			
			newService.run();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
	public void run() throws Exception {
		
		for (IPublisher publisher : publishers) {
			
			String msg;
			if ((msg = publisher.receive()) != null) {
				for (ISubscriber subscriber : subscribers) {
					subscriber.send(msg);
				}
			}
			
		}
		
	}

}
