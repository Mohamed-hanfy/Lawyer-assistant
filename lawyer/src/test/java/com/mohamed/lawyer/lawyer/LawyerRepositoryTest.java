package com.mohamed.lawyer.lawyer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class LawyerRepositoryTest {
    @Autowired
    private LawyerRepository lawyerRepository;

    @Test
    public void LawyerRepository_findByEmail_ReturnTheObject(){
        Lawyer lawyer = Lawyer.builder()
                .email("mohamed.hanafy.mostafa@gmail.com")
                .firstName("Mohamed")
                .lastName("Hanafy")
                .password("dd")
                .build();

        lawyerRepository.save(lawyer);

        Optional<Lawyer> lawyerByEmail = lawyerRepository.findByEmail(lawyer.getEmail());

        Assertions.assertThat(lawyerByEmail.get().getEmail()).isEqualTo(lawyer.getEmail());

    }
}
