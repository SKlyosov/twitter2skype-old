package com.epam.jm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyLoader {
	
	private final static Logger logger = LogManager.getLogger(VkReceiver.class);
	private static Properties props = new Properties();
	
	public static void load() throws IOException {
		
		File file = new File("skypetwitter.properties");
		try (Reader reader = new FileReader(file)) {
			props.load(reader);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public static String getProperty(String key) {
		return props.getProperty(key);
	}

}
