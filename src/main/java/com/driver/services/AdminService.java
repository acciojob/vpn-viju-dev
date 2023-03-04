package com.driver.services;

import com.driver.model.Admin;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;

public interface AdminService { // why this thing like interface and implemantation in service

    public Admin register(String username, String password) ;


    public Admin addServiceProvider(int adminId, String providerName) ;

    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception;
}