package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseImage;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.repository.CourseImageRepository;
import com.follow_me.running_mate.domain.enums.CourseImageType;
import com.follow_me.running_mate.global.common.service.S3ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CourseImageServiceImpl implements CourseImageService {

    private final CourseEntityMapper courseEntityMapper;
    private final CourseImageRepository courseImageRepository;

    private final S3ImageService s3ImageService;


    @Override
    public void saveCourseImages(
        Course course, MultipartFile representativeImage, MultipartFile startImage, MultipartFile endImage
    ) {
        if (representativeImage != null) {
            saveCourseImage(course, representativeImage, CourseImageType.REPRESENTATIVE);
        }
        if (startImage != null) {
            saveCourseImage(course, startImage, CourseImageType.START);
        }
        if (endImage != null) {
            saveCourseImage(course, endImage, CourseImageType.FINISH);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseImage> getCourseImages(Course course) {
        return courseImageRepository.findAllByCourse(course);
    }

    private void saveCourseImage(Course course, MultipartFile image, CourseImageType type) {
        String imageUrl = s3ImageService.upload(image);
        courseImageRepository.save(courseEntityMapper.toCourseImage(course, imageUrl, type));
    }
}
