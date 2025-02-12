package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);
    List<AddressDTO> getAllAddresses();
    AddressDTO getById(Long addressId);
    List<AddressDTO> getAllAddressesByUser();
    AddressDTO updateAddress(Long addressId, @Valid AddressDTO addressDTO);
    String deleteAddress(Long addressId);
}
