package jtn.classSchedule.backend.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.stereotype.Component;


@EnableSolrRepositories(solrTemplateRef = "userSolrTemplate", basePackages = "jtn.classSchedule.backend.persistence.user")
@Component
public class UserSolrConfiguration {

    @Value("${datasource.user.solr.host}")
    private String solrHost;

    @Bean("userSolrClient")
    public SolrClient userSolrClient() {
        return new HttpSolrClient.Builder(solrHost).build();
    }

    @Bean("userSolrTemplate")
    public SolrTemplate solrTemplate() {
        return new SolrTemplate(this::userSolrClient);
    }

}