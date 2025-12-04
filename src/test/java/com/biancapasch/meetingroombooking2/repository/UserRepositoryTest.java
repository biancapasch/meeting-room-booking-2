package com.biancapasch.meetingroombooking2.repository;

import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity newUser(String name, String email) {
        UserEntity u = new UserEntity();
        u.setName(name);
        u.setEmail(email);
        return u;
    }

    @Test
    void shouldSaveUserSuccessfully() {
        UserEntity toSave = newUser("Bianca", "bianca@email.com");

        UserEntity saved = userRepository.save(toSave);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Bianca");
        assertThat(saved.getEmail()).isEqualTo("bianca@email.com");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindUserByIdSuccessfully() {
        UserEntity saved = userRepository.save(newUser("Bia", "bia@email.com"));

        Optional<UserEntity> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getEmail()).isEqualTo("bia@email.com");
    }

    @Test
    void findByIdShouldReturnEmptyWhenIdDoesNotExist() {
        Optional<UserEntity> found = userRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        userRepository.save(newUser("Lucas", "lucas@me.com"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(newUser("Outro Lucas", "lucas@me.com"));
        });
    }

        @Test
        void shouldUpdateTimestampsOnUpdate() {
            UserEntity saved = userRepository.save(newUser("Olivo", "olivo@me.com"));

            OffsetDateTime createdAt = saved.getCreatedAt();
            OffsetDateTime updatedAt1 = saved.getUpdatedAt();

            saved.setName("Olivo Jr");
            UserEntity updated = userRepository.saveAndFlush(saved);

            assertThat(updated.getCreatedAt()).isEqualTo(createdAt);
            assertThat(updated.getUpdatedAt()).isAfter(updatedAt1);
            assertThat(updated.getName()).isEqualTo("Olivo Jr");
        }

    }




