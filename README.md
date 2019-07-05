# herokuds-maven-plugin
## Uso:
### Primeiro, consutrua e instale:
     $mvn clean package isntall

### Cria a env var (ex, no bash):
     $export DATABASE_URL=postgresql://usuario:senha@localhost:5432/superBancoDeDados
     
### Depois use mesmo:
     <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    
        <herokuds.databas_url>${env.DATABASE_URL}</herokuds.databas_url>
    
     </properties>

     <plugin>
        <groupId>com.github.brunoabdon.m2.herokuds</groupId>
        <artifactId>herokuds-maven-plugin</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <executions>
          <execution>
            <phase>validate</phase>
            <goals>
              <goal>parse</goal>
            </goals>
          </execution>
        </executions>
      </plugin> 
