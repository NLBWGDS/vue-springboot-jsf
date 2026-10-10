package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.Course;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.mapper.CourseMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程管理
 **/
@Service
public class CourseService {

    @Resource
    private CourseMapper courseMapper;

    /**
     * 新增
     */
    public void add(Course course) {
        if (ObjectUtil.isNull(course.getName())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "课程名称不能为空");
        }
        if (ObjectUtil.isNull(course.getTime())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "课程周期不能为空");
        }
        if (ObjectUtil.isNull(course.getPrice())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "课程价格不能为空");
        }
        if (ObjectUtil.isNull(course.getCoachId())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "教练不能为空");
        }
        courseMapper.insert(course);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        courseMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            courseMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Course course) {
        courseMapper.updateById(course);
    }

    /**
     * 根据ID查询
     */
    public Course selectById(Integer id) {
        return courseMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Course> selectAll(Course course) {
        return courseMapper.selectAll(course);
    }

    /**
     * 分页查询
     */
    public PageInfo<Course> selectPage(Course course, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> list = courseMapper.selectAll(course);
        return PageInfo.of(list);
    }


    public List<Course> selectFour() {
        return courseMapper.selectFour();
    }
}