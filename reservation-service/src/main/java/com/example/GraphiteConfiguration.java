package com.example;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(name = "graphite.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(GraphiteConfig.class)
public class GraphiteConfiguration {

	@Bean
	GraphiteReporter graphiteReporter(MetricRegistry registry, GraphiteConfig graphite) {
		GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
				.prefixedWith("reservations")
				.build(new Graphite(graphite.host, graphite.port));
		reporter.start(2, TimeUnit.SECONDS);
		return reporter;
	}
}

@Data
@ConfigurationProperties(prefix = "graphite")
class GraphiteConfig {

	String host;

	int port;
}
