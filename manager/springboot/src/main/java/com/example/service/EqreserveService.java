package com.example.service;

import com.example.entity.Eqreserve;
import com.example.entity.Equipment;
import com.example.exception.CustomException;
import com.example.mapper.EqreserveMapper;
import com.example.mapper.EquipmentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EqreserveService {

    @Resource
    private EqreserveMapper eqreserveMapper;
    @Resource
    private EquipmentMapper equipmentMapper;

    /**
     * 新增
     */
    public void add(Eqreserve eqreserve) {
        // 检查时段冲突
        List<Eqreserve> existingReserves = eqreserveMapper.selectAll(new Eqreserve() {
            {
                setEquipmentId(eqreserve.getEquipmentId());
            }
        });
        
        for (Eqreserve existing : existingReserves) {
            // 检查时段是否重叠
            if (isTimeOverlap(eqreserve.getStart(), eqreserve.getEnd(), existing.getStart(), existing.getEnd())) {
                throw new CustomException("-1", "该时段器材已被其他用户预约，无法预约");
            }
        }
        
        eqreserveMapper.insert(eqreserve);
        // 更新器材状态为使用中
        Equipment equipment = equipmentMapper.selectById(eqreserve.getEquipmentId());
        if (equipment != null) {
            equipment.setStatus("使用中");
            equipmentMapper.updateById(equipment);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        eqreserveMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            eqreserveMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Eqreserve eqreserve) {
        // 审核通过时检查时段冲突
        if ("审核通过".equals(eqreserve.getStatus())) {
            // 查询该器材在相同时段的其他预约
            List<Eqreserve> existingReserves = eqreserveMapper.selectAll(new Eqreserve() {
                {
                    setEquipmentId(eqreserve.getEquipmentId());
                }
            });
            
            for (Eqreserve existing : existingReserves) {
                // 排除当前预约本身
                if (!existing.getId().equals(eqreserve.getId())) {
                    // 检查时段是否重叠
                    if (isTimeOverlap(eqreserve.getStart(), eqreserve.getEnd(), existing.getStart(), existing.getEnd())) {
                        throw new CustomException("-1", "该时段器材已被其他用户预约，无法审核通过");
                    }
                }
            }
        }
        
        eqreserveMapper.updateById(eqreserve);
        // 根据审核状态更新器材状态
        Equipment equipment = equipmentMapper.selectById(eqreserve.getEquipmentId());
        if (equipment != null) {
            if ("审核通过".equals(eqreserve.getStatus())) {
                equipment.setStatus("使用中");
            } else if ("审核不通过".equals(eqreserve.getStatus())) {
                equipment.setStatus("空闲中");
            }
            equipmentMapper.updateById(equipment);
        }
    }
    
    /**
     * 检查两个时间段是否重叠
     */
    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        return !(end1.compareTo(start2) <= 0 || start1.compareTo(end2) >= 0);
    }

    /**
     * 根据ID查询
     */
    public Eqreserve selectById(Integer id) {
        return eqreserveMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Eqreserve> selectAll(Eqreserve eqreserve) {
        return eqreserveMapper.selectAll(eqreserve);
    }

    /**
     * 分页查询
     */
    public PageInfo<Eqreserve> selectPage(Eqreserve eqreserve, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Eqreserve> list = eqreserveMapper.selectAll(eqreserve);
        return PageInfo.of(list);
    }

}
