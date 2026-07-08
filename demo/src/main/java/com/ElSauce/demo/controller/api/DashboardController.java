package com.ElSauce.demo.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ElSauce.demo.dto.DashboardResponse;
import com.ElSauce.demo.repository.PlatoRepository;
import com.ElSauce.demo.repository.ReservaRepository;
import com.ElSauce.demo.repository.UserRepository;
import com.ElSauce.demo.repository.ZonaRepository;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final ReservaRepository reservaRepository;
    private final PlatoRepository platoRepository;
    private final ZonaRepository zonaRepository;

    public DashboardController(
            UserRepository userRepository,
            ReservaRepository reservaRepository,
            PlatoRepository platoRepository,
            ZonaRepository zonaRepository) {

        this.userRepository = userRepository;
        this.reservaRepository = reservaRepository;
        this.platoRepository = platoRepository;
        this.zonaRepository = zonaRepository;
    }

    @GetMapping
    public DashboardResponse dashboard() {

        return new DashboardResponse(

                userRepository.count(),

                reservaRepository.count(),

                platoRepository.count(),

                zonaRepository.count()

        );
    }

}
