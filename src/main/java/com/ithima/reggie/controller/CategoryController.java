package com.ithima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithima.reggie.common.R;
import com.ithima.reggie.entity.Category;
import com.ithima.reggie.entity.Dish;
import com.ithima.reggie.entity.Employee;
import com.ithima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐或者菜品名字
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
       //构造分页构造器
        Page pageinfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //添加排序
        queryWrapper.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pageinfo,queryWrapper);

        return R.success(pageinfo);
    }

    /**
     * 删除指定的菜系或者套餐，但是需要逻辑判断
     * 也就如果这个菜系或者套餐被绑定就不可以进行删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String>delete(Long ids){
        /*
        具体实现步骤：
       1）创建自定义业务异常类（CustomException）
       2）在CategoryService中扩展remove方法
       3）在CategoryServiceImpl中实现remove方法
       4）在GlobalExceptionHandler中处理自定义异常
         */
        categoryService.remove(ids);

        return R.success("删除成功");
    }

    /**
     * 根据id进行修改
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateFill(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("更新成功");
    }

    /**
     * 根据条件分页查询数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        //添加排序
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);

        return R.success(list);
    }


}
