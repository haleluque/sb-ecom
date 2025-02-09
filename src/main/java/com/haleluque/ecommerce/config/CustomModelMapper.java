package com.haleluque.ecommerce.config;
import com.haleluque.ecommerce.dto.CartItemDTO;
import com.haleluque.ecommerce.dto.ProductDTO;
import com.haleluque.ecommerce.model.CartItem;
import org.modelmapper.ModelMapper;
import org.modelmapper.Converter;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.collection.spi.PersistentBag;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;

public class CustomModelMapper {
    public static ModelMapper createModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Disable the default merging collection converter
        modelMapper.getConfiguration().setCollectionsMergeEnabled(false);

        // Add a custom converter to handle PersistentBag to List conversion
        modelMapper.createTypeMap(PersistentBag.class, List.class)
                .setConverter(new Converter<PersistentBag, List>() {
                    @Override
                    public List convert(MappingContext<PersistentBag, List> context) {
                        return (List) context.getSource().stream().collect(Collectors.toList());
                    }
                });
        return modelMapper;
    }
}
