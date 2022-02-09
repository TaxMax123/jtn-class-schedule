package jtn.classSchedule.backend.persistence.course;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
@Component("courseRepository")
public interface CourseRepository extends SolrCrudRepository<Course, String> {
    @Query("id:?0")
    Course findCourseByDocId(String id);

    ArrayList<Course> findCoursesByProgramShortNameAndYear(String programShortName, int year);

    ArrayList<Course> findCoursesByClassroom(String classroom);

    ArrayList<Course> findCoursesByClassroomAndWeekday(String classroom, String weekday);

    ArrayList<Course> findCoursesByLecturer(String lecturer);

    ArrayList<Course> findCoursesByLecturerOrHolder(String lecturer, String holder);

    ArrayList<Course> findCoursesById(String id);

    Course findCourseById(String id);

    ArrayList<Course> findCoursesByIdAndProgramShortNameAndNameAndShortNameAndHolderAndLecturerAndLectureTypeAndClassroomAndWeekdayAndSemester(
            String id,
            String programShortName,
            String name,
            String shortName,
            String holder,
            String lecturer,
            String lectureType,
            String classroom,
            String weekday,
            Integer semester
    );
}
