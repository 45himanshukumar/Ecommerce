package com.ecommerce.sb_ecom.Controller;

import com.ecommerce.sb_ecom.Model.User;
import com.ecommerce.sb_ecom.Payload.AddressDTO;
import com.ecommerce.sb_ecom.Service.AddressService;
import com.ecommerce.sb_ecom.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    AddressService addressService;
    @Autowired
    AuthUtil authUtil;
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO>CreateAddress(@Valid @RequestBody AddressDTO addressDTO){
       User user= authUtil.loggedInUser();
     AddressDTO savedAddressDTO= addressService.createAddress(addressDTO,user);
        return  new ResponseEntity<AddressDTO>(savedAddressDTO, HttpStatus.CREATED);
    }
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>>GetAllAddress(){
          List<AddressDTO> addressList= addressService.getAllAddress();
         return new ResponseEntity<List<AddressDTO>>(addressList,HttpStatus.OK);
    }
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO>GetAddressById(@PathVariable Long addressId){
        AddressDTO addressDTO= addressService.getAddress(addressId);
        return new ResponseEntity<AddressDTO>(addressDTO,HttpStatus.OK);
    }
    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>>GetUserAddress(){
        User user=authUtil.loggedInUser();
        List<AddressDTO> addressList= addressService.getUserAddress(user);
        return new ResponseEntity<List<AddressDTO>>(addressList,HttpStatus.OK);
    }
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO>UpdateAddressById(@Valid @RequestBody AddressDTO addressDTO
            ,@PathVariable Long addressId){
        AddressDTO updatedAddress= addressService.UpdateAddressById(addressId,addressDTO);
        return new ResponseEntity<AddressDTO>(updatedAddress,HttpStatus.OK);
    }
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String>DeleteAddress(
            @PathVariable Long addressId){
        String deletedAddress= addressService.deleteAddress(addressId);
        return new ResponseEntity<String>(deletedAddress,HttpStatus.OK);
    }


}
