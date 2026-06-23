package com.riwi.dbmanager.config;

import com.riwi.dbmanager.model.*;
import com.riwi.dbmanager.model.enums.*;
import com.riwi.dbmanager.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds the database with initial data on application startup.
 * <p>
 * The {@link CommandLineRunner} bean is executed automatically by Spring Boot
 * right after the context is ready, on any environment, without manual triggers.
 * Every seed step is idempotent (guarded by {@code count() == 0} or
 * {@code existsBy...}), so restarting the app never duplicates data.
 * <p>
 * Insertion order respects entity dependencies:
 * users (responsible/candidate) &rarr; vacancies &rarr; applications &rarr; interviews.
 */
@Slf4j
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository, PasswordEncoder passwordEncoder,
                               VacanciesRepository vacanciesRepository,
                               ApplicationRepository applicationRepository,
                               InterviewRepository interviewRepository) {
        return args -> seed(userRepository, passwordEncoder, vacanciesRepository,
                applicationRepository, interviewRepository);
    }

    @Transactional
    protected void seed(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        VacanciesRepository vacanciesRepository,
                        ApplicationRepository applicationRepository,
                        InterviewRepository interviewRepository) {
        log.info("Starting database seeding...");

        User admin = createUserIfNotExists(userRepository, passwordEncoder,
                "Admin", "User", "admin@talentboard.com", "Admin123*", Role.ADMIN);

        User recruiter = createUserIfNotExists(userRepository, passwordEncoder,
                "Recruiter", "User", "recruiter@talentboard.com", "Recruiter123*", Role.RECRUITER);

        User candidate = createUserIfNotExists(userRepository, passwordEncoder,
                "Candidate", "User", "candidate@talentboard.com", "Candidate123*", Role.CANDIDATE);

        seedVacancies(vacanciesRepository, recruiter);
        seedApplications(applicationRepository, vacanciesRepository, candidate);
        seedInterviews(interviewRepository, applicationRepository);

        log.info("Database seeding finished.");
    }

    private User createUserIfNotExists(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                       String name, String lastName, String email,
                                       String password, Role role) {
        if (userRepository.existsByEmail(email)) {
            log.debug("User '{}' already exists, skipping.", email);
            return userRepository.findByEmail(email).orElseThrow();
        }

        User user = User.builder()
                .name(name)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        User saved = userRepository.save(user);
        log.info("Seeded {} user: {}", role, email);
        return saved;
    }

    private void seedVacancies(VacanciesRepository vacanciesRepository, User recruiter) {
        if (vacanciesRepository.count() > 0) {
            log.debug("Vacancies already present, skipping.");
            return;
        }

        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .title("Senior Java Developer")
                        .description("We are looking for an experienced Java developer to join our team. Must have strong knowledge of Spring Boot, Hibernate, and microservices architecture.")
                        .category(JobCategory.SOFTWARE_DEVELOPMENT)
                        .mode(WorkMode.REMOTE)
                        .salary(new BigDecimal("85000.00"))
                        .responsible(recruiter)
                        .status(JobStatus.OPEN)
                        .build(),
                Vacancy.builder()
                        .title("Data Analyst")
                        .description("Join our data team to analyze business metrics and create insightful reports. Experience with SQL, Python, and visualization tools required.")
                        .category(JobCategory.DATA_ANALYTICS)
                        .mode(WorkMode.HYBRID)
                        .salary(new BigDecimal("65000.00"))
                        .responsible(recruiter)
                        .status(JobStatus.OPEN)
                        .build(),
                Vacancy.builder()
                        .title("DevOps Engineer")
                        .description("Looking for a DevOps engineer to manage CI/CD pipelines and cloud infrastructure. Kubernetes and AWS experience required.")
                        .category(JobCategory.DEVOPS)
                        .mode(WorkMode.REMOTE)
                        .salary(new BigDecimal("90000.00"))
                        .responsible(recruiter)
                        .status(JobStatus.IN_PROGRESS)
                        .build(),
                Vacancy.builder()
                        .title("UI/UX Designer")
                        .description("Creative designer needed to create intuitive user interfaces and experiences. Figma and Adobe XD experience required.")
                        .category(JobCategory.UI_UX_DESIGN)
                        .mode(WorkMode.ONSITE)
                        .salary(new BigDecimal("55000.00"))
                        .responsible(recruiter)
                        .status(JobStatus.OPEN)
                        .build(),
                Vacancy.builder()
                        .title("Cybersecurity Specialist")
                        .description("Protect our systems and data from cyber threats. Experience with security protocols, penetration testing, and compliance required.")
                        .category(JobCategory.CYBERSECURITY)
                        .mode(WorkMode.HYBRID)
                        .salary(new BigDecimal("95000.00"))
                        .responsible(recruiter)
                        .status(JobStatus.OPEN)
                        .build()
        );

        vacanciesRepository.saveAll(vacancies);
        log.info("Seeded {} vacancies.", vacancies.size());
    }

    private void seedApplications(ApplicationRepository applicationRepository,
                                  VacanciesRepository vacanciesRepository,
                                  User candidate) {
        if (applicationRepository.count() > 0) {
            log.debug("Applications already present, skipping.");
            return;
        }

        // Read back the persisted vacancies instead of assuming generated IDs,
        // so seeding stays correct regardless of the database's id sequence.
        List<Vacancy> vacancies = vacanciesRepository.findAll();
        if (vacancies.size() < 3) {
            log.warn("Not enough vacancies to seed applications, skipping.");
            return;
        }

        Application app1 = Application.builder()
                .candidate(candidate)
                .vacancy(vacancies.get(0))
                .status(ApplicationStatus.UNDER_REVIEW)
                .observations("Candidate has 5 years of experience with Java and Spring Boot.")
                .build();

        Application app2 = Application.builder()
                .candidate(candidate)
                .vacancy(vacancies.get(1))
                .status(ApplicationStatus.PENDING)
                .observations("Strong SQL skills and experience with Python data libraries.")
                .build();

        Application app3 = Application.builder()
                .candidate(candidate)
                .vacancy(vacancies.get(2))
                .status(ApplicationStatus.INTERVIEW_SCHEDULED)
                .observations("Candidate has AWS certification and Kubernetes experience.")
                .build();

        applicationRepository.saveAll(List.of(app1, app2, app3));
        log.info("Seeded 3 applications for candidate {}.", candidate.getEmail());
    }

    private void seedInterviews(InterviewRepository interviewRepository,
                                ApplicationRepository applicationRepository) {
        if (interviewRepository.count() > 0) {
            log.debug("Interviews already present, skipping.");
            return;
        }

        // Attach interviews to the application that was seeded as INTERVIEW_SCHEDULED.
        Application application = applicationRepository.findAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.INTERVIEW_SCHEDULED)
                .findFirst()
                .orElse(null);

        if (application == null) {
            log.warn("No INTERVIEW_SCHEDULED application found, skipping interview seeding.");
            return;
        }

        Interview interview1 = Interview.builder()
                .application(application)
                .scheduledDate(LocalDateTime.now().plusDays(2))
                .type(InterviewType.VIRTUAL)
                .status(InterviewStatus.SCHEDULED)
                .notes("Technical interview to assess DevOps skills.")
                .build();

        Interview interview2 = Interview.builder()
                .application(application)
                .scheduledDate(LocalDateTime.now().plusDays(5))
                .type(InterviewType.IN_PERSON)
                .status(InterviewStatus.SCHEDULED)
                .notes("Final interview with the team lead.")
                .build();

        interviewRepository.saveAll(List.of(interview1, interview2));
        log.info("Seeded 2 interviews for application id {}.", application.getId());
    }
}
