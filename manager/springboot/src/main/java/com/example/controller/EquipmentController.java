package com.example.controller;

import com.example.common.Result;
import com.example.entity.Equipment;
import com.example.service.EquipmentService;
import com.example.utils.OcrUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Resource
    private EquipmentService equipmentService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Equipment equipment) {
        equipmentService.add(equipment);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        equipmentService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        equipmentService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Equipment equipment) {
        equipmentService.updateById(equipment);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Equipment equipment = equipmentService.selectById(id);
        return Result.success(equipment);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Equipment equipment ) {
        List<Equipment> list = equipmentService.selectAll(equipment);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Equipment equipment,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Equipment> page = equipmentService.selectPage(equipment, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 图片文字识别
     */
    @PostMapping("/ocr")
    public Result ocr(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("400", "请选择图片文件");
        }

        try {
            // 保存临时文件
            String tempPath = System.getProperty("java.io.tmpdir") + File.separator + file.getOriginalFilename();
            File tempFile = new File(tempPath);
            file.transferTo(tempFile);

            // 调用百度云OCR接口
            String result = OcrUtils.generalBasic(tempPath);
            String text = OcrUtils.extractText(result);

            // 删除临时文件
            tempFile.delete();

            return Result.success(text);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("500", "图片处理失败");
        }
    }

}