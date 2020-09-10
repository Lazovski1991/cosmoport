package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface ShipService {

    List<Ship> readAll(Specification<Ship> shipSpecification);

    Page<Ship> readAll(Specification<Ship> shipSpecification, Pageable pageable);

    ResponseEntity<Ship> create(Ship ship);

    ResponseEntity<Ship> read(Long id);

    ResponseEntity<Ship> delete(Long id);

    ResponseEntity<Ship> update(Long id, Ship ship);

    Specification<Ship> filterName(String name);

    Specification<Ship> filterPlanet(String planet);

    Specification<Ship> filterShipType(ShipType shipType);

    Specification<Ship> filterDate(Long after, Long before);

    Specification<Ship> filterUsed(Boolean isUsed);

    Specification<Ship> filterSpeed(Double minSpeed, Double maxSpeed);

    Specification<Ship> filterCrewSize(Integer minCrew, Integer maxCrew);

    Specification<Ship> filterRating(Double minRating, Double maxRating);

    Boolean checkDemands(Ship ship);

    Double calculationRating(Double speed, Date prod, Boolean isUsed);

    ResponseEntity<Ship> checkId(Long id);

}
