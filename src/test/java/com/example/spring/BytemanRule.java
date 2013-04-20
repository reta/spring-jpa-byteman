package com.example.spring;

import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class BytemanRule extends BMUnitRunner implements MethodRule {
	public static BytemanRule create( Class< ? > klass ) {
		try {
			return new BytemanRule( klass ); 
		} catch( InitializationError ex ) { 
			throw new RuntimeException( ex ); 
		}
	}
	
	private BytemanRule( Class<?> klass ) throws InitializationError {
		super( klass );
	}
	
	@Override
	public Statement apply( final Statement statement, final FrameworkMethod method, final Object target ) {
		Statement result = addMethodMultiRuleLoader( statement, method ); 
		
		if( result == statement ) {
			result = addMethodSingleRuleLoader( statement, method );
		}
		
		return result;
	}
}
