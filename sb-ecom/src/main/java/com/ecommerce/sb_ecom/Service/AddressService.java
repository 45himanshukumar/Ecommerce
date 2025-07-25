package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Model.User;
import com.ecommerce.sb_ecom.Payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddress();

    AddressDTO getAddress(Long addressId);


    List<AddressDTO> getUserAddress(User user);


    AddressDTO UpdateAddressById(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
