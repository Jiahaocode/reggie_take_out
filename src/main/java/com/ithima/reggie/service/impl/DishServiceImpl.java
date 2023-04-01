package com.ithima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithima.reggie.dto.DishDto;
import com.ithima.reggie.entity.Dish;
import com.ithima.reggie.entity.DishFlavor;
import com.ithima.reggie.mapper.DishMapper;
import com.ithima.reggie.service.DishFlavorService;
import com.ithima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish>implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表格Dish
        this.save(dishDto);

        Long id = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
           item.setDishId(id);
           return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品味道表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id来查询菜品信息和对应的口味信息,回显这个信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdwithFlavor(Long id) {
        //查询基本信息,从dish表查询
        Dish dish = this.getById(id);

        //进行一份拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息,从dish_flavor表来查询
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 更新操作
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清除当前菜品对应口味的数据————dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        //添加当前的提前过来的新口味数据到————dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors=flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
