package com.ithima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithima.reggie.dto.DishDto;
import com.ithima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表格：dish-dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id来查询菜品信息和对应的口味信息
    public DishDto getByIdwithFlavor(Long id);

    //更新菜单操作
    public void updateWithFlavor(DishDto dishDto);
}
