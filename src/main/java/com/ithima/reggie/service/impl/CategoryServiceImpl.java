package com.ithima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithima.reggie.common.CustomException;
import com.ithima.reggie.entity.Category;
import com.ithima.reggie.entity.Dish;
import com.ithima.reggie.entity.Setmeal;
import com.ithima.reggie.mapper.CategoryMapper;
import com.ithima.reggie.service.CategoryService;
import com.ithima.reggie.service.DishService;
import com.ithima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id逻辑删除Dish或者Setmeal中的东西
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前是类是否关联了菜品，如果已经关联，抛出一个业务异常
        //根据Id进行查询
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        int count1 = dishService.count(dishLambdaQueryWrapper);

        if(count1>0){
            //抛出一个业务异常
            throw  new CustomException("当前分类关联了菜品不能删除");
        }


        //查询当前是类是否关联了套餐，如果已经关联，抛出一个业务异常

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2>0){
            //抛出一个业务异常
            throw  new CustomException("当前分类关联了套餐不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}
