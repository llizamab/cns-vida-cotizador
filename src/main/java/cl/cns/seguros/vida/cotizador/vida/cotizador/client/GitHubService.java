package cl.cns.seguros.vida.cotizador.vida.cotizador.client;

import cl.cns.seguros.vida.cotizador.vida.cotizador.to.ContributorGithub;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.http2client.Http2Client;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component(value = "gitHubService")
public class GitHubService {


    public List<ContributorGithub> testFeign(){

        GitHub github = Feign.builder()
                .decoder(new GsonDecoder())
                .client(new Http2Client())
                .target(GitHub.class, "https://api.github.com");

        return github.contributors("OpenFeign", "feign");

    }

    public List<ContributorGithub> testCircuitPersonalizado(){
        // Create a custom configuration for a CircuitBreaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(10)
                .failureRateThreshold(5)
                .waitDurationInOpenState(Duration.ofMillis(10000))
                .ringBufferSizeInHalfOpenState(2)
                .ringBufferSizeInClosedState(2)

                .recordExceptions(IOException.class, TimeoutException.class)
                //.ignoreExceptions(Exception.class, Throwable.class)
                .build();

        // Create a CircuitBreakerRegistry with a custom global configuration
        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("testCircuitPersonalizado");

        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .build();
        GitHub myService = Resilience4jFeign.builder(decorators).decoder(new GsonDecoder()).client(new Http2Client()).target(GitHub.class, "https://apside.proxy.beeceptor.com");

        return myService.contributors("OpenFeign", "feign");

    }


    public List<ContributorGithub> contributors2(){

        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendName");

        RateLimiter rateLimiter = RateLimiter.ofDefaults("backendName");
        FeignDecorators decorators = FeignDecorators.builder()
                .withRateLimiter(rateLimiter)
                .withCircuitBreaker(circuitBreaker)
                .build();
        GitHub myService = Resilience4jFeign.builder(decorators).decoder(new GsonDecoder()).client(new Http2Client()).target(GitHub.class, "https://api.giithub.com");

        return myService.contributors("OpenFeign", "feign");

    }


    public List<ContributorGithub> contributors(Exception e){
        //return null en caso de excepción
        return new ArrayList<>();

    }

    public List<ContributorGithub> contributors(Throwable e){
        //return null en caso de excepción
        return new ArrayList<>();

    }


}
