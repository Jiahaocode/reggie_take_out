package com.ithima.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithima.reggie.entity.DishFlavor;
import com.ithima.reggie.mapper.DishFlavorMapper;
import com.ithima.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>  implements DishFlavorService {
}
