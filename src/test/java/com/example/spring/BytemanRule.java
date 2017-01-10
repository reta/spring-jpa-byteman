package com.example.spring;

import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitConfigState;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class BytemanRule extends BMUnitRunner implements MethodRule {
	private final BMUnitConfig config;
	
	public static BytemanRule create( Class< ? > klass ) {
		try {
			return new BytemanRule( klass ); 
		} catch( Exception ex ) { 
			throw new RuntimeException( ex ); 
		}
	}
	
	private BytemanRule( Class<?> klass ) throws InitializationError {
		super( klass );
		this.config = klass.getAnnotation(BMUnitConfig.class);
	}
	
	@Override
	public Statement apply( final Statement statement, final FrameworkMethod method, final Object target ) {
		Statement result = addMethodMultiRuleLoader( statement, method ); 
		
		if( result == statement ) {
			result = addMethodSingleRuleLoader( statement, method );
		}
		
		final BMUnitConfig methodConfig = method.getAnnotation( BMUnitConfig.class ); 
		if (methodConfig == null && config == null) {
			throw new IllegalStateException("Please annotate test class or test method with @BMUnitConfig anonnation");
		}
		
		return wrap( result,  getTestClass().getJavaClass(), methodConfig == null ? methodConfig : config );
	}

	private Statement wrap( final Statement result, final Class<?> klass, final BMUnitConfig config ) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				BMUnitConfigState.pushConfigurationState( config, klass );
				result.evaluate();
				BMUnitConfigState.popConfigurationState( klass );
			}
		};
	}
}
