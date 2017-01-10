package com.example.spring;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMRules;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.CannotCreateTransactionException;

import com.example.spring.config.AppConfig;
import com.example.spring.domain.Customer;
import com.example.spring.services.CustomerService;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class } )
@BMUnitConfig
public class CustomerServiceTestCase {
	@Rule public BytemanRule byteman = BytemanRule.create( CustomerServiceTestCase.class );
	@Inject private CustomerService customerService;	
	
	@After
	public void tearDown() {
		customerService.deleteAll();
	}

	@Test
	public void testCreateCustomerAndVerifyItHasBeenCreated() throws Exception {
		Customer customer = customerService.create( "Customer A" );
		assertThat( customerService.find( customer.getId() ), notNullValue() );
	}
	
	@Test( expected = CannotCreateTransactionException.class )
	@BMRules(
		rules = {
			@BMRule(
				name="create countDown for AbstractPlainSocketImpl",
                targetClass = "java.net.AbstractPlainSocketImpl",
                targetMethod = "getOutputStream",
                condition = "$0.port==3306",
                action = "createCountDown( \"connection\", 1 )"
            ),
			@BMRule(
		    	name = "throw IOException when trying to execute 2nd query to MySQL",
		        targetClass = "java.net.AbstractPlainSocketImpl",
		        targetMethod = "getOutputStream",
		        condition = "$0.port==3306 && countDown( \"connection\" )",
		        action = "throw new java.io.IOException( \"Connection refused (simulated)\" )"
		    )
		}
	)
	public void testCreateCustomerAndTryToFindItWhenDatabaseIsDown() {
		Customer customer = customerService.create( "Customer A" );
		customerService.find( customer.getId() );
	}
	
	@Test( expected = DataAccessException.class )
	@BMRule(
    	name = "introduce timeout while accessing MySQL database",
        targetClass = "com.mysql.jdbc.PreparedStatement",
        targetMethod = "executeQuery",
        targetLocation = "AT ENTRY",
        condition = "$0.originalSql.startsWith( \"select\" ) && !flagged( \"timeout\" )",
        action = "flag( \"timeout\" ); throw new com.mysql.jdbc.exceptions.MySQLTimeoutException( \"Statement timed out (simulated)\" )"
    )
	public void testCreateCustomerWhileDatabaseIsTimingOut()  {
		Customer customer = customerService.create( "Customer A" );
		customerService.find( customer.getId() );
	}
}
