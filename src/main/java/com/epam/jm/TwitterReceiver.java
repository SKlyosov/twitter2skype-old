package com.epam.jm;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;

public class TwitterReceiver implements IPublisher {

	private final static Logger logger = LogManager.getLogger(TwitterReceiver.class);
	
	private IIdsRepository idsRepository;
	private Twitter twitter;
	private List<Status> statuses;
	private int curPos;
	private String twitter_user_id;
	
	public TwitterReceiver(IPropertyLoader propertLoader, IIdsRepository idsRepository) throws TwitterException {
		Preconditions.checkNotNull(propertLoader, "propertLoader can not be null");
		Preconditions.checkNotNull(idsRepository, "propertLoader can not be null");
		this.idsRepository = idsRepository;
        twitter = new TwitterFactory().getInstance();
        twitter_user_id = propertLoader.getProperty("twitter.user");
        init();
	}
	
	@Override
	public void init() throws TwitterException {
        try {
			statuses = twitter.getUserTimeline(twitter_user_id);
		} catch (TwitterException e) {
			logger.error(e.getErrorMessage(), e);
			throw e;
		}
        curPos = 0;
	}
	
	public String receive() {
		
		String result = null;
		
		if (statuses != null) {
			while (curPos < statuses.size()) {
				Status status = statuses.get(curPos);
				long curId = status.getId();
				if (!idsRepository.contains(curId)) {
	                Date date = status.getCreatedAt();
	                result = new StringBuilder("@")
	                	.append(status.getUser().getScreenName())
	                	.append(" от ")
	                	.append(DateHelper.getShiftedDateString(date, 7))
	                	.append(" ( https://twitter.com/")
	                    .append(twitter_user_id)
	                    .append("/status/")
	                    .append(status.getId())
	                    .append(" ): \r\n")
	                    .append(status.getText())
	                    .append("\r\n***")
	                    .toString();
	                for (URLEntity e : status.getURLEntities()) {
	                    result = result.replaceAll(e.getURL(), e.getExpandedURL());
	                }
	                for (MediaEntity e : status.getMediaEntities()) {
	                	result = result.replaceAll(e.getURL(), e.getMediaURL());
	                }
					curPos++;
					break;
				} else {
					curPos++;
				}
			}
		}
		logger.info("Getting a twitter message \"{}\"", result);
		return result;
	}
	
}
