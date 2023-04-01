package com.ithima.reggie.dto;


import com.ithima.reggie.entity.Dish;
import com.ithima.reggie.entity.Setmeal;
import com.ithima.reggie.entity.SetmealDish;
import com.ithima.reggie.entity.User;
import lombok.Data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

    //注册
    public static void main(String[] args) throws IOException {
        HashMap<String, User> userHashMap = new HashMap<String, User>();
        User user = new User();
        //从键盘写入数据
        userHashMap.put("用户名",user);

        //增加新用户直接让键盘传入对于注册的用户名利用io在txt找

        //改的话，比如改状态，我们在实体类会给用户一个状态，
        User x = userHashMap.get("用户名");
       // x.set你需要改的值，在用io流重新写进去新数值

        //查，解还是用key找value，然后在io里面读


        //调用io流写入到txt
        FileWriter fileWriter = new FileWriter("Desfile.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);



    }
}
