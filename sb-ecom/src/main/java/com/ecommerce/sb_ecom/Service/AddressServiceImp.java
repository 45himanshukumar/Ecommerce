package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Exception.ResourseNotFoundException;
import com.ecommerce.sb_ecom.Model.Address;
import com.ecommerce.sb_ecom.Model.User;
import com.ecommerce.sb_ecom.Payload.AddressDTO;
import com.ecommerce.sb_ecom.Repositry.AddressRepository;
import com.ecommerce.sb_ecom.Repositry.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressServiceImp implements  AddressService{

     @Autowired
    ModelMapper modelMapper;
     @Autowired
    AddressRepository addressRepository;
     @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address=modelMapper.map(addressDTO,Address.class);
        List<Address> addressList=user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress=addressRepository.save(address);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddress() {
      List<Address> addresses=  addressRepository.findAll();
     List<AddressDTO> addressDTOS= addresses.stream()
              .map(address->modelMapper.map(address,AddressDTO.class))
              .collect(Collectors.toList());
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddress(Long addressId) {
         Address address= addressRepository.findById(addressId)
                 .orElseThrow(()-> new ResourseNotFoundException("Address","AddressId",addressId));
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddress(User user) {
        List<Address> addresses= user.getAddresses();
      List<AddressDTO> addressDTOS=  addresses.stream()
                .map(address -> modelMapper.map(address,AddressDTO.class))
                .collect(Collectors.toList());
        return addressDTOS ;
    }

    @Override
    public AddressDTO UpdateAddressById(Long addressId, AddressDTO addressDTO) {
        Address addressFromDB= addressRepository.findById(addressId)
                .orElseThrow(()->new ResourseNotFoundException("Address","addressId",addressId));

        addressFromDB.setCity(addressDTO.getCity());
        addressFromDB.setStreet(addressDTO.getStreet());
        addressFromDB.setBuildingName(addressDTO.getBuildingName());
        addressFromDB.setPincode(addressDTO.getPincode());
        addressFromDB.setState(addressDTO.getState());
        addressFromDB.setCountry(addressDTO.getCountry());

        Address updatedAddress= addressRepository.save(addressFromDB);
        User user=addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDB= addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourseNotFoundException("Address","addressId",addressId));

        User user=addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);
       addressRepository.delete(addressFromDB);
        return "Address Deleted successfully with addressid "  +  addressId;
    }


}
