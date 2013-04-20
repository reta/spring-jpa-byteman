package com.example.spring.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "customers" )
public class Customer implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	private long id;
	
	@Column(name = "name", nullable = false)
	private String name;
		
	public Customer() {
	}
	
	public Customer( final String name ) {
		this.name = name;
	}

	public long getId() {
		return this.id;
	}

	protected void setId( final long id ) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName( final String name ) {
		this.name = name;
	}
}


