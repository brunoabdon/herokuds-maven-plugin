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
 * Plugin maven que cria propriedades na execucao maven com banco de dados, 
 * usuário e senha baseados na variáve de ambiente do heroku "DATABASE_URL".
 * 
 * <p>Serve pra fazer o parse da variável do heroku e depois substituir num
 * yaml pra configurar a base.</p>
 * 
 *  <p>Não tá funcionando pq a variável de ambiente não tá chegando aqui.</p>
 */
@Mojo(name="parse", defaultPhase=LifecyclePhase.VALIDATE)
public class HerokuDSMojo extends AbstractMojo {


    private static final String ENV_VARIABLE_DATABASE_URL = 
        "heroku.DATABASE_URL";
    private static final String POSTGRESQL_JDBC_PREFIX = "jdbc:postgresql://";

    @Parameter(defaultValue = "${project}")
	private MavenProject project;

	public void execute() throws MojoExecutionException {

		final Log logger = getLog();
		
		final Properties properties = this.project.getProperties();
		
        //ensure the Heroku datasource is deployed before the JEE 
        //application, so that the latter's persistence unit will be able 
        //to find a suitable datasource
        final String herokuDBUrl = 
            properties.getProperty(ENV_VARIABLE_DATABASE_URL);
        
        logger.info("Lendo environment variable " + ENV_VARIABLE_DATABASE_URL);
        logger.info("Lendo environment variable " + ENV_VARIABLE_DATABASE_URL);
            
        if(herokuDBUrl == null) {
        	throw new MojoExecutionException(
    			"Configure a env var " + ENV_VARIABLE_DATABASE_URL
			);
        }		
		
		
        
        logger.debug( "Criando URI da envvar");
    	
		try {
			final URI dbUri = new URI(herokuDBUrl);
    	    	
	        logger.debug( "Lendo usuário e senha da envvar");
	    	final String userInfo = dbUri.getUserInfo();
	
	    	if(userInfo == null) {
	    		logger.error(
					"Envvar de acesso ao banco não parece conter usuário e "
					+ "senha. Duvido que vá funcionar. Abortando."
				);
	    		throw new MojoExecutionException(
					"Envvar de acesso ao banco malformada"
				);
	    	}
	    	
	        final String query = dbUri.getQuery();
			final String dbUrl =
	            POSTGRESQL_JDBC_PREFIX
	            + dbUri.getHost()
	            + ":" + dbUri.getPort()
	            + dbUri.getPath()
	            + (query != null ? "?" + query : "");
	
			logger.info("JDBC URL: " + dbUrl);
			properties.setProperty("env.DATABASE_URL", dbUrl);
			
	        final String[] splitted = userInfo.split(":");
	        final String username = splitted[0];
	        logger.info("Usuário: " + username + ". Senha: *****");
	
	        properties.setProperty("env.JDBC_DATABASE_USERNAME",username);
	        properties.setProperty("env.JDBC_DATABASE_PASSWORD",splitted[1]);
	
		} catch (final URISyntaxException e) {
			throw new MojoExecutionException(
				"URI mal formada " + herokuDBUrl
			);
		}
    }
	
}
