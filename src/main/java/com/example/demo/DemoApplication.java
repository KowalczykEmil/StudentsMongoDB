package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}



@Bean
CommandLineRunner runner(
		StudentRepository repository, MongoTemplate mongoTemplate){
	return args -> {
		Address address = new Address(
				"Poland",
				"Rawa Mazowiecka",
				"96-200"
		);
		String email = "Filip@kkowalczykadrian.pl";

		Student student = new Student(
				"Filip",
				"Kowalczyk",
				email,
				Gender.FEMALE,
				address,
				List.of("PL/SQL od podstaw"),
				BigDecimal.TEN,
				LocalDateTime.now()
		);

		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(email));

		List<Student> students = mongoTemplate.find(query, Student.class);

		if (students.size() > 1){
			throw new IllegalStateException(
					"found many students with email" + email);
		}

		if(students.isEmpty()){
			System.out.println("Inserting student " + student);
			repository.insert(student);
			repository.findAll();
			System.out.println("Gituwa");
		} else {
			System.out.println(student + " already exists");
		}

		};



	}
}
