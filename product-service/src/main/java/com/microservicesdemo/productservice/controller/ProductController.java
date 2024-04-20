package com.microservicesdemo.productservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservicesdemo.productservice.dto.ProductRequest;
import com.microservicesdemo.productservice.dto.ProductResponse;
import com.microservicesdemo.productservice.service.ProductService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {

	private final ProductService productService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createProduct(@RequestBody ProductRequest productRequest)
	{
		productService.createProduct(productRequest);
	}
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ProductResponse> getAllProducts()
	{
		return productService.getAllProducts();
	}
}
