package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        //service provider not set because user havent useed any ...its his main country only

        // make ip of user using countrycode and userid
        Country country = countryRepository3.findByCountryName(countryName);
        String code = country.getCode();
        user.setOriginalIp(code+"."+user.getId()); // countrycode.userid
        user.setCountry(countryRepository3.findByCountryName(countryName));
        //no need to set this attributes default is null and false
        user.setMaskedIp(null);
        user.setConnected(false);
        userRepository3.save(user);
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        List<ServiceProvider> providerList = user.getServiceProviderList();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        //setting user to providers too
        List<User> users = serviceProvider.getUsers();
        users.add(user);
        serviceProvider.setUsers(users);

        providerList.add(serviceProvider);
        user.setServiceProviderList(providerList);
        userRepository3.save(user); // no need to save child
        return user;
    }
}
