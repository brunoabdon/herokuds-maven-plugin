package com.github.brunoabdon.m2.herokuds;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Plugin maven que cria propriedades na execução maven com banco de dados, 
 * usuário e senha baseados na variável de ambiente do heroku "DATABASE_URL".
 * 
 * <p>Serve pra fazer o parse da variável do heroku e depois substituir num
 * yaml pra configurar a base.</p>
 * 
 *  <p>A variável de ambiente {@code DATABASE_URL} deve ser setada na 
 *  propriedade do pom {@value #PROP_DATABASE_URL}.</p>
 */
@Mojo(name="parse", defaultPhase=LifecyclePhase.PROCESS_RESOURCES)
public class HerokuDSMojo extends AbstractMojo {

    public static final String PROP_DATABASE_URL = "herokuds.database_url";
    private static final String POSTGRESQL_JDBC_PREFIX = "jdbc:postgresql://";

    //@TODO make it configurable
    private String databaseURLProperty = "herokuds.connectionURL";
    private String usernameProperty = "herokuds.username";
    private String passwordProperty = "herokuds.password";
    
    @Parameter(defaultValue = "${project}")
	private MavenProject project;

	public void execute() throws MojoExecutionException {

		final Log logger = getLog();
		
		final Properties properties = this.project.getProperties();

        logger.info("Reading property " + PROP_DATABASE_URL);
        final String herokuDBUrl =  properties.getProperty(PROP_DATABASE_URL);

        if(herokuDBUrl == null) {
        	throw new MojoExecutionException(
    			"Configure maven property " + PROP_DATABASE_URL
			);
        }		

		try {
	        logger.info("Parsing...");
			final URI dbUri = new URI(herokuDBUrl);
    	    	
	    	final String userInfo = dbUri.getUserInfo();
	
	    	if(userInfo == null) {
	    		logger.error(
					"Aborting: URI set on '" + PROP_DATABASE_URL + "' does not"
			        + " contains user and password. Unlikely to work."
				);
	    		throw new MojoExecutionException(
					"No user info on heroku database URI."
				);
	    	}
	    	
	        final String query = dbUri.getQuery();
			final String dbUrl =
	            POSTGRESQL_JDBC_PREFIX
	            + dbUri.getHost()
	            + ":" + dbUri.getPort()
	            + dbUri.getPath()
	            + (query != null ? "?" + query : "");

			this.setProperty(logger, properties, databaseURLProperty, dbUrl);
			
	        final String[] splitted = userInfo.split(":");
	        final String username = splitted[0];
	
	        this.setProperty(logger, properties, usernameProperty, username);
	        this.setProperty(logger, properties, passwordProperty,splitted[1]);
	
		} catch (final URISyntaxException e) {
			throw new MojoExecutionException(
				"URI mal formada " + herokuDBUrl
			);
		}
    }

	private void setProperty(
            final Log logger,
            final Properties properties,
            final String propertyName,
            final String propertyValue) {

        logger.info("Setting property " + propertyName);
        properties.setProperty(propertyName, propertyValue);
        
    }
}