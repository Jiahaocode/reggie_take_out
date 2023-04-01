package com.ithima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithima.reggie.common.R;
import com.ithima.reggie.dto.DishDto;
import com.ithima.reggie.entity.Category;
import com.ithima.reggie.entity.Dish;
import com.ithima.reggie.entity.DishFlavor;
import com.ithima.reggie.entity.Employee;
import com.ithima.reggie.mapper.DishMapper;
import com.ithima.reggie.service.CategoryService;
import com.ithima.reggie.service.DishFlavorService;
import com.ithima.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String>save(@RequestBody DishDto dishDto){

        //但是由于我们的操作两张表格所以我们不可以直接调用DishService.sava这么简单，我们需要扩展一个新方法
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    // http://localhost:8080/dish/page?page=1&pageSize=10
    // http://localhost:8080/dish/page?page=1&pageSize=10&name=123
    @GetMapping("/page")
    public R<Page>page(int page ,int pageSize,String name){
        //构造分页构造器对象
        /*
        这里解释一下为什么要再Page<DishDto> dishDtopage = new Page<>();
        因为page的底层其实就是查询Sql文件我们有dish这个表所以可以直接查询，但是我们连dishDto这个表都没有所以不可以直接代替
        必须使用这个进行二次拷贝
        */
        Page<Dish> pageinfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtopage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        //queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageinfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageinfo,dishDtopage,"records");
        List<Dish> records = pageinfo.getRecords();
        List<DishDto> list=records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类的id

            //根据Id查询分类的对象
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;

        }).collect(Collectors.toList());

        dishDtopage.setRecords(list);

        return R.success(dishDtopage);
    }


    /**
     * 根据id来查询菜品信息和口味其实就是回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        //不可以直接用dishService来查数据库，因为在这个表中没有我们希望的Flavor，最终需要查俩张表
        //在dishService扩展一个方法来解决我们这个问题
        DishDto dishDto = dishService.getByIdwithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 更新操作
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("添加成功");
    }

    /**
     * 停售服务
     * @param ids,用集合是因为涉及到了批量问题
     * @return
     */
    @PostMapping("/status/0")
    public R<String>updataStatus0(Long[] ids) {

        //将ids数组转换为list集合
        List<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,ids).set(Dish::getStatus,0);
        dishService.update(null,updateWrapper);

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

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,idList).set(Dish::getStatus,1);
        dishService.update(null,updateWrapper);

        return R.success("修改成功！");
    }

    @GetMapping("/list")
    public R<List<DishDto>> get(Dish dish) {
        //条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据传进来的categoryId查询
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //只查询状态为1的菜品（在售菜品）
        queryWrapper.eq(Dish::getStatus, 1);
        //简单排下序，其实也没啥太大作用
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //获取查询到的结果作为返回值
        List<Dish> list = dishService.list(queryWrapper);

        //item就是list中的每一条数据，相当于遍历了
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            //创建一个dishDto对象
            DishDto dishDto = new DishDto();
            //将item的属性全都copy到dishDto里
            BeanUtils.copyProperties(item, dishDto);
            //由于dish表中没有categoryName属性，只存了categoryId
            Long categoryId = item.getCategoryId();
            //所以我们要根据categoryId查询对应的category
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //然后取出categoryName，赋值给dishDto
                dishDto.setCategoryName(category.getName());
            }
            //然后获取一下菜品id，根据菜品id去dishFlavor表中查询对应的口味，并赋值给dishDto
            Long itemId = item.getId();
            //条件构造器
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            //条件就是菜品id
            lambdaQueryWrapper.eq(itemId != null, DishFlavor::getDishId, itemId);
            //根据菜品id，查询到菜品口味
            List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
            //赋给dishDto的对应属性
            dishDto.setFlavors(flavors);
            //并将dishDto作为结果返回
            return dishDto;
            //将所有返回结果收集起来，封装成List
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}
