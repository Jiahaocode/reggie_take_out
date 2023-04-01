package com.ithima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithima.reggie.common.R;
import com.ithima.reggie.dto.DishDto;
import com.ithima.reggie.dto.SetmealDto;
import com.ithima.reggie.entity.*;
import com.ithima.reggie.service.CategoryService;
import com.ithima.reggie.service.SetmealDishService;
import com.ithima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j

public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 保存套餐的菜品
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String>save (@RequestBody SetmealDto setmealDto){
        //自定义的保存套餐方法
        setmealService.saveWithDish(setmealDto);



        return R.success("添加成功");
    }


    /**
     * 分页套餐查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page>page(int page , int pageSize, String name){

        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> pageDto = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        setmealQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName,name);
        setmealQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分类查询
        setmealService.page(pageInfo,setmealQueryWrapper);
        //将分页数据拷贝
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Setmeal> records = pageInfo.getRecords();
        //处理pageInfo中查询到的套餐信息
        //item为遍历的每一个套餐对象
        List<SetmealDto> list = records.stream().map((item)->{
            //将套餐基本信息拷贝到setmealDto
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //获取分类id并查询出分类表中的名字
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(null!=category){
                String categoryName = category.getName();
                //将查询到的分类名设置
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        //将处理好的Records数据给pageDto
        pageDto.setRecords(list);
        return R.success(pageDto);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    // http://localhost:8080/setmeal?ids=1574540412511637505,1415580119015145474
    public R<String>delete (@RequestParam List <Long> ids){
        log.info("ids",ids);
        setmealService.removeWithDish(ids);

        return R.success("套餐数据添加成功");
    }

    /**
     * 停售服务
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String>updataStatus0(Long[] ids) {

        //将ids数组转换为list集合
        List<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,0);
        setmealService.update(null,updateWrapper);

        return R.success("修改成功！");
    }

    /***
     * @Description //TODO 起售
     * @param ids
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/status/1")
    public R<String> updataStatus1(Long[] ids){
        //将ids数组转换为list集合
        List<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,1);
        setmealService.update(null,updateWrapper);

        return R.success("修改成功！");
    }


    /**
     * 前端的查找套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    // http://localhost:8080/setmeal/list?categoryId=1413342269393674242&status=1,封装成一个setmeal
    public R<List<Setmeal>> list(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);

        return R.success(list);
    }






}
