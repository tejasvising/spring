package com.newpackage.neetcounsel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


import java.util.*;

@SpringBootApplication
@EnableMongoRepositories

public class NeetcounselApplication {
	private final static Logger log = LoggerFactory.getLogger(NeetcounselApplication.class);
	public List<String> res1;
	
	public static void main(String[] args) {
		SpringApplication.run(NeetcounselApplication.class, args);
	}
	/*
	 * @Bean CommandLineRunner demo(NeetRepository neetRepository) { return args ->
	 * {
	 * 
	 * 
	 * 
	 * 
	 * 
	 * log.info("Lookup each entry by InstituteCode..."); List<Neet>
	 * res=neetRepository.findByAIR(27); res1=neetRepository.findAllInstituteCode();
	 * System.out.println("res1.size():"+res1.size()); for(int
	 * i=0;i<res1.size();i++) {
	 * 
	 * log.info("Institue: {}",res1.get(i)); } for(int i=0;i<res.size();i++) { Neet
	 * entry=res.get(i); log.info("CollegeName: {}", entry.getInstitute()); }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * }; }
	 */
}
