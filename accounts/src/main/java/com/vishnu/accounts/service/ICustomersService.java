package com.vishnu.accounts.service;

import com.vishnu.accounts.dto.CustomerDetailsDto;


public interface ICustomersService {

    /**
     *
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    CustomerDetailsDto fetchCustomerDetails(String mobileNumber);

}
