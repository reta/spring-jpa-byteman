package com.example.spring.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring.domain.Customer;

@Service
public class CustomerService {
	@PersistenceContext private EntityManager entityManager;
	
	@Transactional( readOnly = true )
	public Customer find( long id ) {
		return this.entityManager.find( Customer.class, id );
	}

	@Transactional( readOnly = false )
	public Customer create( final String name ) {
		final Customer customer = new Customer( name );
		this.entityManager.persist(customer);
		return customer;
	}
	
	@Transactional( readOnly = false )
	public void deleteAll() {
		this.entityManager.createQuery( "delete from Customer" ).executeUpdate();
	}
}
