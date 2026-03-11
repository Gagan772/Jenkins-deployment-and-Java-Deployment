package com.devops.app.graphql;

import com.devops.app.dto.OrderDTO;
import com.devops.app.dto.ProductDTO;
import com.devops.app.service.OrderService;
import com.devops.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GraphQLController {

    private final ProductService productService;
    private final OrderService orderService;

    @QueryMapping
    public List<ProductDTO> getProducts() {
        return productService.getProducts();
    }

    @QueryMapping
    public ProductDTO getProductById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    public ProductDTO createProduct(@Argument String name, 
                                    @Argument String description, 
                                    @Argument Double price, 
                                    @Argument Integer stock) {
        ProductDTO productDTO = new ProductDTO(null, name, description, price, stock);
        return productService.createProduct(productDTO);
    }

    @QueryMapping
    public List<OrderDTO> getOrders() {
        return orderService.getOrders();
    }
}
