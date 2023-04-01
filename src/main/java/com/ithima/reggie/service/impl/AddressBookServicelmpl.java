package com.ithima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithima.reggie.entity.AddressBook;
import com.ithima.reggie.mapper.AddressBookMapper;
import com.ithima.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServicelmpl extends ServiceImpl<AddressBookMapper, AddressBook>implements AddressBookService {
}
