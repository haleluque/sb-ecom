package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.AddressDTO;
import com.haleluque.ecommerce.exceptions.ResourceNotFoundException;
import com.haleluque.ecommerce.model.Address;
import com.haleluque.ecommerce.model.Category;
import com.haleluque.ecommerce.model.User;
import com.haleluque.ecommerce.repositories.AddressRepository;
import com.haleluque.ecommerce.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses
                .stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getById(Long addressId) {
        Address address = findAddressById(addressId);
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddressesByUser() {
        User user = authUtil.loggedInUser();
        return user.getAddresses()
                .stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addresses = user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = findAddressById(addressId);
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPincode(addressDTO.getPincode());
        address = addressRepository.save(address);
        return modelMapper.map(address, AddressDTO.class);
    }

    @Transactional
    @Override
    public String deleteAddress(Long addressId) {
        Address address = findAddressById(addressId);
        // Remove the address from all associated users
        address.getUsers()
                .forEach(user -> user.getAddresses().remove(address));
        // Clear the users set in the address
        address.getUsers().clear();
        addressRepository.save(address);
        addressRepository.delete(address);
        return "Address with id: " + addressId + " deleted successfully !!";
    }

    private Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Address.class.getSimpleName(), "addressId", addressId));
    }

}
