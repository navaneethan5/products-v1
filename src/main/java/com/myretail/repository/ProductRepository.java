package com.myretail.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.myretail.domain.PricingDTO;

public interface ProductRepository extends MongoRepository<PricingDTO, String> {

	PricingDTO findByProductId(String productId);

}
