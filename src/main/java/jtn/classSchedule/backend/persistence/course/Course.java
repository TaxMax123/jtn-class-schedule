package jtn.classSchedule.backend.persistence.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SolrDocument(solrCoreName = "timetable")
public class Course {
    @JsonProperty("id")
    @Field("id")
//    @Indexed(name = "id", type = "string")
    private String docId;
    @JsonProperty("courseId_s")
    @Field("courseId_s")
    private String id;
    @JsonProperty("courseProgramShortName_s")
    @Indexed(name = "courseProgramShortName_s", type = "string")
    private String programShortName;
    @JsonProperty("courseName_s")
    @Indexed(name = "courseName_s", type = "string")
    private String name;
    @JsonProperty("courseShortName_s")
    @Field("courseShortName_s")
    private String shortName;
    @JsonProperty("courseHolder_s")
    @Field("courseHolder_s")
    private String holder;
    @JsonProperty("courseLecturer_s")
    @Field("courseLecturer_s")
    private String lecturer;
    @JsonProperty("courseLectureType_s")
    @Indexed(name = "courseLectureType_s", type = "string")
    private String lectureType;
    @JsonProperty("courseClassroom_s")
    @Field("courseClassroom_s")
    private String classroom;
    @JsonProperty("classroomCapacity_i")
    @Field("classroomCapacity_i")
    private Integer classroomCapacity;
    @JsonProperty("courseStartTime_s")
    @Field("courseStartTime_s")
    private String startTime;
    @JsonProperty("courseEndTime_s")
    @Field("courseEndTime_s")
    private String endTime;
    @JsonProperty("courseWeekday_s")
    @Field("courseWeekday_s")
    private String weekday;
    @JsonProperty("courseYear_i")
    @Indexed(name = "courseYear_i", type = "string")
    private Integer year;
    @JsonProperty("courseSemester_i")
    @Indexed(name = "courseSemester_i", type = "int")
    private Integer semester;
    @JsonProperty("courseRepeatTimes_i")
    @Field("courseRepeatTimes_i")
    private Integer repeatTimes;
    @JsonProperty("courseStartWeek_i")
    @Field("courseStartWeek_i")
    private Integer startWeek;
    @JsonProperty("courseActive_b")
    @Field("courseActive_b")
    private Boolean active;
}
