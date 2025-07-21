package com.increff.pos.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import static com.google.common.base.CaseFormat.*;

public class SnakeCaseNamingStrategy extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = 1L;
	
	private String projectName;

	public SnakeCaseNamingStrategy(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		return new Identifier(this.addPrefix(UPPER_CAMEL.to(LOWER_UNDERSCORE, name.getText())), name.isQuoted());
	}

	private String addPrefix(String tableName) {
		return projectName + "_" + tableName;
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
		return new Identifier(LOWER_CAMEL.to(LOWER_UNDERSCORE, name.getText()), name.isQuoted());
	}
} 