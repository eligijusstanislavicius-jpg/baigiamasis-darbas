package com.feelsent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync        // leidžia @Async metodams (el. laiškams) veikti fone
@EnableScheduling  // leidžia @Scheduled užduotims (re-engagement) veikti automatiškai
@EnableCaching     // įjungia @Cacheable/@CacheEvict anotacijas – kešas saugomas atmintyje
                   // Pastaba: daug instancijų (horizontal scaling) → keisti į Redis kešą
public class FeelSentApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeelSentApplication.class, args);
	}

}