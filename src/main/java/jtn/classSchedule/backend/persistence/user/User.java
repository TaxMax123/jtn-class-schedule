package jtn.classSchedule.backend.persistence.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
@Builder
@SolrDocument(solrCoreName = "users")
public class User {
    @Field("id")
    private String id;
    @Indexed(name = "userName_s", type = "string")
    private String userName;
    @Field("pwd_s")
    private String pwd;
    @Field("role_i")
    private Integer role;
    @Field("firstName_s")
    private String firstName;
    @Field("lastName_s")
    private String lastName;
    @Field("year_i")
    private Integer year;
    @Field("enrolledCourses_s")
    private ArrayList<String> enrolledCourses;
    @Field("allCoursesShown_b")
    private Boolean allCoursesShown;
}
