package com.ithima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ithima.reggie.common.BaseContext;
import com.ithima.reggie.common.R;
import com.ithima.reggie.entity.ShoppingCart;
import com.ithima.reggie.entity.User;
import com.ithima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.ibatis.annotations.One;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart={}", shoppingCart);
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();
        //设置当前用户id
        shoppingCart.setUserId(currentId);
        //获取当前菜品id
        Long dishId = shoppingCart.getDishId();
        //条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //判断添加的是菜品还是套餐
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne != null) {
            //如果已存在就在当前的数量上加1
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //如果不存在，则还需设置一下创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            //如果不存在，则添加到购物车，数量默认为1
            shoppingCartService.save(shoppingCart);
            //这里是为了统一结果，最后都返回cartServiceOne会比较方便
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<ShoppingCart>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);
    }


    /**
     * 删除购物车
     */
    @DeleteMapping("/clean")
    public R<String>clean(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("清空购物车成功");
    }

    /**
     * 减少购物车里面的东西
     */
    @PostMapping("/sub")
    public R<ShoppingCart> del (@RequestBody ShoppingCart shoppingCart){

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();


        if (shoppingCart.getDishId() != null) {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);


        //删除菜品
        if(shoppingCart.getDishId()!=null){

            if(cartServiceOne.getNumber()>1){
                Integer number = cartServiceOne.getNumber();
                cartServiceOne.setNumber(number-1);
                shoppingCartService.updateById(cartServiceOne);
            }else{
                LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                shoppingCartLambdaQueryWrapper1.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
                shoppingCartLambdaQueryWrapper1.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
                shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
            }

            //删除套餐
        }else if (shoppingCart.getSetmealId()!=null){

            if(cartServiceOne.getNumber()>1){
                Integer number = cartServiceOne.getNumber();
                cartServiceOne.setNumber(number-1);
                shoppingCartService.updateById(cartServiceOne);
            }else{
                LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                shoppingCartLambdaQueryWrapper1.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
                shoppingCartLambdaQueryWrapper1.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

                //cartServiceOne.setNumber(null);
                shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

            }

        }
        return R.success(cartServiceOne);
    }
}
