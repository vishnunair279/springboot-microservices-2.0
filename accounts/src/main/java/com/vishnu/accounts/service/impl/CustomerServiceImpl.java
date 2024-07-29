package com.vishnu.accounts.service.impl;

import com.vishnu.accounts.dto.AccountsDto;
import com.vishnu.accounts.dto.CardsDto;
import com.vishnu.accounts.dto.CustomerDetailsDto;
import com.vishnu.accounts.dto.LoansDto;
import com.vishnu.accounts.entity.Accounts;
import com.vishnu.accounts.entity.Customer;
import com.vishnu.accounts.exception.ResourceNotFoundException;
import com.vishnu.accounts.mapper.AccountsMapper;
import com.vishnu.accounts.mapper.CustomerMapper;
import com.vishnu.accounts.repository.AccountsRepository;
import com.vishnu.accounts.repository.CustomerRepository;
import com.vishnu.accounts.service.ICustomersService;
import com.vishnu.accounts.service.client.CardsFeignClient;
import com.vishnu.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomersService {

    private CustomerRepository customerRepository;
    private AccountsRepository accountsRepository;
    private LoansFeignClient loansFeignClient;
    private CardsFeignClient cardsFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(mobileNumber);
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(mobileNumber);
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;
    }



}
