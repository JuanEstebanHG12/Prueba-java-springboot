package com.riwi.dbmanager.config;

import com.riwi.dbmanager.model.*;
import com.riwi.dbmanager.model.enums.*;
import com.riwi.dbmanager.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository, PasswordEncoder passwordEncoder,
                               VacanciesRepository vacanciesRepository,
                               ApplicationRepository applicationRepository,
                               InterviewRepository interviewRepository) {
        return args -> {
            User admin = createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "Admin",
                    "User",
                    "admin@talentboard.com",
                    "Admin123*",
                    Role.ADMIN
            );

            User recruiter = createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "Recruiter",
                    "User",
                    "recruiter@talentboard.com",
                    "Recruiter123*",
                    Role.RECRUITER
            );

            User candidate = createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "Candidate",
                    "User",
                    "candidate@talentboard.com",
                    "Candidate123*",
                    Role.CANDIDATE
            );

            seedVacancies(vacanciesRepository, recruiter);
            seedApplications(applicationRepository, vacanciesRepository, candidate);
            seedInterviews(interviewRepository, applicationRepository);
        };
    }

    private User createUserIfNotExists(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String name,
            String lastName,
            String email,
            String password,
            Role role
    ) {
        if (userRepository.existsByEmail(email)) {
            return userRepository.findByEmail(email).orElse(null);
        }

        User user = User.builder()
                .name(name)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        return userRepository.save(user);
    }

    private void seedVacancies(VacanciesRepository vacanciesRepository, User recruiter) {
        if (vacanciesRepository.count() > 0) {
            return;
        }

        Vacancy vacancy1 = Vacancy.builder()
                .title("Senior Java Developer")
                .description("We are looking for an experienced Java developer to join our team. Must have strong knowledge of Spring Boot, Hibernate, and microservices architecture.")
                .category(JobCategory.SOFTWARE_DEVELOPMENT)
                .mode(WorkMode.REMOTE)
                .salary(new BigDecimal("85000.00"))
                .responsible(recruiter)
                .status(JobStatus.OPEN)
                .build();

        Vacancy vacancy2 = Vacancy.builder()
                .title("Data Analyst")
                .description("Join our data team to analyze business metrics and create insightful reports. Experience with SQL, Python, and visualization tools required.")
                .category(JobCategory.DATA_ANALYTICS)
                .mode(WorkMode.HYBRID)
                .salary(new BigDecimal("65000.00"))
                .responsible(recruiter)
                .status(JobStatus.OPEN)
                .build();

        Vacancy vacancy3 = Vacancy.builder()
                .title("DevOps Engineer")
                .description("Looking for a DevOps engineer to manage CI/CD pipelines and cloud infrastructure. Kubernetes and AWS experience required.")
                .category(JobCategory.DEVOPS)
                .mode(WorkMode.REMOTE)
                .salary(new BigDecimal("90000.00"))
                .responsible(recruiter)
                .status(JobStatus.IN_PROGRESS)
                .build();

        Vacancy vacancy4 = Vacancy.builder()
                .title("UI/UX Designer")
                .description("Creative designer needed to create intuitive user interfaces and experiences. Figma and Adobe XD experience required.")
                .category(JobCategory.UI_UX_DESIGN)
                .mode(WorkMode.ONSITE)
                .salary(new BigDecimal("55000.00"))
                .responsible(recruiter)
                .status(JobStatus.OPEN)
                .build();

        Vacancy vacancy5 = Vacancy.builder()
                .title("Cybersecurity Specialist")
                .description("Protect our systems and data from cyber threats. Experience with security protocols, penetration testing, and compliance required.")
                .category(JobCategory.CYBERSECURITY)
                .mode(WorkMode.HYBRID)
                .salary(new BigDecimal("95000.00"))
                .responsible(recruiter)
                .status(JobStatus.OPEN)
                .build();

        vacanciesRepository.save(vacancy1);
        vacanciesRepository.save(vacancy2);
        vacanciesRepository.save(vacancy3);
        vacanciesRepository.save(vacancy4);
        vacanciesRepository.save(vacancy5);
    }

    private void seedApplications(ApplicationRepository applicationRepository, 
                                   VacanciesRepository vacanciesRepository, 
                                   User candidate) {
        if (applicationRepository.count() > 0) {
            return;
        }

        Vacancy vacancy1 = vacanciesRepository.findById(1L).orElse(null);
        Vacancy vacancy2 = vacanciesRepository.findById(2L).orElse(null);
        Vacancy vacancy3 = vacanciesRepository.findById(3L).orElse(null);

        if (vacancy1 != null) {
            Application app1 = Application.builder()
                    .candidate(candidate)
                    .vacancy(vacancy1)
                    .status(ApplicationStatus.UNDER_REVIEW)
                    .observations("Candidate has 5 years of experience with Java and Spring Boot.")
                    .build();
            applicationRepository.save(app1);
        }

        if (vacancy2 != null) {
            Application app2 = Application.builder()
                    .candidate(candidate)
                    .vacancy(vacancy2)
                    .status(ApplicationStatus.PENDING)
                    .observations("Strong SQL skills and experience with Python data libraries.")
                    .build();
            applicationRepository.save(app2);
        }

        if (vacancy3 != null) {
            Application app3 = Application.builder()
                    .candidate(candidate)
                    .vacancy(vacancy3)
                    .status(ApplicationStatus.INTERVIEW_SCHEDULED)
                    .observations("Candidate has AWS certification and Kubernetes experience.")
                    .build();
            applicationRepository.save(app3);
        }
    }

    private void seedInterviews(InterviewRepository interviewRepository, 
                                ApplicationRepository applicationRepository) {
        if (interviewRepository.count() > 0) {
            return;
        }

        Application application = applicationRepository.findById(3L).orElse(null);

        if (application != null) {
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

            interviewRepository.save(interview1);
            interviewRepository.save(interview2);
        }
    }
}
