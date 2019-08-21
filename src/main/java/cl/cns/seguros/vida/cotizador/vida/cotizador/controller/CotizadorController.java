package cl.cns.seguros.vida.cotizador.vida.cotizador.controller;


import cl.cns.seguros.vida.cotizador.vida.cotizador.client.GitHubService;
import cl.cns.seguros.vida.cotizador.vida.cotizador.to.ContributorGithub;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import java.net.DatagramSocket;
import java.net.InetAddress;

@RestController
public class CotizadorController {

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    private final GitHubService gitHubService;

    public CotizadorController(GitHubService gitHubService){
        this.gitHubService = gitHubService;
    }

    @RequestMapping("/test")
    public String testMessage() {
        String ip = "";
		try(final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
             ip = socket.getLocalAddress().getHostAddress();
        } catch (Exception excp) {
        	
        }
        return String.format(template, ip));
    }


    @RequestMapping("/testCircuitPersonalizado")
    public List<ContributorGithub> testCircuitPersonalizado(@RequestParam(value="name", defaultValue="World") String name) {


        List<ContributorGithub> contributors = this.gitHubService.testCircuitPersonalizado();

        return  contributors;
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {


        List<ContributorGithub> contributors = this.gitHubService.contributors2();
        //this.testResilience();
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name + "-" + contributors.size()));
    }

    private void testFeign() {
        /*
        GitHub github = Feign.builder()
                .decoder(new GsonDecoder())
                .client(new Http2Client())
                .target(GitHub.class, "https://api.github.com");

        List<ContributorGithub> contributors = github.contributors("OpenFeign", "feign");
        for (ContributorGithub contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }
        */
    }

    private void testResilience(){

    }

}

class Greeting {

    private final long id;
    private final String content;

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
