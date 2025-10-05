package by.astakhau.trainee.passengerservice.integration;

import by.astakhau.trainee.passengerservice.entities.Passenger;
import by.astakhau.trainee.passengerservice.repositories.PassengerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PassengerServiceRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private PassengerRepository passengerRepository;

    @Test
    @Transactional
    @Rollback
    void saveAndFindByNameAndPhoneNumber() {
        Passenger p = new Passenger();
        p.setName("Ivan Repo");
        p.setPhoneNumber("+375447006485");
        p.setEmail("ivan.repo@example.com");
        p.setIsDeleted(false);
        p.setDeletedAt(null);
        Passenger saved = passengerRepository.saveAndFlush(p);

        Optional<Passenger> found = passengerRepository.findByNameAndPhoneNumber("Ivan Repo", "+375447006485");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getEmail()).isEqualTo("ivan.repo@example.com");
    }

    @Test
    @Transactional
    @Rollback
    void findAll_withPaging_returnsPage() {
        List<Passenger> list = IntStream.range(0, 5)
                .mapToObj(i -> {
                    Passenger p = new Passenger();
                    p.setName("Passenger " + i);
                    p.setPhoneNumber("+37544700648" + i);
                    p.setEmail("p" + i + "@example.com");
                    p.setIsDeleted(false);
                    p.setDeletedAt(null);
                    return p;
                }).collect(Collectors.toList());
        passengerRepository.saveAll(list);
        passengerRepository.flush();

        Pageable page0 = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Passenger> page = passengerRepository.findAll(page0);

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(5);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    @Transactional
    @Rollback
    void softDeleteByNameAndEmail_marksEntityAsDeleted() throws Exception {
        Passenger p = new Passenger();
        p.setName("ToDelete");
        p.setPhoneNumber("+375447006485");
        p.setEmail("del@example.com");
        p.setIsDeleted(false);
        p.setDeletedAt(null);
        Passenger saved = passengerRepository.saveAndFlush(p);

        Optional<Passenger> before = passengerRepository.findById(saved.getId());
        assertThat(before).isPresent();

        passengerRepository.softDeleteByNameAndEmail("ToDelete", "del@example.com");

        Optional<Passenger> after = passengerRepository.findById(saved.getId());
        assertThat(after).isPresent();

        assertThat(after.get().getIsDeleted()).isTrue();
    }
}
