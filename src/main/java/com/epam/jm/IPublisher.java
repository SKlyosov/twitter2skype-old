package com.epam.jm;

public interface IPublisher {

	String receive() throws Exception;

	void init() throws Exception;
	
}
