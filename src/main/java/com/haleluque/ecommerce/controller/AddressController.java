package com.haleluque.ecommerce.controller;

import com.haleluque.ecommerce.dto.AddressDTO;
import com.haleluque.ecommerce.service.AddressService;
import com.haleluque.ecommerce.utils.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    AuthUtil authUtil;

    @Autowired
    AddressService addressService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOS = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        AddressDTO addressDTO = addressService.getById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddressesByUser() {
        List<AddressDTO> addressDTOS = addressService.getAllAddressesByUser();
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO,
                                                @PathVariable Long addressId){
        AddressDTO updateAddress = addressService.updateAddress(addressId, addressDTO);
        return new ResponseEntity<>(updateAddress, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}