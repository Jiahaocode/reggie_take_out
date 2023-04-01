package com.ithima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithima.reggie.common.CustomException;
import com.ithima.reggie.common.R;
import com.ithima.reggie.dto.SetmealDto;
import com.ithima.reggie.entity.Setmeal;
import com.ithima.reggie.entity.SetmealDish;
import com.ithima.reggie.mapper.SetmealMapper;
import com.ithima.reggie.service.SetmealDishService;
import com.ithima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;



    /**
     * 添加菜品
     * @param setmealDto
     */

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //操作setmeal
        this.save(setmealDto);

        //操作setmeal_dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //给他们保存id
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //select count(*)from setmeal where id in(1,2,3) and status =1

        //查询套餐状态是否可以删除、
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(setmealLambdaQueryWrapper);
        //如果不能删除，抛出一个异常
        if(count>0){
            throw new CustomException("套餐正在销售中，不能删除");
        }

        //如果可以删除,先删除套餐中的数据——setmeal
        this.removeByIds(ids);

        //如果可以删除，先删除套餐中的数据——setmeal_dish
        //delect from setmeal_dish where setmeal id in(1,2,3)
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        //删除关系表的数据
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}
