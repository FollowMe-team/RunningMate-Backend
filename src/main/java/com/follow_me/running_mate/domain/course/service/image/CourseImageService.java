package com.follow_me.running_mate.domain.course.service.image;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseImage;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface CourseImageService {

    void saveCourseImages(
        Course course, MultipartFile representativeImage, MultipartFile startImage, MultipartFile endImage
    );
    List<CourseImage> getCourseImages(Course course);
}
