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
        System.out.println(user.getOriginalCountry().getCountryName());

//        List<ServiceProvider> providerList = user.getServiceProviderList();

//        for (ServiceProvider provider:providerList){ // along with povider we are chcking is there any connection bet them or not
//            if (provider.getConnectionList() != null){
//                throw  new RuntimeException("Already connected");
//            }
//        }
        if (user.getMaskedIp()!=null){
            throw  new RuntimeException("Already connected");
        }
        else if (user.getOriginalCountry().getCountryName().equals(countryName)){
            return user;//do nothing coz user trying to conect same country
        }
//        else if(user.getServiceProviderList() == null){ //!user.getServiceProviderList().contains(countryName)// not sure whether can check or not
//            throw new RuntimeException("Unable to connect");
//        }

        //if providers exist
//        for (ServiceProvider provider:providerList){
//            for (Country country:provider.getCountryList()){
//                if (country.getCountryName().equals(countryName)){
//                    throw new RuntimeException("Unable to connect");
//                }
//            }
//        }

        //if asking for same country do nothing coz for same country don't require connection
//        if (Objects.equals(user.getOriginalCountry().getCountryName(),countryName)){
//            return user;
//        }


//        if (providerList == null){
//            throw new RuntimeException("Unable to connect");
//        }

        //check from user service providers as well // just change reposlist to user.getproviderlist
        int id = Integer.MAX_VALUE;
        List<ServiceProvider> providerList = serviceProviderRepository2.findAll();

        for (ServiceProvider provider:providerList){
            if (provider.getId() < id){
//                id = provider.getId();
                for (Country country:provider.getCountryList()){
                    System.out.println("checking");
                    System.out.println(country.getCountryName()+"  "+countryName);
                    if (country.getCountryName().toString().equalsIgnoreCase(countryName)){ //sometimes toString require to compare strings
                        System.out.println("yes gotcha");
                        id=provider.getId();
                    }
                }
            }
        }
        if (id == Integer.MAX_VALUE){
            throw new RuntimeException("Unable to connect"); //provider is not there present or dont have given country
        }
        ServiceProvider serviceProvider = serviceProviderRepository2.findById(id).get();

        user.setMaskedIp(CountryName.valueOf(countryName).toCode()+"."+serviceProvider.getId()+"."+userId); //"updatedCountryCode.serviceProviderId.userId"
        user.getServiceProviderList().add(serviceProvider); // added new provider

        Connection connection = new Connection();
        connection.setServiceProvider(serviceProvider);
        connection.setUser(user);

        connectionRepository2.save(connection); //make connection // may not required to save child
        //serviceProvider save?

        userRepository2.save(user);//just changed in maked ip other attr are already conected
        return user;
//        boolean connected = false;
//        for (ServiceProvider provider:providerList){// use id function
//            if(provider.getCountryList().contains(countryName)){
//                connected = true;
//                Connection connection = new Connection();
//                connection.setUser(user);
//                connection.setServiceProvider(provider);
////                connectionRepository2.save(connection); // child not require to save
////                provider.setConnectionList(provider.getConnectionList().add(connection));
//                List<Connection> connectionList = provider.getConnectionList();
//                connectionList.add(connection);
//                provider.setConnectionList(connectionList);
//                //user maskedIp //"updatedCountryCode.serviceProviderId.userId"
//
//                //iterate on countrylist to get countrycode
//                String code="";
//                for (Country country: provider.getCountryList()){
//                    if (Objects.equals(country.getCountryName(),countryName)){
//                        code = country.getCode();
//                    }
//                }
//                user.setMaskedIp(code+"."+provider.getId()+"."+user.getId());
//                break; // break out of loop
//
//            }
//        }
//
//        userRepository2.save(user); //saved user so other attributes get saved automatically and no need to save coz other are connected already
////        Connection connection = new Connection();
////         connection.setUser(userRepository2.findById(userId).get());
////         connection.se
//        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if (user.getMaskedIp()==null){
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
        String recCountry = "";
        String code = "";
        if (reciever.getMaskedIp() != null){
            String[] arr = reciever.getMaskedIp().split(".");
             code = arr[0];
        }
//        else {
//            code = reciever.getOriginalCountry().getCountryName().toCode();
//        }
//        recCountry = code.toString();

        String senCountry = sender.getOriginalCountry().getCountryName().toCode();
        System.out.println(code);
        System.out.println(recCountry);
        System.out.println(senCountry);
        return sender;

    }
}
