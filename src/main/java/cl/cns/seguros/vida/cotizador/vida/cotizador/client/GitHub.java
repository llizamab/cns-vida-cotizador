package cl.cns.seguros.vida.cotizador.vida.cotizador.client;

import cl.cns.seguros.vida.cotizador.vida.cotizador.to.ContributorGithub;
import cl.cns.seguros.vida.cotizador.vida.cotizador.to.IssueGithub;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

@FeignClient(value = "github", url = "https://api.github.com")
public  interface GitHub {
    @RequestLine("GET /repos/{owner}/{repo}/contributors")
    public List<ContributorGithub> contributors(@Param("owner") String owner, @Param("repo") String repo);

    @RequestLine("POST /repos/{owner}/{repo}/issues")
    public void createIssue(IssueGithub issue, @Param("owner") String owner, @Param("repo") String repo);

}