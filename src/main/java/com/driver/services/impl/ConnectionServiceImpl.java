package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();
        //if service provider already there then excpetion will be thrown coz already connected
//        if(user.getServiceProviderList() != null){
//            throw  new RuntimeException("Already connected");
//        }
        List<ServiceProvider> providerList = user.getServiceProviderList();
        for (ServiceProvider provider:providerList){ // along with povider we are chcking is there any connection bet them or not
            if (provider.getConnectionList() != null){
                throw  new RuntimeException("Already connected");
            }
        }

        //if asking for same country do nothing coz for same country don't require connection
        if (Objects.equals(user.getCountry().getCountryName(),countryName)){
            return user;
        }

        //
        if (providerList == null){
            throw new RuntimeException("Unable to connect");
        }
        boolean connected = false;
        for (ServiceProvider provider:providerList){// use id function
            if(provider.getCountryList().contains(countryName)){
                connected = true;
                Connection connection = new Connection();
                connection.setUser(user);
                connection.setServiceProvider(provider);
//                connectionRepository2.save(connection); // child not require to save
//                provider.setConnectionList(provider.getConnectionList().add(connection));
                List<Connection> connectionList = provider.getConnectionList();
                connectionList.add(connection);
                provider.setConnectionList(connectionList);
                //user maskedIp //"updatedCountryCode.serviceProviderId.userId"

                //iterate on countrylist to get countrycode
                String code="";
                for (Country country: provider.getCountryList()){
                    if (Objects.equals(country.getCountryName(),countryName)){
                        code = country.getCode();
                    }
                }
                user.setMaskedIp(code+"."+provider.getId()+"."+user.getId());
                break; // break out of loop

            }
        }

        userRepository2.save(user); //saved user so other attributes get saved automatically and no need to save coz other are connected already
//        Connection connection = new Connection();
//         connection.setUser(userRepository2.findById(userId).get());
//         connection.se
        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if (user.getMaskedIp()!=null){
            throw new RuntimeException("Already disconnected");
        }
        user.setMaskedIp(null);
        //        user.getConnectionList().remove(user.getConnectionList().size()-1); //removing last connection
        userRepository2.save(user);
//        List<ServiceProvider> providerList = user.getServiceProviderList();
//        for (ServiceProvider provider:providerList){
//            if (provider.getConnectionList()!= null)
//        }

        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        //Establish a connection between sender and receiver users
        //To communicate to the receiver, sender should be in the current country of the receiver.
        //If the receiver is connected to a vpn, his current country is the one he is connected to.
        //If the receiver is not connected to vpn, his current country is his original country.
        //The sender is initially not connected to any vpn. If the sender's original country does not match receiver's current country, we need to connect the sender to a suitable vpn. If there are multiple options, connect using the service provider having smallest id
        //If the sender's original country matches receiver's current country, we do not need to do anything as they can communicate. Return the sender as it is.
        //If communication can not be established due to any reason, throw "Cannot establish communication" exception

        User sender = userRepository2.findById(senderId).get();
        User reciever = userRepository2.findById(receiverId).get();

        return sender;

    }
}
