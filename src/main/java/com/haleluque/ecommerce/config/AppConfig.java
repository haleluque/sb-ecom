package com.haleluque.ecommerce.config;

import jakarta.persistence.Converter;
import org.hibernate.collection.spi.PersistentBag;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

//    @Bean
//    public ModelMapper modelMapper(){
//        return new ModelMapper();
//    }

    @Bean
    public ModelMapper modelMapper() {
        return CustomModelMapper.createModelMapper();
    }
}
