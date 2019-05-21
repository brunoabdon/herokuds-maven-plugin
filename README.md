# herokuds-maven-plugin
## Uso:
### Primeiro, consutrua e instale:
     $mvn clean package isntall
### Depois use mesmo:
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
