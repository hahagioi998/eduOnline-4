package com.jm.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jm.eduservice.entity.EduChapter;
import com.jm.eduservice.entity.EduCourse;
import com.jm.eduservice.entity.EduCourseDescription;
import com.jm.eduservice.entity.EduVideo;
import com.jm.eduservice.entity.chapter.ChapterVo;
import com.jm.eduservice.entity.vo.CourseInfoVo;
import com.jm.eduservice.entity.vo.CoursePublishVo;
import com.jm.eduservice.mapper.EduCourseMapper;
import com.jm.eduservice.service.EduChapterService;
import com.jm.eduservice.service.EduCourseDescriptionService;
import com.jm.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jm.eduservice.service.EduVideoService;
import com.jm.servicebase.exceptionhandler.JmDiyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author marc
 * @since 2020-11-14
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService eduCourseDescriptionService;
    @Autowired
    private EduVideoService eduVideoService;
    @Autowired
    private EduChapterService eduChapterService;

    //添加课程基本信息,并返回id
    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        int insert = baseMapper.insert(eduCourse);
        if(insert == 0){
            throw new JmDiyException(20001, "添加课程信息失败");
        }
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        String courseId = eduCourse.getId();
        eduCourseDescription.setId(courseId);
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        eduCourseDescriptionService.save(eduCourseDescription);
        return courseId;
    }

    //删除课程
    @Override
    public void removeCourse(String courseId) {
        //根据课程id删除所有小节
        eduVideoService.removeVideoByCourseId(courseId);
        //根据课程id删除所有章节
        eduChapterService.removeChapterByCourseId(courseId);
        //删除课程详情
        eduCourseDescriptionService.removeById(courseId);
        //删除完所有章节和小节后再删除课程
        int result = baseMapper.deleteById(courseId);
        if(result == 0){
            throw new JmDiyException(20001, "删除失败");
        }
    }

    //修改课程信息
    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        int update = baseMapper.updateById(eduCourse);
        if(update == 0) {
            throw new JmDiyException(20001,"修改课程信息失败");
        }
        //修改描述表
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseInfoVo.getId());
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        eduCourseDescriptionService.updateById(eduCourseDescription);
    }

    //根据课程id查询课程基本信息
    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        EduCourse eduCourse = baseMapper.selectById(courseId);
        BeanUtils.copyProperties(eduCourse, courseInfoVo);

        EduCourseDescription eduCourseDescription = eduCourseDescriptionService.getById(courseId);
        courseInfoVo.setDescription(eduCourseDescription.getDescription());
        return courseInfoVo;

    }

    //根据课程id查询课程确认信息
    @Override
    public CoursePublishVo publishCourseInfo(String id) {
        return baseMapper.getPublishCourseInfo(id);
    }

}
