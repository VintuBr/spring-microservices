package br.com.microservices.edge;

import br.com.microservices.edge.filter.ErrorFilter;
import br.com.microservices.edge.filter.PostFilter;
import br.com.microservices.edge.filter.PreFilter;
import br.com.microservices.edge.filter.RouteFilter;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableZuulProxy
@RestController
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class)
				.web(WebApplicationType.SERVLET).run(args);
	}

//	@Bean
//	public AlwaysSampler defaultSampler() {
//	  return new AlwaysSampler();
//	}

	@Bean
	public PreFilter preFilter() {
		return new PreFilter();
	}
	@Bean
	public PostFilter postFilter() {
		return new PostFilter();
	}
	@Bean
	public ErrorFilter errorFilter() {
		return new ErrorFilter();
	}
	@Bean
	public RouteFilter routeFilter() {
		return new RouteFilter();
	}
}
