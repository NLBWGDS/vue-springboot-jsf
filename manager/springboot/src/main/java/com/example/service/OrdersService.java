package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 订单处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CoachMapper coachMapper;
    /**
     * 新增
     */
    public void add(Orders orders) {
        // 检查当前登录用户的角色，只有用户可以购买课程
        Account currentUser = TokenUtils.getCurrentUser();
        if (!RoleEnum.USER.name().equals(currentUser.getRole())) {
            throw new CustomException("-1", "您的角色暂不支持该操作");
        }
        // 先判断下用户的余额够不够
        User user = userMapper.selectById(orders.getUserId());
        if (user.getAccount() < orders.getPrice()) {
            throw new CustomException("-1", "您的余额不足，请到个人中心充值");
        }
        orders.setTime(DateUtil.now());
        orders.setOrderNo(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        orders.setStatus("UNCOMPLETED"); // 默认状态为未完成
        orders.setRefundStatus("NONE"); // 默认无退款
        ordersMapper.insert(orders);

        // 扣除用户的余额
        user.setAccount(user.getAccount() - orders.getPrice());
        userMapper.updateById(user);

    }

    /**
     * 完成订单
     */
    public void complete(Integer id) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (!RoleEnum.COACH.name().equals(currentUser.getRole())) {
            throw new CustomException("-1", "只有教练才能执行此操作");
        }
        Orders orders = ordersMapper.selectById(id);
        if (!orders.getRefundStatus().equals("NONE")) {
            throw new CustomException("-1", "该订单已申请退款，无法完成课程");
        }
        Course course = courseMapper.selectById(orders.getCourseId());
        if (course == null || !course.getCoachId().equals(currentUser.getId())) {
            throw new CustomException("-1", "您没有权限操作该订单");
        }
        orders.setStatus("COMPLETED");
        ordersMapper.updateById(orders);
    }

    /**
     * 申请退款
     */
    public void applyRefund(Integer id) {
        Orders orders = ordersMapper.selectById(id);
        if (orders.getStatus().equals("COMPLETED")) {
            throw new CustomException("-1", "已完成的订单不可退款");
        }
        orders.setRefundStatus("APPLYING");
        ordersMapper.updateById(orders);
    }

    /**
     * 审核退款
     */
    public void auditRefund(Integer id, String status) {
        Orders orders = ordersMapper.selectById(id);
        if (!orders.getRefundStatus().equals("APPLYING")) {
            throw new CustomException("-1", "该订单未申请退款");
        }
        if (status.equals("APPROVED")) {
            orders.setRefundStatus("APPROVED");
            // 退还用户余额
            User user = userMapper.selectById(orders.getUserId());
            user.setAccount(user.getAccount() + orders.getPrice());
            userMapper.updateById(user);
        } else {
            orders.setRefundStatus("REJECTED");
        }
        ordersMapper.updateById(orders);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        // 只有管理员可以删除订单
        Account currentUser = TokenUtils.getCurrentUser();
        if (!RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            throw new CustomException("-1", "您没有权限删除订单");
        }
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        // 只有管理员可以删除订单
        Account currentUser = TokenUtils.getCurrentUser();
        if (!RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            throw new CustomException("-1", "您没有权限删除订单");
        }
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize, Integer coachId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        for (Orders dbOrders : list) {
            Integer userId = dbOrders.getUserId();
            User user = userMapper.selectById(userId);
            if (ObjectUtil.isNotEmpty(user)) {
                dbOrders.setUserName(user.getName());
            }

            Course course = courseMapper.selectById(dbOrders.getCourseId());
            if (ObjectUtil.isNotEmpty(course)) {
                dbOrders.setCourseName(course.getName());
                Coach coach = coachMapper.selectById(course.getCoachId());
                if (ObjectUtil.isNotEmpty(coach)) {
                    dbOrders.setCoachName(coach.getName());
                    dbOrders.setCoachId(coach.getId());
                }

            }
        }
        if (coachId != null) {
            list = list.stream().filter(o -> coachId.equals(o.getCoachId())).collect(java.util.stream.Collectors.toList());
        }
        return PageInfo.of(list);
    }

}