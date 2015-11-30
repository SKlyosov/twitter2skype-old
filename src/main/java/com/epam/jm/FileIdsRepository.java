package com.epam.jm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import com.google.common.base.Preconditions;

public class FileIdsRepository implements IIdsRepository{

	private String fileName;
	private HashSet<Long> ids;
	
	public FileIdsRepository(String fileName) {
		Preconditions.checkNotNull(fileName, "fileName maust be not null"); 
		this.fileName = fileName;
		loadIds();
	}
	
	@SuppressWarnings("unchecked")
	private void loadIds() {
		File file = new File(fileName);
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis)) {
				ids = (HashSet<Long>)ois.readObject();
				
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ids == null) {
			ids = new HashSet<Long>();
		}
	}


	@Override
	public void close() throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		try (FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(ids);
		}
		
	}


	@Override
	public void save(long id) {
		if (!ids.contains(id)) {
			ids.add(id);
		}
		
	}


	@Override
	public boolean contains(long id) {
		return ids.contains(id);
	}


}
