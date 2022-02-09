package jtn.classSchedule.backend.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;


@Configuration
@EnableSolrRepositories(solrTemplateRef = "timetableSolrTemplate", basePackages = "jtn.classSchedule.backend.persistence.course")
@EntityScan
@ComponentScan
public class TimetableSolrConfiguration {

    @Value("${datasource.timetable.solr.host}")
    private String solrHost;

    @Bean("timetableSolrClient")
    public SolrClient timetableSolrClient() {
        return new HttpSolrClient.Builder(solrHost).build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "solrClient")
    public SolrClient solrClient() {
        return timetableSolrClient();
    }

    @Bean("timetableSolrTemplate")
    public SolrTemplate solrTemplate() {
        return new SolrTemplate(this::solrClient);
    }

}