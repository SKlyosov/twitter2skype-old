package com.epam.jm;

import java.io.Closeable;

public interface IIdsRepository extends Closeable{
	
	public void save(long id);
	
	public boolean contains(long id);
	
}
