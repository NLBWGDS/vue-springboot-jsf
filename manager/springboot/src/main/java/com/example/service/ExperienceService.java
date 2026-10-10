package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Coach;
import com.example.entity.Experience;
import com.example.entity.User;
import com.example.mapper.CoachMapper;
import com.example.mapper.ExperienceMapper;
import com.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ExperienceService {

    @Resource
    private ExperienceMapper experienceMapper;

    @Resource
    private UserMapper userMapper;
    @Resource
    private CoachMapper coachMapper;
    /**
     * 新增
     */
    public void add(Experience experience) {
        experience.setTime(DateUtil.now());
        experienceMapper.insert(experience);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        experienceMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            experienceMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Experience experience) {
        experienceMapper.updateById(experience);
    }

    /**
     * 根据ID查询
     */
    public Experience selectById(Integer id) {
        return experienceMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Experience> selectAll(Experience experience) {
        return experienceMapper.selectAll(experience);
    }

    /**
     * 分页查询
     */
    public PageInfo<Experience> selectPage(Experience experience, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Experience> list = experienceMapper.selectAll(experience);
        for(Experience dbExperience:list){
            if(RoleEnum.COACH.name().equals(dbExperience.getRole())){
                Coach coach = coachMapper.selectById(dbExperience.getUserId());
                dbExperience.setUserName(coach.getName());
            }else {
                User user = userMapper.selectById(dbExperience.getUserId());
                dbExperience.setUserName(user.getName());
            }
        }
        return PageInfo.of(list);
    }

}