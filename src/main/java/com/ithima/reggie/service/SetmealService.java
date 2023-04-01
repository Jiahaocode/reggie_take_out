package com.ithima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithima.reggie.dto.SetmealDto;
import com.ithima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //Dto需要插入数据
    public void  saveWithDish(SetmealDto setmealDto);

    //删除套餐
    public  void removeWithDish(List<Long> ids);
}
