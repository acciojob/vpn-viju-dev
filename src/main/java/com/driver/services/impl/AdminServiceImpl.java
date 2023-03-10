package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();
        List<ServiceProvider> providerList = admin.getServiceProviders();
        //created new provider
        ServiceProvider provider = new ServiceProvider();
        //set admin to provider
        provider.setAdmin(admin);
        provider.setName(providerName);
        //added provider in list
        providerList.add(provider);
//        providerList.add(serviceProviderRepository1.findByName(providerName));
        //added providerlist in admin
        admin.setServiceProviders(providerList);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();

        Country country = new Country();
       // country.setCountryName(CountryName.valueOf(countryName)); // get enumvalue of countryname coz we didn't use @enumrated

        //valid loop but not require
//        for (CountryName c:CountryName.values()){
//           if (c.toString().equals(countryName)){
//
//               country.setCountryName(c); //.name() returns/converts enum to string //CountryName.valueOf(c.name()) and that whole function convert string to enum for database values
//               country.setCode(c.toCode());
//           }
//       }

//        CountryName countryName1 = CountryName.valueOf(countryName);
//        System.out.println(countryName1);
//        System.out.println(countryName1.toCode());

        List<Country> countryList = serviceProvider.getCountryList();
        //set country attrs
        countryName = countryName.toUpperCase(); //transfer to uppercase so that it will get match with enum even if its in small

        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.valueOf(countryName).toCode());
       // countryList.add(countryRepository1.findByCountryName(country.getCountryName()));//String.valueOf(countryName1)) // country.getCountryName().name() //again same enumrated thing

        //added country in countryList of serviceProvider
        countryList.add(country);
        //set provider in country
        country.setServiceProvider(serviceProvider);
        serviceProvider.setCountryList(countryList);
        //save provider
        serviceProviderRepository1.save(serviceProvider);
        return serviceProvider;
    }
}
