package com.example.service;

import com.example.entity.Equipment;
import com.example.entity.Eqreserve;
import com.example.mapper.EquipmentMapper;
import com.example.mapper.EqreserveMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class EquipmentService {

    @Resource
    private EquipmentMapper equipmentMapper;
    @Resource
    private EqreserveMapper eqreserveMapper;

    /**
     * 新增
     */
    public void add(Equipment equipment) {
        equipmentMapper.insert(equipment);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        equipmentMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            equipmentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Equipment equipment) {
        equipmentMapper.updateById(equipment);
    }

    /**
     * 根据ID查询
     */
    public Equipment selectById(Integer id) {
        Equipment equipment = equipmentMapper.selectById(id);
        updateEquipmentStatus(equipment);
        return equipment;
    }

    /**
     * 查询所有
     */
    public List<Equipment> selectAll(Equipment equipment) {
        List<Equipment> list = equipmentMapper.selectAll(equipment);
        for (Equipment item : list) {
            updateEquipmentStatus(item);
        }
        return list;
    }

    /**
     * 分页查询
     */
    public PageInfo<Equipment> selectPage(Equipment equipment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Equipment> list = equipmentMapper.selectAll(equipment);
        for (Equipment item : list) {
            updateEquipmentStatus(item);
        }
        return PageInfo.of(list);
    }

    /**
     * 更新器材状态
     */
    private void updateEquipmentStatus(Equipment equipment) {
        if (equipment == null) {
            return;
        }
        
        // 查询该器材的所有审核通过的预约
        List<Eqreserve> reserves = eqreserveMapper.selectAll(new Eqreserve() {
            {
                setEquipmentId(equipment.getId());
                setStatus("审核通过");
            }
        });
        
        // 获取当前时间
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 检查是否有当前时间正在使用的预约
        boolean isInUse = false;
        for (Eqreserve reserve : reserves) {
            try {
                Date start = sdf.parse(reserve.getStart());
                Date end = sdf.parse(reserve.getEnd());
                
                // 如果当前时间在预约时段内
                if (now.after(start) && now.before(end)) {
                    isInUse = true;
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        // 更新器材状态
        if (isInUse) {
            equipment.setStatus("使用中");
        } else {
            equipment.setStatus("空闲中");
        }
    }

}