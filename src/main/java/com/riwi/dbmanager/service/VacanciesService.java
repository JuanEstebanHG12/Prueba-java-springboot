package com.riwi.dbmanager.service;

import com.riwi.dbmanager.dto.request.VacanciesRequest;
import com.riwi.dbmanager.dto.request.VacancyStatusRequest;
import com.riwi.dbmanager.dto.response.VacancyResponse;
import com.riwi.dbmanager.exception.BusinessException;
import com.riwi.dbmanager.exception.UserNotFoundException;
import com.riwi.dbmanager.exception.VacancyNotFoundException;
import com.riwi.dbmanager.mapper.VacanciesMapper;
import com.riwi.dbmanager.model.User;
import com.riwi.dbmanager.model.Vacancy;
import com.riwi.dbmanager.model.enums.Role;
import com.riwi.dbmanager.repository.UserRepository;
import com.riwi.dbmanager.repository.VacanciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //By default, all methods are readOnly, when te fetch is lazy
public class VacanciesService {
    private final VacanciesRepository vacanciesRepository;
    private final UserRepository userRepository;
    private final VacanciesMapper vacanciesMapper;

    @Transactional
    public VacancyResponse saveVacancies(VacanciesRequest vacanciesRequest) {
        User user = userRepository.findById(vacanciesRequest.responsibleId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new BusinessException("This user cannot be responsible for a vacancy");
        }

        Vacancy vacancy = vacanciesMapper.toVacancies(vacanciesRequest);
        vacancy.setResponsible(user);

        vacancy = vacanciesRepository.save(vacancy);

        return vacanciesMapper.toVacanciesRequest(vacancy);
    }

    public List<VacancyResponse> getAllVacancies() {
        List<Vacancy> list = vacanciesRepository.findAll();
        return vacanciesMapper.toVacanciesRequest(list);
    }

    @Transactional
    public VacancyResponse updateVacancies(VacanciesRequest vacanciesRequest, Long id) {
        Vacancy vacancy = vacanciesRepository.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy not found"));

        vacanciesMapper.updateVacancyFromRequest(vacanciesRequest, vacancy);

        if (vacanciesRequest.responsibleId() != null) {
            User user = userRepository.findById(vacanciesRequest.responsibleId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (user.getRole() != Role.ADMIN) {
                throw new BusinessException("This user cannot be responsible for a vacancy");
            }

            vacancy.setResponsible(user);
        }

        return vacanciesMapper.toVacanciesRequest(vacanciesRepository.save(vacancy));
    }

    @Transactional
    public VacancyResponse changeStatus(Long id, VacancyStatusRequest request) {
        Vacancy vacancy = vacanciesRepository.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy not found"));
        vacancy.setStatus(request.status());
        return vacanciesMapper.toVacanciesRequest(vacanciesRepository.save(vacancy));
    }

    public VacancyResponse getVacancyById(Long id) {
        Vacancy vacancy = vacanciesRepository.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy not found"));
        return vacanciesMapper.toVacanciesRequest(vacancy);
    }

    public void deleteVacancies(Long id) {
        vacanciesRepository.deleteById(id);
    }
}
