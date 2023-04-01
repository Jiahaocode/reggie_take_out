package com.ithima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithima.reggie.common.R;
import com.ithima.reggie.entity.Employee;
import com.ithima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        /*
        处理逻辑如下：
        1、将页面提交的密码password进行md5加密处理
        2、根据页面提交的用户名username查询数据库
        3、如果没有查询到则返回登录失败结果
        4、密码比对，如果不一致则返回登录失败结果
        5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        6、登录成功，将员工id存入Session并返回登录成功结果
         */
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());

        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //获得当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }


    /**
     * 分页查询展示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page>page (int page, int pageSize, String name){
        //构造分页构造器
        Page pageinfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤的条件,代替了if（name!=null)
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageinfo,queryWrapper);

        return R.success(pageinfo);
    }



    /*
    在员工管理列表页面，可以对某个员工账号进行启用或者禁用操作。
    账号禁用的员工不能登录系统，启用后的员工可以正常登录。
    需要注意，只有管理员（admin用户）可以对其他普通用户进行启用、禁用操作，所以普通用户登录系统后启用、禁用按钮不显示。
     */

    /**
     * 禁用员工的信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee){

        long empId = (long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }
    /**
     * 根据员工id修改信息
     * @param id
     * @return
     */
    /*
       针对这种URL http://localhost:8080/employee/1566860604135813121，结尾是一个参数
       在前端实现回显，最后利用保存来修改这个数据，当让我们把方法实现了复用也就是上面update方法
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        Employee emp = employeeService.getById(id);
        if(emp!=null){
            return R.success(emp);
        }
        return R.error("没有查到这个人");
    }

}