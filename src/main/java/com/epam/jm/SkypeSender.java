package com.epam.jm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.skype.Chat;
import com.skype.Skype;
import com.skype.SkypeException;

public class SkypeSender implements ISubscriber {

	private final static Logger logger = LogManager.getLogger(SkypeSender.class);

	private Chat chat;
	
	public SkypeSender(IPropertyLoader propertyLoader) throws SkypeException {
		Preconditions.checkNotNull(propertyLoader, "idsRepository can not be null");
		String skypeGroupId = propertyLoader.getProperty("skype.chat_group_id"); 
		try {
			chat = Skype.chat(skypeGroupId);
		} catch (SkypeException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void send(String msg) throws SkypeException {
		try {
			chat.send(msg);
		} catch (SkypeException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

}
