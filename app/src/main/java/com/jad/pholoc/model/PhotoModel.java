package com.jad.pholoc.model;

import java.util.Date;

/**
 * Model Photo
 * 
 * @author Jorge Alvarado
 * 
 */
public class PhotoModel {
	private int id;

	private int idLocation;
	private String name;
	private String path;
	private Date date;

	public PhotoModel(int id, int idLocation, String name, String path,
			Date date) {
		this.id = id;
		this.idLocation = idLocation;
		this.name = name;
		this.path = path;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdLocation() {
		return idLocation;
	}

	public void setIdLocation(int idLocation) {
		this.idLocation = idLocation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
