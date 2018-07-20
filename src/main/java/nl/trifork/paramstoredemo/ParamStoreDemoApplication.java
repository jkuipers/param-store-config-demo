package nl.trifork.paramstoredemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

@SpringBootApplication
public class ParamStoreDemoApplication implements CommandLineRunner {

	@Autowired ConfigurableEnvironment env;

	public static void main(String[] args) {
		SpringApplication.run(ParamStoreDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
        CompositePropertySource awsParamSources = null;
        OUTER: for (PropertySource<?> propertySource : env.getPropertySources()) {
            if (propertySource.getName().equals("bootstrapProperties")) {
                CompositePropertySource bootstrap = (CompositePropertySource) propertySource;
                for (PropertySource<?> nestedSource : bootstrap.getPropertySources()) {
                    if (nestedSource.getName().equals("aws-param-store")) {
                        awsParamSources = (CompositePropertySource) nestedSource;
                        break OUTER;
                    }
                }
            }
        }
        if (awsParamSources == null) {
            System.out.println("No AWS Parameter Store PropertySource found");
        } else {
            System.out.println("Overview of all AWS Param Store property sources:\n");
            for (PropertySource<?> nestedSource : awsParamSources.getPropertySources()) {
                EnumerablePropertySource eps = (EnumerablePropertySource) nestedSource;
                System.out.println(eps.getName() + ":");
                for (String propName : eps.getPropertyNames()) {
                    System.out.println("\t" + propName + " = " + eps.getProperty(propName));
                }

            }
        }
    }
}

